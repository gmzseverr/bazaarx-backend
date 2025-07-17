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
            // 1. Kimlik Doğrulama: Kullanıcı adı ve şifreyi doğrula
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            // 2. Güvenlik Bağlamını Güncelle: Kimliği doğrulanmış kullanıcıyı Spring Security bağlamına ata
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 3. Kullanıcıyı Veritabanından Çek: JWT için gerekli tüm bilgileri almak üzere ApplicationUser objesini al
            ApplicationUser user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new BadCredentialsException("User not found after successful authentication. This should not happen."));

            // 4. Roller: Kullanıcının yetkilerini (rollerini) bir Set<String> olarak hazırla
            Set<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());

            // 5. JWT Oluştur: Kullanıcının e-postası ve rolleri ile bir JWT oluştur
            // Not: Eğer JWT içine user.getId() de eklemek istiyorsanız, JWTService'i bu bilgiyi alacak şekilde güncelleyin.
            // Örneğin: String token = jwtService.generateToken(user.getId(), user.getEmail(), roles);
            String token = jwtService.generateToken(user.getEmail(), roles);

            // 6. Yanıt DTO'sunu Oluştur ve Döndür: Kullanıcı ID'si dahil tüm gerekli bilgileri içeren DTO
            return new AuthResponseDTO(
                    user.getId(),         // ✨ Kullanıcının gerçek MongoDB ID'si
                    user.getFullName(),
                    user.getEmail(),
                    roles,
                    token
            );

        } catch (BadCredentialsException e) {
            // Hatalı kimlik bilgileri (yanlış e-posta veya şifre) için özel hata
            // Bu hata direkt frontend'e 401 Unauthorized olarak gidebilir.
            throw new BadCredentialsException("Invalid email or password provided.");
        } catch (RuntimeException e) {
            // Kullanıcının bulunamaması gibi beklenmedik durumlar (normalde BadCredentialsException fırlatılmalıydı)
            // Detayları logla ve daha genel bir RuntimeException fırlat
            System.err.println("AuthenticationService login error - RuntimeException: " + e.getMessage());
            throw new RuntimeException("An unexpected error occurred during login. Please try again later.");
        } catch (Exception e) {
            // Diğer tüm beklenmedik hatalar
            System.err.println("AuthenticationService login error - Generic Exception: " + e.getMessage());
            throw new RuntimeException("An internal server error occurred during login.");
        }
    }
}
