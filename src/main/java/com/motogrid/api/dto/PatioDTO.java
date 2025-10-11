package com.motogrid.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatioDTO {

    private Long id;

    @NotBlank(message = "O nome do pátio é obrigatório")
    private String nome;

    @NotBlank(message = "A cidade é obrigatória")
    private String cidade;

    private int capacidade;
}