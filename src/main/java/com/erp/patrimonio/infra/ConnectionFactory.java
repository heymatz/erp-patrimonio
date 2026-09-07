package com.erp.patrimonio.infra;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class ConnectionFactory {

    // O DataSource é estático para que exista apenas UM pool de conexões em toda a aplicação
    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        
        // ATENÇÃO: Altere para os dados do seu MySQL local!
        config.setJdbcUrl("jdbc:mysql://localhost:3306/erp_patrimonio"); 
        config.setUsername("root"); 
        config.setPassword("SENHA_OMITIDA_PARA_COMMIT"); 

        // Otimizações de performance do HikariCP
        config.setMaximumPoolSize(10); // Máximo de 10 conexões abertas simultâneas (ideal para nosso escopo)
        config.setMinimumIdle(2);      // Mantém sempre 2 conexões prontas, mesmo sem ninguém usando
        config.setConnectionTimeout(30000); // Se o banco demorar mais de 30s para responder, lança erro
        config.setIdleTimeout(600000); // Fecha conexões ociosas após 10 minutos para economizar recursos

        // Instancia o Pool de conexões
        dataSource = new HikariDataSource(config);
    }

    // Método que os repositórios chamam para pegar uma conexão emprestada do pool
    public Connection recuperarConexao() throws SQLException {
        return dataSource.getConnection();
    }
}