package org.filippov.impl.service.dataanalysis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.serialization.Serde;
import org.filippov.api.service.dataanalysis.AnalyticsDto;
import org.springframework.kafka.support.serializer.JsonSerde;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotDataAnalyticsDto implements AnalyticsDto {
    private String monitorId;
    private Boolean incident;
    private Boolean intervention;
    private Boolean crash;
    private Boolean lowButtery;

    public static Serde<HotDataAnalyticsDto> getSerde() {
        return new JsonSerde<>(HotDataAnalyticsDto.class);
    }
}
