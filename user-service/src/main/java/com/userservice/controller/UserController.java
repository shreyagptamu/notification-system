package com.userservice.controller;

import com.userservice.models.User;
import com.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/api/login")
    public ResponseEntity<String> userLogin(@RequestBody User user){
      String token = userService.login(user);

        return ResponseEntity.ok(token);
    }

    @PostMapping("/api/signUp")
    public ResponseEntity userSignUp(@RequestBody User user){
        try {
            userService.signUp(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }


}

