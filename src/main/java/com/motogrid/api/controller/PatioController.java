package com.motogrid.api.controller;

import com.motogrid.api.dto.PatioDTO;
import com.motogrid.api.dto.mapper.PatioMapper;
import com.motogrid.api.model.Patio;
import com.motogrid.api.service.PatioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patios")
@RequiredArgsConstructor
public class PatioController {

    private final PatioService patioService;

    @GetMapping
    public ResponseEntity<Page<PatioDTO>> listarTodos(
            @ParameterObject
            @PageableDefault(sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<PatioDTO> page = patioService.listarTodos(pageable).map(PatioMapper::toDTO);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatioDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(PatioMapper.toDTO(patioService.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<PatioDTO> salvar(@RequestBody @Valid PatioDTO dto) {
        Patio salvo = patioService.salvar(PatioMapper.toEntity(dto));
        return ResponseEntity.ok(PatioMapper.toDTO(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatioDTO> atualizar(@PathVariable Long id, @RequestBody @Valid PatioDTO dto) {
        Patio atualizado = patioService.atualizar(id, PatioMapper.toEntity(dto));
        return ResponseEntity.ok(PatioMapper.toDTO(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        patioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
