package com.tiket.poc.rest;

import com.tiket.poc.dto.RegistrationSummary;
import com.tiket.poc.dto.RegistrationForm;
import com.tiket.poc.service.RegistrationService;
import io.reactivex.Single;
import io.reactivex.SingleTransformer;
import io.reactivex.schedulers.Schedulers;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * Handle partner registration.
 *
 * @author zakyalvan
 */
@RestController
@RequestMapping("/bitubi/registry")
public class RegistrationController {
    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    /**
     * Handle partner registration.
     *
     * @param form
     * @param bindings
     * @return
     */
    @PostMapping
    DeferredResult<RegistrationSummary> handleRegistration(@Validated @RequestBody RegistrationForm form, BindingResult bindings) {
        Single<RegistrationSummary> summary = Single.just(bindings)
                .compose(extraValidations(form, bindings))
                .flatMap(errors -> errors.hasErrors() ?
                        Single.error(new DataBindingException(errors)) :
                        registrationService.registerPartner(registration -> registration.organizationName(form.getOrganizationName())
                                        .contactPerson(form.getContactPerson())
                                        .businessEmail(form.getBusinessEmail())
                                        .phoneNumber(form.getBusinessPhone()))
                                .map(partner -> RegistrationSummary.builder()
                                        .id(partner.getId())
                                        .partnershipState(partner.getPartnershipState())
                                        .registeredTime(partner.getRegisteredTime())
                                        .build()));

        DeferredResult<RegistrationSummary> deferred = new DeferredResult<>();
        summary.subscribe(deferred::setResult, deferred::setErrorResult);
        return deferred;
    }

    /**
     * Extra validation for submitted partner data.
     *
     * @param form
     * @param bindings
     * @return
     */
    private SingleTransformer<Errors, Errors> extraValidations(RegistrationForm form, BindingResult bindings) {
        Single<Boolean> registeredEmail = Single.just(bindings)
                .flatMap(errors -> errors.hasFieldErrors("businessEmail") ?
                        Single.just(false) :
                        registrationService.hasPartner(form.getBusinessEmail()).subscribeOn(Schedulers.io()));

        return upstream -> upstream.zipWith(registeredEmail, (errors, registered) -> {
            if(registered) {
                errors.rejectValue("businessEmail", "already.registered", "Given business email address already registered");
            }
            return errors;
        });
    }
}
