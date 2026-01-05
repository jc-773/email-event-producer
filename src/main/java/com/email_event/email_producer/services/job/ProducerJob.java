package com.email_event.email_producer.services.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.email_event.email_producer.models.EmailEvent;
import com.email_event.email_producer.services.cache.EmailEventCacheService;
import com.email_event.email_producer.services.cache.RedisEventInterface;
import com.email_event.email_producer.services.gmail.GmailScanService;

@EnableScheduling
@Service
public class ProducerJob {
    private static Logger log = LoggerFactory.getLogger(ProducerJob.class);

    // private RedisEventInterface redisCache;
    private GmailScanService gmailService;

    @Autowired
    public ProducerJob(GmailScanService gmailService) {
        // this.redisCache = redisCache;
        this.gmailService = gmailService;
    }

    // instead of an open stream, just poll every minute Gmail API is generously
    // cheap
    @Scheduled(cron = "0 * * * * *")
    public void jobRunner() {
        var emailEvents = gmailService.readEmails();
        for (EmailEvent emailEvent : emailEvents) {
            // redisCache.handleEmailEvent(emailEvent);
        }
    }
}
