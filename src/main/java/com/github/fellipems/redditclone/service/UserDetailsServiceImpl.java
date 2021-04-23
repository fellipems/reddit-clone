package com.github.fellipems.redditclone.service;

import com.github.fellipems.redditclone.model.User;
import com.github.fellipems.redditclone.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

import static java.util.Collections.singletonList;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {     // pega o username como input e retorna o objeto UserDetail
        Optional<User> userOptional = userRepository.findByUsername(username);  // querie do userRepository pra achar o usuário pelo username
        User user = userOptional
                .orElseThrow(() -> new UsernameNotFoundException("Nenhum usuário " +
                        "com nome: " + username + " encontrado!"));        // se não achar o User, lança exceção

        return new org.springframework.security
                .core.userdetails.User(user.getUsername(), user.getPassword(),
                user.isEnabled(), true, true,
                true, getAuthorities("USER"));      // criamos outro objeto Wrapper com mesmo nome de usuário. Essa classe vai ser provida pelo Spring framework  que implementa userDetail interface
                                                                        // aqui estamos mapeando os detalhes do usuário para a classe usuário. Dando autoridade de SimpleGranted Authority para a role chamada de USER
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String role) {
        return singletonList(new SimpleGrantedAuthority(role));
    }

}
