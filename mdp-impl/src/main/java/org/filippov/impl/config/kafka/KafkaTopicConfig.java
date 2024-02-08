package org.filippov.impl.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {
    @Autowired
    private KafkaConfiguration kafkaConfiguration;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfiguration.BOOTSTRAP_ADDRESS);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic inTopic() {
        return new NewTopic(kafkaConfiguration.INPUT_TOPIC, 1, (short) 1);
    }

    @Bean
    public NewTopic outTopic() {
        return new NewTopic(kafkaConfiguration.OUTPUT_TOPIC, 1, (short) 1);
    }

}