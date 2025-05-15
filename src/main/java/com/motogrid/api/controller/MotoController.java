package com.motogrid.api.controller;

import com.motogrid.api.dto.MotoDTO;
import com.motogrid.api.service.MotoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/motos")
@RequiredArgsConstructor
public class MotoController {

    private final MotoService motoService;

    @GetMapping
    public ResponseEntity<?> listar(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(motoService.listar(pageable));
    }

    @GetMapping("/buscar/placa")
    public ResponseEntity<?> buscarPorPlaca(@RequestParam String placa, Pageable pageable) {
        return ResponseEntity.ok(motoService.buscarPorPlaca(placa, pageable));
    }

    @GetMapping("/buscar/status")
    public ResponseEntity<?> buscarPorStatus(@RequestParam String status, Pageable pageable) {
        return ResponseEntity.ok(motoService.buscarPorStatus(status, pageable));
    }

    @PostMapping
    public ResponseEntity<MotoDTO> salvar(@RequestBody @Valid MotoDTO dto) {
        return ResponseEntity.ok(motoService.salvar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MotoDTO> atualizar(@PathVariable Long id, @RequestBody @Valid MotoDTO dto) {
        if (dto.getId() != null && !dto.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID do corpo e da URL não conferem");
        }

        dto.setId(id);
        return ResponseEntity.ok(motoService.atualizar(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        motoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
