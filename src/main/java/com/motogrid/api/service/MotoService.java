package com.motogrid.api.service;

import com.motogrid.api.dto.MotoDTO;
import com.motogrid.api.model.Moto;
import com.motogrid.api.model.Patio;
import com.motogrid.api.repository.MotoRepository;
import com.motogrid.api.repository.PatioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MotoService {

    private final MotoRepository motoRepository;
    private final PatioRepository patioRepository;

    @Cacheable("motos")
    public Page<MotoDTO> listar(Pageable pageable) {
        return motoRepository.findAll(pageable).map(this::toDTO);
    }

    public Page<MotoDTO> buscarPorPlaca(String placa, Pageable pageable) {
        return motoRepository.findByPlacaContainingIgnoreCase(placa, pageable).map(this::toDTO);
    }

    public Page<MotoDTO> buscarPorStatus(String status, Pageable pageable) {
        return motoRepository.findByStatus(Enum.valueOf(com.motogrid.api.model.StatusMoto.class, status.toUpperCase()), pageable)
                .map(this::toDTO);
    }

    public MotoDTO salvar(MotoDTO dto) {
        Moto moto = toEntity(dto);
        Moto salvo = motoRepository.save(moto);
        return toDTO(salvo);
    }

    private Moto toEntity(MotoDTO dto) {
        Moto moto = new Moto();
        moto.setId(dto.getId());
        moto.setPlaca(dto.getPlaca());
        moto.setModelo(dto.getModelo());
        moto.setStatus(dto.getStatus());

        Patio patio = patioRepository.findById(dto.getPatioId())
                .orElseThrow(() -> new EntityNotFoundException("Pátio não encontrado"));

        moto.setPatio(patio);
        return moto;
    }

    private MotoDTO toDTO(Moto moto) {
        MotoDTO dto = new MotoDTO();
        dto.setId(moto.getId());
        dto.setPlaca(moto.getPlaca());
        dto.setModelo(moto.getModelo());
        dto.setStatus(moto.getStatus());
        dto.setPatioId(moto.getPatio().getId());
        return dto;
    }
}
