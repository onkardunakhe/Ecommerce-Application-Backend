package com.Crud.Crud.Repository;

import com.Crud.Crud.Entity.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {

    Optional<OtpToken> findByEmailAndOtpAndVerifiedFalse(String email, String otp);

    Optional<OtpToken> findTopByEmailOrderByExpiryTimeDesc(String email);
}
