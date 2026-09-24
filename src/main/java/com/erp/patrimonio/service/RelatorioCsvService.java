package com.erp.patrimonio.service;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

import com.erp.patrimonio.model.Patrimonio;

public class RelatorioCsvService {

    /**
     * Recebe uma lista de patrimônios e gera um arquivo CSV físico no computador.
     * 
     * @param itens          A lista de itens (pode ser estoque total, baixo, etc.)
     * @param caminhoArquivo Onde o arquivo será salvo (ex:
     *                       "relatorios/estoque.csv")
     * @param tipoRelatorio  Nome do relatório para exibir nas mensagens e logs
     */
    public void exportarPatrimonios(List<Patrimonio> itens, String caminhoArquivo, String tipoRelatorio) {

        // Impede a geração se a lista estiver vazia
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("Não há itens para gerar o relatório de " + tipoRelatorio + ".");
        }

        // O try-with-resources garante que o arquivo será fechado e salvo corretamente
        // ao final,
        // mesmo se der erro no meio do caminho.
        try (PrintWriter writer = new PrintWriter(new FileWriter(caminhoArquivo))) {

            // Escreve o Cabeçalho da tabela
            writer.println("ID;Nome do Item;Numero de Serie;Categoria;Qtd Atual;Estoque Minimo;Local");

            // Escreve os Dados iterando sobre a lista de itens
            for (Patrimonio p : itens) {

                // Trata os campos que podem ser nulos para evitar NullPointerException
                String nomeCategoria = (p.getCategoria() != null) ? p.getCategoria().getNome() : null;
                String nomeLocal = (p.getLocal() != null) ? p.getLocal().getNome() : null;

                // Usa o printf para formatar a string de forma limpa.
                // Usa ponto e vírgula como separador como padrão para arquivos CSV.
                writer.printf("%d;%s;%s;%s;%d;%d;%s%n",
                        p.getId(),
                        tratarCampoCsv(p.getNome()),
                        tratarCampoCsv(p.getNumeroSerie()),
                        tratarCampoCsv(nomeCategoria),
                        p.getQuantidade(),
                        p.getEstoqueMinimo(),
                        tratarCampoCsv(nomeLocal));
            }

            System.out.println("SUCESSO: Relatório CSV (" + tipoRelatorio + ") gerado em -> " + caminhoArquivo);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar o arquivo CSV de " + tipoRelatorio + ": " + e.getMessage(), e);
        }
    }

    private String tratarCampoCsv(String valor) {
        if (valor == null) {
            return "N/A"; // Resolve o NullPointerException
        }
        // Substitui o ponto e vírgula por vírgula normal para não quebrar a coluna no
        // Excel
        return valor.replace(";", ",");
    }
}
