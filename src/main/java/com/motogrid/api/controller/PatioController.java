package com.motogrid.api.controller;

import com.motogrid.api.dto.PatioDTO;
import com.motogrid.api.service.PatioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patios")
@RequiredArgsConstructor
public class PatioController {

    private final PatioService patioService;

    @GetMapping
    public ResponseEntity<Page<PatioDTO>> listarTodos(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(patioService.listarTodos(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatioDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(patioService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<PatioDTO> salvar(@RequestBody @Valid PatioDTO dto) {
        return ResponseEntity.ok(patioService.salvar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatioDTO> atualizar(@PathVariable Long id, @RequestBody @Valid PatioDTO dto) {
        dto.setId(id);
        return ResponseEntity.ok(patioService.atualizar(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        patioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
