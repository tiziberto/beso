package com.bemo.backend.repository;

import com.bemo.backend.model.Estudio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EstudioRepository extends JpaRepository<Estudio, Long> {
    List<Estudio> findByActivoTrue();
}
