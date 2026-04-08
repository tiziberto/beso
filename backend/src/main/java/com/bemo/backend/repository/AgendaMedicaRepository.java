package com.bemo.backend.repository;

import com.bemo.backend.model.AgendaMedica;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AgendaMedicaRepository extends JpaRepository<AgendaMedica, Long> {
    List<AgendaMedica> findByProfesionalIdAndActivaTrue(Long profesionalId);
    List<AgendaMedica> findByProfesionalIdAndSucursalIdAndActivaTrue(Long profesionalId, Long sucursalId);
    List<AgendaMedica> findBySucursalIdAndActivaTrue(Long sucursalId);
    List<AgendaMedica> findByActivaTrue();
}
