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
import com.erp.patrimonio.infra.TransactionManager;
import com.erp.patrimonio.model.Local;

public class LocalRepositoryJdbc implements LocalRepository {

    private final ConnectionFactory connectionFactory;
    private final TransactionManager transactionManager;

    // Construtor atualizado para receber a Factory e o TransactionManager
    public LocalRepositoryJdbc(ConnectionFactory connectionFactory, TransactionManager transactionManager) {
        this.connectionFactory = connectionFactory;
        this.transactionManager = transactionManager;
    }

    // ===================================================================================
    // MÉTODO CHAVE: Gerenciamento inteligente de conexão (Transacional ou Isolada)
    // ===================================================================================
    private Connection obterConexao() throws Exception {
        Connection conexaoTransacao = transactionManager.getCurrentConnection();
        if (conexaoTransacao != null) {
            return conexaoTransacao; // Usa a conexão da transação (NÃO FECHAR)
        }
        return connectionFactory.recuperarConexao(); // Abre uma nova conexão (DEVE FECHAR)
    }

    private void fecharConexaoSeNecessario(Connection conn) {
        try {
            if (conn != null && transactionManager.getCurrentConnection() != conn) {
                conn.close();
            }
        } catch (Exception e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }

    @Override
    public void salvar(Local local) {
        if (local == null) {
            throw new ValidacaoException("Local não pode ser nulo.");
        }

        String sql = "INSERT INTO locais (nome, descricao) VALUES (?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = obterConexao();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, local.getNome());
            stmt.setString(2, local.getDescricao());

            stmt.executeUpdate();
            rs = stmt.getGeneratedKeys();

            if (rs.next()) {
                local.setId(rs.getInt(1));
            }
        } catch (Exception e) {
            throw new EstadoInvalidoException("Erro ao salvar local no banco MySQL: " + e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (stmt != null)
                    stmt.close();
            } catch (Exception e) {
            }
            fecharConexaoSeNecessario(conn);
        }
    }

    @Override
    public boolean atualizar(Local local) {
        if (local == null) {
            throw new ValidacaoException("Local não pode ser nulo.");
        }

        String sql = "UPDATE locais SET nome = ?, descricao = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = obterConexao();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, local.getNome());
            stmt.setString(2, local.getDescricao());
            stmt.setInt(3, local.getId());

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            throw new EstadoInvalidoException("Erro ao atualizar local no banco MySQL: " + e.getMessage());
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (Exception e) {
            }
            fecharConexaoSeNecessario(conn);
        }
    }

    @Override
    public boolean remover(int id) {
        String sql = "DELETE FROM locais WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = obterConexao();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            throw new EstadoInvalidoException("Erro ao remover local no banco MySQL: " + e.getMessage());
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (Exception e) {
            }
            fecharConexaoSeNecessario(conn);
        }
    }

    @Override
    public Local buscarPorId(int id) {
        String sql = "SELECT id, nome, descricao FROM locais WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = obterConexao();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return new Local(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("descricao"));
            }
        } catch (Exception e) {
            throw new EstadoInvalidoException("Erro ao buscar local por ID no MySQL: " + e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (stmt != null)
                    stmt.close();
            } catch (Exception e) {
            }
            fecharConexaoSeNecessario(conn);
        }
        return null;
    }

    @Override
    public Local buscarPorNome(String nome) {
        if (nome == null || nome.isBlank())
            return null;

        String sql = "SELECT id, nome, descricao FROM locais WHERE nome = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = obterConexao();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, nome.trim());
            rs = stmt.executeQuery();

            if (rs.next()) {
                return new Local(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("descricao"));
            }
        } catch (Exception e) {
            throw new EstadoInvalidoException("Erro ao buscar local por nome no MySQL: " + e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (stmt != null)
                    stmt.close();
            } catch (Exception e) {
            }
            fecharConexaoSeNecessario(conn);
        }
        return null;
    }

    @Override
    public Local buscarPorDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            return null;
        }

        String sql = "SELECT id, nome, descricao FROM locais WHERE descricao = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = obterConexao();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, descricao.trim());
            rs = stmt.executeQuery();

            if (rs.next()) {
                return new Local(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("descricao"));
            }
        } catch (Exception e) {
            throw new EstadoInvalidoException("Erro ao buscar local por descrição no MySQL: " + e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (stmt != null)
                    stmt.close();
            } catch (Exception e) {
            }
            fecharConexaoSeNecessario(conn);
        }
        return null;
    }

    @Override
    public List<Local> listarTodos() {
        List<Local> locais = new ArrayList<>();
        String sql = "SELECT id, nome, descricao FROM locais";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = obterConexao();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                locais.add(new Local(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("descricao")));
            }
        } catch (Exception e) {
            throw new EstadoInvalidoException("Erro ao listar locais do MySQL: " + e.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (stmt != null)
                    stmt.close();
            } catch (Exception e) {
            }
            fecharConexaoSeNecessario(conn);
        }
        return locais;
    }
}
