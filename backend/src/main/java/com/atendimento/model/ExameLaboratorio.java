package com.atendimento.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "exame_laboratorio")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExameLaboratorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String posologia;

    @ManyToOne
    @JoinColumn(name = "atendimento_id")
    private Atendimento atendimento;
}
