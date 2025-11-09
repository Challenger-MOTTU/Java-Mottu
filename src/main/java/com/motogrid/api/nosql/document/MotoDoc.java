package com.motogrid.api.nosql.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Document(collection = "motos")
public class MotoDoc {

    @Id
    private String id;

    @Field("id_moto")           // <- mantém o nome no Mongo ("id_moto")
    private Integer idMoto;     // <- nome Java em camelCase (recomendado)

    @Indexed(unique = true)
    private String placa;

    private String modelo;
    private String cor;
    private Integer ano;
    private String patio;

    private List<Mov> movimentacoes;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Mov {
        private String tipo;       // ENTRADA/SAIDA/TRANSFERENCIA
        private String data;       // "YYYY-MM-DD" (string para casar com o import)
        private BigDecimal valor;
        private String funcionario;
    }
}
