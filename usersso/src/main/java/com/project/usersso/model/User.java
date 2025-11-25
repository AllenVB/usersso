package com.project.usersso.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", 
       uniqueConstraints = { 
           @UniqueConstraint(columnNames = "username"),
           @UniqueConstraint(columnNames = "email") 
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String username;

    @Column(nullable = false, length = 50)
    private String email;

    @Column(nullable = false, length = 120)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(  name = "user_roles", 
                 joinColumns = @JoinColumn(name = "user_id"), 
                 inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    // Kayıt işlemi sırasında kolaylık olması için bu constructor'ı ekleyelim
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}