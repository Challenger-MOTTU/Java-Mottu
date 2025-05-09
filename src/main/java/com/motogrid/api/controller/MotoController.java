    package com.motogrid.api.controller;

    import com.motogrid.api.dto.MotoDTO;
    import org.springdoc.core.annotations.ParameterObject;
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
            dto.setId(id);
            return ResponseEntity.ok(motoService.atualizar(dto));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deletar(@PathVariable Long id) {
            motoService.deletar(id);
            return ResponseEntity.noContent().build();
        }

    }
