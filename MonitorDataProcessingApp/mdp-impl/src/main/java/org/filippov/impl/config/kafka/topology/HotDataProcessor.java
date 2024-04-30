package org.filippov.impl.config.kafka.topology;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.filippov.api.model.MonitorData;
import org.filippov.api.model.MonitorData.MonitorDataDto;
import org.filippov.impl.config.kafka.KafkaConfiguration;
import org.filippov.impl.service.dataanalysis.HotDataAnalyser;
import org.filippov.impl.service.dataanalysis.HotDataAnalyticsDto;
import org.filippov.impl.service.dataanalysis.HotDataAnalyticsHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class HotDataProcessor {
    private static final Serde<String> STRING_SERDE = Serdes.String();
    private static final JsonSerde<MonitorDataDto> MONITOR_DTO_SERDE = new JsonSerde<>(MonitorDataDto.class);
    private static final Serde<List<MonitorDataDto>> MONITOR_DTO_LIST_SERDE = Serdes.ListSerde(ArrayList.class, MONITOR_DTO_SERDE);

    @Autowired
    private HotDataAnalyser hotDataAnalyser;

    @Autowired
    private HotDataAnalyticsHandler hotDataAnalyticsHandler;

    @Autowired
    private KafkaConfiguration kafkaConfiguration;

    @Autowired
    private void buildPipeline(StreamsBuilder streamsBuilder) {
        KStream<String, MonitorDataDto> inputStream = streamsBuilder
                .stream(kafkaConfiguration.INPUT_TOPIC, Consumed.with(STRING_SERDE, MONITOR_DTO_SERDE));

        Duration windowSize = Duration.ofSeconds(30);
        Duration advance = Duration.ofSeconds(10);
        TimeWindows hotDataWindow = TimeWindows.ofSizeWithNoGrace(windowSize).advanceBy(advance);

        TimeWindowedKStream<String, MonitorDataDto> hotData = inputStream.groupByKey().windowedBy(hotDataWindow);

        KStream<Windowed<String>, List<MonitorDataDto>> batchedDataForChecks = hotData
                .aggregate(
                        () -> (List<MonitorDataDto>) new ArrayList<MonitorDataDto>(),
                        (windowKey, newValue, aggValue) -> {aggValue.add(newValue); return aggValue;},
                        Materialized.with(STRING_SERDE, MONITOR_DTO_LIST_SERDE)
                )
                .toStream();

        KStream<String, HotDataAnalyticsDto> outputStream = batchedDataForChecks
                .mapValues(dtoList -> hotDataAnalyser.analyse(
                        dtoList.stream().map(MonitorData::new).collect(Collectors.toList())
                ))
                .selectKey((wk, v) -> wk.key());

        outputStream.foreach((k, v) -> hotDataAnalyticsHandler.handle(v));

        // for debug
        outputStream.to(kafkaConfiguration.OUTPUT_TOPIC, Produced.with(STRING_SERDE, hotDataAnalyser.getDtoSerde()));
    }

}
