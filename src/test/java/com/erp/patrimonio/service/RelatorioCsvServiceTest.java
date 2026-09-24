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
        service.exportarPatrimonios(itens, caminhoArquivoTeste.toString(), "Estoque Baixo");

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
                () -> service.exportarPatrimonios(List.of(), caminhoArquivoTeste.toString(), "Estoque Baixo"));

        // Verifica se a mensagem de erro é a esperada
        org.junit.jupiter.api.Assertions.assertEquals("Não há itens para gerar o relatório de Estoque Baixo.",
                exception.getMessage());

        // Garante que o arquivo físico não foi criado no disco
        org.junit.jupiter.api.Assertions.assertFalse(java.nio.file.Files.exists(caminhoArquivoTeste),
                "O arquivo CSV não deveria ser criado.");
    }

    @Test
    void deveLancarExcecaoSeListaForNull(@TempDir Path tempDir) {
        // Arrange
        RelatorioCsvService service = new RelatorioCsvService();
        Path caminho = tempDir.resolve("teste_nulo.csv");

        // Act & Assert
        IllegalArgumentException exception = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.exportarPatrimonios(null, caminho.toString(), "Estoque Baixo"));

        assertEquals("Não há itens para gerar o relatório de Estoque Baixo.", exception.getMessage());
    }

    @Test
    void deveTratarCamposNulosSemLancarNullPointerException(@TempDir Path tempDir) throws Exception {
        // Arrange
        RelatorioCsvService service = new RelatorioCsvService();
        Path caminho = tempDir.resolve("teste_campos_nulos.csv");

        // Usamos o construtor vazio para forçar um objeto nulo e contornar a validação
        Patrimonio itemDefeituoso = new Patrimonio();
        itemDefeituoso.setId(106);
        // Como não definimos o resto, o nome, série, categoria e local ficam a null (e os números a 0)

        // Act
        service.exportarPatrimonios(List.of(itemDefeituoso), caminho.toString(), "Defeituosos");

        // Assert
        List<String> linhas = Files.readAllLines(caminho);
        // Verifica se o cabeçalho foi escrito corretamente
        assertEquals("106;N/A;N/A;N/A;0;0;N/A", linhas.get(1));
    }

    @Test
    void deveLimparPontoEVirgulaDosCamposParaNaoQuebrarCsv(@TempDir Path tempDir) throws Exception {
        // Arrange
        RelatorioCsvService service = new RelatorioCsvService();
        Path caminho = tempDir.resolve("teste_delimitador.csv");

        Categoria cat = new Categoria(1, "Hardware", "TI", TipoItem.ESTOQUE);
        Local loc = new Local(1, "Sala 1", "Sede");
        
        Patrimonio itemMalicioso = new Patrimonio(
                107, "Monitor; Dell; 24", "Desc", cat, loc, "SN;123", 100.0, UnidadeMedida.UNIDADE, 10, 2);

        // Act
        service.exportarPatrimonios(List.of(itemMalicioso), caminho.toString(), "Auditoria");

        // Assert
        List<String> linhas = Files.readAllLines(caminho);
        assertEquals("107;Monitor, Dell, 24;SN,123;Hardware;10;2;Sala 1", linhas.get(1));
    }

    @Test
    void deveLancarExcecaoAmigavelAoTentarSalvarEmCaminhoInvalido() {
        // Arrange
        RelatorioCsvService service = new RelatorioCsvService();
        Categoria cat = new Categoria(1, "TI", "TI", TipoItem.ESTOQUE);
        Local loc = new Local(1, "Sala 1", "Sede");
        Patrimonio p = new Patrimonio(1, "Cabo", "Desc", cat, loc, "SN", 10.0, UnidadeMedida.UNIDADE, 1, 1);
        
        String caminhoInvalido = "Z:/diretorio_falso_que_nao_existe/relatorio.csv";

        // Act & Assert
        org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class,
                () -> service.exportarPatrimonios(List.of(p), caminhoInvalido, "Erro IO"));
    }
}
