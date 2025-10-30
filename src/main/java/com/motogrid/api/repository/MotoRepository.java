package com.motogrid.api.repository;

import com.motogrid.api.model.Moto;
import com.motogrid.api.model.StatusMoto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MotoRepository extends JpaRepository<Moto, Long> {

    Page<Moto> findByStatus(StatusMoto status, Pageable pageable);

    Page<Moto> findByPlacaContainingIgnoreCase(String placa, Pageable pageable);
    List<Moto> findByStatus(StatusMoto status, Sort sort);
    List<Moto> findByPatioId(Long patioId, Sort sort);
    List<Moto> findByStatusAndPatioId(StatusMoto status, Long patioId, Sort sort);
}



