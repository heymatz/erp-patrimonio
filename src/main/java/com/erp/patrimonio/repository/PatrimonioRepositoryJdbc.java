package com.erp.patrimonio.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.erp.patrimonio.enums.UnidadeMedida;
import com.erp.patrimonio.exception.ValidacaoException;
import com.erp.patrimonio.infra.ConnectionFactory;
import com.erp.patrimonio.model.Patrimonio;

public class PatrimonioRepositoryJdbc implements PatrimonioRepository {

    private final ConnectionFactory factory;

    // Injeção de dependência
    public PatrimonioRepositoryJdbc(ConnectionFactory factory) {
        this.factory = factory;
    }

    @Override
    public void salvar(Patrimonio patrimonio) {
        if (patrimonio == null) {
            throw new ValidacaoException("Patrimônio não pode ser nulo.");
        }

        // Validações adicionais podem ser feitas aqui, se necessário
        String sql = "INSERT INTO patrimonio (nome, descricao, numero_serie, valor, unidade_medida, ativo, categoria_id, local_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = factory.recuperarConexao(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, patrimonio.getNome());
            stmt.setString(2, patrimonio.getDescricao());
            stmt.setString(3, patrimonio.getNumeroSerie());
            stmt.setDouble(4, patrimonio.getValor()); // Mapeando double para DECIMAL

            // Extrai o nome em String do Enum UnidadeMedida
            stmt.setString(5, patrimonio.getUnidadeMedida().name());

            stmt.setBoolean(6, patrimonio.isAtivo());

            // Valida e extrai chaves estrangeiras
            if (patrimonio.getCategoria() == null || patrimonio.getCategoria().getId() <= 0) {
                throw new ValidacaoException("O patrimônio precisa estar vinculado a uma categoria com ID válido.");
            }
            stmt.setInt(7, patrimonio.getCategoria().getId());

            if (patrimonio.getLocal() == null || patrimonio.getLocal().getId() <= 0) {
                throw new ValidacaoException("O patrimônio precisa estar vinculado a um local com ID válido.");
            }
            stmt.setInt(8, patrimonio.getLocal().getId());

            stmt.executeUpdate();

            // Recupera e injeta o ID gerado pelo banco
            try (ResultSet chavesGeradas = stmt.getGeneratedKeys()) {
                if (chavesGeradas.next()) {
                    patrimonio.setId(chavesGeradas.getInt(1)); // Atualiza o ID do patrimônio com o valor gerado pelo banco
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar o patrimônio no banco de dados.", e);
        }
    }

    @Override
    public Patrimonio buscarPorNome(String nome) {
        if (nome == null || nome.isBlank()) {
            return null;
        }

        String sql = "SELECT * FROM patrimonio WHERE nome = ?";

        try (Connection conn = factory.recuperarConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Cria o objeto Patrimonio a partir do ResultSet
                    Patrimonio patrimonio = new Patrimonio(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("descricao"),
                            null, // Categoria precisa ser buscada separadamente
                            null, // Local precisa ser buscado separadamente
                            rs.getString("numero_serie"),
                            rs.getDouble("valor"),
                            UnidadeMedida.valueOf(rs.getString("unidade_medida"))
                    );
                    return patrimonio;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar o patrimônio por nome no banco de dados.", e);
        }

        return null;
    }

    @Override
    public boolean remover(int id) {
        String sql = "DELETE FROM patrimonio WHERE id = ?";

        try (Connection conn = factory.recuperarConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover o patrimônio do banco de dados.", e);
        }
    }

    @Override
    public boolean atualizar(Patrimonio patrimonio) {
        throw new UnsupportedOperationException("Método atualizar ainda não implementado no JDBC.");
    }

    @Override
    public Patrimonio buscarPorId(int id) {
        throw new UnsupportedOperationException("Método buscarPorId ainda não implementado no JDBC.");
    }

    @Override
    public Patrimonio buscarPorNumeroSerie(String numeroSerie) {
        throw new UnsupportedOperationException("Método buscarPorNumeroSerie ainda não implementado no JDBC.");
    }

    @Override
    public List<Patrimonio> listarTodos() {
        throw new UnsupportedOperationException("Método listarTodos ainda não implementado no JDBC.");
    }
}
