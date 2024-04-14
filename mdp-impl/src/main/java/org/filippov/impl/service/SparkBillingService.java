package org.filippov.impl.service;

import org.apache.spark.sql.SparkSession;
import org.filippov.api.model.MonitorData.Columns;
import org.filippov.api.model.dbentities.ResourceMonitor;
import org.filippov.api.model.dbentities.ResourceMonitor.ResourceType;
import org.filippov.api.service.BillingService;
import org.filippov.api.service.integration.NotificationSendService;
import org.filippov.impl.repository.ResourceMonitorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.spark.sql.functions.*;
import static org.filippov.api.model.dbentities.ResourceMonitor.ResourceType.*;

@Service
public class SparkBillingService implements BillingService {
    @Autowired
    private SparkSession spark;

    @Autowired
    private NotificationSendService notificationSendService;

    @Autowired
    private ResourceMonitorRepository resourceMonitorRepository;

    private final Map<ResourceType, BigDecimal> resourcesCost = new HashMap<>();

    public SparkBillingService () {
        resourcesCost.put(ELECTRICITY, BigDecimal.valueOf(10.0));
        resourcesCost.put(WATER, BigDecimal.valueOf(100.0));
        resourcesCost.put(GAS, BigDecimal.valueOf(1000.0));
    }

    @Override
    public BigDecimal getResourceAmountForMonth(String monitorId, Integer year, Integer month) {
////        for debug
//        spark.table("SmartMonitoring.MonitoringData")
//                .where(col(Columns.MONITOR_ID).equalTo(monitorId)
//                        .and(year(col(Columns.TIMESTAMP)).equalTo(year))
//                        .and(month(col(Columns.TIMESTAMP)).equalTo(month))
//                )
//                .show();

        BigDecimal amount = spark.table("SmartMonitoring.MonitoringData")
                .where(col(Columns.MONITOR_ID).equalTo(monitorId)
                        .and(year(col(Columns.TIMESTAMP)).equalTo(year))
                        .and(month(col(Columns.TIMESTAMP)).equalTo(month))
                )
                .select(max(col(Columns.AMOUNT)).minus(min(col(Columns.AMOUNT))))
                .collectAsList().get(0).getDecimal(0);

        return amount == null ? BigDecimal.ZERO : amount;
    }

    // in future it should be possible to specify month
    // in future invoice info should be saved to db
    @Override
    public void sendMonthInvoice(String monitorId) {
        ResourceMonitor monitor = resourceMonitorRepository.findById(monitorId).get();
        sendMonthInvoice(monitor);
    }

    @Override
    public void sendMonthInvoice(ResourceMonitor monitor) {
        LocalDate date = LocalDate.now().minusMonths(1);
        BigDecimal resourceAmount = getResourceAmountForMonth(monitor.getId(), date.getYear(), date.getMonthValue());
        BigDecimal cost = resourcesCost.get(monitor.getResourceType());
        BigDecimal moneyAmount = resourceAmount.multiply(cost);

        notificationSendService.sendEmail(monitor.getUserEmail(),
                "Monthly " + monitor.getResourceType().getName() + " invoice",
                "Hello, dear customer. Please pay " + moneyAmount);
    }

    @Scheduled(cron = "0 0 0 2 * *") // every month on second day
    public void sendAllInvoices() {
        List<ResourceMonitor> allMonitors = resourceMonitorRepository.findAll();
        for (ResourceMonitor monitor : allMonitors) {
            sendMonthInvoice(monitor);
        }
    }
}
