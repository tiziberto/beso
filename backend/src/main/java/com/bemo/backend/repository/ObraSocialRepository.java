package com.bemo.backend.repository;

import com.bemo.backend.model.ObraSocial;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ObraSocialRepository extends JpaRepository<ObraSocial, Long> {
    List<ObraSocial> findByActivaTrue();
    boolean existsByNombre(String nombre);
}
