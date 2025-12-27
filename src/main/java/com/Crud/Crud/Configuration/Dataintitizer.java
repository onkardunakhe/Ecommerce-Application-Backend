package com.Crud.Crud.Configuration;

import com.Crud.Crud.Entity.Role;
import com.Crud.Crud.Repository.Rolerepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class Dataintitizer {
    private final Rolerepo rolerepo;

    @Bean
    CommandLineRunner initRoles() {
        return args -> {

            if (rolerepo.findByName("ROLE_USER").isEmpty()) {
                rolerepo.save(
                        Role.builder()
                                .name("ROLE_USER")
                                .build()
                );
            }

            if (rolerepo.findByName("ROLE_ADMIN").isEmpty()) {
                rolerepo.save(
                        Role.builder()
                                .name("ROLE_ADMIN")
                                .build()
                );
            }
        };
    }


}