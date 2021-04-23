package com.github.fellipems.redditclone.controller;

import com.github.fellipems.redditclone.dto.AuthenticationResponse;
import com.github.fellipems.redditclone.dto.LoginRequest;
import com.github.fellipems.redditclone.dto.RegisterRequest;
import com.github.fellipems.redditclone.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;  // injetando para chamar o método de signup(cadastrar usuário)

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody RegisterRequest registerRequest){   //input do tipo RegisterRequest, que será a classe onde vamos passar os detalhes do usuário como o email, senha, usuário
        authService.signup(registerRequest);    // requisição de cadastro
        return new ResponseEntity<>("Usuário registrado com sucesso!", HttpStatus.OK);  // sem erros e se for registrado, chama o ResponseEntity com status ok
    }

    @GetMapping("accountVerification/{token}")  // API de confirmação e ativação da conta
    public ResponseEntity<String> verifyAccount(@PathVariable String token){
        authService.verifyAccount(token);
        return new ResponseEntity<>("Conta ativada com sucesso. Obrigado por se cadastrar no SB Reddit <3", HttpStatus.OK);
    }

    @PostMapping("/login")      // método e lógica que recebe o request de autorização do client
    public AuthenticationResponse login(@RequestBody LoginRequest loginRequest) {   // passando um objeto do DTO do tipo LoginRequest
        return authService.login(loginRequest);        // input do nosso loginRequest do usuário e senha
    }
}
