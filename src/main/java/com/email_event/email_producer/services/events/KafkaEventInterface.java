package com.email_event.email_producer.services.events;

import com.email_event.email_producer.models.EmailEvent;

public interface KafkaEventInterface {
    public void sendEmailEvent(EmailEvent emailEvent);
}
