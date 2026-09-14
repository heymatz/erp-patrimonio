package com.erp.patrimonio.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.erp.patrimonio.enums.TipoItem;
import com.erp.patrimonio.exception.EstadoInvalidoException;
import com.erp.patrimonio.exception.ValidacaoException;
import com.erp.patrimonio.infra.ConnectionFactory;
import com.erp.patrimonio.infra.TransactionManager;
import com.erp.patrimonio.model.Categoria;

public class CategoriaRepositoryJdbc implements CategoriaRepository {

    private final ConnectionFactory connectionFactory;
    private final TransactionManager transactionManager;

    // Construtor atualizado para receber o TransactionManager
    public CategoriaRepositoryJdbc(ConnectionFactory connectionFactory, TransactionManager transactionManager) {
        this.connectionFactory = connectionFactory;
        this.transactionManager = transactionManager;
    }

    // ===================================================================================
    // MÉTODO CHAVE: Decide se usa a conexão da transação ou abre uma nova
    // ===================================================================================
    private Connection obterConexao() throws Exception {
        Connection conexaoTransacao = transactionManager.getCurrentConnection();
        if (conexaoTransacao != null) {
            return conexaoTransacao; // Usa a conexão da transação atual (NÃO FECHAR DEPOIS)
        }
        return connectionFactory.recuperarConexao(); // Abre uma nova (DEVE FECHAR DEPOIS)
    }

    // MÉTODO AUXILIAR: Fecha a conexão apenas se ela NÃO for a conexão da transação
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
    public void salvar(Categoria categoria) {
        if (categoria == null) {
            throw new ValidacaoException("Categoria não pode ser nula.");
        }

        // Validação de segurança arquitetural
        if (categoria.getTipoItem() == null) {
            throw new ValidacaoException("O Tipo de Item (PATRIMONIO ou ESTOQUE) é obrigatório.");
        }

        // Traz o tipo_item do Enum e salva como String no banco de dados
        String sql = "INSERT INTO categorias (nome, descricao, tipo_item) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = obterConexao();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, categoria.getNome());
            stmt.setString(2, categoria.getDescricao());
            stmt.setString(3, categoria.getTipoItem().name()); // Salva o Enum como String

            stmt.executeUpdate();
            rs = stmt.getGeneratedKeys();

            if (rs.next()) {
                categoria.setId(rs.getInt(1));
            }
        } catch (Exception e) {
            throw new EstadoInvalidoException("Erro ao salvar categoria no banco MySQL: " + e.getMessage());
        } finally {
            // Fecha o ResultSet e PreparedStatement, mas não fecha a conexão se for da
            // transação
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
    public Categoria buscarPorId(int id) {
        String sql = "SELECT id, nome, descricao, tipo_item FROM categorias WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = obterConexao();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return new Categoria(
                        // Adiciona o tipo_item ao construtor da Categoria
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("descricao"),
                        TipoItem.valueOf(rs.getString("tipo_item")));
            }
        } catch (Exception e) {
            throw new EstadoInvalidoException("Erro ao buscar categoria por ID no MySQL: " + e.getMessage());
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
    public Categoria buscarPorNome(String nome) {
        if (nome == null || nome.isBlank())
            return null;

        String sql = "SELECT id, nome, descricao, tipo_item FROM categorias WHERE nome = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = obterConexao();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, nome.trim());
            rs = stmt.executeQuery();

            if (rs.next()) {
                return new Categoria(
                        // Adiciona o tipo_item ao construtor da Categoria
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("descricao"),
                        TipoItem.valueOf(rs.getString("tipo_item")));
            }
        } catch (Exception e) {
            throw new EstadoInvalidoException("Erro ao buscar categoria por nome no MySQL: " + e.getMessage());
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
    public Categoria buscarPorDescricao(String descricao) {
        if (descricao == null || descricao.isBlank())
            return null;

        String sql = "SELECT id, nome, descricao, tipo_item FROM categorias WHERE descricao = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = obterConexao();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, descricao.trim());
            rs = stmt.executeQuery();

            if (rs.next()) {
                return new Categoria(
                        // Adiciona o tipo_item ao construtor da Categoria
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("descricao"),
                        TipoItem.valueOf(rs.getString("tipo_item")));
            }
        } catch (Exception e) {
            throw new EstadoInvalidoException("Erro ao buscar categoria por descrição no MySQL: " + e.getMessage());
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
    public List<Categoria> listarTodos() {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT id, nome, descricao, tipo_item FROM categorias";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = obterConexao();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                categorias.add(new Categoria(
                        // Adiciona o tipo_item ao construtor da Categoria
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("descricao"),
                        TipoItem.valueOf(rs.getString("tipo_item"))));
            }
        } catch (Exception e) {
            throw new EstadoInvalidoException("Erro ao listar categorias do MySQL: " + e.getMessage());
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
        return categorias;
    }

    @Override
    public boolean atualizar(Categoria categoria) {
        if (categoria == null) {
            throw new ValidacaoException("Categoria não pode ser nula.");
        }

        if (categoria.getTipoItem() == null) {
            throw new ValidacaoException("O Tipo de Item não pode ser nulo na atualização.");
        }

        // Adiciona o tipo_item no UPDATE para atualizar o Enum no banco de dados
        String sql = "UPDATE categorias SET nome = ?, descricao = ?, tipo_item = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = obterConexao();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, categoria.getNome());
            stmt.setString(2, categoria.getDescricao());
            stmt.setString(3, categoria.getTipoItem().name()); // Atualiza o Enum
            stmt.setInt(4, categoria.getId());

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            throw new EstadoInvalidoException("Erro ao atualizar categoria no banco MySQL: " + e.getMessage());
        } finally {
            // Fecha o PreparedStatement, mas não fecha a conexão se for da transação
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
        String sql = "DELETE FROM categorias WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = obterConexao();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            throw new EstadoInvalidoException("Erro ao remover categoria no banco MySQL: " + e.getMessage());
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (Exception e) {
            }
            fecharConexaoSeNecessario(conn);
        }
    }
}
