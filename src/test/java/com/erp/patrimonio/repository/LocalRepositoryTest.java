package com.erp.patrimonio.repository;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.erp.patrimonio.infra.ConnectionFactory;
import com.erp.patrimonio.infra.TransactionManager;
import com.erp.patrimonio.model.Local;

public class LocalRepositoryTest {

    private LocalRepository localRepository;
    private ConnectionFactory connectionFactory;
    private TransactionManager transactionManager;

    @BeforeEach
    public void setUp() {
        connectionFactory = new ConnectionFactory();
        transactionManager = new TransactionManager(connectionFactory);
        localRepository = new LocalRepositoryJdbc(connectionFactory, transactionManager);

        // Limpa as tabelas respeitando a Foreign Key (filhos primeiro)
        try (Connection conn = connectionFactory.recuperarConexao();
                Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM patrimonios");
            stmt.executeUpdate("DELETE FROM locais");
            stmt.executeUpdate("DELETE FROM categorias");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao limpar banco para os testes de local: " + e.getMessage());
        }
    }

    private Local criarLocalPadrao() {
        return new Local(0, "Sala de TI", "Sala destinada aos equipamentos de informática");
    }

    @Test
    void deveSalvarLocalComSucesso() {
        Local local = criarLocalPadrao();
        localRepository.salvar(local);

        assertTrue(local.getId() > 0, "O ID do local deveria ser gerado pelo banco.");

        Local salvo = localRepository.buscarPorId(local.getId());
        assertNotNull(salvo);
        assertEquals("Sala de TI", salvo.getNome());
    }

    @Test
    void deveBuscarLocalPorIdExistente() {
        Local local = criarLocalPadrao();
        localRepository.salvar(local);

        Local encontrado = localRepository.buscarPorId(local.getId());
        assertNotNull(encontrado);
        assertEquals(local.getNome(), encontrado.getNome());
    }

    @Test
    void deveRetornarNullAoBuscarLocalInexistente() {
        Local encontrado = localRepository.buscarPorId(9999);
        assertNull(encontrado);
    }

    @Test
    void deveAtualizarLocalExistente() {
        Local local = criarLocalPadrao();
        localRepository.salvar(local);

        local.setNome("Sala de Servidores");
        boolean atualizado = localRepository.atualizar(local);

        assertTrue(atualizado);
        assertEquals("Sala de Servidores", localRepository.buscarPorId(local.getId()).getNome());
    }

    @Test
    void deveRemoverLocalExistente() {
        Local local = criarLocalPadrao();
        localRepository.salvar(local);

        boolean removido = localRepository.remover(local.getId());

        assertTrue(removido);
        assertNull(localRepository.buscarPorId(local.getId()));
    }

    @Test
    void deveListarTodosOsLocais() {
        localRepository.salvar(new Local(0, "Local A", "Desc A"));
        localRepository.salvar(new Local(0, "Local B", "Desc B"));

        List<Local> locais = localRepository.listarTodos();

        assertEquals(2, locais.size());
    }
}
