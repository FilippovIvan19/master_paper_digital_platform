package org.filippov.impl.service;

import org.apache.spark.sql.SparkSession;
import org.filippov.api.model.MonitorData.Columns;
import org.filippov.api.service.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static org.apache.spark.sql.functions.*;

@Service
public class SparkBillingService implements BillingService {
    @Autowired
    private SparkSession spark;

    @Override
    public BigDecimal getResourceAmountForMonth(String monitorId, Integer year, Integer month) {
//        spark.sql("USE SmartMonitoring");
//        spark.sql("SELECT * FROM MonitoringData").show();
////        StructType schema = spark.table("SmartMonitoring.MonitoringData").schema();
//
//        spark.sql("SHOW PARTITIONS MonitoringData").show();

//        for debug
        spark.table("SmartMonitoring.MonitoringData")
                .where(col(Columns.MONITOR_ID).equalTo(monitorId)
                        .and(year(col(Columns.TIMESTAMP)).equalTo(year))
                        .and(month(col(Columns.TIMESTAMP)).equalTo(month))
                )
                .show();

        BigDecimal amount = spark.table("SmartMonitoring.MonitoringData")
                .where(col(Columns.MONITOR_ID).equalTo(monitorId)
                        .and(year(col(Columns.TIMESTAMP)).equalTo(year))
                        .and(month(col(Columns.TIMESTAMP)).equalTo(month))
                )
                .select(max(col(Columns.AMOUNT)).minus(min(col(Columns.AMOUNT))))
                .collectAsList().get(0).getDecimal(0);

        return amount;
    }
}
