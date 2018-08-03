package com.tiket.poc.outbound;

import org.springframework.mail.javamail.MimeMessagePreparator;

/**
 * Heavily rely on spring abstraction :).
 *
 * @author zakyalvan
 */
public interface MailBuilder {
    MailBuilder recipientAddress(String emailAddress);
    MimeMessagePreparator build();
}
