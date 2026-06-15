package com.atendimento;

import com.atendimento.model.Contato;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TESTES DE INTEGRAÇÃO
 * Usa @SpringBootTest para carregar todo o contexto da aplicação
 * Testa a integração real entre Controller → Service → Repository → Banco
 * No CI, roda com PostgreSQL real via container
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class IntegracaoTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void deveExecutarFluxoCompletoProfissionalSaude() throws Exception {

        String json = """
        {
            "nome": "Dr. João",
            "telefone": "31999999999",
            "endereco": "Rua A",
            "categoria": "MEDICO"
        }
        """;

        MvcResult result = mockMvc.perform(post("/api/profissional")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Dr. João"))
                .andReturn();

        Long id = objectMapper.readTree(
                        result.getResponse().getContentAsString())
                .get("id")
                .asLong();

        mockMvc.perform(get("/api/profissional/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Dr. João"));

        String updateJson = """
        {
            "nome": "Dr. João Silva",
            "telefone": "31999999999",
            "endereco": "Rua B",
            "categoria": "MEDICO"
        }
        """;

        mockMvc.perform(put("/api/profissional/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Dr. João Silva"));

        mockMvc.perform(delete("/api/profissional/" + id))
                .andExpect(status().isOk());
    }

    @Test
    void deveCriarAtendimentoComProfissional() throws Exception {

        String profissionalJson = """
        {
            "nome": "Dr. Carlos",
            "telefone": "31988888888",
            "endereco": "Rua Central",
            "categoria": "MEDICO"
        }
        """;

        MvcResult profissionalResult =
                mockMvc.perform(post("/api/profissional")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(profissionalJson))
                        .andExpect(status().isCreated())
                        .andReturn();

        Long profissionalId = objectMapper.readTree(
                        profissionalResult.getResponse().getContentAsString())
                .get("id")
                .asLong();

        String atendimentoJson = String.format("""
        {
            "titulo": "Consulta",
            "data": "2026-06-15",
            "horario": "14:00:00",
            "link_call": "https://meet.test",
            "receitas": ["Dipirona"],
            "profissionalSaude": {
                "id": %d
            }
        }
        """, profissionalId);

        mockMvc.perform(post("/api/atendimento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(atendimentoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Consulta"))
                .andExpect(jsonPath("$.profissionalSaude.id").value(profissionalId));
    }

    @Test
    void deveCriarAtendimentoComExames() throws Exception {

        String profissionalJson = """
        {
            "nome": "Dra. Ana",
            "telefone": "31977777777",
            "endereco": "Rua Teste",
            "categoria": "MEDICO"
        }
        """;

        MvcResult profissionalResult =
                mockMvc.perform(post("/api/profissional")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(profissionalJson))
                        .andExpect(status().isCreated())
                        .andReturn();

        Long profissionalId = objectMapper.readTree(
                        profissionalResult.getResponse().getContentAsString())
                .get("id")
                .asLong();

        String atendimentoJson = String.format("""
        {
            "titulo": "Consulta Exames",
            "data": "2026-06-15",
            "horario": "10:00:00",
            "link_call": "https://meet.test",
            "receitas": ["Paracetamol"],
            "profissionalSaude": {
                "id": %d
            },
            "exames": [
                {
                    "descricao": "Hemograma",
                    "posologia": "Realizar em jejum"
                },
                {
                    "descricao": "Glicemia",
                    "posologia": "8 horas de jejum"
                }
            ]
        }
        """, profissionalId);

        mockMvc.perform(post("/api/atendimento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(atendimentoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exames.length()").value(2))
                .andExpect(jsonPath("$.exames[0].descricao").value("Hemograma"));
    }
}