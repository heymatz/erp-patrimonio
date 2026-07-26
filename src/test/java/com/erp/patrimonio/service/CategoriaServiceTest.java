package com.erp.patrimonio.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.erp.patrimonio.exception.DuplicidadeException;
import com.erp.patrimonio.exception.EntidadeNaoEncontradaException;
import com.erp.patrimonio.exception.ValidacaoException;
import com.erp.patrimonio.model.Categoria;
import com.erp.patrimonio.repository.CategoriaRepository;

class CategoriaServiceTest {

    private CategoriaRepository categoriaRepository;
    private CategoriaService categoriaService;

    @BeforeEach
    void setUp() {
        categoriaRepository = new CategoriaRepository();
        categoriaService = new CategoriaService(categoriaRepository);
    }

    @Test
    void deveCadastrarCategoriaComSucesso() {

        Categoria categoria = categoriaService.cadastrar(
                "Eletrônicos",
                "Categoria de produtos eletrônicos"
        );

        assertEquals(1, categoria.getId());
        assertEquals("Eletrônicos", categoria.getNome());
        assertEquals(
                "Categoria de produtos eletrônicos",
                categoria.getDescricao()
        );

        assertEquals(1, categoriaRepository.listarTodos().size());
    }

    @Test
    void naoDeveCadastrarCategoriaDuplicada() {

        // Arrange
        categoriaService.cadastrar(
                "Eletrônicos",
                "Categoria de produtos eletrônicos"
        );

        // Act + Assert
        assertThrows(
                DuplicidadeException.class,
                () -> categoriaService.cadastrar(
                        "Eletrônicos",
                        "Categoria de produtos eletrônicos"
                )
        );
    }

    @Test
    void naoDeveCadastrarCategoriaComNomeMaiorQue100Caracteres() {

        String nome = "A".repeat(101);

        assertThrows(ValidacaoException.class,
                () -> categoriaService.cadastrar(
                        nome,
                        "Categoria de produtos eletrônicos"
                )
        );
    }

    @Test
    void naoDeveCadastrarCategoriaComNomeVazio() {
        assertThrows(
                ValidacaoException.class,
                () -> categoriaService.cadastrar(
                        "",
                        "Categoria de produtos eletrônicos"
                )
        );
    }

    @Test
    void naoDeveCadastrarCategoriaComNomeEmBranco() {
        assertThrows(
                ValidacaoException.class,
                () -> categoriaService.cadastrar(
                        " ",
                        "Categoria de produtos eletrônicos"
                )
        );
    }

    @Test
    void naoDeveCadastrarCategoriaComNomeNulo() {
        assertThrows(
                ValidacaoException.class,
                () -> categoriaService.cadastrar(
                        null,
                        "Categoria de produtos eletrônicos"
                )
        );
    }

    @Test
    void naoDeveCadastrarCategoriaComDescricaoMaiorQue255Caracteres() {

        String descricao = "A".repeat(256);

        assertThrows(
                ValidacaoException.class,
                () -> categoriaService.cadastrar(
                        "Eletrônicos",
                        descricao
                )
        );
    }

    @Test
    void naoDeveCadastrarCategoriaComDescricaoVazia() {
        assertThrows(
                ValidacaoException.class,
                () -> categoriaService.cadastrar(
                        "Eletrônicos",
                        ""
                )
        );
    }

    @Test
    void naoDeveCadastrarCategoriaComDescricaoEmBranco() {
        assertThrows(
                ValidacaoException.class,
                () -> categoriaService.cadastrar(
                        "Eletrônicos",
                        " "
                )
        );
    }

    @Test
    void naoDeveCadastrarCategoriaComDescricaoNula() {
        assertThrows(
                ValidacaoException.class,
                () -> categoriaService.cadastrar(
                        "Eletrônicos",
                        null
                )
        );
    }

    @Test
    void deveBuscarCategoriaPorId() {

        Categoria categoria = categoriaService.cadastrar(
                "Eletrônicos",
                "Categoria de produtos eletrônicos"
        );

        Categoria encontrada = categoriaService.buscarPorId(categoria.getId());

        assertEquals(categoria.getId(), encontrada.getId());
        assertEquals("Eletrônicos", encontrada.getNome());
    }

    @Test
    void naoDeveBuscarCategoriaInexistente() {
        assertThrows(
                EntidadeNaoEncontradaException.class,
                () -> categoriaService.buscarPorId(999)
        );
    }

    @Test
    void deveAtualizarCategoriaComSucesso() {

        Categoria criada = categoriaService.cadastrar(
                "Eletrônicos",
                "Categoria de produtos eletrônicos"
        );

        Categoria categoria = categoriaService.atualizar(
                criada.getId(),
                "Móveis",
                "Categoria de móveis planejados"
        );

        assertEquals(criada.getId(), categoria.getId());
        assertEquals("Móveis", categoria.getNome());
        assertEquals(
                "Categoria de móveis planejados",
                categoria.getDescricao()
        );
    }

    @Test
    void naoDeveAtualizarCategoriaDuplicada() {

        Categoria categoria1 = categoriaService.cadastrar(
                "Eletrônicos",
                "Categoria de produtos eletrônicos"
        );

        Categoria categoria2 = categoriaService.cadastrar(
                "Móveis",
                "Categoria de móveis"
        );

        assertThrows(
                DuplicidadeException.class,
                () -> categoriaService.atualizar(
                        categoria2.getId(),
                        "Eletrônicos",
                        "Categoria de produtos eletrônicos"
                )
        );
    }

    @Test
    void naoDeveAtualizarCategoriaInexistente() {
        assertThrows(
                EntidadeNaoEncontradaException.class,
                () -> categoriaService.atualizar(
                        999,
                        "Informática",
                        "Categoria de produtos de informática"
                )
        );
    }

    @Test
    void deveRemoverCategoriaComSucesso() {
        Categoria categoria = categoriaService.cadastrar(
                "Eletrônicos",
                "Categoria de produtos eletrônicos"
        );

        categoriaService.remover(categoria.getId());

        assertThrows(
                EntidadeNaoEncontradaException.class,
                () -> categoriaService.buscarPorId(categoria.getId())
        );

    }

    @Test
    void naoDeveRemoverCategoriaInexistente() {
        assertThrows(EntidadeNaoEncontradaException.class,
                () -> categoriaService.remover(999)
        );
    }

    @Test
    void deveListarCategorias() {

        categoriaService.cadastrar(
                "Eletrônicos",
                "Categoria de produtos eletrônicos"
        );

        categoriaService.cadastrar(
                "Móveis",
                "Categoria de móveis"
        );

        assertEquals(2, categoriaService.listarTodos().size());
    }
}
