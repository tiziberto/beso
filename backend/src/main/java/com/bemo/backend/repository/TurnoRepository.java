package com.bemo.backend.repository;

import com.bemo.backend.model.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface TurnoRepository extends JpaRepository<Turno, Long> {

    @Query("SELECT t FROM Turno t WHERE t.fechaHora >= :desde AND t.fechaHora < :hasta ORDER BY t.fechaHora ASC")
    List<Turno> findByFechaBetween(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    @Query("SELECT t FROM Turno t WHERE t.profesional.id = :profId AND t.fechaHora >= :desde AND t.fechaHora < :hasta ORDER BY t.fechaHora ASC")
    List<Turno> findByProfesionalAndFecha(@Param("profId") Long profesionalId, @Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    @Query("SELECT t FROM Turno t WHERE t.sucursal.id = :sucId AND t.fechaHora >= :desde AND t.fechaHora < :hasta ORDER BY t.fechaHora ASC")
    List<Turno> findBySucursalAndFecha(@Param("sucId") Long sucursalId, @Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    @Query("SELECT t FROM Turno t WHERE t.paciente.id = :pacId ORDER BY t.fechaHora DESC")
    List<Turno> findByPacienteId(@Param("pacId") Long pacienteId);

    @Query("SELECT t FROM Turno t WHERE t.profesional.id = :profId AND t.fechaHora = :fechaHora AND t.estado <> 'CANCELADO'")
    List<Turno> findConflictos(@Param("profId") Long profesionalId, @Param("fechaHora") LocalDateTime fechaHora);

    @Query("SELECT t FROM Turno t WHERE t.fechaHora >= :desde AND t.fechaHora < :hasta ORDER BY t.fechaHora ASC")
    List<Turno> findByRango(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    @Query("SELECT t FROM Turno t WHERE t.profesional.id = :profId AND t.fechaHora >= :desde AND t.fechaHora < :hasta ORDER BY t.fechaHora ASC")
    List<Turno> findByProfesionalAndRango(@Param("profId") Long profesionalId, @Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);
}
