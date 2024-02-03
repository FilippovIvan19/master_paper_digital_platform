package org.filippov.api.service;

import org.filippov.api.model.MonitorData;

public interface DataSender {
    void sendMonitorData(MonitorData data);
}
