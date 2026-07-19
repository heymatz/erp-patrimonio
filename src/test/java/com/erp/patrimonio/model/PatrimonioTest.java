package com.erp.patrimonio.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.erp.patrimonio.exception.ValidacaoException;

class PatrimonioTest {

    private int id;
    private String nome;
    private String descricao;
    private Categoria categoria;
    private Local local;
    private String numeroSerie;
    private double valor;

    @BeforeEach
    void configurar() {
        id = 1;
        nome = "Notebook";
        descricao = "Notebook Dell";
        categoria = new Categoria(1, "Eletrônicos", "Equipamentos");
        local = new Local(1, "Sala 101", "Primeiro andar");
        numeroSerie = "SN123456";
        valor = 5000.00;
    }

    @Test
    void deveCriarPatrimonioComDadosValidos() {

        // Arrange & Act
        Patrimonio patrimonio = new Patrimonio(
                id,
                nome,
                descricao,
                categoria,
                local,
                numeroSerie,
                valor
        );

        // Assert
        assertEquals(id, patrimonio.getId());
        assertEquals(nome, patrimonio.getNome());
        assertEquals(descricao, patrimonio.getDescricao());
        assertEquals(categoria, patrimonio.getCategoria());
        assertEquals(local, patrimonio.getLocal());
        assertEquals(numeroSerie, patrimonio.getNumeroSerie());
        assertEquals(valor, patrimonio.getValor());
        assertTrue(patrimonio.isAtivo());
    }

    @Test
    void deveLancarExcecaoQuandoNomeForNulo() {

        assertThrows(ValidacaoException.class,
                () -> new Patrimonio(
                        id,
                        null,
                        descricao,
                        categoria,
                        local,
                        numeroSerie,
                        valor));
    }

    @Test
    void deveLancarExcecaoQuandoNomeForVazio() {

        assertThrows(ValidacaoException.class,
                () -> new Patrimonio(
                        id,
                        "",
                        descricao,
                        categoria,
                        local,
                        numeroSerie,
                        valor));
    }

    @Test
    void deveLancarExcecaoQuandoNomeExcederLimite() {

        String nomeGrande = "A".repeat(101);

        assertThrows(ValidacaoException.class,
                () -> new Patrimonio(
                        id,
                        nomeGrande,
                        descricao,
                        categoria,
                        local,
                        numeroSerie,
                        valor));
    }

    @Test
    void deveLancarExcecaoQuandoDescricaoForNula() {

        assertThrows(ValidacaoException.class,
                () -> new Patrimonio(
                        id,
                        nome,
                        null,
                        categoria,
                        local,
                        numeroSerie,
                        valor));
    }

    @Test
    void deveLancarExcecaoQuandoDescricaoForVazia() {

        assertThrows(ValidacaoException.class,
                () -> new Patrimonio(
                        id,
                        nome,
                        "",
                        categoria,
                        local,
                        numeroSerie,
                        valor));
    }

    @Test
    void deveLancarExcecaoQuandoDescricaoExcederLimite() {

        String descricaoGrande = "A".repeat(256);

        assertThrows(ValidacaoException.class,
                () -> new Patrimonio(
                        id,
                        nome,
                        descricaoGrande,
                        categoria,
                        local,
                        numeroSerie,
                        valor));
    }

    @Test
    void deveAtualizarValor() {

        Patrimonio patrimonio = new Patrimonio(
                id,
                nome,
                descricao,
                categoria,
                local,
                numeroSerie,
                valor);

        patrimonio.setValor(7500);

        assertEquals(7500, patrimonio.getValor());
    }

    @Test
    void deveLancarExcecaoQuandoValorForNegativo() {

        Patrimonio patrimonio = new Patrimonio(
                id,
                nome,
                descricao,
                categoria,
                local,
                numeroSerie,
                valor);

        assertThrows(ValidacaoException.class,
                () -> patrimonio.setValor(-1));
    }

    @Test
    void deveDesativarEAtivarPatrimonio() {

        Patrimonio patrimonio = new Patrimonio(
                id,
                nome,
                descricao,
                categoria,
                local,
                numeroSerie,
                valor);

        patrimonio.desativar();
        assertFalse(patrimonio.isAtivo());

        patrimonio.ativar();
        assertTrue(patrimonio.isAtivo());
    }
}