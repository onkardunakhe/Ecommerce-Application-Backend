package com.Crud.Crud.Controllers;

import com.Crud.Crud.Dtos.LoginRequest;
import com.Crud.Crud.Dtos.RefreshTokenRequest;
import com.Crud.Crud.Dtos.TokenResponse;
import com.Crud.Crud.Dtos.UserDto;
import com.Crud.Crud.Entity.Refreshtoken;
import com.Crud.Crud.Entity.User;
import com.Crud.Crud.Repository.RefreshtokenRepo;
import com.Crud.Crud.Repository.Userrepo;
import com.Crud.Crud.Security.Cookieservice;
import com.Crud.Crud.Security.Jwtservice;
import com.Crud.Crud.Service.Adminservice;
import com.Crud.Crud.Service.Authservice;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class Authcontrollers {
    private final Authservice authservice;
    private final AuthenticationManager authenticationManager;
    private final Userrepo userrepo;
    private final Jwtservice jwtservice;
    private final ModelMapper modelMapper;
    private final RefreshtokenRepo refreshtokenRepo;
    private final Cookieservice cookieservice;
    private final Adminservice adminservice;


    @PostMapping("/register")
    public ResponseEntity<UserDto> registeruser(@RequestBody UserDto userDto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(authservice.registerUser(userDto));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        Authentication authenticate = authenticate(loginRequest);
        User user = userrepo.findByEmail(loginRequest.email()).orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!user.isEnable()) {
            throw new DisabledException("User is disabled");
        }
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        System.out.println(authorities);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(auth.getAuthorities());

        //user is authenticated now generate tokens
        //for genration of refresh token jti is needed which is to be stored in database
        String jti = UUID.randomUUID().toString();
        var refreshtokenob = Refreshtoken.builder()
                .jti(jti)
                .user(user)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(jwtservice.getRefreshTtlSeconds()))
                .revoked(false)
                .build();

//        Saving information of refresh token in database
        Refreshtoken save = refreshtokenRepo.save(refreshtokenob);


        String accesstoken = jwtservice.generateAccessToken(user);
        String refreshtoken = jwtservice.generateRefreshToken(user, refreshtokenob.getJti());

        cookieservice.attachRefreshCookie(response, refreshtoken, (int) jwtservice.getRefreshTtlSeconds());
        cookieservice.addNoStoreHeaders(response);

        TokenResponse tokenResponse = TokenResponse.of(accesstoken, refreshtoken, jwtservice.getAccessTtlSeconds(), modelMapper.map(user, UserDto.class));
        return ResponseEntity.ok(tokenResponse);

    }

    private Authentication authenticate(LoginRequest loginRequest) {
        try {
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid Username or Password");
        }
    }


    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(
            @RequestBody(required = false) RefreshTokenRequest body,
            HttpServletResponse response,
            HttpServletRequest request
    ) throws InterruptedException {


        //Thread.sleep(5000);

        String refreshToken = readRefreshTokenFromRequest(body, request).orElseThrow(() -> new BadCredentialsException("Refresh token is missing"));


        if (!jwtservice.isRefreshToken(refreshToken)) {
            throw new BadCredentialsException("Invalid Refresh Token Type");
        }

        String jti = jwtservice.getJti(refreshToken);
        UUID userId = jwtservice.getUserId(refreshToken);
        Refreshtoken storedRefreshToken = refreshtokenRepo.findByJti(jti).orElseThrow(() -> new BadCredentialsException("Refresh token not recognized"));

        if (storedRefreshToken.isRevoked()) {
            throw new BadCredentialsException("Refresh token expired or revoked");
        }

        if (storedRefreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new BadCredentialsException("Refresh token expired");
        }

        if (!storedRefreshToken.getUser().getId().equals(userId)) {
            throw new BadCredentialsException("Refresh token does not belong to this user");
        }

        //refresh token ko rotate:
        storedRefreshToken.setRevoked(true);
        String newJti = UUID.randomUUID().toString();
        storedRefreshToken.setReplacedByToken(newJti);
        refreshtokenRepo.save(storedRefreshToken);

        User user = storedRefreshToken.getUser();

        var newRefreshTokenOb = Refreshtoken.builder()
                .jti(newJti)
                .user(user)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(jwtservice.getRefreshTtlSeconds()))
                .revoked(false)
                .build();

        refreshtokenRepo.save(newRefreshTokenOb);
        String newAccessToken = jwtservice.generateAccessToken(user);
        String newRefreshToken = jwtservice.generateRefreshToken(user, newRefreshTokenOb.getJti());


        cookieservice.attachRefreshCookie(response, newRefreshToken, (int) jwtservice.getRefreshTtlSeconds());
        cookieservice.addNoStoreHeaders(response);
        return ResponseEntity.ok(TokenResponse.of(newAccessToken, newRefreshToken, jwtservice.getAccessTtlSeconds(), modelMapper.map(user, UserDto.class)));


    }

   
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        readRefreshTokenFromRequest(null, request).ifPresent(token -> {
            try {
                if (jwtservice.isRefreshToken(token)) {
                    String jti = jwtservice.getJti(token);
                    refreshtokenRepo.findByJti(jti).ifPresent(rt -> {
                        rt.setRevoked(true);
                        refreshtokenRepo.save(rt);
                    });
                }
            } catch (JwtException ignored) {
            }
        });

        // Use CookieUtil (same behavior)
        cookieservice.clearRefreshCookie(response);
        cookieservice.addNoStoreHeaders(response);
        SecurityContextHolder.clearContext();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    //this method will read refresh token from request header or body.
    private Optional<String> readRefreshTokenFromRequest(RefreshTokenRequest body, HttpServletRequest request) {
//            1. prefer reading refresh token from cookie
        if (request.getCookies() != null) {

            Optional<String> fromCookie = Arrays.stream(request.getCookies())
                    .filter(c -> cookieservice.getRefreshTokenCookieName().equals(c.getName()))
                    .map(Cookie::getValue)
                    .filter(v -> !v.isBlank())
                    .findFirst();

            if (fromCookie.isPresent()) {
                return fromCookie;
            }


        }

        // 2 body:
        if (body != null && body.refreshToken() != null && !body.refreshToken().isBlank()) {
            return Optional.of(body.refreshToken());
        }

        //3. custom header
        String refreshHeader = request.getHeader("X-Refresh-Token");
        if (refreshHeader != null && !refreshHeader.isBlank()) {
            return Optional.of(refreshHeader.trim());
        }

        //Authorization = Bearer <token>
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.regionMatches(true, 0, "Bearer ", 0, 7)) {
            String candidate = authHeader.substring(7).trim();
            if (!candidate.isEmpty()) {
                try {
                    if (jwtservice.isRefreshToken(candidate)) {
                        return Optional.of(candidate);
                    }
                } catch (Exception ignored) {
                }
            }
        }

        return Optional.empty();


    }

}
