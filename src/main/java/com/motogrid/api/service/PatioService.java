package com.motogrid.api.service;

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

    public Page<Patio> listarTodos(Pageable pageable) {
        return patioRepository.findAll(pageable);
    }

    public Patio buscarPorId(Long id) {
        return patioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pátio não encontrado"));
    }

    public Patio salvar(Patio patio) {
        return patioRepository.save(patio);
    }

    public Patio atualizar(Long id, Patio patio) {
        Patio existente = patioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pátio não encontrado"));

        existente.setNome(patio.getNome());
        existente.setCidade(patio.getCidade());
        existente.setCapacidade(patio.getCapacidade());
        return patioRepository.save(existente);
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
}
