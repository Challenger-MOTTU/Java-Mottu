package com.motogrid.api.service;

import com.motogrid.api.model.Moto;
import com.motogrid.api.model.StatusMoto;
import com.motogrid.api.repository.MotoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MotoRelatorioService {

    private final MotoRepository motoRepository;
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.ASC, "placa");
    public List<Moto> filtrar(String statusRaw, Long patioId) {
        var stOpt = StatusMoto.from(statusRaw);
        if (stOpt.isPresent() && patioId != null)
            return motoRepository.findByStatusAndPatioId(stOpt.get(), patioId, DEFAULT_SORT);
        if (stOpt.isPresent())
            return motoRepository.findByStatus(stOpt.get(), DEFAULT_SORT);
        if (patioId != null)
            return motoRepository.findByPatioId(patioId, DEFAULT_SORT);
        return motoRepository.findAll(DEFAULT_SORT);
    }

    public byte[] gerarPlanilha(List<Moto> motos) throws Exception {
        try (var wb = new XSSFWorkbook(); var out = new ByteArrayOutputStream()) {
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
                var cell = header.createCell(c);
                cell.setCellValue(cols[c]);
                cell.setCellStyle(hStyle);
            }

            int r = 1;
            for (Moto m : motos) {
                Row row = sh.createRow(r++);
                row.createCell(0).setCellValue(safe(m.getPlaca()));
                row.createCell(1).setCellValue(safe(m.getModelo()));
                row.createCell(2).setCellValue(m.getStatus() != null ? m.getStatus().getLabel() : "");
                row.createCell(3).setCellValue(m.getPatio() != null ? safe(m.getPatio().getNome()) : "");
            }

            for (int c = 0; c < cols.length; c++) sh.autoSizeColumn(c);
            sh.createFreezePane(0, 1);
            sh.setAutoFilter(new CellRangeAddress(0, 0, 0, cols.length - 1));

            wb.write(out);
            return out.toByteArray();
        }
    }

    public byte[] gerarCsv(List<Moto> motos, String sepRaw) throws Exception {
        String sep = ";".equals(sepRaw) ? ";" : ",";
        try (var out = new ByteArrayOutputStream();
             Writer w = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {

            // BOM + sep= (Excel friendly)
            out.write(0xEF); out.write(0xBB); out.write(0xBF);
            w.write("sep=" + sep + "\r\n");
            w.write(String.join(sep, List.of("Placa","Modelo","Status","Pátio")) + "\r\n");

            for (Moto m : motos) {
                String placa  = esc(safe(m.getPlaca()), sep);
                String modelo = esc(safe(m.getModelo()), sep);
                String status = esc(m.getStatus() != null ? m.getStatus().getLabel() : "", sep);
                String patio  = esc(m.getPatio() != null ? safe(m.getPatio().getNome()) : "", sep);
                w.write(String.join(sep, List.of(placa, modelo, status, patio)) + "\r\n");
            }
            w.flush();
            return out.toByteArray();
        }
    }

    private static String safe(String s) { return s == null ? "" : s.replace('~', ' ').trim(); }
    private static String esc(String s, String sep) {
        boolean asp = s.contains(sep) || s.contains("\"") || s.contains("\n") || s.contains("\r");
        String v = s.replace("\"", "\"\"");
        return asp ? ("\"" + v + "\"") : v;
    }
}
