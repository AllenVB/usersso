package com.project.usersso.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {

	// 1. HERKES (Public) Erişim
	@GetMapping("/all")
	public String allAccess() {
		return "Bu içerik HERKESE açık. (Public Content)";
	}

	// 2. KULLANICI (User) Erişimi
	// Kullanıcı, Moderatör veya Admin rolü olan herkes girebilir
	@GetMapping("/user")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public String userAccess() {
		return "Bu içerik SADECE ÜYE olanlara açık. (User Content)";
	}

	// 3. MODERATÖR (Mod) Erişimi
	@GetMapping("/mod")
	@PreAuthorize("hasRole('MODERATOR')")
	public String moderatorAccess() {
		return "Bu içerik SADECE MODERATÖRLERE açık. (Moderator Board)";
	}

	// 4. ADMİN (Admin) Erişimi
	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public String adminAccess() {
		return "Bu içerik SADECE PATRONLARA (Admin) açık. (Admin Board)";
	}
}