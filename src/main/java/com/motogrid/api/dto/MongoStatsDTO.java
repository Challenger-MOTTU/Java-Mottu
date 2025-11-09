package com.motogrid.api.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MongoStatsDTO {
    private String patio;
    private String tipo;
    private Long qtdeMov;
    private BigDecimal somaValor;
}
