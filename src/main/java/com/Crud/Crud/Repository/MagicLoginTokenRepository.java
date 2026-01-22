package com.Crud.Crud.Repository;

import com.Crud.Crud.Entity.MagicLoginToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MagicLoginTokenRepository
        extends JpaRepository<MagicLoginToken, Long> {

    Optional<MagicLoginToken> findByTokenAndUsedFalse(String token);
}
