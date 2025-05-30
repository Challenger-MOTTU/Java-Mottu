package com.motogrid.api.service;

import com.motogrid.api.dto.MotoDTO;
import com.motogrid.api.model.Moto;
import com.motogrid.api.model.Patio;
import com.motogrid.api.model.StatusMoto;
import com.motogrid.api.repository.MotoRepository;
import com.motogrid.api.repository.PatioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
        try {
            StatusMoto statusEnum = StatusMoto.valueOf(status.toUpperCase());
            return motoRepository.findByStatus(statusEnum, pageable).map(this::toDTO);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status inválido. Use: DISPONIVEL, EM_USO, EM_MANUTENCAO ou INATIVA."
            );
        }
    }

    @CacheEvict(value = "motos", allEntries = true)
    public MotoDTO salvar(MotoDTO dto) {
        Moto moto = toEntity(dto);
        Moto salvo = motoRepository.save(moto);
        return toDTO(salvo);
    }

    @CacheEvict(value = "motos", allEntries = true)
    public MotoDTO atualizar(MotoDTO dto) {
        Moto existente = motoRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Moto não encontrada"));

        existente.setModelo(dto.getModelo());
        existente.setPlaca(dto.getPlaca());
        existente.setStatus(dto.getStatus());

        Patio patio = patioRepository.findById(dto.getPatioId())
                .orElseThrow(() -> new EntityNotFoundException("Pátio não encontrado"));

        existente.setPatio(patio);

        return toDTO(motoRepository.save(existente));
    }

    @CacheEvict(value = "motos", allEntries = true)
    public void deletar(Long id) {
        if (!motoRepository.existsById(id)) {
            throw new EntityNotFoundException("Moto não encontrada");
        }
        motoRepository.deleteById(id);
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
