package com.erp.patrimonio.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.erp.patrimonio.enums.TipoItem;
import com.erp.patrimonio.enums.UnidadeMedida;
import com.erp.patrimonio.model.Categoria;
import com.erp.patrimonio.model.Local;
import com.erp.patrimonio.model.Patrimonio;

public class RelatorioCsvServiceTest {

    @Test
    void deveGerarArquivoCsvComSucessoEFormatarCorretamente(@TempDir Path tempDir) throws Exception {
        // Arrange
        RelatorioCsvService service = new RelatorioCsvService();

        // Cria um caminho para o arquivo dentro da pasta temporária do JUnit
        Path caminhoArquivoTeste = tempDir.resolve("teste_estoque_baixo.csv");

        // Cria dados simulados na memória para testar a exportação
        Categoria categoria = new Categoria(1, "Material de Escritório", "Itens de uso diário", TipoItem.ESTOQUE);
        Local local = new Local(1, "Almoxarifado Central", "Prateleira A");

        Patrimonio caneta = new Patrimonio(
                105,
                "Caneta Azul",
                "Caneta esferográfica padrão",
                categoria,
                local,
                "SN-999",
                1.50,
                UnidadeMedida.UNIDADE,
                2,
                50);

        List<Patrimonio> itens = List.of(caneta);

        // Act
        service.exportarEstoqueBaixo(itens, caminhoArquivoTeste.toString());

        // Assert
        assertTrue(Files.exists(caminhoArquivoTeste), "O arquivo CSV deveria ter sido criado fisicamente.");

        // Lê o conteúdo do arquivo gerado para verificar se os dados foram escritos
        // corretamente
        List<String> linhas = Files.readAllLines(caminhoArquivoTeste);

        assertEquals(2, linhas.size(), "O arquivo deve conter exatamente 2 linhas (1 cabeçalho + 1 dado).");

        // Verifica se o cabeçalho foi escrito corretamente
        assertEquals("ID;Nome do Item;Numero de Serie;Categoria;Qtd Atual;Estoque Minimo;Local", linhas.get(0));

        // Verifica se os dados do patrimônio foram formatados corretamente com o
        // separador ';'
        assertEquals("105;Caneta Azul;SN-999;Material de Escritório;2;50;Almoxarifado Central", linhas.get(1));
    }

    @Test
    void deveLancarExcecaoENaoGerarCsvSeListaEstiverVazia(@TempDir Path tempDir) {
        // Arrange
        RelatorioCsvService service = new RelatorioCsvService();
        Path caminhoArquivoTeste = tempDir.resolve("teste_vazio.csv");

        // Act & Assert
        // Verifica se o sistema lança a exceção corretamente
        IllegalArgumentException exception = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.exportarEstoqueBaixo(List.of(), caminhoArquivoTeste.toString()));

        // Verifica se a mensagem de erro é a esperada
        org.junit.jupiter.api.Assertions.assertEquals("Não há itens com estoque baixo para gerar o relatório.",
                exception.getMessage());

        // Garante que o arquivo físico não foi criado no disco
        org.junit.jupiter.api.Assertions.assertFalse(java.nio.file.Files.exists(caminhoArquivoTeste),
                "O arquivo CSV não deveria ser criado.");
    }
}
