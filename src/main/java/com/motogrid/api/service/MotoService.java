package com.motogrid.api.service;

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
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MotoService {

    private final MotoRepository motoRepository;
    private final PatioRepository patioRepository;

    @Cacheable("motos")
    public Page<Moto> listar(Pageable pageable) {
        return motoRepository.findAll(pageable);
    }

    public Moto buscarPorId(Long id) {
        return motoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Moto não encontrada"));
    }

    public Page<Moto> buscarPorPlaca(String placa, Pageable pageable) {
        return motoRepository.findByPlacaContainingIgnoreCase(placa, pageable);
    }

    public Page<Moto> buscarPorStatus(String status, Pageable pageable) {
        try {
            StatusMoto statusEnum = StatusMoto.valueOf(status.toUpperCase());
            return motoRepository.findByStatus(statusEnum, pageable);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status inválido. Use: DISPONIVEL, EM_USO, EM_MANUTENCAO ou INATIVA."
            );
        }
    }

    @CacheEvict(value = "motos", allEntries = true)
    public Moto salvar(Moto moto) {
        Long patioId = (moto.getPatio() != null) ? moto.getPatio().getId() : null;
        if (patioId == null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "patioId é obrigatório");
        }
        Patio patio = patioRepository.findById(patioId)
                .orElseThrow(() -> new EntityNotFoundException("Pátio não encontrado"));
        moto.setPatio(patio);
        return motoRepository.save(moto);
    }

    @CacheEvict(value = "motos", allEntries = true)
    public Moto atualizar(Long id, Moto moto) {
        Moto existente = motoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Moto não encontrada"));

        Patio novoPatio = null;
        if (moto.getPatio() != null && moto.getPatio().getId() != null) {
            novoPatio = patioRepository.findById(moto.getPatio().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Pátio não encontrado"));
        } else {
            novoPatio = existente.getPatio();
        }

        existente.atualizarDados(moto, novoPatio);

        return motoRepository.save(existente);
    }

    @CacheEvict(value = "motos", allEntries = true)
    public void deletar(Long id) {
        if (!motoRepository.existsById(id)) {
            throw new EntityNotFoundException("Moto não encontrada");
        }
        motoRepository.deleteById(id);
    }

    public List<Moto> listarTodas() {
        return motoRepository.findAll(Sort.by(Sort.Direction.ASC, "placa"));
    }
}