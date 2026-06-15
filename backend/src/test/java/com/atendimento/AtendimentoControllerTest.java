package com.atendimento;

import com.atendimento.controller.AtendimentoController;
import com.atendimento.model.Atendimento;
import com.atendimento.repository.AtendimentoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TESTES UNITÁRIOS - Atendimentos (DEV 2 - Rian)
 * Usa @WebMvcTest para testar apenas o controller isoladamente
 */
@WebMvcTest(AtendimentoController.class)
class AtendimentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AtendimentoRepository repository;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void deveCriarAtendimentoComSucesso() throws Exception {
        Atendimento comp = new Atendimento();
        comp.setId(1L);
        comp.setTitulo("Atendimento com Paciente");
        comp.setData(LocalDate.of(2026, 6, 13));
        comp.setHorario(LocalTime.of(15, 30));
        comp.setLink_call("https://www.meet.com/123");

        when(repository.save(any(Atendimento.class))).thenReturn(comp);

        mockMvc.perform(post("/api/atendimento")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comp)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Atendimento com Paciente"));
    }

    @Test
    void deveListarAtendimentoOrdenados() throws Exception {
        Atendimento comp1 = new Atendimento();
        comp1.setId(1L);
        comp1.setTitulo("Atendimento hoje");
        comp1.setData(LocalDate.of(2026, 6, 13));

        Atendimento comp2 = new Atendimento();
        comp2.setId(2L);
        comp2.setTitulo("Atendimento amanha");
        comp2.setData(LocalDate.of(2026, 6, 14));

        when(repository.findAllByOrderByDataAscHorarioAsc())
                .thenReturn(Arrays.asList(comp1, comp2));

        mockMvc.perform(get("/api/atendimento"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Atendimento hoje"))
                .andExpect(jsonPath("$[1].titulo").value("Atendimento amanha"));
    }

    @Test
    void deveRetornar404ParaCompromissoInexistente() throws Exception {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/atendimento/11113333"))
                .andExpect(status().isNotFound());
    }
}
