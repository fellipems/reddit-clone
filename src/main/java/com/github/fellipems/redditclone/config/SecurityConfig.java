package com.github.fellipems.redditclone.config;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity  // habilitar o módulo web de segurança em nosso projeto, dependencia do spring web security starter
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {       // extends websecurityAdapter pois é a class base para configuração de segurança que provê todas as configurações padrão que podemos sobreescrever e customizar para nossa aplicação  // toda configuração do nosso back-end

    private final UserDetailsService userDetailsService;

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override       // implementação da interface do nosso Autowired do AuthenticationManager
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable()  // desabilitando o csrf pois usamos cookies para autenticar nossas sessões. Usando webTokens pois rests API são stateless
                .authorizeRequests()
                .antMatchers("/api/auth/**")       // permitindo qualquer requests da nossa API do backend e os que não forem desta URI bloquear e ser autenticado
                .permitAll()
                .antMatchers("/h2/**")
                .permitAll()
                .anyRequest()
                .authenticated();

        httpSecurity.headers().frameOptions().disable();    // liberar o h2 console
    }

    @Autowired      // injetando o managerBuilder para este método
    public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {     // criando o administrador de autenticação
        authenticationManagerBuilder.userDetailsService(userDetailsService)       // usando o ManagerBuilder e o userDetailsService para pegar o input do tipo detailService
                .passwordEncoder(passwordEncoder());

    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();     // hash de criptografia de nossas senhas
    }
}
