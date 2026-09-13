package com.erp.patrimonio.service;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

import com.erp.patrimonio.model.Patrimonio;

public class RelatorioCsvService {

    /**
     * Recebe uma lista de patrimônios e gera um arquivo CSV físico no computador.
     * 
     * @param itens A lista de itens que estão com estoque baixo
     * @param caminhoArquivo Onde o arquivo será salvo (ex: "relatorios/estoque_baixo.csv")
     */
    public void exportarEstoqueBaixo(List<Patrimonio> itens, String caminhoArquivo) {
        
        // Impede a geração se a lista estiver vazia
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("Não há itens com estoque baixo para gerar o relatório.");
        }

        // O try-with-resources garante que o arquivo será fechado e salvo corretamente ao final, 
        // mesmo se der erro no meio do caminho.
        try (PrintWriter writer = new PrintWriter(new FileWriter(caminhoArquivo))) {
            
            // Escreve o Cabeçalho da tabela
            writer.println("ID;Nome do Item;Numero de Serie;Categoria;Qtd Atual;Estoque Minimo;Local");

            // Escreve os Dados iterando sobre a lista de itens
            for (Patrimonio p : itens) {
                // Usa o printf para formatar a string de forma limpa. 
                // Usa ponto e vírgula como separador como padrão para arquivos CSV.
                writer.printf("%d;%s;%s;%s;%d;%d;%s%n",
                        p.getId(),
                        p.getNome(),
                        p.getNumeroSerie(),
                        p.getCategoria().getNome(),
                        p.getQuantidade(),
                        p.getEstoqueMinimo(),
                        p.getLocal().getNome()
                );
            }
            
            System.out.println("SUCESSO: Relatório CSV gerado em -> " + caminhoArquivo);
            
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar o arquivo CSV de Estoque Baixo: " + e.getMessage(), e);
        }
    }
}
