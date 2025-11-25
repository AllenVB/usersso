# 🛡️ UserSSO - Spring Boot JWT Authentication System

Bu proje, Spring Boot 3 ve Spring Security 6 kullanılarak geliştirilmiş, güvenli, ölçeklenebilir ve Rol Tabanlı (RBAC) bir Kimlik Yönetim Sistemidir.

## 🚀 Özellikler

- **Kayıt Ol & Giriş Yap:** Güvenli kullanıcı kaydı ve girişi.
- **JWT (JSON Web Token):** Stateless kimlik doğrulama.
- **Rol Tabanlı Yetkilendirme (RBAC):** Admin, Moderatör ve Kullanıcı rolleri.
- **Güvenlik:** BCrypt ile şifreleme, CORS ayarları ve Anti-Patterns koruması.
- **Veritabanı:** PostgreSQL entegrasyonu.

## 🛠️ Teknolojiler

- Java 17
- Spring Boot 3.x
- Spring Security 6
- Spring Data JPA
- PostgreSQL
- Lombok
- JWT (jjwt)

## ⚙️ Kurulum

1. `application.properties` dosyasındaki veritabanı ayarlarını kendi PostgreSQL ayarlarınıza göre güncelleyin.
2. PostgreSQL'de `usersso` adında bir veritabanı oluşturun.
3. Projeyi çalıştırın (Tablolar otomatik oluşacaktır).
4. Rolleri veritabanına ekleyin:
   ```sql
   INSERT INTO roles(name) VALUES('ROLE_USER');
   INSERT INTO roles(name) VALUES('ROLE_MODERATOR');
   INSERT INTO roles(name) VALUES('ROLE_ADMIN');
