package com.project.usersso.security.jwt;

import com.project.usersso.security.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 1. İstekten JWT'yi ayıkla
            String jwt = parseJwt(request);

            // 2. Token var mı ve geçerli mi kontrol et
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                
                // 3. Token'dan kullanıcı adını al
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                // 4. Kullanıcı detaylarını veritabanından getir
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 5. Güvenlik Context'ine (Bağlamına) kullanıcıyı işle
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());
                
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Artık Spring Security bu kullanıcının kim olduğunu biliyor!
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Kullanıcı kimlik doğrulaması yapılamadı: {}", e.getMessage());
        }

        // Zincirdeki diğer filtrelere devam et
        filterChain.doFilter(request, response);
    }

    // Header'dan "Bearer " kısmını kesip sadece token'ı alan yardımcı metod
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}