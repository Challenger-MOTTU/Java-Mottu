package com.motogrid.api.web;

import com.motogrid.api.dto.MotoDTO;
import com.motogrid.api.dto.mapper.MotoMapper;
import com.motogrid.api.model.Moto;
import com.motogrid.api.model.StatusMoto;
import com.motogrid.api.service.MotoService;
import com.motogrid.api.service.PatioService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/web/motos")
@RequiredArgsConstructor
public class MotoWebController {

    private final MotoService motoService;
    private final PatioService patioService;

    @GetMapping
    public String listar(@RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "placa") String sort,
                         @RequestParam(defaultValue = "ASC") Sort.Direction dir,
                         Model model) {
        Page<Moto> p = motoService.listar(PageRequest.of(page, 10, Sort.by(dir, sort)));
        model.addAttribute("page", p.map(MotoMapper::toDTO));
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir.name());
        return "motos/list";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("moto", new MotoDTO());
        model.addAttribute("patios", patioService.listarTodos(Pageable.unpaged()).getContent());
        model.addAttribute("statusList", StatusMoto.values());
        return "motos/form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        Moto m = motoService.buscarPorId(id);
        model.addAttribute("moto", MotoMapper.toDTO(m));
        model.addAttribute("patios", patioService.listarTodos(Pageable.unpaged()).getContent());
        model.addAttribute("statusList", StatusMoto.values());
        return "motos/form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    /** Salva criação ou atualização (usa id no DTO para diferenciar) */
    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("moto") MotoDTO dto, BindingResult br, Model model) {
        if (br.hasErrors()) {
            model.addAttribute("patios", patioService.listarTodos(Pageable.unpaged()).getContent());
            model.addAttribute("statusList", StatusMoto.values());
            return "motos/form";
        }
        if (dto.getId() == null) {
            motoService.salvar(MotoMapper.toEntity(dto));
        } else {
            motoService.atualizar(dto.getId(), MotoMapper.toEntity(dto));
        }
        return "redirect:/web/motos";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id) {
        motoService.deletar(id);
        return "redirect:/web/motos";
    }
}
