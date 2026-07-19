package com.erp.patrimonio.model;

import com.erp.patrimonio.exception.ValidacaoException;

public class Categoria {

    private static final int MAX_NOME = 100;
    private static final int MAX_DESCRICAO = 255;

    private final int id; // O ID não muda depois de cadastrado
    private String nome;
    private String descricao;

    public Categoria(int id, String nome, String descricao) {
        this.id = id;
        setNome(nome);
        setDescricao(descricao);
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new ValidacaoException("O nome é obrigatório.");
        }

        nome = nome.trim(); 

        if (nome.length() > MAX_NOME) {
            throw new ValidacaoException(
                    "O nome deve ter no máximo " + MAX_NOME + " caracteres."
            );
        }

        this.nome = nome;
    }

    public void setDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            throw new ValidacaoException("A descrição é obrigatória.");
        }

        descricao = descricao.trim();

        if (descricao.length() > MAX_DESCRICAO) {
            throw new ValidacaoException(
                    "A descrição deve ter no máximo " + MAX_DESCRICAO + " caracteres."
            );
        }

        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return "Categoria{"
                + "id=" + id
                + ", nome='" + nome + '\''
                + ", descricao='" + descricao + '\''
                + '}';
    }
}
