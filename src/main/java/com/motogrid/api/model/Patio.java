package com.motogrid.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "nome", "cidade"})
public class Patio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String cidade;

    private int capacidade;

    @OneToMany(mappedBy = "patio", fetch = FetchType.EAGER)
    private List<Moto> motos;

    public void atualizarDados(Patio novosDados) {
        if (novosDados.getNome() != null) {
            this.setNome(novosDados.getNome());
        }
        if (novosDados.getCidade() != null) {
            this.setCidade(novosDados.getCidade());
        }
        this.setCapacidade(novosDados.getCapacidade());
    }
}