package com.email_event.email_producer.services.cache;

import java.util.List;

import com.email_event.email_producer.models.EmailEvent;

public interface RedisEventInterface {
    public void saveEmailEventIfNotAlreadyPresent(String userId, EmailEvent emailEvent);

    public List<EmailEvent> getEmailEventsForUser(String userId);

    public boolean doesEmailEventAlreadyExist(String userId, EmailEvent emailEvent);
}
