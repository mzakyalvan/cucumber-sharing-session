package com.tiket.poc.outbound;

import com.tiket.poc.entity.PartnershipState;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author zakyalvan
 */
@Data
@Accessors(fluent = true, chain = true)
public class RegistrationMailBuilder implements MailBuilder {
    private String recipientAddress;
    private String organizationId;
    private String organizationName;
    private String contactPerson;
    private PartnershipState partnershipState;
    private LocalDateTime registeredTime;

    @Override
    public MimeMessagePreparator build() {
        return message -> {
            MimeMessageHelper helper = new MimeMessageHelper(message);

            helper.setFrom("technology@tiket.com");
            helper.setTo(recipientAddress);
            helper.setSubject("Welcome to tiket.com");

            String content = String.format("Hi %s, Welcome to tiket.com. Your account registered on %s",
                    organizationName,
                    registeredTime.format(DateTimeFormatter.ISO_DATE_TIME));
            helper.setText(content, true);
        };
    }
}
