package com.motogrid.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
public class Moto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "A placa é obrigatória")
    @Size(min = 7, max = 8)
    @Column(unique = true)
    private String placa;

    @NotBlank(message = "O modelo é obrigatório")
    private String modelo;

    @Enumerated(EnumType.STRING)
    private StatusMoto status;

    @ManyToOne
    @JoinColumn(name = "patio_id")
    private Patio patio;
}
