package com.bazaarx.bazaarxbackend.config;

import com.bazaarx.bazaarxbackend.service.user.CustomUserDetailsService;
import com.bazaarx.bazaarxbackend.service.user.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        String token = null;
        String userEmail = null;

        // 1. Authorization başlığını kontrol et
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // "Bearer " kısmını atla

            // ✨ ÖNEMLİ DÜZELTME: JWT ayrıştırma işlemini try-catch bloğuna al
            try {
                // Token'ın geçerli bir string olduğundan emin ol
                if (token != null && !token.trim().isEmpty()) {
                    userEmail = jwtService.extractUsername(token);
                }
            } catch (io.jsonwebtoken.MalformedJwtException e) {
                // Token formatı geçersiz (örn. nokta yok, boş string)
                // Bu durum, permitAll() endpoint'lerine token olmadan gelen isteklerde normaldir.
                // Loglayıp filtrenin normal şekilde devam etmesine izin veriyoruz.
                System.err.println("Invalid JWT format encountered (likely no token for permitAll endpoint): " + e.getMessage());
                // Buraya 401 Unauthorized yanıtı döndürebilirsiniz
                // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                // return; // Eğer hatada isteği durdurmak isterseniz
            } catch (io.jsonwebtoken.security.SignatureException e) {
                // İmza geçersiz (secret key yanlış veya token manipüle edilmiş)
                System.err.println("Invalid JWT signature: " + e.getMessage());
                // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                // return;
            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                // Token süresi dolmuş
                System.err.println("Expired JWT: " + e.getMessage());
                // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                // return;
            } catch (Exception e) {
                // Diğer genel JWT işleme hataları
                System.err.println("General error processing JWT: " + e.getMessage());
                // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                // return;
            }
        }

        // 2. E-posta null değilse ve SecurityContext'te zaten bir kimlik doğrulama yoksa devam et
        // userEmail null ise (yani Authorization başlığı yoktu veya JWT hatası oldu),
        // bu blok atlanır ve filtre zinciri devam eder.
        // Bu sayede permitAll() endpoint'leri işlenir.
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            if (jwtService.isTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}