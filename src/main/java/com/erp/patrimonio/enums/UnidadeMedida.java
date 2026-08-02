package com.erp.patrimonio.enums;

import com.erp.patrimonio.exception.ValidacaoException;

public enum UnidadeMedida {

    UNIDADE("un"),
    METRO("m"),
    CENTIMETRO("cm"),
    QUILOGRAMA("kg"),
    GRAMA("g"),
    LITRO("L"),
    MILILITRO("mL"),
    CAIXA("cx"),
    PACOTE("pct"),
    ROLO("rolo"),
    CONE("cone"),
    BOBINA("bobina"),
    FARDO("fd");

    private final String sigla;

    UnidadeMedida(String sigla) {
        this.sigla = sigla;
    }

    public String getSigla() {
        return sigla;
    }

    @Override
    public String toString() {
        return sigla;
    }

    public static UnidadeMedida fromString(String valor) {

        if (valor == null || valor.isBlank()) {
            throw new ValidacaoException("A unidade de medida é obrigatória.");
        }

        valor = valor.trim();

        for (UnidadeMedida unidade : values()) {
            if (unidade.name().equalsIgnoreCase(valor)
                    || unidade.getSigla().equalsIgnoreCase(valor)) {
                return unidade;
            }
        }

        throw new ValidacaoException("Unidade de medida inválida.");
    }
}
