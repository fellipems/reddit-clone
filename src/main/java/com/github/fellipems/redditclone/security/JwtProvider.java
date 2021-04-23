package com.github.fellipems.redditclone.security;

import com.github.fellipems.redditclone.exceptions.SpringRedditException;
import com.github.fellipems.redditclone.model.User;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtProvider {

    private KeyStore keyStore;
    @Value("${jwt.expiration.time}")
    private Long jwtExpirationInMillis;

    @PostConstruct
    public void init() {
        try {
            keyStore = KeyStore.getInstance("JKS");     // provendo um key store no campo e depois pegando o input stream da key
            InputStream resourceAsStream = getClass().getResourceAsStream("/springblog.jks");       // input stream do arquivo da key store
            keyStore.load(resourceAsStream, "secret".toCharArray());        // depois de carregar o inputStream da KeyStore, temos que prover o inputStream para o método load passando a senha do KeyStore
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new SpringRedditException("Exception occurred while loading keystore", e);
        }

    }

    public String generateToken(Authentication authentication) {
//        User principal = (User) authentication.getPrincipal();  // cast para ser um objeto User
//        return Jwts.builder()   // criando o Jwt. Setando o username como Subject
//                .setSubject(principal.getUsername())
//                .signWith(getPrivateKey())  // temos que prover uma chave. Neste exemplo usamos uma encriptação assimetrica que significa que vamos usar uma key store para assinar o Web Token
//                .compact();
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();  // cast para ser um objeto User
        return Jwts.builder()   // criando o Jwt. Setando o username como Subject
                .setSubject(principal.getUsername())
                .setIssuedAt(Date.from(Instant.now()))
                .signWith(getPrivateKey())  // temos que prover uma chave. Neste exemplo usamos uma encriptação assimetrica que significa que vamos usar uma key store para assinar o Web Token
                .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis)))
                .compact();
    }

    public String generateTokenWithUserName(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date.from(Instant.now()))
                .signWith(getPrivateKey())
                .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis)))
                .compact();
    }

    private PrivateKey getPrivateKey() {
        try {
            return (PrivateKey) keyStore.getKey("springblog", "secret".toCharArray());  // provendo o alias da KeyStore, seguido da senha do KeyStore
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new SpringRedditException("Exception occured while retrieving public key from keystore", e);
        }
    }

}
