package com.aquariux.CryptoTradingApplication.controllers;

import com.aquariux.CryptoTradingApplication.exceptions.UsernameNotUniqueException;
import com.aquariux.CryptoTradingApplication.models.UserModel;
import com.aquariux.CryptoTradingApplication.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping(value = "/register")
    public ResponseEntity<UserModel> register(@RequestBody UserModel userModel ) {
        try {
            log.info("Registering user with username: {}", userModel.getUsername());
            UserModel saved = service.registerUser(userModel);
            return ResponseEntity.ok(saved);
        } catch (UsernameNotUniqueException e) {
            log.error("Username: {} is not unique", userModel.getUsername());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error occurred whilst creating user");
            return ResponseEntity.internalServerError().build();
        }
    }

}
