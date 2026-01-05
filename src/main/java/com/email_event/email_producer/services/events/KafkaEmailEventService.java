package com.email_event.email_producer.services.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.email_event.email_producer.models.EmailEvent;

@Service
public class KafkaEmailEventService implements KafkaEventInterface {
    private static Logger log = LoggerFactory.getLogger(KafkaEmailEventService.class);

    private KafkaTemplate<String, EmailEvent> kafkaTemplate;

    @Autowired
    public KafkaEmailEventService(KafkaTemplate<String, EmailEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendEmailEvent(EmailEvent emailEvent) {
        kafkaTemplate.send("email-event", emailEvent);
        log.info("email event sent to kafka topic");
    }
}
