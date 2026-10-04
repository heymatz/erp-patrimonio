package com.erp.patrimonio.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.erp.patrimonio.enums.UnidadeMedida;
import com.erp.patrimonio.infra.ConnectionFactory;
import com.erp.patrimonio.infra.TransactionManager;
import com.erp.patrimonio.model.Categoria;
import com.erp.patrimonio.model.Local;
import com.erp.patrimonio.model.Patrimonio;

public class PatrimonioRepositoryJdbcTest {

    private PatrimonioRepository repository;
    private ConnectionFactory connectionFactory;

    @BeforeEach
    public void setUp() {
        connectionFactory = new ConnectionFactory();
        TransactionManager transactionManager = new TransactionManager(connectionFactory);
        repository = new PatrimonioRepositoryJdbc(connectionFactory, transactionManager);

        // Limpa o banco de dados antes de cada teste para garantir isolamento
        limparBancoDeDados(connectionFactory);
    }

    private void limparBancoDeDados(ConnectionFactory factory) {
        try (Connection conn = factory.recuperarConexao()) {
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DELETE FROM patrimonios");
                stmt.executeUpdate("DELETE FROM locais");
                stmt.executeUpdate("DELETE FROM categorias");
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao limpar banco para os testes: " + e.getMessage());
        }
    }

    @Test
    public void deveSalvarPatrimonioComSucessoNoBancoDeDados() throws Exception {
        // 1. Arrange
        Categoria categoria = prepararCategoriaNoBanco(connectionFactory);
        Local local = prepararLocalNoBanco(connectionFactory);

        String numeroSerieUnico = "SN-TESTE-" + System.currentTimeMillis();

        Patrimonio patrimonio = new Patrimonio(
                0,
                "Notebook de Teste",
                "Equipamento inserido pelo teste de integração",
                categoria,
                local,
                numeroSerieUnico,
                4500.00,
                UnidadeMedida.UNIDADE);

        // 2. Act
        repository.salvar(patrimonio);

        // 3. Assert
        assertTrue(patrimonio.getId() > 0, "O ID do patrimônio deveria ser preenchido com o valor gerado pelo banco.");
        System.out.println("SUCESSO: Patrimônio salvo no banco MySQL! ID gerado: " + patrimonio.getId());
    }

