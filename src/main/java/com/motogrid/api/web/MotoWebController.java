package com.motogrid.api.web;

import com.motogrid.api.dto.MotoDTO;
import com.motogrid.api.dto.mapper.MotoMapper;
import com.motogrid.api.model.Moto;
import com.motogrid.api.model.StatusMoto;
import com.motogrid.api.service.MotoService;
import com.motogrid.api.service.PatioService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

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

    @PreAuthorize("hasAnyRole('ADMIN','OPERADOR')") // GET /web/** já está liberado, mas deixo explícito
    @GetMapping(value = "/export", produces = "text/csv")
    public void exportarCsv(HttpServletResponse resp) throws IOException {
        resp.setHeader("Content-Disposition", "attachment; filename=motos.csv");
        resp.setCharacterEncoding("UTF-8");

        try (PrintWriter w = resp.getWriter()) {
            w.println("placa,modelo,status,patioId");
            for (Moto m : motoService.listarTodas()) {
                String status = (m.getStatus() != null) ? m.getStatus().name() : "";
                String patioId = (m.getPatio() != null) ? String.valueOf(m.getPatio().getId()) : "";
                String modeloSafe = (m.getModelo() != null) ? m.getModelo().replace(",", " ") : "";
                w.printf("%s,%s,%s,%s%n", m.getPlaca(), modeloSafe, status, patioId);
            }
            w.flush();
        }
    }
    @GetMapping("/export")
    public void exportCsv(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long patioId,
            @RequestParam(required = false, defaultValue = ",") String sep,
            HttpServletResponse response
    ) throws IOException {
        String separator = ";".equals(sep) ? ";" : ",";
        String ts = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
        String filename = "motos_" + ts + ".csv";

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        var out = response.getOutputStream();
        out.write(0xEF); out.write(0xBB); out.write(0xBF);
        var writer = new java.io.OutputStreamWriter(out, StandardCharsets.UTF_8);
        writer.write("sep=" + separator + "\r\n");
        writer.write(String.join(separator, List.of("Placa","Modelo","Status","Pátio")) + "\r\n");
        java.util.List<com.motogrid.api.model.Moto> motos =
                motoService.listar(org.springframework.data.domain.Pageable.unpaged()).getContent();
        if (status != null && !status.isBlank()) {
            try {
                var st = com.motogrid.api.model.StatusMoto.valueOf(status.toUpperCase());
                motos = motos.stream().filter(m -> st.equals(m.getStatus())).toList();
            } catch (IllegalArgumentException ignore) {
            }
        }
        if (patioId != null) {
            motos = motos.stream()
                    .filter(m -> m.getPatio() != null && patioId.equals(m.getPatio().getId()))
                    .toList();
        }
        java.util.function.Function<String,String> esc = s -> {
            if (s == null) return "";
            boolean precisaAspas = s.contains(separator) || s.contains("\"") || s.contains("\n") || s.contains("\r");
            String v = s.replace("\"", "\"\"");
            return precisaAspas ? ("\"" + v + "\"") : v;
        };

        for (var m : motos) {
            String placa  = esc.apply(m.getPlaca());
            String modelo = esc.apply(m.getModelo());
            String stat   = esc.apply(m.getStatus() != null ? m.getStatus().name() : "");
            String patio  = esc.apply(m.getPatio() != null ? m.getPatio().getNome() : "");

            writer.write(String.join(separator, List.of(placa, modelo, stat, patio)) + "\r\n");
        }

        writer.flush();
    }
}
