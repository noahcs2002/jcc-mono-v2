package com.software.teamfive.jcc_product_inventory_management.controller;

import com.software.teamfive.jcc_product_inventory_management.model.biz.User;
import com.software.teamfive.jcc_product_inventory_management.model.dto.request.user.LoginRequest;
import com.software.teamfive.jcc_product_inventory_management.model.dto.request.user.RegistrationRequest;
import com.software.teamfive.jcc_product_inventory_management.model.dto.response.user.LoginResponse;
import com.software.teamfive.jcc_product_inventory_management.model.dto.response.user.RegistrationResponse;
import com.software.teamfive.jcc_product_inventory_management.repo.UserRepository;
import com.software.teamfive.jcc_product_inventory_management.service.JwtService;
import com.software.teamfive.jcc_product_inventory_management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/jcc/api/auth")
public class UserController {

    @Autowired
    private final JwtService jwtService;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final UserService userService;

    @Autowired
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserController(JwtService jwtService, UserRepository userRepository, UserService userService, AuthenticationManager authenticationManager) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("xyz")
    public ResponseEntity<?> xyz() {
        try {
            return ResponseEntity.ok("User Controller Online");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest request) {
        RegistrationResponse result = this.userService.register(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        LoginResponse response = this.userService.login(request);
        return ResponseEntity.ok(response);
    }
}
