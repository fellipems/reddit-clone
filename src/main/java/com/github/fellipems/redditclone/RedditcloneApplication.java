package com.github.fellipems.redditclone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync	// executar o código que manda o email de verificação(MailService) de modo assíncrono(pra nao demorar tanto) rodando em Threads diferentes. Googlar dps - run mail sending functionality asinchrnously
public class RedditcloneApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedditcloneApplication.class, args);
	}

}


