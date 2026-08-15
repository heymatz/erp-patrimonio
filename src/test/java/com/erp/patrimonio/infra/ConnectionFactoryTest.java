package com.erp.patrimonio.infra;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

public class ConnectionFactoryTest {

    @Test
    public void deveEstabelecerConexaoComOBancoDeDados() {
        ConnectionFactory factory = new ConnectionFactory();
        
        try (Connection conexao = factory.recuperarConexao()) {
            
            assertNotNull(conexao, "A conexão não deveria ser nula.");
            System.out.println("Teste OK: Conexão estabelecida com sucesso.");
            
        } catch (SQLException e) {
            fail("Falha de SQL ao tentar fechar a conexão: " + e.getMessage());
        } catch (RuntimeException e) {
            // Extraindo a causa raiz mascarada
            Throwable causa = e.getCause();
            String mensagemReal = (causa != null) ? causa.getMessage() : e.getMessage();
            fail("Falha de infraestrutura real: " + mensagemReal);
        }
    }
}
