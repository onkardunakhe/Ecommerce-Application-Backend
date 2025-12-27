package com.Crud.Crud.Service;

import com.Crud.Crud.Entity.Role;
import com.Crud.Crud.Entity.User;
import com.Crud.Crud.Repository.Rolerepo;
import com.Crud.Crud.Repository.Userrepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class Adminservice {

    private final Userrepo userrepo;
    private final Rolerepo rolerepo;

    @Transactional
    public void makeUserAdmin(UUID userId) {
        try {

            User user = userrepo.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Role adminRole = rolerepo.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found"));

            // prevent duplicate role
            if (user.getRoles().contains(adminRole)) {
                return;
            }

            user.getRoles().add(adminRole);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
