package com.motogrid.api.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Moto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String placa;

    private String modelo;

    @Enumerated(EnumType.STRING)
    private StatusMoto status;

    @ManyToOne
    @JoinColumn(name = "patio_id")
    private Patio patio;

    public void atualizarDados(Moto novosDados, Patio novoPatio) {
        this.setModelo(novosDados.getModelo());
        this.setPlaca(novosDados.getPlaca());
        this.setStatus(novosDados.getStatus());
        this.setPatio(novoPatio);
    }
}