package com.tiket.poc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.tiket.poc.entity.PartnershipState;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author zakyalvan
 */
@Getter
@NoArgsConstructor
@JsonDeserialize(builder = RegistrationSummary.RegistrationSummaryBuilder.class)
@SuppressWarnings("serial")
public class RegistrationSummary implements Serializable {
    @NotNull
    private UUID id;

    @NotNull
    private PartnershipState partnershipState;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime registeredTime;

    @Builder
    protected RegistrationSummary(UUID id, PartnershipState partnershipState, LocalDateTime registeredTime) {
        this.id = id;
        this.partnershipState = partnershipState;
        this.registeredTime = registeredTime;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class RegistrationSummaryBuilder {
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        public RegistrationSummaryBuilder registeredTime(LocalDateTime registeredTime) {
            this.registeredTime = registeredTime;
            return this;
        }
    }
}
