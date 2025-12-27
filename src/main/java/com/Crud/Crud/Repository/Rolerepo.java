package com.Crud.Crud.Repository;

import com.Crud.Crud.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface Rolerepo extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(String name);
}
