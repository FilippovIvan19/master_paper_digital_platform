package org.filippov.impl.service;

import org.filippov.api.model.MonitorData.MonitorDataDto;
import org.filippov.api.service.DataSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Qualifier("kafkaSender")
public class KafkaSender implements DataSender {
    @Autowired
    private KafkaTemplate<String, MonitorDataDto> monitorDataTemplate;

    public void sendMonitorData(MonitorDataDto data) {
        monitorDataTemplate.sendDefault(data.getMonitorId(), data);
    }
}
