package com.erp.patrimonio.infra;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionManager {

    // O ThreadLocal garante que cada requisição/thread tenha a sua própria conexão.
    // Assim, se duas requisições chegarem ao mesmo tempo, cada uma terá a sua transação isolada.
    private static final ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

    private final ConnectionFactory connectionFactory;

    public TransactionManager(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    // Inicia uma transação para a thread atual
    public void beginTransaction() throws SQLException {
        if (threadLocal.get() != null) {
            throw new IllegalStateException("Já existe uma transação em andamento.");
        }
        Connection conn = connectionFactory.recuperarConexao();
        conn.setAutoCommit(false);
        threadLocal.set(conn);
    }

    // Finaliza a transação para a thread atual
    public void commit() throws SQLException {
        Connection conn = threadLocal.get();
        if (conn != null) {
            conn.commit();
            conn.close();
            threadLocal.remove(); // Limpa a gaveta
        }
    }

    // Desfaz a transação para a thread atual
    public void rollback() {
        Connection conn = threadLocal.get();
        if (conn != null) {
            try {
                conn.rollback();
                conn.close();
            } catch (SQLException e) {
                System.err.println("Erro crítico ao tentar fazer rollback: " + e.getMessage());
            } finally {
                threadLocal.remove(); // Limpa a gaveta de qualquer jeito
            }
        }
    }

    // Permite que os repositórios acessem a conexão da transação atual
    public Connection getCurrentConnection() {
        return threadLocal.get();
    }
}
