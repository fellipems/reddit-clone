package com.github.fellipems.redditclone.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.Instant;

@Entity
@Data       // gera os getter e setter, to string, equals, etc, implicitos
@Builder    // método builder para nossa classe
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @NotBlank(message = "Nome do post não pode estar vazio!")
    private String postName;

    @Nullable
    private String url;

    @Nullable
    @Lob        // lob por causa dos textos extensos da descrição
    private String description;
    private Integer voteCount;      // contador de vote pelos post acompanhando de uma referÊncia para o usuário do post

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private User user;      // um post pode ter vários usuários
    private Instant createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", referencedColumnName = "id")
    private Subreddit subreddit;

}
