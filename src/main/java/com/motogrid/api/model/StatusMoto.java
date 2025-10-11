package com.motogrid.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Optional;

@Schema(description = "Status da moto")
public enum StatusMoto {
    DISPONIVEL("DISPONÍVEL"),
    EM_USO("EM USO"),
    EM_MANUTENCAO("EM MANUTENÇÃO"),
    INATIVA("INATIVA");

    private final String label;

    StatusMoto(String label) { this.label = label; }

    public String getLabel() { return label; }
    public static Optional<StatusMoto> from(String raw) {
        if (raw == null || raw.isBlank()) return Optional.empty();
        try { return Optional.of(StatusMoto.valueOf(raw.trim().toUpperCase())); }
        catch (IllegalArgumentException e) { return Optional.empty(); }
    }
}
