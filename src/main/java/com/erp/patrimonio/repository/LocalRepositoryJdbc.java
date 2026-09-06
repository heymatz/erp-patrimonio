package com.erp.patrimonio.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.erp.patrimonio.exception.ValidacaoException;
import com.erp.patrimonio.infra.ConnectionFactory;
import com.erp.patrimonio.model.Local;

public class LocalRepositoryJdbc implements LocalRepository {

    private final ConnectionFactory factory;

    public LocalRepositoryJdbc(ConnectionFactory factory) {
        this.factory = factory;
    }

    @Override
    public void salvar(Local local) {
        if (local == null) {
            throw new ValidacaoException("Local não pode ser nulo.");
        }

        String sql = "INSERT INTO local (nome, descricao) VALUES (?, ?)";

        try (Connection conn = factory.recuperarConexao();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, local.getNome());
            stmt.setString(2, local.getDescricao());
            stmt.executeUpdate();

            try (ResultSet chavesGeradas = stmt.getGeneratedKeys()) {
                if (chavesGeradas.next()) {
                    local.setId(chavesGeradas.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar o local no banco de dados.", e);
        }
    }

    @Override
    public boolean atualizar(Local local) {
        String sql = "UPDATE local SET nome = ?, descricao = ? WHERE id = ?";

        try (Connection conn = factory.recuperarConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Valida o ID do local antes de atualizar
            if (local.getId() <= 0) {
                throw new ValidacaoException("ID do local inválido para atualização.");
            }

            // Injeta os dados do local nos "setters"
            stmt.setString(1, local.getNome());
            stmt.setString(2, local.getDescricao());
            stmt.setInt(3, local.getId()); // Onde id = ?

            // Executa o comando e retorna true se alguma linha foi alterada
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar o local no banco de dados.", e);
        }
    }

    @Override
    public boolean remover(int id) {
        String sql = "DELETE FROM local WHERE id = ?";

        try (Connection conn = factory.recuperarConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Valida o ID do local antes de remover
            if (id <= 0) {
                throw new ValidacaoException("ID do local inválido para remoção.");
            }

            // Injeta o ID do local no "setter"
            stmt.setInt(1, id);

            // Executa o comando e retorna true se alguma linha foi removida
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover o local no banco de dados.", e);
        }
    }

    @Override
    public Local buscarPorNome(String nome) {
        String sql = "SELECT id, nome, descricao FROM local WHERE nome = ?";

        try (Connection conn = factory.recuperarConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                // O 'if' garante que puxe apenas 1 registro (assumindo que o nome do local não
                // se repete)
                if (rs.next()) {
                    return new Local(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("descricao"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar o local por nome no banco de dados.", e);
        }

        // Retorno padrão caso não encontre nada no banco
        return null;
    }

    @Override
    public Local buscarPorDescricao(String descricao) {
        String sql = "SELECT id, nome, descricao FROM local WHERE descricao = ?";

        try (Connection conn = factory.recuperarConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, descricao.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                // O 'if' garante que puxe apenas 1 registro (assumindo que a descrição do local
                // não se repete)
                if (rs.next()) {
                    return new Local(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("descricao"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar o local por descricao no banco de dados.", e);
        }

        // Retorno padrão caso não encontre nada no banco
        return null;
    }

    @Override
    public Local buscarPorId(int id) {
        String sql = "SELECT id, nome, descricao FROM local WHERE id = ?";

        if (id <= 0) {
            return null;
        }

        try (Connection conn = factory.recuperarConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Local(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("descricao"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar o local por ID no banco de dados.", e);
        }
        return null;
    }

    @Override
    public List<Local> listarTodos() {
        String sql = "SELECT id, nome, descricao FROM local";
        try (Connection conn = factory.recuperarConexao();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            List<Local> locais = new ArrayList<>();
            while (rs.next()) {
                Local local = new Local(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("descricao"));
                locais.add(local);
            }
            return locais; // Se der sucesso, sai por aqui

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar todos os locais no banco de dados.", e); // Se der erro, sai por aqui
        }
    }
}
