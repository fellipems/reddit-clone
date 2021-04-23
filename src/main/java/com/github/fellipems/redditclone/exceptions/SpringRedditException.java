package com.github.fellipems.redditclone.exceptions;

import org.springframework.mail.MailException;

public class SpringRedditException extends RuntimeException {
    public SpringRedditException(String exMessage, Exception exception) {   // exception de envio de email com problema
        super(exMessage);
    }

    public SpringRedditException(String exMessage) {
        super(exMessage);
    }
}
