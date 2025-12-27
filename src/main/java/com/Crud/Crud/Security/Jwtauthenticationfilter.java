package com.Crud.Crud.Security;

import com.Crud.Crud.Repository.Userrepo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class Jwtauthenticationfilter extends OncePerRequestFilter {

    private final Jwtservice jwtservice;
    private final Userrepo userrepo;
    private final Logger logger = LoggerFactory.getLogger(Jwtauthenticationfilter.class);

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api/auth/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        logger.info("Authorization header : {}", header);

        try {
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);

                if (!jwtservice.isAccessToken(token)) {
                    filterChain.doFilter(request, response);
                    return;
                }

                Jws<Claims> parse = jwtservice.parse(token);
                Claims payload = parse.getBody();
                UUID userId = UUID.fromString(payload.getSubject());

                userrepo.findById(userId).ifPresent(user -> {
                    if (user.isEnable()) {
                        List<SimpleGrantedAuthority> authorities = user.getRoles() == null ?
                                List.of() :
                                user.getRoles().stream()
                                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                                        .collect(Collectors.toList());

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(user, null, authorities);
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // Always set authentication
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        logger.info("Authentication set for user: {} with roles: {}", user.getEmail(), authorities);
                    }
                });
            }
        } catch (ExpiredJwtException e) {
            request.setAttribute("error", "Token Expired");
        } catch (Exception e) {
            request.setAttribute("error", "Invalid Token");
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }
}
