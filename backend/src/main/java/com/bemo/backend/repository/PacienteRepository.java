package com.bemo.backend.repository;

import com.bemo.backend.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    Optional<Paciente> findByDni(String dni);
    boolean existsByDni(String dni);

    @Query("SELECT p FROM Paciente p WHERE p.activo = true AND (" +
           "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(p.apellido) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "p.dni LIKE CONCAT('%', :q, '%') OR " +
           "LOWER(p.email) LIKE LOWER(CONCAT('%', :q, '%')))")
    List<Paciente> search(@Param("q") String query);

    List<Paciente> findByActivoTrue();
}
