package com.bemo.backend.service;

import com.bemo.backend.dto.ProfesionalDto;
import com.bemo.backend.model.*;
import com.bemo.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfesionalService {

    private final ProfesionalRepository repo;
    private final ObraSocialRepository osRepo;
    private final EstudioRepository estudioRepo;
    private final SucursalRepository sucursalRepo;
    private final UserRepository userRepo;
    private final EspecialidadRepository especialidadRepo;

    public List<ProfesionalDto> getAll() {
        return repo.findByActivoTrue().stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<ProfesionalDto> search(String query) {
        if (query == null || query.isBlank()) return getAll();
        return repo.search(query.trim()).stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<ProfesionalDto> getBySucursal(Long sucursalId) {
        return repo.findBySucursalId(sucursalId).stream().map(this::toDto).collect(Collectors.toList());
    }

    public ProfesionalDto getById(Long id) {
        return toDto(repo.findById(id).orElseThrow(() -> new RuntimeException("Profesional no encontrado")));
    }

    public ProfesionalDto getByUserId(Long userId) {
        return repo.findByUserId(userId)
            .map(this::toDto)
            .orElseThrow(() -> new RuntimeException("Profesional no vinculado a este usuario"));
    }

    @Transactional
    public ProfesionalDto create(ProfesionalDto dto) {
        Profesional p = new Profesional();
        mapFromDto(p, dto);
        return toDto(repo.save(p));
    }

    @Transactional
    public ProfesionalDto update(Long id, ProfesionalDto dto) {
        Profesional p = repo.findById(id).orElseThrow(() -> new RuntimeException("Profesional no encontrado"));
        mapFromDto(p, dto);
        return toDto(repo.save(p));
    }

    public void delete(Long id) {
        Profesional p = repo.findById(id).orElseThrow(() -> new RuntimeException("Profesional no encontrado"));
        p.setActivo(false);
        repo.save(p);
    }

    private void mapFromDto(Profesional p, ProfesionalDto dto) {
        if (dto.getNombre() != null) p.setNombre(dto.getNombre());
        if (dto.getApellido() != null) p.setApellido(dto.getApellido());
        if (dto.getDni() != null) p.setDni(dto.getDni());
        if (dto.getMatricula() != null) p.setMatricula(dto.getMatricula());
        if (dto.getTelefono() != null) p.setTelefono(dto.getTelefono());
        if (dto.getEmail() != null) p.setEmail(dto.getEmail());

        if (dto.getEspecialidadesIds() != null) {
            p.setEspecialidades(new HashSet<>(especialidadRepo.findAllById(dto.getEspecialidadesIds())));
        }
        if (dto.getObrasSocialesIds() != null) {
            p.setObrasSociales(new HashSet<>(osRepo.findAllById(dto.getObrasSocialesIds())));
        }
        if (dto.getEstudiosIds() != null) {
            p.setEstudios(new HashSet<>(estudioRepo.findAllById(dto.getEstudiosIds())));
        }
        if (dto.getSucursalesIds() != null) {
            p.setSucursales(new HashSet<>(sucursalRepo.findAllById(dto.getSucursalesIds())));
        }
        if (dto.getUserId() != null) {
            p.setUser(userRepo.findById(dto.getUserId()).orElse(null));
        }
    }

    private ProfesionalDto toDto(Profesional p) {
        return new ProfesionalDto(
            p.getId(), p.getNombre(), p.getApellido(), p.getDni(), p.getMatricula(),
            p.getTelefono(), p.getEmail(),
            p.getEspecialidades().stream().map(Especialidad::getId).collect(Collectors.toSet()),
            p.getObrasSociales().stream().map(ObraSocial::getId).collect(Collectors.toSet()),
            p.getEstudios().stream().map(Estudio::getId).collect(Collectors.toSet()),
            p.getSucursales().stream().map(Sucursal::getId).collect(Collectors.toSet()),
            p.getUser() != null ? p.getUser().getId() : null,
            p.getActivo()
        );
    }
}
