package com.github.fellipems.redditclone.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@AllArgsConstructor
public class MailContentBuilder {

    private final TemplateEngine templateEngine;

    public String build(String message) {       // pega o email de confirmação  e envia para os usuários
        Context context = new Context();
        context.setVariable("message", message);    // setando o template dentro do conteúdo do ThymeLeaf do mailTemplate
        return templateEngine.process("mailTemplate", context);     // passando o arquivo HTML para o template engine processar o método. Thymeleaf automaticamente vai adicionar a mensagem de email para o template HTML
    }
}
