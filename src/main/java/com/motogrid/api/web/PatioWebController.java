package com.motogrid.api.web;

import com.motogrid.api.dto.PatioDTO;
import com.motogrid.api.dto.mapper.PatioMapper;
import com.motogrid.api.service.PatioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/web/patios")
@RequiredArgsConstructor
public class PatioWebController {

    private final PatioService patioService;

    @GetMapping
    public String listar(@RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "nome") String sort,
                         @RequestParam(defaultValue = "ASC") Sort.Direction dir,
                         Model model) {
        model.addAttribute("page",
                patioService.listarTodos(PageRequest.of(page, 10, Sort.by(dir, sort))).map(PatioMapper::toDTO));
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir.name());
        return "patios/list";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("patio", new PatioDTO());
        return "patios/form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("patio", PatioMapper.toDTO(patioService.buscarPorId(id)));
        return "patios/form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("patio") PatioDTO dto, BindingResult br) {
        if (br.hasErrors()) return "patios/form";
        if (dto.getId() == null) patioService.salvar(PatioMapper.toEntity(dto));
        else patioService.atualizar(dto.getId(), PatioMapper.toEntity(dto));
        return "redirect:/web/patios";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id) {
        patioService.deletar(id);
        return "redirect:/web/patios";
    }
}
