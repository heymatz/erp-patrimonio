package com.erp.patrimonio.model;

public class Local {

    private static final int MAX_NOME = 100;
    private static final int MAX_DESCRICAO = 255;

    private int id;
    private String nome;
    private String descricao;

    public Local(int id, String nome, String descricao) {
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
            throw new IllegalArgumentException("O nome é obrigatório.");
        }
        if (nome.length() > MAX_NOME) {
            throw new IllegalArgumentException(
                    "O nome deve ter no máximo " + MAX_NOME + " caracteres."
            );
        }

        this.nome = nome;
    }

    public void setDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("A descrição é obrigatória.");
        }
        if (descricao.length() > MAX_DESCRICAO) {
            throw new IllegalArgumentException(
                    "A descrição deve ter no máximo " + MAX_DESCRICAO + " caracteres."
            );
        }

        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return "Local{"
                + "id=" + id
                + ", nome='" + nome + '\''
                + ", descricao='" + descricao + '\''
                + '}';
    }
}
