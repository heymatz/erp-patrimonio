package com.erp.patrimonio.model;

public class Patrimonio {

    private static final int MAX_NOME = 100;
    private static final int MAX_DESCRICAO = 255;

    private final int id;

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

    public void setCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException(
                    "A categoria é obrigatória."
            );
        }
        this.categoria = categoria;
    }

    public void setLocal(Local local) {
        if (local == null) {
            throw new IllegalArgumentException(
                    "O local é obrigatório."
            );
        }
        this.local = local;
    }

    public void setNumeroSerie(String numeroSerie) {
        if (numeroSerie == null || numeroSerie.isBlank()) {
            throw new IllegalArgumentException(
                    "O número de série é obrigatório."
            );
        }
        this.numeroSerie = numeroSerie;
    }

    public void setValor(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException(
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
