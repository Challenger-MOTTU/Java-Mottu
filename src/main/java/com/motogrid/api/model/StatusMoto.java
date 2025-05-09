package com.motogrid.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Status da moto")
public enum StatusMoto {
    DISPONIVEL,
    EM_USO,
    EM_MANUTENCAO,
    INATIVA
}
