package com.atendimento.controller;

import com.atendimento.model.ProfissionalSaude;
import com.atendimento.repository.ProfissionalSaudeRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profissional")
@CrossOrigin(origins = "*")
public class ProfissionalSaudeController {
    private final ProfissionalSaudeRepository repository;

    public ProfissionalSaudeController(ProfissionalSaudeRepository repository) { this.repository = repository; }

    // CREATE - Criar novo profissional
    @PostMapping
    public ResponseEntity<ProfissionalSaude> criar(@Valid @RequestBody ProfissionalSaude profissionalSaude) {
        ProfissionalSaude salvo = repository.save(profissionalSaude);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    // READ - Listar todos
    @GetMapping
    public ResponseEntity<List<ProfissionalSaude>> listar() {
        List<ProfissionalSaude> profissionais = repository.findAllByOrderByNomeAsc();
        return ResponseEntity.ok(profissionais);
    }

    // READ - Buscar profissional por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE - Atualizar profissional
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id,
                                       @Valid @RequestBody ProfissionalSaude dados) {
        return repository.findById(id)
                .map(profissional -> {
                    profissional.setNome(dados.getNome());
                    profissional.setEndereco(dados.getEndereco());
                    profissional.setTelefone(dados.getTelefone());
                    profissional.setCategoria(dados.getCategoria());
                    return ResponseEntity.ok(repository.save(profissional));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE - Remover profissional
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        return repository.findById(id)
                .map(profissional -> {
                    repository.delete(profissional);
                    return ResponseEntity.ok(Map.of("mensagem", "Profissional removido com sucesso"));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
