package com.motogrid.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Patio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do pátio é obrigatório")
    @Size(min = 3, max = 50)
    private String nome;

    @NotBlank(message = "A cidade é obrigatória")
    private String cidade;

    private int capacidade;

    @OneToMany(mappedBy = "patio")
    private List<Moto> motos;
}
