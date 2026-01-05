package com.email_event.email_producer.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import com.email_event.email_producer.models.EmailEvent;

@Configuration
public class KafkaConfig {

    @Bean
    public KafkaTemplate<String, EmailEvent> kafkaTemplate(ProducerFactory<String, EmailEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
