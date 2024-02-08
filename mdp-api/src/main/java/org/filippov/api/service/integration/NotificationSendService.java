package org.filippov.api.service.integration;

public interface NotificationSendService {
    void sendEmail(String email, String subject, String text);
}
