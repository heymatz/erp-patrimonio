package com.erp.patrimonio.infra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    public Connection recuperarConexao() {
        try {
            return DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/erp_patrimonio?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC", 
                    "root", 
                    "SENHA_OMITIDA_PARA_COMMIT" // <-- ATENÇÃO: COLOQUE A SENHA REAL AQUI
            );
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar ao banco de dados.", e);
        }
    }
}
