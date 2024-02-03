package org.filippov.config.kafka.topology;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.filippov.model.MonitorData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value(value = "${monitor-data.topic.input}")
    private String inputTopicName;
    @Value(value = "${monitor-data.topic.output}")
    private String outputTopicName;

    @Autowired
    void buildPipeline(StreamsBuilder streamsBuilder) {
//        KStream<String, String> messageStream = streamsBuilder
//                .stream("input-topic", Consumed.with(STRING_SERDE, STRING_SERDE));
//        KTable<String, Long> wordCounts = messageStream
//                .mapValues((ValueMapper<String, String>) String::toLowerCase)
//                .flatMapValues(value -> Arrays.asList(value.split("\\W+")))
//                .groupBy((key, word) -> word, Grouped.with(STRING_SERDE, STRING_SERDE))
//                .count();
//        wordCounts.toStream().to("output-topic");
//
//        streamsBuilder.<String, String>stream("streams-plaintext-input")
//                .flatMapValues(value -> Arrays.asList(value.toLowerCase(Locale.getDefault()).split("\\W+")))
//                .groupBy((key, value) -> value)
//                .count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("counts-store"))
//                .<String, Long>toStream()
//                .mapValues(v -> String.valueOf(v))
//                .to("streams-linesplit-output", Produced.with(Serdes.String(), Serdes.String()));
//
//        KStream<String, Bet> input = streamsBuilder.
//                stream(BET_TOPIC,
//                        Consumed.with(Serdes.String(),
//                                new JsonSerde<>(Bet.class))
//                );
//
//        // Aggregating with time-based windowing (here: with 5-minute sliding windows and 30-minute grace period)
//        KTable<Windowed<String>, Long> timeWindowedAggregatedStream = inputStream.groupByKey().windowedBy(SlidingWindows.ofTimeDifferenceAndGrace(Duration.ofMinutes(5), Duration.ofMinutes(30)))
//                .aggregate(
//                        () -> 0L, /* initializer */
//                        (aggKey, newValue, aggValue) -> aggValue + newValue, /* adder */
//                        Materialized.<String, Long, WindowStore<Bytes, byte[]>>as("time-windowed-aggregated-stream-store") /* state store name */
//                                .withValueSerde(Serdes.Long())); /* serde for aggregate value */




        KStream<Long, MonitorData> inputStream = streamsBuilder.stream(inputTopicName, Consumed.with(LONG_SERDE, MONITOR_SERDE));

        Duration windowSize = Duration.ofSeconds(30);
        Duration advance = Duration.ofSeconds(10);
        TimeWindows hotDataWindow = TimeWindows.ofSizeWithNoGrace(windowSize).advanceBy(advance);

//        SlidingWindows hotDataWindow = SlidingWindows.ofTimeDifferenceAndGrace(Duration.ofSeconds(5), Duration.ofSeconds(10));

        TimeWindowedKStream<Long, MonitorData> hotData = inputStream.groupByKey().windowedBy(hotDataWindow);


//        Materialized mat = Materialized.with(WindowedSerdes.timeWindowedSerdeFrom(Long.class), Serdes.ListSerde(ArrayList.class, MONITOR_SERDE));

        KStream<Windowed<Long>, List<MonitorData>> batchedDataForChecks = hotData
                .aggregate(
                        () -> (List<MonitorData>) new ArrayList<MonitorData>(),
                        (windowKey, newValue, aggValue) -> {aggValue.add(newValue); return aggValue;},
                        Materialized.with(LONG_SERDE, Serdes.ListSerde(ArrayList.class, MONITOR_SERDE))
                )
                .toStream()
                ;


        // AccidentProbability calculating
        KStream<Long, Integer> outputStream = batchedDataForChecks
                .mapValues(this::detectAccidentProbability)
                .selectKey((wk, v) -> wk.key());
//        outputStream.to("output-topic");
        outputStream.to(outputTopicName, Produced.with(LONG_JSON_SERDE, INT_JSON_SERDE));

    }

    public Integer detectAccidentProbability(List<MonitorData> data) {
        return Integer.parseInt(data.get(0).getFlags().toString());
    }

}

