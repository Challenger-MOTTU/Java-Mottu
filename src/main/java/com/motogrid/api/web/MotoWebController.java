package com.motogrid.api.web;

import com.motogrid.api.dto.MotoDTO;
import com.motogrid.api.dto.mapper.MotoMapper;
import com.motogrid.api.model.Moto;
import com.motogrid.api.model.StatusMoto;
import com.motogrid.api.service.MotoRelatorioService;
import com.motogrid.api.service.MotoService;
import com.motogrid.api.service.PatioService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/web/motos")
@RequiredArgsConstructor
public class MotoWebController {

    private final MotoService motoService;
    private final PatioService patioService;
    private final MotoRelatorioService relatorioService;

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

    @PreAuthorize("hasAnyRole('ADMIN','OPERADOR')")
    @GetMapping(value = "/export.xlsx")
    public void exportXlsx(@RequestParam(required = false) String status,
                           @RequestParam(required = false) Long patioId,
                           HttpServletResponse resp) throws Exception {

        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
        String filename = "motos_" + ts + ".xlsx";

        var motos = relatorioService.filtrar(status, patioId);
        var bytes = relatorioService.gerarPlanilha(motos);

        resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        resp.getOutputStream().write(bytes);
        resp.getOutputStream().flush();
    }

    @PreAuthorize("hasAnyRole('ADMIN','OPERADOR')")
    @GetMapping(value = "/export", produces = "text/csv")
    public void exportCsv(@RequestParam(required = false) String status,
                          @RequestParam(required = false) Long patioId,
                          @RequestParam(required = false, defaultValue = ";") String sep,
                          HttpServletResponse resp) throws Exception {

        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
        String filename = "motos_" + ts + ".csv";

        var motos = relatorioService.filtrar(status, patioId);
        var bytes = relatorioService.gerarCsv(motos, sep);

        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("text/csv; charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        resp.getOutputStream().write(bytes);
        resp.getOutputStream().flush();
    }
}
