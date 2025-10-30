package com.motogrid.api.dto;

import com.motogrid.api.model.StatusMoto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MotoDTO {

    private Long id;

    @NotBlank(message = "A placa é obrigatória")
    @Size(min = 7, max = 8)
    private String placa;

    @NotBlank(message = "O modelo é obrigatório")
    private String modelo;

    private StatusMoto status;

    private Long patioId;
}