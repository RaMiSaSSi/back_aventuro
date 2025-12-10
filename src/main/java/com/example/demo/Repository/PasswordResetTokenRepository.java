// java
package com.example.demo.Repository;

import com.example.demo.Model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByEmailAndToken(String email, String token);
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByEmail(String email);
}