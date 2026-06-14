package com.atendimento.repository;

import com.atendimento.model.ExameLaboratorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExameLaboratorioRepository extends JpaRepository<ExameLaboratorio, Long> {

    List<ExameLaboratorio> findAllByOrderById();

}
