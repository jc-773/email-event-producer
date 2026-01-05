package com.email_event.email_producer.services.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EmailEventService implements KafkaEventInterface {
    private static Logger log = LoggerFactory.getLogger(EmailEventService.class);

    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public EmailEventService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendEmailEvent(String emailEvent) {
        // var list = List.of(emailEvent);
        // kafkaTemplate.send("email-event", list);
        // log.info("email event sent to kafka topic");
    }
}