    // --- Métodos Auxiliares ajustados para usar as tabelas no plural ---
    private Categoria prepararCategoriaNoBanco(ConnectionFactory factory) throws Exception {
        String sql = "INSERT INTO categorias (nome, descricao) VALUES (?, ?)";
        try (Connection conn = factory.recuperarConexao();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, "TI Testes");
            stmt.setString(2, "Categoria criada para testes automatizados");
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return new Categoria(rs.getInt(1), "TI Testes", "Categoria criada para testes automatizados");
                }
            }
        }
        throw new RuntimeException("Falha ao preparar Categoria no banco.");
    }

    private Local prepararLocalNoBanco(ConnectionFactory factory) throws Exception {
        String sql = "INSERT INTO locais (nome, descricao) VALUES (?, ?)";
        try (Connection conn = factory.recuperarConexao();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, "Sala de Testes");
            stmt.setString(2, "Local criado para testes automatizados");
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return new Local(rs.getInt(1), "Sala de Testes", "Local criado para testes automatizados");
                }
            }
        }
        throw new RuntimeException("Falha ao preparar Local no banco.");
    }

    @Test
    @DisplayName("Deve atualizar os dados de um patrimônio existente")
    void deveAtualizarPatrimonio() throws Exception {
        // Arrange: Insere dependências e um patrimônio inicial para podermos atualizar
        Categoria categoria = prepararCategoriaNoBanco(connectionFactory);
        Local local = prepararLocalNoBanco(connectionFactory);

        Patrimonio patrimonioInicial = new Patrimonio(
                0, "Notebook Antigo", "Desc", categoria, local, "SN-" + System.currentTimeMillis(), 3000.00,
                UnidadeMedida.UNIDADE);
        repository.salvar(patrimonioInicial);

        List<Patrimonio> lista = repository.listarTodos();
        assertFalse(lista.isEmpty());

        Patrimonio patrimonioAlvo = lista.get(0);
        String nomeOriginal = patrimonioAlvo.getNome();
        String novoNome = "Notebook Teste Atualizado";
        double novoValor = 9999.99;

        patrimonioAlvo.setNome(novoNome);
        patrimonioAlvo.setValor(novoValor);

        // Act
        boolean atualizou = repository.atualizar(patrimonioAlvo);

        // Assert
        assertTrue(atualizou, "O método atualizar deveria retornar true indicando sucesso.");

        Patrimonio patrimonioModificado = repository.buscarPorId(patrimonioAlvo.getId());

        assertEquals(novoNome, patrimonioModificado.getNome(), "O nome não foi atualizado no banco.");
        assertEquals(novoValor, patrimonioModificado.getValor(), "O valor não foi atualizado no banco.");

        System.out.println("SUCESSO: Patrimônio atualizado no banco de [" + nomeOriginal + "] para ["
                + patrimonioModificado.getNome() + "]");
    }

    @Test
    @DisplayName("Deve listar todos os patrimônios com Categoria e Local populados")
    void deveListarTodosOsPatrimonios() throws Exception {
        // Arrange: Garante que há pelo menos um registro para listar
        Categoria categoria = prepararCategoriaNoBanco(connectionFactory);
        Local local = prepararLocalNoBanco(connectionFactory);

        Patrimonio patrimonio = new Patrimonio(
                0, "Notebook Listagem", "Desc", categoria, local, "SN-" + System.currentTimeMillis(), 4000.00,
                UnidadeMedida.UNIDADE);
        repository.salvar(patrimonio);

        // Act
        List<Patrimonio> lista = repository.listarTodos();

        // Assert
        assertFalse(lista.isEmpty(), "A lista não deveria estar vazia.");

        Patrimonio patrimonioExtraido = lista.get(0);

        assertNotNull(patrimonioExtraido.getCategoria(), "A Categoria veio nula! O mapeamento falhou.");
        assertTrue(patrimonioExtraido.getCategoria().getId() > 0, "O ID da Categoria não foi mapeado.");

        assertNotNull(patrimonioExtraido.getLocal(), "O Local veio nulo! O mapeamento falhou.");
        assertTrue(patrimonioExtraido.getLocal().getId() > 0, "O ID do Local não foi mapeado.");

        System.out.println("SUCESSO: Listagem trouxe o patrimônio: " + patrimonioExtraido.getNome()
                + " | Categoria: " + patrimonioExtraido.getCategoria().getNome()
                + " | Local: " + patrimonioExtraido.getLocal().getNome());
    }

    @Test
    void deveRetornarTrueSeCategoriaTiverPatrimonioVinculado() throws Exception {
        // Teste de integração precisa do TransactionManager e ConnectionFactory
        // igual aos outros testes dessa classe.
        TransactionManager tx = new TransactionManager(connectionFactory);
        CategoriaRepositoryJdbc categoriaRepository = new CategoriaRepositoryJdbc(connectionFactory, tx);

        // Insere uma Categoria nova no banco de dados (Usando caminho completo para evitar problema de import)
        Categoria catTI = new Categoria(0, "TI Teste Trava", "Equipamentos", com.erp.patrimonio.enums.TipoItem.PATRIMONIO);
        categoriaRepository.salvar(catTI);

        // Verifica que acabou de ser criada e não está em uso
        assertFalse(
                categoriaRepository.isCategoriaEmUso(catTI.getId()),
                "Categoria recém criada não deveria estar em uso.");

        // Insere um Local no banco de dados (Direto via SQL para não depender de outros repositórios)
        int localId;
        try (Connection conn = connectionFactory.recuperarConexao();
             PreparedStatement stmtLocal = conn.prepareStatement(
                     "INSERT INTO locais (nome, descricao) VALUES ('Sala Trava', 'Sede')",
                     Statement.RETURN_GENERATED_KEYS)) {

            stmtLocal.executeUpdate();
            ResultSet rsLocal = stmtLocal.getGeneratedKeys();
            rsLocal.next();
            localId = rsLocal.getInt(1);

            // Cadastra um Patrimônio vinculado a essa Categoria (Direto via SQL para controle total)
            try (PreparedStatement stmtPat = conn.prepareStatement(
                    "INSERT INTO patrimonios (nome, descricao, numero_serie, valor, unidade_medida, quantidade, estoque_minimo, categoria_id, local_id) " +
                    "VALUES ('Monitor Trava', 'Desc', 'SN-TRAVA', 1500.0, 'UNIDADE', 1, 0, ?, ?)")) {
                stmtPat.setInt(1, catTI.getId());
                stmtPat.setInt(2, localId);
                stmtPat.executeUpdate();
            }
        }

        // Verifica que agora a Categoria está em uso
        assertTrue(
                categoriaRepository.isCategoriaEmUso(catTI.getId()),
                "A categoria deve constar como EM USO após vincularmos um patrimônio a ela!");
    }
}
