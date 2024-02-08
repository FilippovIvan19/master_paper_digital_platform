package org.filippov.api.service.dataanalysis;

import org.apache.kafka.common.serialization.Serde;
import org.filippov.api.model.MonitorData;

import java.util.List;

public interface DataAnalyser<T extends AnalyticsDto> {
    T analyse(List<MonitorData> data);
    Serde<T> getDtoSerde();
    Class<T> getDtoClass();
}
