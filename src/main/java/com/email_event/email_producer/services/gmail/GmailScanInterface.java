package com.email_event.email_producer.services.gmail;

import java.util.List;

import com.email_event.email_producer.models.EmailEvent;
import com.google.api.services.gmail.Gmail;

public interface GmailScanInterface {
    public Gmail getService() throws Exception;

    public List<EmailEvent> readEmails();
}
