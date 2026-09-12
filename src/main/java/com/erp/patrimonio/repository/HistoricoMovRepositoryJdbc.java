package com.erp.patrimonio.repository;

import com.erp.patrimonio.infra.ConnectionFactory;
import com.erp.patrimonio.infra.TransactionManager;
import com.erp.patrimonio.model.HistoricoMov;
import com.erp.patrimonio.model.Local;
import com.erp.patrimonio.model.Patrimonio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class HistoricoMovRepositoryJdbc implements HistoricoMovRepository {

    private final ConnectionFactory connectionFactory;
    private final TransactionManager transactionManager;

    public HistoricoMovRepositoryJdbc(ConnectionFactory connectionFactory, TransactionManager transactionManager) {
        this.connectionFactory = connectionFactory;
        this.transactionManager = transactionManager;
    }

    @Override
    public void salvar(HistoricoMov historico) {
        String sql = """
                INSERT INTO historico_movimentacao
                (patrimonio_id, local_origem_id, local_destino_id, data_movimentacao, motivo)
                VALUES (?, ?, ?, ?, ?)
                """;

        // Obtem a conexão da transação atual
        Connection conn = transactionManager.getCurrentConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, historico.getPatrimonio().getId());
            pstmt.setInt(2, historico.getLocalOrigem().getId());
            pstmt.setInt(3, historico.getLocalDestino().getId());
            pstmt.setTimestamp(4, Timestamp.valueOf(historico.getDataMovimentacao()));
            pstmt.setString(5, historico.getMotivo());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar histórico de movimentação", e);
        }
    }

    @Override
    public List<HistoricoMov> listarPorPatrimonio(int patrimonioId) {
        String sql = "SELECT * FROM historico_movimentacao WHERE patrimonio_id = ? ORDER BY data_movimentacao DESC";
        List<HistoricoMov> historicos = new ArrayList<>();

        Connection conn = transactionManager.getCurrentConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, patrimonioId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                HistoricoMov hist = new HistoricoMov();
                hist.setId(rs.getInt("id"));

                // Mapeamento básico dos objetos relacionados apenas com os IDs por enquanto
                Patrimonio p = new Patrimonio();
                p.setId(rs.getInt("patrimonio_id"));
                hist.setPatrimonio(p);

                Local origem = new Local();
                origem.setId(rs.getInt("local_origem_id"));
                hist.setLocalOrigem(origem);

                Local destino = new Local();
                destino.setId(rs.getInt("local_destino_id"));
                hist.setLocalDestino(destino);

                hist.setDataMovimentacao(rs.getTimestamp("data_movimentacao").toLocalDateTime());
                hist.setMotivo(rs.getString("motivo"));

                historicos.add(hist);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar histórico do patrimônio", e);
        }

        return historicos;
    }
}
