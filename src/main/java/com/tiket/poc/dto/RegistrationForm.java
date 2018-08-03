package com.tiket.poc.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

/**
 * @author zakyalvan
 */
@Getter
@JsonDeserialize(builder = RegistrationForm.RegistrationFormBuilder.class)
@SuppressWarnings("serial")
public class RegistrationForm implements Serializable {
    @NotBlank
    private String organizationName;

    @NotBlank
    private String contactPerson;

    @Email
    @NotBlank
    private String businessEmail;

    @NotBlank
    private String businessPhone;

    @Builder
    protected RegistrationForm(String organizationName, String contactPerson, String businessEmail, String businessPhone) {
        this.organizationName = organizationName;
        this.contactPerson = contactPerson;
        this.businessEmail = businessEmail;
        this.businessPhone = businessPhone;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class RegistrationFormBuilder {

    }
}
