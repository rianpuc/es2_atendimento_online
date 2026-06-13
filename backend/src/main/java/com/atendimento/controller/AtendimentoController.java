package com.atendimento.controller;

import com.atendimento.model.Atendimento;
import com.atendimento.repository.AtendimentoRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/atendimento")
@CrossOrigin(origins = "*")
public class AtendimentoController {

    private final AtendimentoRepository repository;

    public AtendimentoController(AtendimentoRepository repository) {
        this.repository = repository;
    }

    // CREATE - Criar novo atendimento
    @PostMapping
    public ResponseEntity<Atendimento> criar(@Valid @RequestBody Atendimento atendimento) {
        Atendimento salvo = repository.save(atendimento);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    // READ - Listar todos os atendimentos
    @GetMapping
    public ResponseEntity<List<Atendimento>> listar() {
        List<Atendimento> atendimentos = repository.findAllByOrderByDataAscHorarioAsc();
        return ResponseEntity.ok(atendimentos);
    }

    // READ - Buscar atendimento por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(null));
    }

    // UPDATE - Atualizar atendimento
    @PutMapping("/editar/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id,
                                       @Valid @RequestBody Atendimento dados) {
        return repository.findById(id)
                .map(comp -> {
                    comp.setTitulo(dados.getTitulo());
                    comp.setData(dados.getData());
                    comp.setHorario(dados.getHorario());
                    comp.setTitulo(dados.getTitulo());
                    comp.setLink_call(dados.getLink_call());
                    comp.setReceitas(dados.getReceitas());
                    return ResponseEntity.ok(repository.save(comp));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE - Remover atendimento
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        return repository.findById(id)
                .map(comp -> {
                    repository.delete(comp);
                    return ResponseEntity.ok(Map.of("mensagem", "Atendimento removido com sucesso"));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
