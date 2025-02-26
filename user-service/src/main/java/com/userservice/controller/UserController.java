package com.userservice.controller;

import com.userservice.dto.TokenDTO;
import com.userservice.models.Error;
import com.userservice.models.Token;
import com.userservice.models.User;
import com.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/api/login")
    public ResponseEntity<TokenDTO> userLogin(@RequestBody User user){
      String token = userService.login(user);
      return ResponseEntity.ok(TokenDTO.builder().token(token).build());
    }

    @PostMapping("/api/signUp")
    public ResponseEntity<Optional<Error>> userSignUp(@RequestBody User user){
        try {
            userService.signUp(user);
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.badRequest().body(
                    Optional.of(Error.builder().message("Unable to sign-up user").errorCode("user-service.100").build()));
        }
        return ResponseEntity.ok(Optional.empty());
    }

}

