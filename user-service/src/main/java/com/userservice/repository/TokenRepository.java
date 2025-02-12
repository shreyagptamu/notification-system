package com.userservice.repository;

import com.userservice.models.Token;
import com.userservice.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends CrudRepository<Token,String> {
    public Optional<Token> findByToken(String token);
}
