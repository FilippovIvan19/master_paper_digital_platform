package org.filippov.impl.service.dataanalysis;

import org.filippov.api.model.dbentities.ResourceMonitor;
import org.filippov.api.service.dataanalysis.AnalyticsHandler;
import org.filippov.api.service.integration.EmergencyCallService;
import org.filippov.api.service.integration.NotificationSendService;
import org.filippov.impl.repository.ResourceMonitorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HotDataAnalyticsHandler implements AnalyticsHandler<HotDataAnalyticsDto> {

    @Autowired
    private EmergencyCallService emergencyCallService;

    @Autowired
    private NotificationSendService notificationSendService;

    @Autowired
    private ResourceMonitorRepository resourceMonitorRepository;

    @Override
    public void handle(HotDataAnalyticsDto input) {
        if (input.getIncident()) {
            emergencyCallService.callEmergency(input.getMonitorId());
        }
        if (input.getLowButtery()) {
            Optional<ResourceMonitor> monitor = resourceMonitorRepository.findById(input.getMonitorId());
            if (monitor.isPresent()) {
                notificationSendService.sendEmail(monitor.get().getUserEmail(),
                        "monitor low buttery",
                        "Hello, dear customer. Please pay attention that your monitor is running out of buttery.");
            }
        }
    }
}
