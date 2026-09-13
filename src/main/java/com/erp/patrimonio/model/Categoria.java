package com.erp.patrimonio.model;

import com.erp.patrimonio.enums.TipoItem;
import com.erp.patrimonio.exception.ValidacaoException;

public class Categoria {

    private static final int MAX_NOME = 100;
    private static final int MAX_DESCRICAO = 255;

    private int id;
    private String nome;
    private String descricao;
    private TipoItem tipoItem;

    // --- Retrocompatibilidade com versões antigas do sistema ---
    public Categoria(int id, String nome, String descricao, TipoItem tipoItem) {
        this.id = id;
        this.setNome(nome);
        this.setDescricao(descricao);
        this.tipoItem = (tipoItem != null) ? tipoItem : TipoItem.PATRIMONIO;
    }

    // Seta o tipoItem como PATRIMONIO por padrão para evitar problemas de quebra de
    // código em versões antigas do sistema que não passavam esse parâmetro
    public Categoria(int id, String nome, String descricao) {
        this(id, nome, descricao, TipoItem.PATRIMONIO);
    }

    // Construtor vazio para frameworks que necessitam de um construtor sem argumentos
    public Categoria() {
    }
    // --- Fim da retrocompatibilidade ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoItem getTipoItem() {
        return tipoItem;
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new ValidacaoException("O nome é obrigatório.");
        }

        nome = nome.trim();

        if (nome.length() > MAX_NOME) {
            throw new ValidacaoException(
                    "O nome deve ter no máximo " + MAX_NOME + " caracteres.");
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
                    "A descrição deve ter no máximo " + MAX_DESCRICAO + " caracteres.");
        }

        this.descricao = descricao;
    }

    public void setTipoItem(TipoItem tipoItem) {
        if (tipoItem == null) {
            throw new ValidacaoException("O tipo de item é obrigatório.");
        }
        this.tipoItem = tipoItem;
    }

    @Override
    public String toString() {
        return "Categoria{"
                + "id=" + id
                + ", nome='" + nome + '\''
                + ", descricao='" + descricao + '\''
                + '}';
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + ((nome == null) ? 0 : nome.hashCode());
        result = prime * result + ((descricao == null) ? 0 : descricao.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Categoria other = (Categoria) obj;
        if (id != other.id)
            return false;
        if (nome == null) {
            if (other.nome != null)
                return false;
        } else if (!nome.equals(other.nome))
            return false;
        if (descricao == null) {
            if (other.descricao != null)
                return false;
        } else if (!descricao.equals(other.descricao))
            return false;
        return true;
    }
}
