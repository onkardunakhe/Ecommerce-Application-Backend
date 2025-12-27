package com.Crud.Crud.Security;

import com.Crud.Crud.Entity.Provider;
import com.Crud.Crud.Entity.Refreshtoken;
import com.Crud.Crud.Entity.Role;
import com.Crud.Crud.Entity.User;
import com.Crud.Crud.Repository.RefreshtokenRepo;
import com.Crud.Crud.Repository.Rolerepo;
import com.Crud.Crud.Repository.Userrepo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;


@Component
@RequiredArgsConstructor
public class Oauth2successhandler implements AuthenticationSuccessHandler {
    private final Jwtservice jwtservice;
    private final Userrepo userrepo;
    private final Cookieservice cookieservice;
    private final RefreshtokenRepo refreshtokenRepo;
    private final Rolerepo rolerepo;
    Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        logger.info("Successful OAuth2 authentication for user: " + authentication.getName());
        logger.info(authentication.toString());

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        //identify user:

        String registrationId = "unknown";
        if (authentication instanceof OAuth2AuthenticationToken token) {
            registrationId = token.getAuthorizedClientRegistrationId();
        }
        Role userRole = rolerepo.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));


        User user;
        switch (registrationId) {
            case "google" -> {
                String googleId = oAuth2User.getAttributes().getOrDefault("sub", "").toString();

                String email = oAuth2User.getAttributes().getOrDefault("email", "").toString();
                String name = oAuth2User.getAttributes().getOrDefault("name", "").toString();
                String picture = oAuth2User.getAttributes().getOrDefault("picture", "").toString();
                User newUser = User.builder()
                        .email(email)
                        .name(name)
                        .image(picture)
                        .enable(true)
                        .roles(Set.of(userRole))
                        .provider(Provider.GOOGLE)
                        .build();


                user = userrepo.findByEmail(email).orElseGet(() -> userrepo.save(newUser));
                logger.info(user.getAuthorities().toString());

            }
            default -> {
                throw new RuntimeException("Invalid registration id");
            }
        }
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(Set.of(userRole));
            userrepo.save(user);
        }

        String jti = UUID.randomUUID().toString();
        Refreshtoken refreshTokenOb = Refreshtoken.builder()
                .jti(jti)
                .user(user)
                .revoked(false)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(jwtservice.getRefreshTtlSeconds()))
                .build();

        refreshtokenRepo.save(refreshTokenOb);

        String accessToken = jwtservice.generateAccessToken(user);
        String refreshToken = jwtservice.generateRefreshToken(user, refreshTokenOb.getJti());
        cookieservice.attachRefreshCookie(response, refreshToken, (int) jwtservice.getRefreshTtlSeconds());
        response.getWriter().write("Login Successful via OAuth2 \n");
        response.getWriter().write("Acess token:  " + accessToken + "\n");
        response.getWriter().write("Refresh Token:  " + refreshToken);
    }
}