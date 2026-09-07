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

import com.erp.patrimonio.enums.UnidadeMedida;
import com.erp.patrimonio.infra.ConnectionFactory;
import com.erp.patrimonio.infra.TransactionManager;
import com.erp.patrimonio.model.Categoria;
import com.erp.patrimonio.model.Local;
import com.erp.patrimonio.model.Patrimonio;

public class PatrimonioRepositoryTest {

    private PatrimonioRepository patrimonioRepository;
    private CategoriaRepository categoriaRepository;
    private LocalRepository localRepository;
    private ConnectionFactory connectionFactory;
    private TransactionManager transactionManager;

    @BeforeEach
    public void setUp() {
        connectionFactory = new ConnectionFactory();
        transactionManager = new TransactionManager(connectionFactory);

        patrimonioRepository = new PatrimonioRepositoryJdbc(connectionFactory, transactionManager);
        categoriaRepository = new CategoriaRepositoryJdbc(connectionFactory, transactionManager);
        localRepository = new LocalRepositoryJdbc(connectionFactory, transactionManager);

        // Limpa as tabelas respeitando a Foreign Key
        try (Connection conn = connectionFactory.recuperarConexao();
                Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM patrimonios");
            stmt.executeUpdate("DELETE FROM locais");
            stmt.executeUpdate("DELETE FROM categorias");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao limpar banco para os testes de patrimônio: " + e.getMessage());
        }
    }

    private Patrimonio criarPatrimonioValido() {
        Categoria categoria = new Categoria(0, "Informática", "Equipamentos de TI");
        categoriaRepository.salvar(categoria);

        Local local = new Local(0, "Escritório Central", "Prédio principal");
        localRepository.salvar(local);

        return new Patrimonio(
                0,
                "Notebook Dell",
                "Core i7 16GB",
                categoria,
                local,
                "SN123456",
                4500.00,
                UnidadeMedida.UNIDADE);
    }

    @Test
    void deveSalvarPatrimonioComSucesso() {
        Patrimonio patrimonio = criarPatrimonioValido();
        patrimonioRepository.salvar(patrimonio);

        assertTrue(patrimonio.getId() > 0, "O ID do patrimônio deveria ser gerado.");

        Patrimonio salvo = patrimonioRepository.buscarPorId(patrimonio.getId());
        assertNotNull(salvo);
        assertEquals("Notebook Dell", salvo.getNome());
        assertEquals("SN123456", salvo.getNumeroSerie());
    }

    @Test
    void deveBuscarPatrimonioPorIdExistente() {
        Patrimonio patrimonio = criarPatrimonioValido();
        patrimonioRepository.salvar(patrimonio);

        Patrimonio encontrado = patrimonioRepository.buscarPorId(patrimonio.getId());
        assertNotNull(encontrado);
        assertEquals(patrimonio.getId(), encontrado.getId());
    }

    @Test
    void deveAtualizarPatrimonioExistente() {
        Patrimonio patrimonio = criarPatrimonioValido();
        patrimonioRepository.salvar(patrimonio);

        patrimonio.setNome("Notebook Dell Atualizado");
        boolean atualizado = patrimonioRepository.atualizar(patrimonio);

        assertTrue(atualizado);
        assertEquals("Notebook Dell Atualizado", patrimonioRepository.buscarPorId(patrimonio.getId()).getNome());
    }

    @Test
    void deveRemoverPatrimonioExistente() {
        Patrimonio patrimonio = criarPatrimonioValido();
        patrimonioRepository.salvar(patrimonio);

        boolean removido = patrimonioRepository.remover(patrimonio.getId());

        assertTrue(removido);
        assertNull(patrimonioRepository.buscarPorId(patrimonio.getId()));
    }

    @Test
    void deveListarTodosOsPatrimonios() {
        Patrimonio p1 = criarPatrimonioValido();
        patrimonioRepository.salvar(p1);

        List<Patrimonio> lista = patrimonioRepository.listarTodos();
        assertEquals(1, lista.size());
    }
}
