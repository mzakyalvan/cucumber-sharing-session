package com.tiket.poc.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author zakyalvan
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "business-partner")
public class BusinessPartner implements Persistable<UUID>, Serializable {
    @Id
    private UUID id;

    @NotBlank
    private String organizationName;

    @NotBlank
    private String contactName;

    @Email
    @NotBlank
    @Indexed(unique = true)
    private String emailAddress;

    @NotBlank
    private String phoneNumber;

    @NotNull
    private PartnershipState partnershipState;

    @NotNull
    private LocalDateTime registeredTime;

    @Transient
    private boolean alreadyRegistered = true;

    @Builder
    protected BusinessPartner(UUID id, String organizationName, String contactName, String emailAddress, String phoneNumber, PartnershipState partnershipState, @NotNull LocalDateTime registeredTime) {
        this.id = id;
        this.organizationName = organizationName;
        this.contactName = contactName;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.partnershipState = partnershipState;
        this.registeredTime = registeredTime;
        this.alreadyRegistered = false;
    }

    @Override
    public boolean isNew() {
        return !alreadyRegistered;
    }
}
