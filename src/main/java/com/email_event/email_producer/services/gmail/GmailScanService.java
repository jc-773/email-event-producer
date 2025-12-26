package com.email_event.email_producer.services.gmail;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.email_event.email_producer.models.EmailEvent;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

@Service
public class GmailScanService {
    private static Logger log = LoggerFactory.getLogger(GmailScanService.class);
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_READONLY);
    String path = System.getenv("GMAIL_CREDENTIALS");

    private final GmailParser gmailParser;

    @Autowired
    public GmailScanService(GmailParser gmailParser) throws FileNotFoundException {
        if (path == null) {
            throw new IllegalStateException("GMAIL_CREDENTIALS not set");
        }
        // this.returnsRepo = returnsRepo;
        this.gmailParser = gmailParser;
    }

    public Gmail getService() throws Exception {
        var httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        var jsonFactory = GsonFactory.getDefaultInstance();

        InputStream in = new FileInputStream(path);

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, clientSecrets, SCOPES)
                .setDataStoreFactory(
                        new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();

        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

        return new Gmail.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName("Returns Pooler")
                .build();
    }

    public List<EmailEvent> readEmails() {
        try {
            log.info("kicking off scan for emails");
            Gmail service = getService();

            ListMessagesResponse listResponse = service.users().messages().list("me")
                    .setQ("from:(amazon.com OR jorge.solar.1190@gmail.com) subject:(return) newer_than:1d")
                    .setMaxResults(5L)
                    .execute();

            if (listResponse.getMessages() == null) {
                return List.of();
            }

            List<EmailEvent> results = new ArrayList<>();

            for (Message msg : listResponse.getMessages()) {
                Message fullMsg = service.users().messages()
                        .get("me", msg.getId())
                        .setFormat("full")
                        .execute();

                results.add(gmailParser.toEmailEvent(fullMsg));
            }
            log.info("found {} potential return emails", results);
            return results;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
