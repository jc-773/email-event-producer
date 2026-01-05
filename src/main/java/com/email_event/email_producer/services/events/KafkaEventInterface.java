package com.email_event.email_producer.services.events;

public interface KafkaEventInterface {
    public void sendEmailEvent(String emailEvent);
}
