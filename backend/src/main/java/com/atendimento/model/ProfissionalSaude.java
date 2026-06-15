package com.atendimento.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "profissionalsaude")
@NoArgsConstructor
@AllArgsConstructor
public class ProfissionalSaude {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Column(length = 100, nullable = false)
    private String nome;

    @Column(length = 20)
    private String telefone;

    @Column(length = 200)
    private String endereco;

    @NotNull(message = "A categoria é obrigatória")
    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private Categoria categoria;

    @OneToMany(mappedBy = "profissionalSaude")
    @JsonIgnore
    private List<Atendimento> atendimentos = new ArrayList<>();
}
