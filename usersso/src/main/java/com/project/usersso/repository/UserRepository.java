package com.project.usersso.repository;

import com.project.usersso.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Login işlemi için: Kullanıcı adından kullanıcıyı bul
    Optional<User> findByUsername(String username);

    // Kayıt işlemi için: Kullanıcı adı daha önce alınmış mı?
    Boolean existsByUsername(String username);

    // Kayıt işlemi için: Email daha önce kullanılmış mı?
    Boolean existsByEmail(String email);
}