package com.erp.patrimonio.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LocalTest {

    private int id;
    private String nome;
    private String descricao;

    @BeforeEach
    void configurar() {
        id = 1;
        nome = "Armazém";
        descricao = "Local de armazenamento de produtos";
    }

    @Test
    void deveCriarLocalComDadosValidos() {

        // Arrange & Act
        Local local = new Local(id, nome, descricao);

        // Assert
        assertEquals(id, local.getId());
        assertEquals(nome, local.getNome());
        assertEquals(descricao, local.getDescricao());
    }

    @Test
    void deveLancarExcecaoQuandoNomeForNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Local(id, null, descricao));
    }

    @Test
    void deveLancarExcecaoQuandoDescricaoForNula() {
        assertThrows(IllegalArgumentException.class,
                () -> new Local(id, nome, null));
    }

    @Test
    void deveLancarExcecaoQuandoNomeForVazio() {
        assertThrows(IllegalArgumentException.class,
                () -> new Local(id, "", descricao));
    }

    @Test
    void deveLancarExcecaoQuandoDescricaoForVazia() {
        assertThrows(IllegalArgumentException.class,
                () -> new Local(id, nome, ""));
    }

    @Test
    void deveLancarExcecaoQuandoNomeExcederLimite() {

        String nomeGrande = "A".repeat(101);

        assertThrows(IllegalArgumentException.class,
                () -> new Local(id, nomeGrande, descricao));
    }

    @Test
    void deveLancarExcecaoQuandoDescricaoExcederLimite() {

        String descricaoGrande = "A".repeat(256);

        assertThrows(IllegalArgumentException.class,
                () -> new Local(id, nome, descricaoGrande));
    }
}