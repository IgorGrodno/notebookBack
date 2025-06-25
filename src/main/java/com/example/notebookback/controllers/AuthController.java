package com.example.notebookback.controllers;

import com.example.notebookback.models.ntities.ERole;
import com.example.notebookback.models.ntities.Role;
import com.example.notebookback.models.ntities.User;
import com.example.notebookback.repositories.RoleRepository;
import com.example.notebookback.repositories.UserRepository;
import com.example.notebookback.security.JWT.JwtUtils;
import com.example.notebookback.security.request.LoginRequest;
import com.example.notebookback.security.request.SignupRequest;
import com.example.notebookback.security.respose.MessageResponse;
import com.example.notebookback.security.respose.UserInfoResponse;
import com.example.notebookback.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 600, allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository,
                          RoleRepository roleRepository, PasswordEncoder encoder, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

            logger.info("Generated JWT for user '{}'", userDetails.getUsername());
            logger.info("Set-Cookie header: {}", jwtCookie.toString());

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .body(new UserInfoResponse(
                            userDetails.getId(),
                            userDetails.getUsername(),
                            userDetails.getEmail(),
                            roles
                    ));
        } catch (BadCredentialsException ex) {
            logger.warn("Authentication failed for user '{}'", loginRequest.getUsername());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Ошибка: неверное имя пользователя или пароль"));
        } catch (AuthenticationException ex) {
            logger.error("Authentication error: {}", ex.getMessage(), ex);
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Ошибка аутентификации: " + ex.getMessage()));
        } catch (Exception ex) {
            logger.error("Internal error during authentication", ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Внутренняя ошибка сервера"));
        }
    }


    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        logger.info("Clearing JWT cookie");
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("You've been signed out!"));
    }

    @PostMapping("/newuserregister")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            logger.info("Username already exists");
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "errorCode", "USERNAME_TAKEN",
                            "message", "Имя пользователя уже занято."
                    )
            );
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            logger.info("Email already exists");
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "errorCode", "EMAIL_IN_USE",
                            "message", "Этот email уже используется."
                    )
            );
        }

        User user = new User(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword())
        );

        Set<Role> roles = new HashSet<>();

        roles.add(roleRepository.findByName(ERole.ROLE_USER).get());
        user.setRoles(roles);
        userRepository.save(user);

        logger.info("User created successfully: " + user.getUsername());
        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Пользователь зарегистрирован успешно.",
                        "username", user.getUsername()
                )
        );
    }


}
