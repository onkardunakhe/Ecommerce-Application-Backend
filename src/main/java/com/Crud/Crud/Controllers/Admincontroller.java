package com.Crud.Crud.Controllers;

import com.Crud.Crud.Service.Adminservice;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class Admincontroller {
    private final Adminservice adminservice;

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/makeadmin/{userId}")
    public ResponseEntity<String> makeAdmin(@PathVariable UUID userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Principal: " + auth.getPrincipal());
        System.out.println("Authorities: " + auth.getAuthorities());

        adminservice.makeUserAdmin(userId);

        return ResponseEntity.ok("User promoted to ADMIN successfully");
    }
}
