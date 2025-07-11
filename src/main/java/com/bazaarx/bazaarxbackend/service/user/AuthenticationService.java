package com.bazaarx.bazaarxbackend.service.user;

import com.bazaarx.bazaarxbackend.dto.AuthResponseDTO;
import com.bazaarx.bazaarxbackend.dto.UserResponseDTO;
import com.bazaarx.bazaarxbackend.exceptions.EmailAlreadyExistsException;
import com.bazaarx.bazaarxbackend.entity.user.ApplicationUser;
import com.bazaarx.bazaarxbackend.entity.user.Role;
import com.bazaarx.bazaarxbackend.repo.UserRepository;
import com.bazaarx.bazaarxbackend.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public UserResponseDTO register(String fullName, String email, String password) {
        userRepository.findByEmail(email).ifPresent(user -> {
            throw new EmailAlreadyExistsException(email);
        });


        Role userRole = Role.ROLE_USER;

        ApplicationUser user = new ApplicationUser();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setAuthorities(Set.of(userRole));
        user.setEnabled(true);

        ApplicationUser savedUser = userRepository.save(user);
        return userMapper.UserToUserResponseDTO(savedUser);
    }

    @Transactional
    public AuthResponseDTO login(String email, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            ApplicationUser user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Invalid email or password. User not found after authentication."));

            System.out.println("DEBUG (AuthenticationService): Fetched User FullName: " + user.getFullName() +
                    ", Email: " + user.getEmail() +
                    ", ID: " + user.getId());

            // 🔥 Rolleri doğru şekilde Set<String> olarak topladığımızdan emin olalım
            Set<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority) // GrantedAuthority'den String rol adını al
                    .collect(Collectors.toSet()); // Bir Set'e topla

            System.out.println("DEBUG (AuthenticationService): User Roles: " + roles); // Rolleri konsola yazdır

            return new AuthResponseDTO(
                    user.getId(),
                    user.getFullName(),
                    user.getEmail(),
                    roles
            );

        } catch (Exception e) {
            System.err.println("AuthenticationService Login Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Invalid email or password.");
        }
    }
}