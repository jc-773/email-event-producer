package com.email_event.email_producer.services.cache;

import com.email_event.email_producer.models.EmailEvent;

public interface RedisEventInterface {
    public boolean doesEmailEventExist(EmailEvent emailEvent);

    public void handleEmailEvent(EmailEvent emailEvent);
}
