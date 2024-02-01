package org.filippov.config.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
@EnableKafka
@EnableKafkaStreams
public class KafkaConfiguration {
    private static final Serde<String> STRING_SERDE = Serdes.String();

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration getStreamsConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 2);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, STRING_SERDE.getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, STRING_SERDE.getClass().getName());
        props.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG,
                 LogAndContinueExceptionHandler.class);
//        LogAndFailExceptionHandler
        KafkaStreamsConfiguration streamsConfig = new KafkaStreamsConfiguration(props);
        return streamsConfig;
    }

//    @Bean
//    StreamsBuilderFactoryBeanConfigurer uncaughtExceptionConfigurer(
//            @Qualifier(KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_BUILDER_BEAN_NAME) StreamsBuilderFactoryBean factoryBean,
//            ApplicationContext ctx) {
//        return new StreamsBuilderFactoryBeanConfigurer(factoryBean, ctx);
//    }
//
//    @AllArgsConstructor
//    static class StreamsBuilderFactoryBeanConfigurer implements InitializingBean {
//        private final StreamsBuilderFactoryBean factoryBean;
//        private final ApplicationContext ctx;
//
//        @Override
//        public void afterPropertiesSet() {
//
//            this.factoryBean.setUncaughtExceptionHandler(
//                    (t, e) -> {
//                        log.error("Uncaught exception in thread {}", t.getName(), e);
//                        factoryBean.getKafkaStreams().close(Duration.ofSeconds(10));
//                        log.info("Kafka streams closed.");
//                    });
//            this.factoryBean.setStateListener((newState, oldState) -> {
//                if (newState == KafkaStreams.State.NOT_RUNNING) {
//                    log.info("Now exiting the application.");
//                    SpringApplication.exit(ctx, () -> 1);
//                }
//            });
//        }
//    }

}
