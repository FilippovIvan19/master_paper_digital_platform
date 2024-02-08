package org.filippov.impl.service.dataanalysis;

import org.filippov.api.service.dataanalysis.AnalyticsHandler;
import org.filippov.api.service.integration.EmergencyCallService;
import org.filippov.api.service.integration.NotificationSendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HotDataAnalyticsHandler implements AnalyticsHandler<HotDataAnalyticsDto> {

    @Autowired
    private EmergencyCallService emergencyCallService;

    @Autowired
    private NotificationSendService notificationSendService;

    @Override
    public void handle(HotDataAnalyticsDto input) {
        if (input.getIncident()) {
            emergencyCallService.callEmergency(input.getMonitorId());
        }
        if (input.getLowButtery()) {
            notificationSendService.sendEmail(input.getMonitorId() + "@gmail.com",
                    "monitor low buttery",
                    "Hello, dear customer. Please pay attention that your monitor is running out of buttery.");
        }
    }
}
