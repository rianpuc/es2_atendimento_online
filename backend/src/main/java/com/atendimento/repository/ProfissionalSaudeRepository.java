package com.atendimento.repository;

import com.atendimento.model.Categoria;
import com.atendimento.model.ProfissionalSaude;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProfissionalSaudeRepository extends JpaRepository<ProfissionalSaude, Long> {
    List<ProfissionalSaude> findByCategoria(Categoria categoria);

    List<ProfissionalSaude> findByNomeContainingIgnoreCase(String nome);

    List<ProfissionalSaude> findAllByOrderByNomeAsc();
}
