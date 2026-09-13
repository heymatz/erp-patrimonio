package com.erp.patrimonio.enums;

public enum TipoItem {
    PATRIMONIO("Patrimônio (Bem Durável)"),
    ESTOQUE("Estoque (Item de Consumo/Reposição)");

    private final String descricao;

    TipoItem(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
