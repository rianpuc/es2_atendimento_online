package com.atendimento.controller;

import com.atendimento.model.ExameLaboratorio;
import com.atendimento.repository.ExameLaboratorioRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exames")
@CrossOrigin(origins = "*")
public class ExameLaboratorioController {

    private final ExameLaboratorioRepository repository;

    public ExameLaboratorioController(ExameLaboratorioRepository repository) {
        this.repository = repository;
    }

    // CREATE - Criar novo exame
    @PostMapping
    public ResponseEntity<ExameLaboratorio> criar(@Valid @RequestBody ExameLaboratorio exame) {
        ExameLaboratorio salvo = repository.save(exame);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    // READ - Listar todos os exames
    @GetMapping
    public ResponseEntity<List<ExameLaboratorio>> listar() {
        List<ExameLaboratorio> exames = repository.findAllByOrderById();
        return ResponseEntity.ok(exames);
    }

    // READ - Buscar exame por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(null));
    }

    // UPDATE - Atualizar exame
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id,
                                       @Valid @RequestBody ExameLaboratorio dados) {
        return repository.findById(id)
                .map(exame -> {
                    exame.setDescricao(dados.getDescricao());
                    exame.setPosologia(dados.getPosologia());
                    return ResponseEntity.ok(repository.save(exame));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE - Remover exame
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        return repository.findById(id)
                .map(exame -> {
                    repository.delete(exame);
                    return ResponseEntity.ok(Map.of("mensagem", "Exame removido com sucesso"));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
