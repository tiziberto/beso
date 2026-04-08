package com.bemo.backend.service;

import com.bemo.backend.dto.TurnoDto;
import com.bemo.backend.model.*;
import com.bemo.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TurnoService {

    private final TurnoRepository repo;
    private final PacienteRepository pacienteRepo;
    private final ProfesionalRepository profRepo;
    private final SucursalRepository sucursalRepo;
    private final EstudioRepository estudioRepo;
    private final ObraSocialRepository osRepo;
    private final PlanRepository planRepo;

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public List<TurnoDto> getByFecha(String fecha) {
        LocalDate date = LocalDate.parse(fecha);
        return repo.findByFechaBetween(date.atStartOfDay(), date.plusDays(1).atStartOfDay())
            .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<TurnoDto> getByProfesionalAndFecha(Long profesionalId, String fecha) {
        LocalDate date = LocalDate.parse(fecha);
        return repo.findByProfesionalAndFecha(profesionalId, date.atStartOfDay(), date.plusDays(1).atStartOfDay())
            .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<TurnoDto> getBySucursalAndFecha(Long sucursalId, String fecha) {
        LocalDate date = LocalDate.parse(fecha);
        return repo.findBySucursalAndFecha(sucursalId, date.atStartOfDay(), date.plusDays(1).atStartOfDay())
            .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<TurnoDto> getByRango(String desde, String hasta, Long profesionalId) {
        LocalDateTime desdeDateTime = parseFlexible(desde);
        LocalDateTime hastaDateTime = parseFlexible(hasta);
        if (profesionalId != null) {
            return repo.findByProfesionalAndRango(profesionalId, desdeDateTime, hastaDateTime)
                .stream().map(this::toDto).collect(Collectors.toList());
        }
        return repo.findByRango(desdeDateTime, hastaDateTime)
            .stream().map(this::toDto).collect(Collectors.toList());
    }

    /** Acepta tanto "2026-04-06" como "2026-04-06T00:00:00" */
    private LocalDateTime parseFlexible(String s) {
        if (s != null && s.contains("T")) {
            return LocalDateTime.parse(s, DT_FMT);
        }
        return LocalDate.parse(s).atStartOfDay();
    }

    public List<TurnoDto> getByPaciente(Long pacienteId) {
        return repo.findByPacienteId(pacienteId).stream().map(this::toDto).collect(Collectors.toList());
    }

    public TurnoDto create(TurnoDto dto) {
        Paciente paciente = pacienteRepo.findById(dto.getPacienteId())
            .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        Profesional prof = profRepo.findById(dto.getProfesionalId())
            .orElseThrow(() -> new RuntimeException("Profesional no encontrado"));
        Sucursal suc = sucursalRepo.findById(dto.getSucursalId())
            .orElseThrow(() -> new RuntimeException("Sucursal no encontrada"));

        LocalDateTime fechaHora = LocalDateTime.parse(dto.getFechaHora(), DT_FMT);
        List<Turno> conflictos = repo.findConflictos(prof.getId(), fechaHora);
        if (!conflictos.isEmpty()) {
            throw new RuntimeException("El profesional ya tiene un turno en ese horario");
        }

        Turno t = new Turno();
        t.setPaciente(paciente);
        t.setProfesional(prof);
        t.setSucursal(suc);
        t.setFechaHora(fechaHora);
        t.setEstado("PENDIENTE");
        t.setObservaciones(dto.getObservaciones());

        if (dto.getEstudioId() != null) t.setEstudio(estudioRepo.findById(dto.getEstudioId()).orElse(null));
        if (dto.getObraSocialId() != null) t.setObraSocial(osRepo.findById(dto.getObraSocialId()).orElse(null));
        if (dto.getPlanId() != null) t.setPlan(planRepo.findById(dto.getPlanId()).orElse(null));

        return toDto(repo.save(t));
    }

    public TurnoDto updateEstado(Long id, String nuevoEstado) {
        Turno t = repo.findById(id).orElseThrow(() -> new RuntimeException("Turno no encontrado"));
        t.setEstado(nuevoEstado);
        t.setUpdatedAt(LocalDateTime.now());
        return toDto(repo.save(t));
    }

    public TurnoDto reprogramar(Long id, String nuevaFechaHora) {
        Turno t = repo.findById(id).orElseThrow(() -> new RuntimeException("Turno no encontrado"));
        LocalDateTime newDt = LocalDateTime.parse(nuevaFechaHora, DT_FMT);
        List<Turno> conflictos = repo.findConflictos(t.getProfesional().getId(), newDt);
        conflictos.removeIf(c -> c.getId().equals(id));
        if (!conflictos.isEmpty()) throw new RuntimeException("El profesional ya tiene un turno en ese horario");
        t.setFechaHora(newDt);
        t.setEstado("PENDIENTE");
        t.setUpdatedAt(LocalDateTime.now());
        return toDto(repo.save(t));
    }

    public TurnoDto cancelar(Long id) {
        return updateEstado(id, "CANCELADO");
    }

    public TurnoDto update(Long id, TurnoDto dto) {
        Turno t = repo.findById(id).orElseThrow(() -> new RuntimeException("Turno no encontrado"));
        if (dto.getPacienteId() != null) t.setPaciente(pacienteRepo.findById(dto.getPacienteId()).orElseThrow());
        if (dto.getProfesionalId() != null) t.setProfesional(profRepo.findById(dto.getProfesionalId()).orElseThrow());
        if (dto.getSucursalId() != null) t.setSucursal(sucursalRepo.findById(dto.getSucursalId()).orElseThrow());
        if (dto.getFechaHora() != null) t.setFechaHora(LocalDateTime.parse(dto.getFechaHora(), DT_FMT));
        if (dto.getEstudioId() != null) t.setEstudio(estudioRepo.findById(dto.getEstudioId()).orElse(null));
        if (dto.getObraSocialId() != null) t.setObraSocial(osRepo.findById(dto.getObraSocialId()).orElse(null));
        if (dto.getPlanId() != null) t.setPlan(planRepo.findById(dto.getPlanId()).orElse(null));
        if (dto.getObservaciones() != null) t.setObservaciones(dto.getObservaciones());
        if (dto.getEstado() != null) t.setEstado(dto.getEstado());
        t.setUpdatedAt(LocalDateTime.now());
        return toDto(repo.save(t));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public TurnoDto toDto(Turno t) {
        return new TurnoDto(
            t.getId(),
            t.getPaciente().getId(),
            t.getPaciente().getNombre(),
            t.getPaciente().getApellido(),
            t.getPaciente().getDni(),
            t.getPaciente().getEmail(),
            t.getPaciente().getTelefono(),
            t.getProfesional().getId(),
            t.getProfesional().getNombre(),
            t.getProfesional().getApellido(),
            t.getSucursal().getId(),
            t.getSucursal().getNombre(),
            t.getEstudio() != null ? t.getEstudio().getId() : null,
            t.getEstudio() != null ? t.getEstudio().getNombre() : null,
            t.getObraSocial() != null ? t.getObraSocial().getId() : null,
            t.getObraSocial() != null ? t.getObraSocial().getNombre() : null,
            t.getPlan() != null ? t.getPlan().getId() : null,
            t.getPlan() != null ? t.getPlan().getNombre() : null,
            t.getFechaHora().toString(),
            t.getEstado(),
            t.getObservaciones(),
            t.getCreatedAt() != null ? t.getCreatedAt().toString() : null,
            t.getConfirmacionEnviadaAt() != null ? t.getConfirmacionEnviadaAt().toString() : null
        );
    }
}
