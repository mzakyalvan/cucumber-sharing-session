package com.tiket.poc.service;

import com.tiket.poc.entity.BusinessPartner;
import io.reactivex.Single;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.function.Consumer;

/**
 * @author zakyalvan
 */
public interface RegistrationService {
    Single<BusinessPartner> registerPartner(@NotNull Consumer<RegistrationData> customizer);
    Single<Boolean> hasPartner(String businessEmail);

    @Data
    @Accessors(fluent = true, chain = true)
    @SuppressWarnings("serial")
    class RegistrationData implements Serializable {
        private String organizationName;
        private String contactPerson;
        private String businessEmail;
        private String phoneNumber;
    }
}
