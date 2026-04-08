package com.bemo.backend.service;

import com.bemo.backend.dto.PacienteDto;
import com.bemo.backend.model.ObraSocial;
import com.bemo.backend.model.Paciente;
import com.bemo.backend.model.Plan;
import com.bemo.backend.repository.ObraSocialRepository;
import com.bemo.backend.repository.PacienteRepository;
import com.bemo.backend.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository repo;
    private final ObraSocialRepository osRepo;
    private final PlanRepository planRepo;

    public List<PacienteDto> getAll() {
        return repo.findByActivoTrue().stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<PacienteDto> search(String query) {
        if (query == null || query.isBlank()) return getAll();
        return repo.search(query.trim()).stream().map(this::toDto).collect(Collectors.toList());
    }

    public PacienteDto getById(Long id) {
        return toDto(repo.findById(id).orElseThrow(() -> new RuntimeException("Paciente no encontrado")));
    }

    public PacienteDto getByDni(String dni) {
        return toDto(repo.findByDni(dni).orElseThrow(() -> new RuntimeException("Paciente no encontrado")));
    }

    public PacienteDto create(PacienteDto dto) {
        if (repo.existsByDni(dto.getDni())) {
            throw new RuntimeException("Ya existe un paciente con DNI " + dto.getDni());
        }
        Paciente p = new Paciente();
        mapFromDto(p, dto);
        return toDto(repo.save(p));
    }

    public PacienteDto update(Long id, PacienteDto dto) {
        Paciente p = repo.findById(id).orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        if (dto.getDni() != null && !dto.getDni().equals(p.getDni()) && repo.existsByDni(dto.getDni())) {
            throw new RuntimeException("Ya existe otro paciente con DNI " + dto.getDni());
        }
        mapFromDto(p, dto);
        return toDto(repo.save(p));
    }

    public void delete(Long id) {
        Paciente p = repo.findById(id).orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        p.setActivo(false);
        repo.save(p);
    }

    private void mapFromDto(Paciente p, PacienteDto dto) {
        if (dto.getNombre() != null) p.setNombre(dto.getNombre());
        if (dto.getApellido() != null) p.setApellido(dto.getApellido());
        if (dto.getDni() != null) p.setDni(dto.getDni());
        if (dto.getFechaNacimiento() != null && !dto.getFechaNacimiento().isBlank()) {
            p.setFechaNacimiento(LocalDate.parse(dto.getFechaNacimiento()));
        }
        if (dto.getSexo() != null) p.setSexo(dto.getSexo());
        if (dto.getTelefono() != null) p.setTelefono(dto.getTelefono());
        if (dto.getEmail() != null) p.setEmail(dto.getEmail());
        if (dto.getDireccion() != null) p.setDireccion(dto.getDireccion());
        if (dto.getNumeroAfiliado() != null) p.setNumeroAfiliado(dto.getNumeroAfiliado());
        if (dto.getObservaciones() != null) p.setObservaciones(dto.getObservaciones());

        if (dto.getObraSocialId() != null) {
            ObraSocial os = osRepo.findById(dto.getObraSocialId())
                .orElseThrow(() -> new RuntimeException("Obra Social no encontrada"));
            p.setObraSocial(os);
        } else {
            p.setObraSocial(null);
        }

        if (dto.getPlanId() != null) {
            Plan plan = planRepo.findById(dto.getPlanId())
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));
            p.setPlan(plan);
        } else {
            p.setPlan(null);
        }
    }

    private PacienteDto toDto(Paciente p) {
        return new PacienteDto(
            p.getId(), p.getNombre(), p.getApellido(), p.getDni(),
            p.getFechaNacimiento() != null ? p.getFechaNacimiento().toString() : null,
            p.getSexo(), p.getTelefono(), p.getEmail(), p.getDireccion(),
            p.getObraSocial() != null ? p.getObraSocial().getId() : null,
            p.getObraSocial() != null ? p.getObraSocial().getNombre() : null,
            p.getPlan() != null ? p.getPlan().getId() : null,
            p.getPlan() != null ? p.getPlan().getNombre() : null,
            p.getNumeroAfiliado(), p.getObservaciones(), p.getActivo()
        );
    }
}
