package org.filippov.impl.config.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
@EnableKafka
@EnableKafkaStreams
public class KafkaConfiguration {
    public final String BOOTSTRAP_ADDRESS;
    public final String INPUT_TOPIC;
    public final String OUTPUT_TOPIC;

    public KafkaConfiguration(
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapAddress,
            @Value("${monitor-data.topic.input}") String inputTopic,
            @Value("${monitor-data.topic.output}") String outputTopic
    ) {
        BOOTSTRAP_ADDRESS = bootstrapAddress;
        INPUT_TOPIC = inputTopic;
        OUTPUT_TOPIC = outputTopic;
    }


    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration getStreamsConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_ADDRESS);
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 2);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, JsonSerde.class.getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, JsonSerde.class.getName());
        props.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG, LogAndContinueExceptionHandler.class);
        KafkaStreamsConfiguration streamsConfig = new KafkaStreamsConfiguration(props);
        return streamsConfig;
    }

}
