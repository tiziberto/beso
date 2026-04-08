package com.bemo.backend.repository;

import com.bemo.backend.model.Profesional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ProfesionalRepository extends JpaRepository<Profesional, Long> {
    List<Profesional> findByActivoTrue();

    @Query("SELECT p FROM Profesional p WHERE p.activo = true AND (" +
           "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(p.apellido) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "p.matricula LIKE CONCAT('%', :q, '%'))")
    List<Profesional> search(@Param("q") String query);

    @Query("SELECT p FROM Profesional p JOIN p.sucursales s WHERE s.id = :sucursalId AND p.activo = true")
    List<Profesional> findBySucursalId(@Param("sucursalId") Long sucursalId);

    @Query("SELECT p FROM Profesional p WHERE p.user.id = :userId AND p.activo = true")
    java.util.Optional<Profesional> findByUserId(@Param("userId") Long userId);
}
