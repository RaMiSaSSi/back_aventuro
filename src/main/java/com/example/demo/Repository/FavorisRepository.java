
package com.example.demo.Repository;

import com.example.demo.Model.Favoris;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavorisRepository extends JpaRepository<Favoris, Long> {
    Optional<Favoris> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}