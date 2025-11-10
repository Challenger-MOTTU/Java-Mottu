package com.motogrid.api.nosql.web;

import com.motogrid.api.nosql.document.MotoDoc;
import com.motogrid.api.nosql.repository.MotoDocRepository;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.context.annotation.Profile; // <-- ADICIONE ESTE IMPORT
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Profile("!prod")
@RestController
@RequestMapping("/api/mongo")
@RequiredArgsConstructor
public class MongoController {

    private final MotoDocRepository repo;
    private final MongoTemplate template;

    @GetMapping("/motos")
    public List<MotoDoc> listar(@RequestParam(defaultValue = "50") int limit) {
        return template.find(new Query().limit(limit), MotoDoc.class);
    }

    @GetMapping("/motos/{placa}")
    public ResponseEntity<MotoDoc> porPlaca(@PathVariable String placa) {
        return repo.findByPlacaIgnoreCase(placa)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // agregação: soma de valores por pátio e tipo (o mesmo que você rodou no mongosh)
    @GetMapping("/stats/por-patio-tipo")
    public List<Document> somaPorPatioTipo() {
        Aggregation agg = newAggregation(
                unwind("movimentacoes"),
                group(fields().and("patio", "$patio").and("tipo", "$movimentacoes.tipo"))
                        .sum("$movimentacoes.valor").as("soma"),
                sort(Sort.by(Sort.Direction.ASC, "_id.patio", "_id.tipo"))
        );
        AggregationResults<Document> result =
                template.aggregate(agg, "motos", Document.class);
        return result.getMappedResults();
    }

    // agregação: total geral (com filtro opcional desde=YYYY-MM-DD)
    @GetMapping("/stats/total")
    public List<Document> totalGeral(@RequestParam(required = false) String desde) {
        MatchOperation match = (desde == null)
                ? match(new Criteria())
                : match(Criteria.where("movimentacoes.data").gte(desde));

        Aggregation agg = newAggregation(
                match,
                unwind("movimentacoes"),
                group().sum("$movimentacoes.valor").as("total")
        );
        return template.aggregate(agg, "motos", Document.class).getMappedResults();
    }

    // amostra de 2 docs (evidência)
    @GetMapping("/sample")
    public List<MotoDoc> sample() {
        return template.find(new Query().limit(2), MotoDoc.class);
    }

    // listar índices da coleção (evidência)
    @GetMapping("/indices")
    public List<Document> indices() {
        return template.getCollection("motos")
                .listIndexes()
                .into(new java.util.ArrayList<>());
    }
}