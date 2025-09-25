package com.motogrid.api.dto.mapper;

import com.motogrid.api.dto.PatioDTO;
import com.motogrid.api.model.Patio;

public final class PatioMapper {
    private PatioMapper() {}

    public static PatioDTO toDTO(Patio p) {
        PatioDTO dto = new PatioDTO();
        dto.setId(p.getId());
        dto.setNome(p.getNome());
        dto.setCidade(p.getCidade());
        dto.setCapacidade(p.getCapacidade());
        return dto;
    }

    public static Patio toEntity(PatioDTO dto) {
        Patio p = new Patio();
        p.setId(dto.getId());
        p.setNome(dto.getNome());
        p.setCidade(dto.getCidade());
        p.setCapacidade(dto.getCapacidade());
        return p;
    }
}
