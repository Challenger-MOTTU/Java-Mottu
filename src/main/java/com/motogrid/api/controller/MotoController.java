    package com.motogrid.api.controller;

    import com.motogrid.api.dto.MotoDTO;
    import com.motogrid.api.dto.mapper.MotoMapper;
    import com.motogrid.api.model.Moto;
    import com.motogrid.api.service.MotoService;
    import jakarta.validation.Valid;
    import lombok.RequiredArgsConstructor;
    import org.springdoc.core.annotations.ParameterObject;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
    import org.springframework.data.domain.Sort;
    import org.springframework.data.web.PageableDefault;
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
        public ResponseEntity<Page<MotoDTO>> listar(
                @ParameterObject
                @PageableDefault(sort = "placa", direction = Sort.Direction.ASC) Pageable pageable) {
            Page<MotoDTO> page = motoService.listar(pageable).map(MotoMapper::toDTO);
            return ResponseEntity.ok(page);
        }

        @GetMapping("/buscar/placa")
        public ResponseEntity<Page<MotoDTO>> buscarPorPlaca(
                @RequestParam String placa,
                @ParameterObject Pageable pageable) {
            return ResponseEntity.ok(motoService.buscarPorPlaca(placa, pageable).map(MotoMapper::toDTO));
        }

        @GetMapping("/buscar/status")
        public ResponseEntity<Page<MotoDTO>> buscarPorStatus(
                @RequestParam String status,
                @ParameterObject Pageable pageable) {
            return ResponseEntity.ok(motoService.buscarPorStatus(status, pageable).map(MotoMapper::toDTO));
        }

        @PostMapping
        public ResponseEntity<MotoDTO> salvar(@RequestBody @Valid MotoDTO dto) {
            Moto salvo = motoService.salvar(MotoMapper.toEntity(dto));
            return ResponseEntity.ok(MotoMapper.toDTO(salvo));
        }

        @PutMapping("/{id}")
        public ResponseEntity<MotoDTO> atualizar(@PathVariable Long id, @RequestBody @Valid MotoDTO dto) {
            if (dto.getId() != null && !dto.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID do corpo e da URL não conferem");
            }
            Moto atualizado = motoService.atualizar(id, MotoMapper.toEntity(dto));
            return ResponseEntity.ok(MotoMapper.toDTO(atualizado));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deletar(@PathVariable Long id) {
            motoService.deletar(id);
            return ResponseEntity.noContent().build();
        }
    }
