package com.erp.patrimonio.model;

import com.erp.patrimonio.exception.ValidacaoException;

public class Patrimonio {

    private static final int MAX_NOME = 100;
    private static final int MAX_DESCRICAO = 255;
    private static final int MAX_NUM_SERIE = 50;

    private final int id; // O ID não muda depois de cadastrado
    private String nome;
    private String descricao;

    private Categoria categoria;
    private Local local;

    private String numeroSerie;
    private double valor;
    private boolean ativo;

    public Patrimonio(
            int id,
            String nome,
            String descricao,
            Categoria categoria,
            Local local,
            String numeroSerie,
            double valor) {

        this.id = id;

        setNome(nome);
        setDescricao(descricao);
        setCategoria(categoria);
        setLocal(local);
        setNumeroSerie(numeroSerie);
        setValor(valor);
        this.ativo = true;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public Local getLocal() {
        return local;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public double getValor() {
        return valor;
    }

    public boolean isAtivo() {
        return ativo;
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

    public void setCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new ValidacaoException(
                    "A categoria é obrigatória."
            );
        }
        this.categoria = categoria;
    }

    public void setLocal(Local local) {
        if (local == null) {
            throw new ValidacaoException(
                    "O local é obrigatório."
            );
        }
        this.local = local;
    }

    public void setNumeroSerie(String numeroSerie) {
        if (numeroSerie == null || numeroSerie.isBlank()) {
            throw new ValidacaoException(
                    "O número de série é obrigatório."
            );
        }

        this.numeroSerie = numeroSerie.trim();

        if (numeroSerie.length() > MAX_NUM_SERIE) {
            throw new ValidacaoException(
                "O número de série deve ter no máximo "
                + MAX_NUM_SERIE + " caracteres."
            );
        } // Limitando o tamanho através de uma constante

        this.numeroSerie = numeroSerie;
    }

    public void setValor(double valor) {
        if (valor <= 0) {
            throw new ValidacaoException(
                    "O valor deve ser maior que zero."
            );
        }
        this.valor = valor;
    }

    public void ativar() {
        this.ativo = true;
    }

    public void desativar() {
        this.ativo = false;
    }

    @Override
    public String toString() {
        return "Patrimonio{"
                + "id=" + id
                + ", nome='" + nome + '\''
                + ", descricao='" + descricao + '\''
                + ", categoria=" + categoria
                + ", local=" + local
                + ", numeroSerie='" + numeroSerie + '\''
                + ", valor=" + valor
                + ", ativo=" + ativo
                + '}';
    }
}
