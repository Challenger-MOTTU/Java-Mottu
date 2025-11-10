package com.motogrid.api.nosql.repository;

import com.motogrid.api.nosql.document.MotoDoc;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

@Profile("!prod") // <-- ADICIONE ESTA ANOTAÇÃO
public interface MotoDocRepository extends MongoRepository<MotoDoc, String> {

    Optional<MotoDoc> findByPlaca(String placa);

    // ✅ adiciona este (é o que seu controller usa)
    Optional<MotoDoc> findByPlacaIgnoreCase(String placa);

    // paginação
    Page<MotoDoc> findAll(Pageable pageable);

    // top 50 ordenado por idMoto (campo Java em camelCase)
    List<MotoDoc> findTop50ByOrderByIdMotoAsc();

    // (opcional) busca parcial por placa, ignorando caixa
    List<MotoDoc> findByPlacaContainingIgnoreCase(String trecho);
}