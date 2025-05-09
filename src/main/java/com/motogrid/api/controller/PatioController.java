package com.motogrid.api.controller;

import com.motogrid.api.dto.PatioDTO;
import com.motogrid.api.service.PatioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patios")
@RequiredArgsConstructor
public class PatioController {

    private final PatioService patioService;

    @GetMapping
    public ResponseEntity<List<PatioDTO>> listarTodos() {
        return ResponseEntity.ok(patioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatioDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(patioService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<PatioDTO> salvar(@RequestBody @Valid PatioDTO dto) {
        return ResponseEntity.ok(patioService.salvar(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        patioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
