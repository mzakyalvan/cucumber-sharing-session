package com.tiket.poc.outbound;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import static io.reactivex.Completable.fromAction;

/**
 * @author zakyalvan
 */
@Component
@Validated
class DefaultEventMailSender implements EventMailSender {
    private final JavaMailSender mailSender;

    public DefaultEventMailSender(JavaMailSender mailSender) {
        Assert.notNull(mailSender, "Mail sender object must be provided");
        this.mailSender = mailSender;
    }

    @Override
    public Completable send(MailBuilder builder) {
        return Single.just(builder)
                .map(MailBuilder::build)
                .flatMapCompletable(preparer -> fromAction(() -> mailSender.send(preparer)).subscribeOn(Schedulers.io()));
    }
}
