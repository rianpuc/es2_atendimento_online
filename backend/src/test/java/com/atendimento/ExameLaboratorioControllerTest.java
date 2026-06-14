package com.atendimento;

import com.atendimento.controller.ExameLaboratorioController;
import com.atendimento.model.ExameLaboratorio;
import com.atendimento.repository.ExameLaboratorioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * TESTES UNITÁRIOS - Exames (DEV 3 - Rafael)
 * Usa @WebMvcTest para testar apenas o controller isoladamente
 */
@WebMvcTest(ExameLaboratorioController.class)
class ExameLaboratorioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExameLaboratorioRepository repository;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void deveCriarExameComSucesso() throws Exception {
        ExameLaboratorio exame = new ExameLaboratorio();
        exame.setId(1L);
        exame.setDescricao("Hemograma Completo");
        exame.setPosologia("Jejum de 8 horas antes da coleta.");

        when(repository.save(any(ExameLaboratorio.class))).thenReturn(exame);

        mockMvc.perform(post("/api/exames")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(exame)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.descricao").value("Hemograma Completo"));
    }

    @Test
    void deveListarExamesOrdenados() throws Exception {
        ExameLaboratorio exame1 = new ExameLaboratorio();
        exame1.setId(1L);
        exame1.setDescricao("Hemograma Completo");
        exame1.setPosologia("Jejum de 8 horas antes da coleta.");

        ExameLaboratorio exame2 = new ExameLaboratorio();
        exame2.setId(2L);
        exame2.setDescricao("Glicemia em Jejum");
        exame2.setPosologia("Realizar após jejum mínimo de 8 horas.");

        when(repository.findAllByOrderById())
                .thenReturn(Arrays.asList(exame1, exame2));

        mockMvc.perform(get("/api/exames"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].descricao").value("Hemograma Completo"))
                .andExpect(jsonPath("$[1].descricao").value("Glicemia em Jejum"));
    }

    @Test
    void deveRetornar404ParaExameInexistente() throws Exception {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/exames/999"))
                .andExpect(status().isNotFound());
    }
}
