package com.motogrid.api.controller;

import com.motogrid.api.dto.MotoDTO;
import com.motogrid.api.service.MotoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/motos")
@RequiredArgsConstructor
public class MotoController {

    private final MotoService motoService;

    @GetMapping
    public ResponseEntity<?> listar(Pageable pageable) {
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
}
