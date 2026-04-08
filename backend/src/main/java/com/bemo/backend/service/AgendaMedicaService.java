package com.bemo.backend.service;

import com.bemo.backend.dto.AgendaMedicaDto;
import com.bemo.backend.model.AgendaMedica;
import com.bemo.backend.model.Profesional;
import com.bemo.backend.model.Sucursal;
import com.bemo.backend.repository.AgendaMedicaRepository;
import com.bemo.backend.repository.ProfesionalRepository;
import com.bemo.backend.repository.SucursalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgendaMedicaService {

    private final AgendaMedicaRepository repo;
    private final ProfesionalRepository profRepo;
    private final SucursalRepository sucRepo;

    public List<AgendaMedicaDto> getAll() {
        return repo.findByActivaTrue().stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<AgendaMedicaDto> getByProfesional(Long profesionalId) {
        return repo.findByProfesionalIdAndActivaTrue(profesionalId).stream()
            .map(this::toDto).collect(Collectors.toList());
    }

    public List<AgendaMedicaDto> getByProfesionalAndSucursal(Long profesionalId, Long sucursalId) {
        return repo.findByProfesionalIdAndSucursalIdAndActivaTrue(profesionalId, sucursalId).stream()
            .map(this::toDto).collect(Collectors.toList());
    }

    public AgendaMedicaDto create(AgendaMedicaDto dto) {
        Profesional prof = profRepo.findById(dto.getProfesionalId())
            .orElseThrow(() -> new RuntimeException("Profesional no encontrado"));
        Sucursal suc = sucRepo.findById(dto.getSucursalId())
            .orElseThrow(() -> new RuntimeException("Sucursal no encontrada"));

        AgendaMedica a = new AgendaMedica();
        a.setProfesional(prof);
        a.setSucursal(suc);
        a.setDiaSemana(dto.getDiaSemana());
        a.setHoraInicio(LocalTime.parse(dto.getHoraInicio()));
        a.setHoraFin(LocalTime.parse(dto.getHoraFin()));
        a.setDuracionTurnoMinutos(dto.getDuracionTurnoMinutos() != null ? dto.getDuracionTurnoMinutos() : 30);
        a.setActiva(true);
        return toDto(repo.save(a));
    }

    public AgendaMedicaDto update(Long id, AgendaMedicaDto dto) {
        AgendaMedica a = repo.findById(id).orElseThrow(() -> new RuntimeException("Agenda no encontrada"));
        if (dto.getDiaSemana() != null) a.setDiaSemana(dto.getDiaSemana());
        if (dto.getHoraInicio() != null) a.setHoraInicio(LocalTime.parse(dto.getHoraInicio()));
        if (dto.getHoraFin() != null) a.setHoraFin(LocalTime.parse(dto.getHoraFin()));
        if (dto.getDuracionTurnoMinutos() != null) a.setDuracionTurnoMinutos(dto.getDuracionTurnoMinutos());
        if (dto.getActiva() != null) a.setActiva(dto.getActiva());

        if (dto.getProfesionalId() != null) {
            a.setProfesional(profRepo.findById(dto.getProfesionalId())
                .orElseThrow(() -> new RuntimeException("Profesional no encontrado")));
        }
        if (dto.getSucursalId() != null) {
            a.setSucursal(sucRepo.findById(dto.getSucursalId())
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada")));
        }
        return toDto(repo.save(a));
    }

    public void delete(Long id) {
        AgendaMedica a = repo.findById(id).orElseThrow(() -> new RuntimeException("Agenda no encontrada"));
        a.setActiva(false);
        repo.save(a);
    }

    private AgendaMedicaDto toDto(AgendaMedica a) {
        return new AgendaMedicaDto(
            a.getId(),
            a.getProfesional().getId(),
            a.getProfesional().getApellido() + ", " + a.getProfesional().getNombre(),
            a.getSucursal().getId(),
            a.getSucursal().getNombre(),
            a.getDiaSemana(),
            a.getHoraInicio().toString(),
            a.getHoraFin().toString(),
            a.getDuracionTurnoMinutos(),
            a.getActiva()
        );
    }
}
