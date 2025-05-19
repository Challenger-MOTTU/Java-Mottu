package com.motogrid.api.service;

import com.motogrid.api.dto.PatioDTO;
import com.motogrid.api.model.Patio;
import com.motogrid.api.repository.PatioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PatioService {

    private final PatioRepository patioRepository;

    // Novo método com suporte a paginação
    public Page<PatioDTO> listarTodos(Pageable pageable) {
        return patioRepository.findAll(pageable)
                .map(this::toDTO);
    }

    public PatioDTO buscarPorId(Long id) {
        Patio patio = patioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pátio não encontrado"));
        return toDTO(patio);
    }

    public PatioDTO salvar(PatioDTO dto) {
        Patio patio = toEntity(dto);
        Patio salvo = patioRepository.save(patio);
        return toDTO(salvo);
    }

    public void deletar(Long id) {
        Patio patio = patioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pátio não encontrado"));

        if (patio.getMotos() != null && !patio.getMotos().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Não é possível deletar um pátio com motos vinculadas"
            );
        }

        patioRepository.deleteById(id);
    }

    public PatioDTO atualizar(PatioDTO dto) {
        Patio existente = patioRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Pátio não encontrado"));

        existente.setNome(dto.getNome());
        existente.setCidade(dto.getCidade());
        existente.setCapacidade(dto.getCapacidade());

        return toDTO(patioRepository.save(existente));
    }

    private Patio toEntity(PatioDTO dto) {
        Patio p = new Patio();
        p.setId(dto.getId());
        p.setNome(dto.getNome());
        p.setCidade(dto.getCidade());
        p.setCapacidade(dto.getCapacidade());
        return p;
    }

    private PatioDTO toDTO(Patio patio) {
        PatioDTO dto = new PatioDTO();
        dto.setId(patio.getId());
        dto.setNome(patio.getNome());
        dto.setCidade(patio.getCidade());
        dto.setCapacidade(patio.getCapacidade());
        return dto;
    }
}
