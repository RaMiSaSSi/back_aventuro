package com.example.demo.Repository;


import com.example.demo.Model.Role;
import com.example.demo.Model.UtilisateurInscrit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UtilisateurInscritRepository extends JpaRepository<UtilisateurInscrit, Long> {
    Optional<UtilisateurInscrit> findByEmail(String email);
    boolean existsByEmail(String email);
    Page<UtilisateurInscrit> findAllByRole(Role role, Pageable pageable);
    Optional<UtilisateurInscrit> findByIdAndRole(Long id, Role role);
    boolean existsByEmailAndIdNot(String email, Long id);
}
