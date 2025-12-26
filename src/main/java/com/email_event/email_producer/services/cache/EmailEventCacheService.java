package com.email_event.email_producer.services.cache;

import java.time.Duration;
import java.util.ArrayList;
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

    private Duration standardExpiry = Duration.ofDays(2);
    private RedisTemplate<String, List<EmailEvent>> redisTemplate;
    private List<EmailEvent> emailEvents;

    @Autowired
    public EmailEventCacheService(RedisTemplate<String, List<EmailEvent>> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.emailEvents = new ArrayList<>();
    }

    @Override
    public void saveEmailEventIfNotAlreadyPresent(String userId, EmailEvent emailEvent) {
        var emailEvents = redisTemplate.opsForValue().get(userId);
        if (!emailEvents.contains(emailEvent)) {
            emailEvents.add(emailEvent);
            redisTemplate.opsForValue().set(userId, emailEvents, standardExpiry);
        }
        emailEvents.add(emailEvent);
    }

    @Override
    public List<EmailEvent> getEmailEventsForUser(String userId) {
        return redisTemplate.opsForValue().get(userId);
    }

    @Override
    public boolean doesEmailEventAlreadyExist(String userId, EmailEvent emailEvent) {
        log.info("checking if email event-{} exists in cache", emailEvent.getMessageId());
        List<EmailEvent> list = redisTemplate.opsForValue().get(userId);
        if (list != null) {
            for (EmailEvent event : list) {
                if (emailEvent.getMessageId().equals(event.getMessageId())) {
                    log.info("found email event in the cache at messageId—{}", emailEvent.getMessageId());
                    return true;
                }
            }
        } else {
            emailEvents.add(emailEvent);
            redisTemplate.opsForValue().set(userId, emailEvents, standardExpiry);
        }
        return false;
    }
}
