package com.finanfe_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoriaDTO {

    private Long id;

    @NotBlank(message = "Nome da categoria é obrigatório")
    @Size(max = 100)
    private String nome;

    @Size(max = 255)
    private String descricao;

    private String icone;

    // Constructors
    public CategoriaDTO() {}

    public CategoriaDTO(Long id, String nome, String descricao, String icone) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.icone = icone;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getIcone() { return icone; }
    public void setIcone(String icone) { this.icone = icone; }
}

