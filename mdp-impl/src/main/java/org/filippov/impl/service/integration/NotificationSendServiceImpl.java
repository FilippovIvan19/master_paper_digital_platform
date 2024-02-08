package org.filippov.impl.service.integration;

import lombok.extern.slf4j.Slf4j;
import org.filippov.api.service.integration.NotificationSendService;
import org.springframework.stereotype.Service;

// Sample of some outer system that send an email notification
@Slf4j
@Service
public class NotificationSendServiceImpl implements NotificationSendService {
    @Override
    public void sendEmail(String email, String subject, String text) {
        log.info("sendEmail - {} {} {}", email, subject, text);
        return;
    }
}
