package com.atendimento.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "atendimento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Atendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Data é obrigatória")
    private LocalDate data;

    @NotNull(message = "Horario eh obrigatorio")
    private LocalTime horario;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String titulo;

    @Column(name = "link_call", nullable = false)
    private String link_call;

    @ElementCollection
    private List<String> receitas = new ArrayList<>();
}
