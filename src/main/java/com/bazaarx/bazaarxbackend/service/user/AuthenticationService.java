package com.bazaarx.bazaarxbackend.service.user;

import com.bazaarx.bazaarxbackend.dto.AuthResponseDTO;
import com.bazaarx.bazaarxbackend.dto.UserResponseDTO;
import com.bazaarx.bazaarxbackend.exceptions.EmailAlreadyExistsException;
import com.bazaarx.bazaarxbackend.entity.user.ApplicationUser;
import com.bazaarx.bazaarxbackend.entity.user.Role;
import com.bazaarx.bazaarxbackend.repo.UserRepository;
import com.bazaarx.bazaarxbackend.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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

    @Autowired
    private JWTService jwtService;

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
                    .orElseThrow(() -> new BadCredentialsException("User not found after successful authentication. This should not happen."));

            Set<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());


            String token = jwtService.generateToken(user.getEmail(), roles);


            return new AuthResponseDTO(
                    user.getId(),
                    user.getFullName(),
                    user.getEmail(),
                    roles,
                    token
            );

        } catch (BadCredentialsException e) {

            throw new BadCredentialsException("Invalid email or password provided.");
        } catch (RuntimeException e) {

            System.err.println("AuthenticationService login error - RuntimeException: " + e.getMessage());
            throw new RuntimeException("An unexpected error occurred during login. Please try again later.");
        } catch (Exception e) {

            System.err.println("AuthenticationService login error - Generic Exception: " + e.getMessage());
            throw new RuntimeException("An internal server error occurred during login.");
        }
    }
}
