package org.filippov.service;

import org.filippov.model.MonitorData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaSender {
    @Autowired
    private KafkaTemplate<Long, MonitorData> monitorDataTemplate;

    public void sendMonitorData(MonitorData data) {
        monitorDataTemplate.sendDefault(data.getMonitorId(), data);
    }
}
