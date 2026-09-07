package com.erp.patrimonio.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.erp.patrimonio.enums.UnidadeMedida;
import com.erp.patrimonio.exception.ValidacaoException;
import com.erp.patrimonio.infra.ConnectionFactory;
import com.erp.patrimonio.infra.TransactionManager;
import com.erp.patrimonio.model.Categoria;
import com.erp.patrimonio.model.Local;
import com.erp.patrimonio.model.Patrimonio;

public class PatrimonioRepositoryJdbc implements PatrimonioRepository {

    private final ConnectionFactory connectionFactory;
    private final TransactionManager transactionManager;

    // Construtor atualizado para aceitar o TransactionManager
    public PatrimonioRepositoryJdbc(ConnectionFactory connectionFactory, TransactionManager transactionManager) {
        this.connectionFactory = connectionFactory;
        this.transactionManager = transactionManager;
    }

    private Connection obterConexao() throws Exception {
        Connection conexaoTransacao = transactionManager.getCurrentConnection();
        if (conexaoTransacao != null) {
            return conexaoTransacao; // Usa a conexão da transação (NÃO FECHAR)
        }
        return connectionFactory.recuperarConexao(); // Abre uma nova (DEVE FECHAR)
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
    public void salvar(Patrimonio patrimonio) {
        if (patrimonio == null) {
            throw new ValidacaoException("Patrimônio não pode ser nulo.");
        }

        // Validações adicionais podem ser feitas aqui, se necessário
        String sql = "INSERT INTO patrimonios (nome, descricao, numero_serie, valor, unidade_medida, ativo, categoria_id, local_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet chavesGeradas = null;

        try {
            conn = obterConexao();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

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
            chavesGeradas = stmt.getGeneratedKeys();
            if (chavesGeradas.next()) {
                patrimonio.setId(chavesGeradas.getInt(1)); // Atualiza o ID do patrimônio com o valor gerado pelo banco
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar o patrimônio no banco de dados.", e);
        } finally {
            try {
                if (chavesGeradas != null)
                    chavesGeradas.close();
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
    public Patrimonio buscarPorNome(String nome) {
        if (nome == null || nome.isBlank()) {
            return null;
        }

        // Consulta SQL para buscar o patrimônio pelo nome, incluindo Categoria e Local
        String sql = """
                SELECT
                    p.id AS patrimonio_id,
                    p.nome AS patrimonio_nome,
                    p.descricao AS patrimonio_descricao,
                    p.valor,
                    p.unidade_medida,
                    p.numero_serie,
                    c.id AS categoria_id,
                    c.nome AS categoria_nome,
                    c.descricao AS categoria_descricao,
                    l.id AS local_id,
                    l.nome AS local_nome,
                    l.descricao AS local_descricao
                FROM patrimonios p
                INNER JOIN categorias c ON p.categoria_id = c.id
                INNER JOIN locais l ON p.local_id = l.id
                WHERE p.nome = ?
                """;

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = obterConexao();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, nome.trim());
            rs = stmt.executeQuery();

            // O 'if' representa que o nome deve ser único, retornando no máximo 1 registro
            if (rs.next()) {

                // Mapeamento completo
                Categoria categoria = new Categoria(
                        rs.getInt("categoria_id"),
                        rs.getString("categoria_nome"),
                        rs.getString("categoria_descricao"));

                Local local = new Local(
                        rs.getInt("local_id"),
                        rs.getString("local_nome"),
                        rs.getString("local_descricao"));

                Patrimonio patrimonio = new Patrimonio(
                        rs.getInt("patrimonio_id"),
                        rs.getString("patrimonio_nome"),
                        rs.getString("patrimonio_descricao"),
                        categoria,
                        local,
                        rs.getString("numero_serie"),
                        rs.getDouble("valor"),
                        UnidadeMedida.valueOf(rs.getString("unidade_medida")));

                return patrimonio;
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar o patrimônio por nome no banco de dados.", e);
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
    public boolean remover(int id) {
        String sql = "DELETE FROM patrimonios WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = obterConexao();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao remover o patrimônio do banco de dados.", e);
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
    public boolean atualizar(Patrimonio patrimonio) {
        if (patrimonio == null || patrimonio.getId() <= 0) {
            throw new ValidacaoException("Patrimônio inválido para atualização.");
        }

        String sql = """
                UPDATE patrimonios
                SET nome = ?,
                    descricao = ?,
                    numero_serie = ?,
                    valor = ?,
                    unidade_medida = ?,
                    ativo = ?,
                    categoria_id = ?,
                    local_id = ?
                WHERE id = ?
                """;

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = obterConexao();
            stmt = conn.prepareStatement(sql);

            // Extrai o id_categoria e o id_local validando se não são nulos
            if (patrimonio.getCategoria() == null || patrimonio.getCategoria().getId() <= 0) {
                throw new ValidacaoException("Categoria inválida para atualização do patrimônio.");
            }
            if (patrimonio.getLocal() == null || patrimonio.getLocal().getId() <= 0) {
                throw new ValidacaoException("Local inválido para atualização do patrimônio.");
            }

            // Injeta os dados do patrimonio nos "setters"
            stmt.setString(1, patrimonio.getNome());
            stmt.setString(2, patrimonio.getDescricao());
            stmt.setString(3, patrimonio.getNumeroSerie());
            stmt.setDouble(4, patrimonio.getValor());
            stmt.setString(5, patrimonio.getUnidadeMedida().name());
            stmt.setBoolean(6, patrimonio.isAtivo());
            stmt.setInt(7, patrimonio.getCategoria().getId());
            stmt.setInt(8, patrimonio.getLocal().getId());
            stmt.setInt(9, patrimonio.getId());

            // Executa o comando e retorna true se alguma linha foi alterada
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar o patrimônio no banco de dados.", e);
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
    public Patrimonio buscarPorId(int id) {
        if (id <= 0) {
            return null;
        }

        // Consulta SQL para buscar o patrimônio pelo id, incluindo Categoria e Local
        String sql = """
                SELECT
                    p.id AS patrimonio_id,
                    p.nome AS patrimonio_nome,
                    p.descricao AS patrimonio_descricao,
                    p.valor,
                    p.unidade_medida,
                    p.numero_serie,
                    c.id AS categoria_id,
                    c.nome AS categoria_nome,
                    c.descricao AS categoria_descricao,
                    l.id AS local_id,
                    l.nome AS local_nome,
                    l.descricao AS local_descricao
                FROM patrimonios p
                INNER JOIN categorias c ON p.categoria_id = c.id
                INNER JOIN locais l ON p.local_id = l.id
                WHERE p.id = ?
                """;

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = obterConexao();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            // O 'if' representa que o id deve ser único, retornando no máximo 1 registro
            if (rs.next()) {

                // Mapeamento completo
                Categoria categoria = new Categoria(
                        rs.getInt("categoria_id"),
                        rs.getString("categoria_nome"),
                        rs.getString("categoria_descricao"));

                Local local = new Local(
                        rs.getInt("local_id"),
                        rs.getString("local_nome"),
                        rs.getString("local_descricao"));

                Patrimonio patrimonio = new Patrimonio(
                        rs.getInt("patrimonio_id"),
                        rs.getString("patrimonio_nome"),
                        rs.getString("patrimonio_descricao"),
                        categoria,
                        local,
                        rs.getString("numero_serie"),
                        rs.getDouble("valor"),
                        UnidadeMedida.valueOf(rs.getString("unidade_medida")));

                return patrimonio;
            }
        } catch (Exception e) {
            throw new RuntimeException(
                    "Erro ao buscar o patrimônio por id no banco de dados.", e);
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
    public Patrimonio buscarPorNumeroSerie(String numeroSerie) {
        if (numeroSerie == null || numeroSerie.isBlank()) {
            return null;
        }

        // Consulta SQL para buscar o patrimônio pelo número de série, incluindo
        // Categoria e Local
        String sql = """
                SELECT
                    p.id AS patrimonio_id,
                    p.nome AS patrimonio_nome,
                    p.descricao AS patrimonio_descricao,
                    p.valor,
                    p.unidade_medida,
                    p.numero_serie,
                    c.id AS categoria_id,
                    c.nome AS categoria_nome,
                    c.descricao AS categoria_descricao,
                    l.id AS local_id,
                    l.nome AS local_nome,
                    l.descricao AS local_descricao
                FROM patrimonios p
                INNER JOIN categorias c ON p.categoria_id = c.id
                INNER JOIN locais l ON p.local_id = l.id
                WHERE p.numero_serie = ?
                """;

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = obterConexao();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, numeroSerie.trim());
            rs = stmt.executeQuery();

            // O 'if' representa que o número de série deve ser único, retornando no máximo
            // 1 registro
            if (rs.next()) {

                // Mapeamento completo
                Categoria categoria = new Categoria(
                        rs.getInt("categoria_id"),
                        rs.getString("categoria_nome"),
                        rs.getString("categoria_descricao"));

                Local local = new Local(
                        rs.getInt("local_id"),
                        rs.getString("local_nome"),
                        rs.getString("local_descricao"));

                Patrimonio patrimonio = new Patrimonio(
                        rs.getInt("patrimonio_id"),
                        rs.getString("patrimonio_nome"),
                        rs.getString("patrimonio_descricao"),
                        categoria,
                        local,
                        rs.getString("numero_serie"),
                        rs.getDouble("valor"),
                        UnidadeMedida.valueOf(rs.getString("unidade_medida")));

                return patrimonio;
            }
        } catch (Exception e) {
            throw new RuntimeException(
                    "Erro ao buscar o patrimônio por numero de serie no banco de dados.", e);
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
    public List<Patrimonio> listarTodos() {
        List<Patrimonio> patrimonios = new ArrayList<>();

        String sql = """
                SELECT
                    p.id AS patrimonio_id,
                    p.nome AS patrimonio_nome,
                    p.descricao AS patrimonio_descricao,
                    p.valor,
                    p.unidade_medida,
                    p.numero_serie,
                    c.id AS categoria_id,
                    c.nome AS categoria_nome,
                    c.descricao AS categoria_descricao,
                    l.id AS local_id,
                    l.nome AS local_nome,
                    l.descricao AS local_descricao
                FROM patrimonios p
                INNER JOIN categorias c ON p.categoria_id = c.id
                INNER JOIN locais l ON p.local_id = l.id
                """;

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = obterConexao();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Categoria categoria = new Categoria(
                        rs.getInt("categoria_id"),
                        rs.getString("categoria_nome"),
                        rs.getString("categoria_descricao"));

                Local local = new Local(
                        rs.getInt("local_id"),
                        rs.getString("local_nome"),
                        rs.getString("local_descricao"));

                Patrimonio patrimonio = new Patrimonio(
                        rs.getInt("patrimonio_id"),
                        rs.getString("patrimonio_nome"),
                        rs.getString("patrimonio_descricao"),
                        categoria,
                        local,
                        rs.getString("numero_serie"),
                        rs.getDouble("valor"),
                        UnidadeMedida.valueOf(rs.getString("unidade_medida")));
                patrimonios.add(patrimonio);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar patrimônios do banco de dados.", e);
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

        return patrimonios;
    }
}
