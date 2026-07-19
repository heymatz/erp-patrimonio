package com.erp.patrimonio.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.erp.patrimonio.exception.ValidacaoException;

class CategoriaTest {

    private static final int id = 1;
    private static final String nome = "Eletrônicos";
    private static final String descricao = "Categoria de produtos eletrônicos";

    private Categoria categoria;

    @BeforeEach
    void configurar() {
        // Arrange & Act
        categoria = new Categoria(id, nome, descricao);
    }

    @Test
    void deveCriarCategoriaComDadosValidos() {
        // Assert
        assertEquals(id, categoria.getId());
        assertEquals(nome, categoria.getNome());
        assertEquals(descricao, categoria.getDescricao());
    }

    @Test
    void deveLancarExcecaoQuandoNomeForNulo() {

        assertThrows(
                ValidacaoException.class,
                () -> new Categoria(id, null, descricao)
        );
    }

    @Test
    void deveLancarExcecaoQuandoDescricaoForNula() {
        assertThrows(
                ValidacaoException.class,
                () -> new Categoria(id, nome, null)
        );
    }

    @Test
    void deveLancarExcecaoQuandoNomeForVazio() {
        assertThrows(
                ValidacaoException.class,
                () -> new Categoria(id, "", descricao)
        );
    }

    @Test
    void deveLancarExcecaoQuandoDescricaoForVazia() {
        assertThrows(
                ValidacaoException.class,
                () -> new Categoria(id, nome, "")
        );
    }

    @Test
    void deveLancarExcecaoQuandoNomeExcederLimite() {

        String nomeExcedente = "A".repeat(101);

        assertThrows(
                ValidacaoException.class,
                () -> new Categoria(id, nomeExcedente, descricao)
        );
    }

    @Test
    void deveLancarExcecaoQuandoDescricaoExcederLimite() {

        String descricaoExcedente = "A".repeat(256);

        assertThrows(
                ValidacaoException.class,
                () -> new Categoria(id, nome, descricaoExcedente)
        );
    }
}
