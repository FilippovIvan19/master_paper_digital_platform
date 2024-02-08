package org.filippov.impl.config.kafka.topology;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.filippov.api.model.MonitorData;
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

@Component
public class HotDataProcessor {
    private static final Serde<String> STRING_SERDE = Serdes.String();
    private static final Serde<Long> LONG_SERDE = Serdes.Long();
    private static final JsonSerde<Long> LONG_JSON_SERDE = new JsonSerde<>(Long.class);
    private static final JsonSerde<Integer> INT_JSON_SERDE = new JsonSerde<>(Integer.class);
    private static final JsonSerde<MonitorData> MONITOR_SERDE = new JsonSerde<>(MonitorData.class);
    private static final JsonSerde<List<MonitorData>> MONITOR_LIST_SERDE = new JsonSerde<>(List.class);

    @Autowired
    private HotDataAnalyser hotDataAnalyser;

    @Autowired
    private HotDataAnalyticsHandler hotDataAnalyticsHandler;

    @Autowired
    private KafkaConfiguration kafkaConfiguration;

    @Autowired
    private void buildPipeline(StreamsBuilder streamsBuilder) {
        KStream<String, MonitorData> inputStream = streamsBuilder
                .stream(kafkaConfiguration.INPUT_TOPIC, Consumed.with(STRING_SERDE, MONITOR_SERDE));

        Duration windowSize = Duration.ofSeconds(30);
        Duration advance = Duration.ofSeconds(10);
        TimeWindows hotDataWindow = TimeWindows.ofSizeWithNoGrace(windowSize).advanceBy(advance);

        TimeWindowedKStream<String, MonitorData> hotData = inputStream.groupByKey().windowedBy(hotDataWindow);

        KStream<Windowed<String>, List<MonitorData>> batchedDataForChecks = hotData
                .aggregate(
                        () -> (List<MonitorData>) new ArrayList<MonitorData>(),
                        (windowKey, newValue, aggValue) -> {aggValue.add(newValue); return aggValue;},
                        Materialized.with(STRING_SERDE, Serdes.ListSerde(ArrayList.class, MONITOR_SERDE))
                )
                .toStream();

        KStream<String, HotDataAnalyticsDto> outputStream = batchedDataForChecks
                .mapValues(hotDataAnalyser::analyse)
                .selectKey((wk, v) -> wk.key());

        outputStream.foreach((k, v) -> hotDataAnalyticsHandler.handle(v));

        // for debug
        outputStream.to(kafkaConfiguration.OUTPUT_TOPIC, Produced.with(STRING_SERDE, hotDataAnalyser.getDtoSerde()));
    }

}
