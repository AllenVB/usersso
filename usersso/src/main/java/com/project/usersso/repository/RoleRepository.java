package com.project.usersso.repository;

import com.project.usersso.model.ERole;
import com.project.usersso.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    
    // Enum isminden (ROLE_USER vb.) rolü bul
    Optional<Role> findByName(ERole name);
}