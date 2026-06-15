package com.atendimento.repository;

import com.atendimento.model.Atendimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {

    List<Atendimento> findAllByOrderByDataAscHorarioAsc();
    List<Atendimento> findByProfissionalSaudeId(Long id);

}
