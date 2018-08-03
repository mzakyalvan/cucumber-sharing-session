package com.tiket.poc.outbound;

import io.reactivex.Completable;

import javax.validation.constraints.NotNull;

/**
 * Contract for component responsible for sending email on any event emitted by application.
 *
 * @author zakyalvan
 */
public interface EventMailSender {
    /**
     *
     * @param builder
     * @return
     */
    Completable send(@NotNull MailBuilder builder);
}
