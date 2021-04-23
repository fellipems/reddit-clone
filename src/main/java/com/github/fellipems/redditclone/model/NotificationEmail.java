package com.github.fellipems.redditclone.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEmail {
    private String subject;     // assunto do email
    private String recipient;   //  destinatário do email
    private String body;    // corpo do email
}
