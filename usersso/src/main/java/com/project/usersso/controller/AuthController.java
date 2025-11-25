package com.project.usersso.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.usersso.model.ERole;
import com.project.usersso.model.Role;
import com.project.usersso.model.User;
import com.project.usersso.dto.request.LoginRequest;
import com.project.usersso.dto.request.SignupRequest;
import com.project.usersso.dto.response.JwtResponse;
import com.project.usersso.dto.response.MessageResponse;
import com.project.usersso.repository.RoleRepository;
import com.project.usersso.repository.UserRepository;
import com.project.usersso.security.jwt.JwtUtils;
import com.project.usersso.security.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600) // Tarayıcı kısıtlamalarını kaldırır (CORS)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    // --- 1. GİRİŞ YAPMA (LOGIN) ---
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        // Kullanıcı adı ve şifre kontrolü yapılır
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        // Güvenlik bağlamına (context) kullanıcı işlenir
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // JWT Token üretilir
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        // Kullanıcı detayları çekilir
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();        
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        // Cevap olarak Token ve kullanıcı bilgileri dönülür
        return ResponseEntity.ok(new JwtResponse(jwt, 
                                                 userDetails.getId(), 
                                                 userDetails.getUsername(), 
                                                 userDetails.getEmail(), 
                                                 roles));
    }

    // --- 2. KAYIT OLMA (REGISTER) ---
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        // Kullanıcı adı dolu mu?
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Hata: Bu kullanıcı adı zaten alınmış!"));
        }

        // Email dolu mu?
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Hata: Bu email zaten kullanılıyor!"));
        }

        // Yeni kullanıcı oluştur (Şifreyi şifreleyerek kaydet)
        User user = new User(signUpRequest.getUsername(), 
                             signUpRequest.getEmail(),
                             encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        // Rol ataması yap
        if (strRoles == null) {
            // Eğer rol belirtilmemişse varsayılan olarak USER yap
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Hata: Rol veritabanında bulunamadı."));
            roles.add(userRole);
        } else {
            // Gelen isteğe göre (admin, mod) rol ata
            strRoles.forEach(role -> {
                switch (role) {
                case "admin":
                    Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Hata: Rol veritabanında bulunamadı."));
                    roles.add(adminRole);
                    break;
                case "mod":
                    Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                            .orElseThrow(() -> new RuntimeException("Hata: Rol veritabanında bulunamadı."));
                    roles.add(modRole);
                    break;
                default:
                    Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Hata: Rol veritabanında bulunamadı."));
                    roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Kullanıcı başarıyla kaydedildi!"));
    }
}