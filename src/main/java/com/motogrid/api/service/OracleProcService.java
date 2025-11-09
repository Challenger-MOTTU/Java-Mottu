package com.motogrid.api.service;

import java.sql.*;
import java.util.*;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

@Service
public class OracleProcService {

    private final JdbcTemplate jt;
    private final SimpleJdbcCall listarMotosRC;

    public OracleProcService(JdbcTemplate oracleJdbcTemplate) {
        this.jt = oracleJdbcTemplate;

        this.listarMotosRC = new SimpleJdbcCall(jt)
                .withCatalogName("PKG_MOTOGRID")      // pacote
                .withProcedureName("LISTAR_MOTOS_RC") // PROCEDURE listar_motos_rc(p_rc OUT SYS_REFCURSOR)
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(new SqlOutParameter("P_RC", Types.REF_CURSOR, new ColumnMapRowMapper()));
    }

    // Exemplo de FUNÇÃO: SELECT pkg_motogrid.validar_placa(?) FROM dual
    public int validarPlaca(String placa) {
        Integer r = jt.queryForObject("select pkg_motogrid.validar_placa(?) from dual", Integer.class, placa);
        return r == null ? 0 : r;
    }

    // Exemplo de PROCEDURE com REF CURSOR
    @SuppressWarnings("unchecked")
    public List<Map<String,Object>> listarMotosViaProcedure() {
        Map<String, Object> out = listarMotosRC.execute(new HashMap<>());
        Object rs = out.get("P_RC"); // algumas versões usam "#result-set-1"
        if (rs == null) rs = out.get("#result-set-1");
        return rs == null ? List.of() : (List<Map<String, Object>>) rs;
    }

    // (Opcional) Capturar DBMS_OUTPUT da procedure resumo_valor_patio_tipo
    public List<String> resumoComDbmsOutput() {
        return jt.execute((ConnectionCallback<List<String>>) con -> {
            List<String> lines = new ArrayList<>();
            try (CallableStatement en = con.prepareCall("{call dbms_output.enable(1000000)}")) { en.execute(); }
            try (CallableStatement c = con.prepareCall("{call pkg_motogrid.resumo_valor_patio_tipo}")) { c.execute(); }
            try (CallableStatement gl = con.prepareCall("{call dbms_output.get_line(?,?)}")) {
                gl.registerOutParameter(1, Types.VARCHAR);
                gl.registerOutParameter(2, Types.INTEGER);
                int status;
                do {
                    gl.execute();
                    status = gl.getInt(2);
                    if (status == 0) lines.add(gl.getString(1));
                } while (status == 0);
            }
            return lines;
        });
    }
}
