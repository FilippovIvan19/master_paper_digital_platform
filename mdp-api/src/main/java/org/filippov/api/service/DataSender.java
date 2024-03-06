package org.filippov.api.service;

import org.filippov.api.model.MonitorData.MonitorDataDto;

public interface DataSender {
    void sendMonitorData(MonitorDataDto data);
}
