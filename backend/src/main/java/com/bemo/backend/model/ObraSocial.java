package com.bemo.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "obras_sociales")
public class ObraSocial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 50)
    private String codigo;

    @Column(length = 50)
    private String telefono;

    @Column(length = 150)
    private String email;

    @Column(name = "is_active")
    private Boolean activa = true;

    @OneToMany(mappedBy = "obraSocial", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Plan> planes = new ArrayList<>();
}
