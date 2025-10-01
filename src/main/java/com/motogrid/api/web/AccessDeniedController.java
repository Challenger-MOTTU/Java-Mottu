package com.motogrid.api.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccessDeniedController {
    @GetMapping("/acesso-negado")
    public String acessoNegado() { return "access-denied"; }
}
