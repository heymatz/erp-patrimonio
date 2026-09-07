package com.erp.patrimonio.repository;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.erp.patrimonio.exception.ValidacaoException;
import com.erp.patrimonio.infra.ConnectionFactory;
import com.erp.patrimonio.infra.TransactionManager;
import com.erp.patrimonio.model.Categoria;;

public class CategoriaRepositoryTest {

    private CategoriaRepository categoriaRepository;

    private Categoria criarCategoria() {
        return new Categoria(
                1,
                "Eletrônicos",
                "Categoria de produtos eletrônicos");
    }

    @BeforeEach
    public void setUp() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        TransactionManager transactionManager = new TransactionManager(connectionFactory);
        categoriaRepository = new CategoriaRepositoryJdbc(connectionFactory, transactionManager);

        // Limpa a tabela filha primeiro, depois as tabelas pai para respeitar a Foreign
        // Key
        try (Connection conn = connectionFactory.recuperarConexao();
                Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM patrimonios");
            stmt.executeUpdate("DELETE FROM locais");
            stmt.executeUpdate("DELETE FROM categorias");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao limpar banco para os testes: " + e.getMessage());
        }
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
                ValidacaoException.class,
                () -> categoriaRepository.salvar(null));
    }

    @Test
    void deveAtualizarCategoriaExistente() {
        Categoria categoria = criarCategoria();
        categoriaRepository.salvar(categoria);
        int idGerado = categoria.getId(); // Pega o ID real gerado pelo banco

        categoria.setNome("Eletrônicos Atualizados");
        boolean atualizado = categoriaRepository.atualizar(categoria);

        assertTrue(atualizado);
        assertEquals(
                "Eletrônicos Atualizados",
                categoriaRepository.buscarPorId(idGerado).getNome());
    }

    @Test
    void deveRemoverCategoriaExistente() {
        Categoria categoria = criarCategoria();
        categoriaRepository.salvar(categoria);
        int idGerado = categoria.getId(); // Pega o ID real gerado pelo banco

        boolean removido = categoriaRepository.remover(idGerado);

        assertTrue(removido);
        assertEquals(0, categoriaRepository.listarTodos().size());
    }

    @Test
    void deveBuscarCategoriaPorIdExistente() {
        Categoria categoria = criarCategoria();
        categoriaRepository.salvar(categoria);
        int idGerado = categoria.getId(); // Pega o ID real gerado pelo banco

        Categoria encontrada = categoriaRepository.buscarPorId(idGerado);
        assertEquals(categoria, encontrada);
    }

    @Test
    void deveListarTodasAsCategoriasExistentes() {
        Categoria categoria1 = new Categoria(
                1,
                "Eletrônicos",
                "Produtos eletrônicos");

        Categoria categoria2 = new Categoria(
                2,
                "Móveis",
                "Móveis corporativos");

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
