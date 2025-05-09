package com.motogrid.api.service;

import com.motogrid.api.dto.PatioDTO;
import com.motogrid.api.model.Patio;
import com.motogrid.api.repository.PatioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatioService {

    private final PatioRepository patioRepository;

    public List<PatioDTO> listarTodos() {
        return patioRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
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
        patioRepository.deleteById(id);
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

    public PatioDTO atualizar(PatioDTO dto) {
        Patio existente = patioRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Pátio não encontrado"));

        existente.setNome(dto.getNome());
        existente.setCidade(dto.getCidade());
        existente.setCapacidade(dto.getCapacidade());

        return toDTO(patioRepository.save(existente));
    }

}
