package com.bemo.backend.service;

import com.bemo.backend.dto.EstudioDto;
import com.bemo.backend.model.Estudio;
import com.bemo.backend.repository.EstudioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstudioService {

    private final EstudioRepository repo;

    public List<EstudioDto> getAll() {
        return repo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public EstudioDto create(EstudioDto dto) {
        Estudio e = new Estudio();
        e.setNombre(dto.getNombre());
        e.setCodigo(dto.getCodigo());
        e.setDescripcion(dto.getDescripcion());
        e.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        return toDto(repo.save(e));
    }

    public EstudioDto update(Long id, EstudioDto dto) {
        Estudio e = repo.findById(id).orElseThrow(() -> new RuntimeException("Estudio no encontrado"));
        if (dto.getNombre() != null) e.setNombre(dto.getNombre());
        if (dto.getCodigo() != null) e.setCodigo(dto.getCodigo());
        if (dto.getDescripcion() != null) e.setDescripcion(dto.getDescripcion());
        if (dto.getActivo() != null) e.setActivo(dto.getActivo());
        return toDto(repo.save(e));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    private EstudioDto toDto(Estudio e) {
        return new EstudioDto(e.getId(), e.getNombre(), e.getCodigo(), e.getDescripcion(), e.getActivo());
    }
}
