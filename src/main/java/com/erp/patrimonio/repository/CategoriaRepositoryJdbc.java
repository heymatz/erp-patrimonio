package com.erp.patrimonio.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.erp.patrimonio.exception.EstadoInvalidoException;
import com.erp.patrimonio.exception.ValidacaoException;
import com.erp.patrimonio.infra.ConnectionFactory;
import com.erp.patrimonio.model.Categoria;

public class CategoriaRepositoryJdbc implements CategoriaRepository {

    private final ConnectionFactory connectionFactory;

    public CategoriaRepositoryJdbc(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void salvar(Categoria categoria) {
        if (categoria == null) {
            throw new ValidacaoException("Categoria não pode ser nula.");
        }

        String sql = "INSERT INTO categorias (nome, descricao) VALUES (?, ?)";

        try (Connection conn = connectionFactory.recuperarConexao();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, categoria.getNome());
            stmt.setString(2, categoria.getDescricao());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    categoria.setId(rs.getInt(1));
                }
            }

        } catch (Exception e) {
            throw new EstadoInvalidoException("Erro ao salvar categoria no banco MySQL: " + e.getMessage());
        }
    }

    @Override
    public boolean atualizar(Categoria categoria) {
        if (categoria == null) {
            throw new ValidacaoException("Categoria não pode ser nula.");
        }

        String sql = "UPDATE categorias SET nome = ?, descricao = ? WHERE id = ?";

        try (Connection conn = connectionFactory.recuperarConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, categoria.getNome());
            stmt.setString(2, categoria.getDescricao());
            stmt.setInt(3, categoria.getId());

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (Exception e) {
            throw new EstadoInvalidoException("Erro ao atualizar categoria no banco MySQL: " + e.getMessage());
        }
    }

    @Override
    public boolean remover(int id) {
        String sql = "DELETE FROM categorias WHERE id = ?";

        try (Connection conn = connectionFactory.recuperarConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (Exception e) {
            throw new EstadoInvalidoException("Erro ao remover categoria no banco MySQL: " + e.getMessage());
        }
    }

    @Override
    public Categoria buscarPorId(int id) {
        String sql = "SELECT id, nome, descricao FROM categorias WHERE id = ?";

        try (Connection conn = connectionFactory.recuperarConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Categoria(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("descricao"));
                }
            }

        } catch (Exception e) {
            throw new EstadoInvalidoException("Erro ao buscar categoria por ID no MySQL: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Categoria buscarPorNome(String nome) {
        if (nome == null || nome.isBlank()) {
            return null;
        }

        String sql = "SELECT id, nome, descricao FROM categorias WHERE nome = ?";

        try (Connection conn = connectionFactory.recuperarConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Categoria(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("descricao"));
                }
            }

        } catch (Exception e) {
            throw new EstadoInvalidoException("Erro ao buscar categoria por nome no MySQL: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Categoria buscarPorDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            return null;
        }

        String sql = "SELECT id, nome, descricao FROM categorias WHERE descricao = ?";

        try (Connection conn = connectionFactory.recuperarConexao();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, descricao.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Categoria(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("descricao"));
                }
            }

        } catch (Exception e) {
            throw new EstadoInvalidoException("Erro ao buscar categoria por descrição no MySQL: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Categoria> listarTodos() {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT id, nome, descricao FROM categorias";

        try (Connection conn = connectionFactory.recuperarConexao();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                categorias.add(new Categoria(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("descricao")));
            }

        } catch (Exception e) {
            throw new EstadoInvalidoException("Erro ao listar categorias do MySQL: " + e.getMessage());
        }
        return categorias;
    }
}