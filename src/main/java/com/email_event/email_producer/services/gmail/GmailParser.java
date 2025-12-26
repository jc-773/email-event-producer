package com.email_event.email_producer.services.gmail;

import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.email_event.email_producer.models.EmailEvent;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;

@Service
public class GmailParser {

    public EmailEvent toEmailEvent(Message msg) {

        EmailEvent event = new EmailEvent();

        event.setMessageId(msg.getId());
        event.setThreadId(msg.getThreadId());
        event.setTimestamp(msg.getInternalDate());

        // Extract headers
        var headers = msg.getPayload().getHeaders();
        event.setFrom(findHeader(headers, "From"));
        event.setTo(findHeader(headers, "To"));
        event.setSubject(findHeader(headers, "Subject"));

        // Body extraction
        event.setSnippet(msg.getSnippet());
        event.setBodyText(extractPlainText(msg.getPayload()));

        // Optional: merchant, carrier, deadline, label URL
        event.setMerchant(detectMerchant(event));
        event.setCarrier(detectCarrier(event));
        event.setLabelUrl(findLabelUrl(event.getBodyText()));
        event.setDeadline(findDeadline(event.getBodyText()));

        event.setReturnRelated(isReturnRelated(event));

        return event;
    }

    private String findHeader(List<MessagePartHeader> headers, String name) {
        return headers.stream()
                .filter(h -> h.getName().equalsIgnoreCase(name))
                .map(MessagePartHeader::getValue)
                .findFirst()
                .orElse(null);
    }

    private String extractPlainText(MessagePart payload) {
        if (payload == null)
            return "";

        if (payload.getMimeType().equals("text/plain")) {
            return new String(Base64.getUrlDecoder().decode(payload.getBody().getData()));
        }

        if (payload.getParts() != null) {
            for (MessagePart part : payload.getParts()) {
                if (part.getMimeType().equals("text/plain")) {
                    return new String(Base64.getUrlDecoder().decode(part.getBody().getData()));
                }
            }
        }

        return "";
    }

    private String detectMerchant(EmailEvent e) {
        // Minimal MVP version
        if (e.getFrom() != null) {
            if (e.getFrom().contains("amazon"))
                return "Amazon";
            if (e.getFrom().contains("zara"))
                return "Zara";
            if (e.getFrom().contains("temu"))
                return "Temu";
        }
        return null;
    }

    private String detectCarrier(EmailEvent e) {
        String body = e.getBodyText().toLowerCase();
        if (body.contains("ups"))
            return "UPS";
        if (body.contains("fedex"))
            return "FedEx";
        if (body.contains("usps"))
            return "USPS";
        if (body.contains("amazon logistics"))
            return "Amazon Logistics";
        return null;
    }

    private boolean isReturnRelated(EmailEvent e) {
        String text = (e.getSubject() + " " + e.getBodyText()).toLowerCase();
        return text.contains("return")
                || text.contains("refund")
                || text.contains("rma")
                || text.contains("label");
    }

    private String findLabelUrl(String body) {
        if (body == null)
            return null;

        Pattern p = Pattern.compile("https?://[^\\s]+\\.pdf");
        Matcher m = p.matcher(body);

        return m.find() ? m.group() : null;
    }

    private String findDeadline(String body) {
        // Optional for MVP — can add later
        return null;
    }
}
