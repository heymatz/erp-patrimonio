package com.erp.patrimonio.repository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.erp.patrimonio.model.Categoria;

public class CategoriaRepositoryTest {

    private CategoriaRepository categoriaRepository;

    private Categoria criarCategoria() {
        return new Categoria(
                1,
                "Eletrônicos",
                "Categoria de produtos eletrônicos"
        );
    }

    @BeforeEach
    void setUp() {
        categoriaRepository = new CategoriaRepository();
    }

    @Test
    void deveSalvarCategoriaComDadosValidos() {
        Categoria categoria = criarCategoria();
        categoriaRepository.salvar(categoria);
        assertEquals(1, categoriaRepository.listarTodos().size());
    }

    @Test
    void deveLancarExcecaoQuandoSalvarCategoriaNula() {
        assertThrows(
                IllegalArgumentException.class,
                () -> categoriaRepository.salvar(null)
        );
    }

    @Test
    void deveAtualizarCategoriaExistente() {
        Categoria categoria = criarCategoria();
        categoriaRepository.salvar(categoria);

        categoria.setNome("Eletrônicos Atualizados");

        boolean atualizado = categoriaRepository.atualizar(categoria);

        assertTrue(atualizado);
        assertEquals(
                "Eletrônicos Atualizados",
                categoriaRepository.buscarPorId(1).getNome()
        );
    }

    @Test
    void deveRemoverCategoriaExistente() {
        Categoria categoria = criarCategoria();
        categoriaRepository.salvar(categoria);

        boolean removido = categoriaRepository.remover(1);

        assertTrue(removido);
        assertEquals(0, categoriaRepository.listarTodos().size());
    }

    @Test
    void deveBuscarCategoriaPorIdExistente() {
        Categoria categoria = criarCategoria();
        categoriaRepository.salvar(categoria);

        Categoria encontrada = categoriaRepository.buscarPorId(1);
        assertEquals(categoria, encontrada);
    }

    @Test
    void deveListarTodasAsCategoriasExistentes() {
        Categoria categoria1 = new Categoria(
                1,
                "Eletrônicos",
                "Produtos eletrônicos"
        );

        Categoria categoria2 = new Categoria(
                2,
                "Móveis",
                "Móveis corporativos"
        );

        categoriaRepository.salvar(categoria1);
        categoriaRepository.salvar(categoria2);

        List<Categoria> categorias = categoriaRepository.listarTodos();

        assertEquals(2, categorias.size());
        assertEquals("Eletrônicos", categorias.get(0).getNome());
        assertEquals("Móveis", categorias.get(1).getNome());
    }

    @Test
    void deveRetornarFalseAoRemoverCategoriaInexistente() {
        boolean removido = categoriaRepository.remover(99);

        assertFalse(removido);
    }

    @Test
    void deveRetornarFalseAoAtualizarCategoriaInexistente() {
        Categoria categoria = criarCategoria();

        boolean atualizado = categoriaRepository.atualizar(categoria);

        assertFalse(atualizado);
    }

    @Test
    void deveRetornarNullAoBuscarCategoriaInexistente() {
        Categoria categoria = categoriaRepository.buscarPorId(99);

        assertNull(categoria);
    }
}
