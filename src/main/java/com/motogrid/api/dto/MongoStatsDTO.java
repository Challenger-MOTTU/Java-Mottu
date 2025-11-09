package com.motogrid.api.mongo.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MongoStatsDTO {
    private String patio;
    private String tipo;          // ENTRADA/SAIDA/TRANSFERENCIA
    private Long qtdeMov;         // quantidade de movimentações
    private BigDecimal somaValor; // soma dos valores
}
