package com.atendimento;

import com.atendimento.controller.ProfissionalSaudeController;
import com.atendimento.model.Categoria;
import com.atendimento.model.ProfissionalSaude;
import com.atendimento.repository.ProfissionalSaudeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfissionalSaudeController.class)
class ProfissionalSaudeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfissionalSaudeRepository repository;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        // Não precisamos do JavaTimeModule aqui a menos que ProfissionalSaude
        // tenha atributos de data (como LocalDate ou LocalDateTime).
    }

    @Test
    void deveCriarProfissionalComSucesso() throws Exception {
        // 1. Prepara os dados (Given)
        ProfissionalSaude profissional = new ProfissionalSaude();
        profissional.setId(1L);
        profissional.setNome("Dr. João");
        profissional.setCategoria(Categoria.MEDICO);
        profissional.setTelefone("31999999999");
        profissional.setEndereco("Rua das Flores, 123");

        // 2. Define o comportamento do Mock (When)
        when(repository.save(any(ProfissionalSaude.class))).thenReturn(profissional);

        // 3. Executa a requisição e valida o resultado (Then)
        mockMvc.perform(post("/api/profissional") // Use a rota exata do seu Controller
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profissional)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Dr. João"))
                .andExpect(jsonPath("$.categoria").value("MEDICO"));
    }

    @Test
    void deveListarProfissionaisOrdenados() throws Exception {
        ProfissionalSaude prof1 = new ProfissionalSaude();
        prof1.setId(1L);
        prof1.setNome("Ana Fisioterapeuta");
        prof1.setCategoria(Categoria.FISIOTERAPEUTA);

        ProfissionalSaude prof2 = new ProfissionalSaude();
        prof2.setId(2L);
        prof2.setNome("Dr. Carlos");
        prof2.setCategoria(Categoria.MEDICO);

        // Atenção aqui: o Mock precisa usar o mesmo method que o Controller chama!
        when(repository.findAllByOrderByNomeAsc())
                .thenReturn(Arrays.asList(prof1, prof2));

        mockMvc.perform(get("/api/profissional"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Ana Fisioterapeuta"))
                .andExpect(jsonPath("$[1].nome").value("Dr. Carlos"));
    }

    @Test
    void deveRetornar404ParaProfissionalInexistente() throws Exception {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/profissional/999"))
                .andExpect(status().isNotFound());
    }
}