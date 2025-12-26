package com.email_event.email_producer.services.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.email_event.email_producer.models.EmailEvent;
import com.email_event.email_producer.services.cache.EmailEventCacheService;
import com.email_event.email_producer.services.events.EmailEventService;
import com.email_event.email_producer.services.gmail.GmailScanService;

@EnableScheduling
@Service
public class ProducerJob {
    private static Logger log = LoggerFactory.getLogger(ProducerJob.class);

    private EmailEventCacheService redisCache;
    private EmailEventService kafkaService;
    private GmailScanService gmailService;

    @Autowired
    public ProducerJob(EmailEventCacheService redisCache, EmailEventService kafkaService,
            GmailScanService gmailService) {
        this.redisCache = redisCache;
        this.kafkaService = kafkaService;
        this.gmailService = gmailService;
    }

    @Scheduled(cron = "0 * * * * *")
    public void jobRunner() {
        var emailEvents = gmailService.readEmails();
        for (EmailEvent emailEvent : emailEvents) {
            if (!redisCache.doesEmailEventAlreadyExist("me", emailEvent)) {
                kafkaService.sendEmailEvent(emailEvent);
            } else {
                log.info("email event already in cache, not sending to kafka");
            }
        }
    }
}
