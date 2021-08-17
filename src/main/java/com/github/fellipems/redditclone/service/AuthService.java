package com.github.fellipems.redditclone.service;

import com.github.fellipems.redditclone.dto.AuthenticationResponse;
import com.github.fellipems.redditclone.dto.LoginRequest;
import com.github.fellipems.redditclone.dto.RegisterRequest;
import com.github.fellipems.redditclone.exceptions.SpringRedditException;
import com.github.fellipems.redditclone.model.NotificationEmail;
import com.github.fellipems.redditclone.model.User;
import com.github.fellipems.redditclone.model.VerificationToken;
import com.github.fellipems.redditclone.repository.UserRepository;
import com.github.fellipems.redditclone.repository.VerificationTokenRepository;
import com.github.fellipems.redditclone.security.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service    // classe que vai ter toda nossa lógica de negócio para registrar os usuários. Criar os usuário e salva-los no BD e enviar o email de confirmação
@AllArgsConstructor     // construtor para nossa injeção de dependência, assim não precisaremos usar o @Autowired já que não é muito recomendado para injeção
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;  // injetando o serviço de email para enviar
    private final AuthenticationManager authenticationManager;  // como é uma interface, se não explicitarmos qual bean criar, spring lançará uma exceção. Teremos que criar um bean dentro do nosso SecurityConfig para resolver isso e ele saber qual injetar
    private final JwtProvider jwtProvider;  // injetando a classe JwtProvider

    @Transactional
    public void signup(RegisterRequest registerRequest) {
        User user = new User();   // criação do objeto usuário
        //mapeando os dados que vieram do registerRequest para o nosso userObject
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));    // usando o @Bean do enconder BCrypt para as nossas senhas
        user.setCreated(Instant.now());
        user.setEnabled(false);     // falso pois ele só se registrou, depois que confirmar o email passa a flag para true
        userRepository.save(user);
    
        String token = generateVerificationToken(user);    // gera um token aleatório
        mailService.sendMail(new NotificationEmail("Bem vindo! Por favor ative sua conta!",
                user.getEmail(),
                "Obrigado por se registrar no SB Reddit. Por último, pedimos para que ative a sua conta!\n" +
                        "Por favor, clique no link abaixo para fazer a ativação da sua nova conta para desfrutar do nosso site.\n" +
                        "http://localhost:8081/api/auth/accountverification/" + token)); // primeiro argumento é o assunto, segundo é o destinatário e o último é o corpo do email;
    }

    private String generateVerificationToken(User user) {     // geração do token de verificação
        String token = UUID.randomUUID().toString();// cria um único e aleatório valor de 128-bit que podemos usar como nosso token de verificação. Além de enviar o email temos que guardar o token de verificação em nosso BD
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationTokenRepository.save(verificationToken);    // salvando o token no BD
        return token;       //  retornando o token de volta para nosso método do signup
    }       // enviando o email de ativação para os usuários

    public void verifyAccount(String token) {       // verificando a conta com o token gerado
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token); // como retorna um Optional(que pode ter ou não) colocaremos um exception throw
        verificationToken.orElseThrow(() -> new SpringRedditException("Token Inválido!!")); // entra aqui caso o Optional retorne vazio/não exista
        fetchUserAndEnable(verificationToken.get());    // se o usuário foi associado ao token, vamos dar permissão à ele. Agora que temos a query que corresponde ao usuário e associado ao seu token, vamos habilitar o usuário.
    }

    @Transactional
    private void fetchUserAndEnable(VerificationToken verificationToken) {  // busca o usuário do token e autentica/valida
        String username = verificationToken.getUser().getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new SpringRedditException("Usuário de nome " + username + " não foi encontrado!"));
        user.setEnabled(true);  // permitindo o usuário que já validou o email
        userRepository.save(user);
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {      // lógica para autenticar o usuário
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));// passando um objeto do tipo username e password token de autenticação passando o new username e password que vem do objeto request login como argumento do construtor
        SecurityContextHolder.getContext().setAuthentication(authenticate);     // guardar o Authentication Object dentro do SecurityContext e mostrar se ele está logado ou não
        String token = jwtProvider.generateToken(authenticate);// chamando o método para gerar o token e o método retornar como um String que é nosso código de autenticação e enviar de volta esse token para o User
        return new AuthenticationResponse(token, loginRequest.getUsername());
    }

    /*
        fluxo de autenticação JWT - 1 login request, 2 criação do JWT, 3 envia JWT para o client, 4 Cliente usa JWT para se autenticar, 5 Validação do JWT, 6 Resposta para o client
        chama o AuthService(cria o JWT) que receberá o request de autenticação em que, dentro dele, terá o username e password da request, em que vamos criar o objeto UsernamePassword token de autenticação.
        Passamos esse objeto criado para o AuthenticationManager que vai cuidar de toda a autenticação dos nossos usuários, esse Manager vai usar uma interface de UserDetailsService
        em que vai pegar os detalhes do ususário para múltiplos recursos acessando nosso BD para ver se os dados estão corretos ou não. Se estiverem corretos, o UserDetails passará para o gerenciador de autenticação(AuthenticationManager)
        que retornará um objeto Authentication para terminar nosso serviço de autenticação(voltando para o AuthService)
    */
}

