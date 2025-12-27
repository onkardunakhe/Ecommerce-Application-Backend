package com.Crud.Crud.Repository;

import com.Crud.Crud.Entity.Refreshtoken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshtokenRepo extends JpaRepository<Refreshtoken, UUID> {
    Optional<Refreshtoken> findByJti(String jti);
}
