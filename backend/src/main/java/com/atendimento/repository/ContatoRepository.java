package com.atendimento.repository;

import com.atendimento.model.Contato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContatoRepository extends JpaRepository<Contato, Long> {

    List<Contato> findAllByOrderByNomeAsc();

    List<Contato> findByNomeContainingIgnoreCase(String nome);
}
