package com.motogrid.api.web;

import com.motogrid.api.dto.MotoDTO;
import com.motogrid.api.dto.mapper.MotoMapper;
import com.motogrid.api.model.Moto;
import com.motogrid.api.model.StatusMoto;
import com.motogrid.api.service.MotoService;
import com.motogrid.api.service.PatioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

// POI
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
    public void exportXlsx(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long patioId,
            HttpServletResponse resp
    ) throws IOException {

        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
        String filename = "motos_" + ts + ".xlsx";

        resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        List<Moto> motos = filtrar(status, patioId);

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sh = wb.createSheet("Motos");
            Font hFont = wb.createFont();
            hFont.setBold(true);
            hFont.setColor(IndexedColors.WHITE.getIndex());
            CellStyle hStyle = wb.createCellStyle();
            hStyle.setFont(hFont);
            hStyle.setFillForegroundColor(IndexedColors.TEAL.getIndex());
            hStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            hStyle.setAlignment(HorizontalAlignment.CENTER);
            hStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            String[] cols = {"Placa","Modelo","Status","Pátio"};
            Row header = sh.createRow(0);
            for (int c = 0; c < cols.length; c++) {
                Cell cell = header.createCell(c);
                cell.setCellValue(cols[c]);
                cell.setCellStyle(hStyle);
            }

            // Dados
            int r = 1;
            for (Moto m : motos) {
                Row row = sh.createRow(r++);
                row.createCell(0).setCellValue(safe(m.getPlaca()));
                row.createCell(1).setCellValue(safe(m.getModelo()));
                row.createCell(2).setCellValue(legivel(m.getStatus()));
                row.createCell(3).setCellValue(m.getPatio() != null ? safe(m.getPatio().getNome()) : "");
            }

            // Melhorias visuais
            for (int c = 0; c < cols.length; c++) sh.autoSizeColumn(c);
            sh.createFreezePane(0, 1);
            sh.setAutoFilter(new CellRangeAddress(0, 0, 0, cols.length - 1));

            wb.write(resp.getOutputStream());
            resp.getOutputStream().flush();
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','OPERADOR')")
    @GetMapping(value = "/export", produces = "text/csv")
    public void exportCsv(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long patioId,
            @RequestParam(required = false, defaultValue = ";") String sep,
            HttpServletResponse response
    ) throws IOException {

        String separator = ";".equals(sep) ? ";" : ",";
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
        String filename = "motos_" + ts + ".csv";

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        var out = response.getOutputStream();
        out.write(0xEF); out.write(0xBB); out.write(0xBF);

        try (Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
            writer.write("sep=" + separator + "\r\n");
            writer.write(String.join(separator, List.of("Placa","Modelo","Status","Pátio")) + "\r\n");

            List<Moto> motos = filtrar(status, patioId);
            Function<String,String> clean = s -> s == null ? "" : s.replace('~', ' ').trim();
            Function<String,String> esc = s -> {
                s = clean.apply(s);
                boolean asp = s.contains(separator) || s.contains("\"") || s.contains("\n") || s.contains("\r");
                String v = s.replace("\"", "\"\"");
                return asp ? ("\"" + v + "\"") : v;
            };
            for (Moto m : motos) {
                String placa  = esc.apply(m.getPlaca());
                String modelo = esc.apply(m.getModelo());
                String stat   = esc.apply(legivel(m.getStatus()));
                String patio  = esc.apply(m.getPatio() != null ? m.getPatio().getNome() : "");
                writer.write(String.join(separator, List.of(placa, modelo, stat, patio)) + "\r\n");
            }
            writer.flush();
        }
    }

    private List<Moto> filtrar(String status, Long patioId) {
        List<Moto> motos = motoService.listar(Pageable.unpaged()).getContent();
        if (status != null && !status.isBlank()) {
            try {
                StatusMoto st = StatusMoto.valueOf(status.trim().toUpperCase());
                motos = motos.stream().filter(m -> st.equals(m.getStatus())).collect(Collectors.toList());
            } catch (IllegalArgumentException ignored) {}
        }
        if (patioId != null) {
            motos = motos.stream()
                    .filter(m -> m.getPatio() != null && patioId.equals(m.getPatio().getId()))
                    .collect(Collectors.toList());
        }
        return motos;
    }

    private String legivel(StatusMoto st) {
        if (st == null) return "";
        return switch (st) {
            case DISPONIVEL -> "DISPONÍVEL";
            case EM_USO -> "EM USO";
            case EM_MANUTENCAO -> "EM MANUTENÇÃO";
            case INATIVA -> "INATIVA";
        };
    }

    private String safe(String s){ return s == null ? "" : s.replace('~', ' ').trim(); }
}
