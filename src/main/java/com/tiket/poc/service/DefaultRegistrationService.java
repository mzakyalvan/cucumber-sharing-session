package com.tiket.poc.service;

import com.tiket.poc.entity.BusinessPartner;
import com.tiket.poc.entity.PartnershipState;
import com.tiket.poc.outbound.EventMailSender;
import com.tiket.poc.outbound.MailBuilder;
import com.tiket.poc.outbound.RegistrationMailBuilder;
import com.tiket.poc.repo.BusinessPartnerRepository;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author zakyalvan
 */
@Service
@Validated
public class DefaultRegistrationService implements RegistrationService {
    private final BusinessPartnerRepository partnerRepository;

    private EventMailSender mailSender;

    public DefaultRegistrationService(BusinessPartnerRepository partnerRepository) {
        Assert.notNull(partnerRepository, "Partner repository must be provided");
        this.partnerRepository = partnerRepository;
    }

    @Override
    public Single<BusinessPartner> registerPartner(Consumer<RegistrationData> customizer) {
        return Single.just(customizer)
                .map(consumer -> {
                    RegistrationData registration = new RegistrationData();
                    consumer.accept(registration);

                    return BusinessPartner.builder()
                            .id(UUID.randomUUID())
                            .organizationName(registration.organizationName())
                            .contactName(registration.contactPerson())
                            .partnershipState(PartnershipState.PENDING)
                            .emailAddress(registration.businessEmail())
                            .phoneNumber(registration.phoneNumber())
                            .registeredTime(LocalDateTime.now())
                            .build();
                })
                .flatMap(partner -> Single.fromCallable(() -> partnerRepository.save(partner))
                        .subscribeOn(Schedulers.io()))
                .flatMap(partner -> {
                    MailBuilder mail = new RegistrationMailBuilder()
                            .recipientAddress(partner.getEmailAddress())
                            .organizationId(partner.getId().toString())
                            .organizationName(partner.getOrganizationName())
                            .contactPerson(partner.getContactName())
                            .partnershipState(partner.getPartnershipState())
                            .registeredTime(partner.getRegisteredTime());

                    return mailSender.send(mail)
                            // I don't want to break all flow because of mail sending failure.
                            // We can publish an event as failure sign using "onErrorResumeNext" but don't break the flow.
                            //.onErrorComplete()
                            .andThen(Single.just(partner));
                });
    }

    @Override
    public Single<Boolean> hasPartner(String businessEmail) {
        return Single.fromCallable(() -> {
            if(StringUtils.hasText(businessEmail)) {
                boolean exists = partnerRepository.exists(Example.of(BusinessPartner.builder().emailAddress(businessEmail).build()));
                return exists;
            }
            return false;
        });
    }

    @Autowired(required = false)
    public void setMailSender(EventMailSender mailSender) {
        this.mailSender = mailSender;
    }
}
