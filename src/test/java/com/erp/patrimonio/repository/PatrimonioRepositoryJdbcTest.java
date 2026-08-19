package com.erp.patrimonio.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.erp.patrimonio.enums.UnidadeMedida;
import com.erp.patrimonio.infra.ConnectionFactory;
import com.erp.patrimonio.model.Categoria;
import com.erp.patrimonio.model.Local;
import com.erp.patrimonio.model.Patrimonio;

public class PatrimonioRepositoryJdbcTest {

    @Test
    public void deveSalvarPatrimonioComSucessoNoBancoDeDados() throws Exception {
        // 1. Arrange (Preparação)
        ConnectionFactory factory = new ConnectionFactory();
        PatrimonioRepository repository = new PatrimonioRepositoryJdbc(factory);

        // Prepara as chaves estrangeiras fisicamente no banco de dados para evitar violação de Foreign Key
        Categoria categoria = prepararCategoriaNoBanco(factory);
        Local local = prepararLocalNoBanco(factory);

        // Cria a entidade gerando um numero de série dinâmico para não falhar na restrição UNIQUE do banco
        String numeroSerieUnico = "SN-TESTE-" + System.currentTimeMillis();

        Patrimonio patrimonio = new Patrimonio(
                0, // Definido 0 pois o banco é quem vai gerar o ID real
                "Notebook de Teste",
                "Equipamento inserido pelo teste de integração",
                categoria,
                local,
                numeroSerieUnico,
                4500.00,
                UnidadeMedida.UNIDADE
        );

        // 2. Act (Ação)
        repository.salvar(patrimonio);

        // 3. Assert (Validação)
        assertTrue(patrimonio.getId() > 0, "O ID do patrimônio deveria ser preenchido com o valor gerado pelo banco.");
        System.out.println("SUCESSO: Patrimônio salvo no banco MySQL! ID gerado: " + patrimonio.getId());
    }

    // --- Métodos Auxiliares para preparar o banco de dados antes do teste ---
    private Categoria prepararCategoriaNoBanco(ConnectionFactory factory) throws Exception {
        String sql = "INSERT INTO categoria (nome, descricao) VALUES (?, ?)";
        try (Connection conn = factory.recuperarConexao(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, "TI Testes");
            stmt.setString(2, "Categoria criada para testes automatizados");
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    // Cria o objeto da Categoria usando o ID gerado pelo banco
                    return new Categoria(rs.getInt(1), "TI Testes", "Categoria criada para testes automatizados");
                }
            }
        }
        throw new RuntimeException("Falha ao preparar Categoria no banco.");
    }

    private Local prepararLocalNoBanco(ConnectionFactory factory) throws Exception {
        String sql = "INSERT INTO local (nome, descricao) VALUES (?, ?)";
        try (Connection conn = factory.recuperarConexao(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, "Sala de Testes");
            stmt.setString(2, "Local criado para testes automatizados");
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    // Cria o objeto do Local usando o ID gerado pelo banco
                    return new Local(rs.getInt(1), "Sala de Testes", "Local criado para testes automatizados");
                }
            }
        }
        throw new RuntimeException("Falha ao preparar Local no banco.");
    }
}
