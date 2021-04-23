package com.github.fellipems.redditclone.repository;

import com.github.fellipems.redditclone.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {
}
