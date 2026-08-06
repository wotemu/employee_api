package com.example.employee_api.controller;


import com.example.employee_api.dto.AuthenticationResponse;
import com.example.employee_api.dto.LoginRequest;
import com.example.employee_api.dto.RegisterRequest;
import com.example.employee_api.model.Role;
import com.example.employee_api.model.User;
import com.example.employee_api.repository.UserRepository;
import com.example.employee_api.security.CustomUserDetailsService;
import com.example.employee_api.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class AuthenticationController {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;


    public AuthenticationController(
            UserRepository repository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            CustomUserDetailsService customUserDetailsService) {

        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @PostMapping("/auth/register")
    public String register(@RequestBody RegisterRequest request) {

        System.out.println(">>> REGISTER CALLED <<<");

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

        repository.save(user);

        System.out.println(">>> USER SAVED <<<");

        return "User registered.";
    }

    @PostMapping("/auth/login")
    public AuthenticationResponse login(
            @RequestBody LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));

        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername(
                        request.getUsername());

        String token = jwtService.generateToken(userDetails);

        return new AuthenticationResponse(token);
    }
}
