package com.github.fellipems.redditclone.service;

import com.github.fellipems.redditclone.exceptions.SpringRedditException;
import com.github.fellipems.redditclone.model.NotificationEmail;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j      // cria uma instância do objeto logger Slf4j
public class MailService {

    private final JavaMailSender mailSender;
    private final MailContentBuilder mailContentBuilder;

    @Async  // tempo de resposta mais rápido; este Async está ligado com o EnableAsync da classe mainApplication para rodar em threads diferentes e ser mais rápido a respósta do email de verificação
    public void sendMail(NotificationEmail notificationEmail) {     // este método pega o objeto do tipo notificationEmail como input
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);   // criaçã oda instância do MimeMessageHelper
            messageHelper.setFrom("springreddit@email.com");    // informação do remetente
            messageHelper.setTo(notificationEmail.getRecipient());      // métodos setTo, setSubject e setText mapeados do objeto classe NotificationEmail
            messageHelper.setSubject(notificationEmail.getSubject());
            messageHelper.setText(mailContentBuilder.build(notificationEmail.getBody()));   // método do MailContentBuilder que irá fazer o build e retornar a mensagem em formato HTML
        };
        try {
            mailSender.send(messagePreparator);
            log.info("Email de ativação enviado!!");
        } catch (MailException e) {
            log.error("Ocorreu um erro ao enviar o email", e);
            throw new SpringRedditException("Ocorreu um erro ao enviar o email para " + notificationEmail.getRecipient(), e);
        }
    }
}

