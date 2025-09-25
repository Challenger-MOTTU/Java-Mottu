package com.motogrid.api.dto.mapper;

import com.motogrid.api.dto.MotoDTO;
import com.motogrid.api.model.Moto;
import com.motogrid.api.model.Patio;

public final class MotoMapper {
    private MotoMapper() {}

    public static MotoDTO toDTO(Moto m) {
        MotoDTO dto = new MotoDTO();
        dto.setId(m.getId());
        dto.setPlaca(m.getPlaca());
        dto.setModelo(m.getModelo());
        dto.setStatus(m.getStatus());
        dto.setPatioId(m.getPatio() != null ? m.getPatio().getId() : null);
        return dto;
    }

    public static Moto toEntity(MotoDTO dto) {
        Moto m = new Moto();
        m.setId(dto.getId());
        m.setPlaca(dto.getPlaca());
        m.setModelo(dto.getModelo());
        m.setStatus(dto.getStatus());
        if (dto.getPatioId() != null) {
            Patio p = new Patio();
            p.setId(dto.getPatioId());
            m.setPatio(p);
        }
        return m;
    }
}
