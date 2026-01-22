package com.Crud.Crud.Service;

import com.Crud.Crud.Entity.MagicLoginToken;
import com.Crud.Crud.Entity.User;
import com.Crud.Crud.Repository.MagicLoginTokenRepository;
import com.Crud.Crud.Repository.Userrepo;
import com.Crud.Crud.Security.Jwtservice;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MagicLoginService {
    private final MagicLoginTokenRepository magicLoginTokenRepository;
    private final Userrepo userrepo;
    private final EmailService emailService;
    private final Jwtservice jwtservice;


    public void sendMagicToken(String email) {
        User user = userrepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User Not Found"));

        String token = UUID.randomUUID().toString();

        MagicLoginToken magicLoginToken = new MagicLoginToken();
        magicLoginToken.setEmail(email);
        magicLoginToken.setToken(token);
        magicLoginToken.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        magicLoginTokenRepository.save(magicLoginToken);
        String loginLink =
                "http://localhost:8080/api/auth/magic-login?token=" + token;
        emailService.sendEmail(
                email,
                "Your Login Link From Ecommerce App",
                "Click to login (valid for 10 minutes):\n" + loginLink);
    }

    @Transactional
    public String LoginWithMagic(String magictoken) {
        MagicLoginToken magicLoginToken = magicLoginTokenRepository.findByTokenAndUsedFalse(magictoken).orElseThrow(() -> new RuntimeException("Token Is Used or Invalid"));

        User user = userrepo.findByEmail(magicLoginToken.getEmail()).orElseThrow(() -> new RuntimeException("User Not Found"));
        if (magicLoginToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Link expired");
        }
        magicLoginToken.setUsed(true);
        magicLoginTokenRepository.save(magicLoginToken);

        return jwtservice.generateAccessToken(user);

    }
}
