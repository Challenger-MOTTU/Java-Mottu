package com.motogrid.api.controller;

import com.motogrid.api.service.OracleProcService;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/oracle")
public class OracleController {

    private final OracleProcService svc;
    public OracleController(OracleProcService svc){ this.svc = svc; }

    @GetMapping("/validar-placa/{placa}")
    public Map<String,Object> validar(@PathVariable String placa){
        return Map.of("placa", placa, "valido", svc.validarPlaca(placa));
    }

    @GetMapping("/motos/procedure")
    public List<Map<String,Object>> listarPorProcedure(){
        return svc.listarMotosViaProcedure();
    }

    @GetMapping("/resumo/dbms-output")
    public List<String> resumoDbmsOutput(){
        return svc.resumoComDbmsOutput();
    }
}
