package com.email_event.email_producer.services.cache;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.email_event.email_producer.models.EmailEvent;

@Service
public class EmailEventCacheService implements RedisEventInterface {
    private static Logger log = LoggerFactory.getLogger(EmailEventCacheService.class);

    private RedisTemplate<String, List<EmailEvent>> redisTemplate;
    // private EmailEventService kafkaService;

    @Autowired
    public EmailEventCacheService(
            RedisTemplate<String, List<EmailEvent>> redisTemplate) {
        // this.kafkaService = kafkaService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean doesEmailEventExist(EmailEvent emailEvent) {
        String toEmail = emailEvent.getTo();
        var listOfEmailEvents = redisTemplate.opsForValue().get(toEmail);
        for (EmailEvent event : listOfEmailEvents) {
            if (event.getMessageId() == emailEvent.getMessageId())
                return true;

        }
        return false;

    }

    public void handleEmailEvent(EmailEvent event) {
        if (!doesEmailEventExist(event)) {
            // kafkaService.sendEmailEvent(event.getSubject());// TODO: Should send the
            // entire email event, but kafka is
            // setup to only send Strings not objects
            String messageId = event.getMessageId();
            var listOfEmailEvents = redisTemplate.opsForValue().get(messageId);
            listOfEmailEvents.add(event);
            redisTemplate.opsForValue().set(event.getMessageId(), listOfEmailEvents);
            return;
        }
    }

}
