package com.motogrid.api.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Patio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String cidade;

    private int capacidade;

    @OneToMany(mappedBy = "patio")
    private List<Moto> motos;
}

