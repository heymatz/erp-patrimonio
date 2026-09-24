package com.erp.patrimonio.menu;

import java.util.List;

import com.erp.patrimonio.enums.UnidadeMedida;
import com.erp.patrimonio.exception.DuplicidadeException;
import com.erp.patrimonio.exception.EntidadeNaoEncontradaException;
import com.erp.patrimonio.exception.ValidacaoException;
import com.erp.patrimonio.model.Categoria;
import com.erp.patrimonio.model.HistoricoMov;
import com.erp.patrimonio.model.Local;
import com.erp.patrimonio.model.Patrimonio;
import com.erp.patrimonio.service.CategoriaService;
import com.erp.patrimonio.service.LocalService;
import com.erp.patrimonio.service.PatrimonioService;
import com.erp.patrimonio.util.ConsoleUtils;

public class PatrimonioMenu {

    private final PatrimonioService patrimonioService;
    private final CategoriaService categoriaService;
    private final LocalService localService;
    private final ConsoleUtils console;

    public PatrimonioMenu(
            ConsoleUtils console,
            PatrimonioService patrimonioService,
            CategoriaService categoriaService,
            LocalService localService) {

        this.console = console;
        this.patrimonioService = patrimonioService;
        this.categoriaService = categoriaService;
        this.localService = localService;
    }

    public void executar() {

        int opcao;

        do {
            System.out.println("\n=== Menu Patrimônios ===");
            System.out.println("1 - Cadastrar");
            System.out.println("2 - Atualizar");
            System.out.println("3 - Remover");
            System.out.println("4 - Listar");
            System.out.println("5 - Buscar por ID");
            System.out.println("6 - Listar histórico de movimentações");
            System.out.println("7 - Relatório: Itens com Estoque Baixo");
            System.out.println("8 - Exportar Relatório (CSV)");
            System.out.println("0 - Voltar");

            opcao = console.lerInteiro("Escolha uma opção: ");

            switch (opcao) {
                case 1 ->
                    cadastrarPatrimonio();
                case 2 ->
                    atualizarPatrimonio();
                case 3 ->
                    removerPatrimonio();
                case 4 ->
                    listarTodosPatrimonios();
                case 5 ->
                    buscarPatrimonioPorId();
                case 6 ->
                    listarHistoricoMovimentacoes();
                case 7 ->
                    relatorioEstoqueBaixo();
                case 8 ->
                    exportarParaCsv();
                case 0 ->
                    System.out.println("Voltando...");
                default ->
                    System.out.println("Opção inválida.");
            }

        } while (opcao != 0);
    }

    private void cadastrarPatrimonio() {
        String nome = console.lerTexto("Nome: ");
        String descricao = console.lerTexto("Descrição: ");

        Categoria categoria = null;
        while (categoria == null) {
            try {
                System.out.println("=== Categorias ===");
                for (Categoria c : categoriaService.listarTodos()) {
                    System.out.println(c);
                }

                int categoriaId = console.lerInteiro("ID da categoria: ");
                categoria = categoriaService.buscarPorId(categoriaId);

            } catch (EntidadeNaoEncontradaException e) {
                System.out.println(e.getMessage());
            }
        }

        Local local = null;
        while (local == null) {
            try {
                System.out.println("=== Locais ===");
                for (Local l : localService.listarTodos()) {
                    System.out.println(l);
                }

                int localId = console.lerInteiro("ID do local: ");
                local = localService.buscarPorId(localId);

            } catch (EntidadeNaoEncontradaException e) {
                System.out.println(e.getMessage());
            }
        }

        String numeroSerie = console.lerTexto("Número de série: ");
        double valor = console.lerDouble("Valor: ");
        
        // Coletando a quantidade e o estoque mínimo
        int quantidade = console.lerInteiro("Quantidade: ");
        int estoqueMinimo = console.lerInteiro("Estoque mínimo: ");

        UnidadeMedida unidadeMedida = null;
        while (unidadeMedida == null) {
            System.out.println("\n=== Unidades de Medida ===");
            for (UnidadeMedida unidade : UnidadeMedida.values()) {
                System.out.printf("%-15s (%s)%n",
                        unidade.name(),
                        unidade.getSigla());
            }

            try {
                String opcao = console.lerTexto("Digite a unidade: ");
                unidadeMedida = UnidadeMedida.fromString(opcao);
            } catch (ValidacaoException e) {
                System.out.println(e.getMessage());
            }
        }

        // Chamada única ao Service após coletar e validar todos os dados com segurança
        try {
            patrimonioService.cadastrar(
                    nome,
                    descricao,
                    categoria,
                    local,
                    numeroSerie,
                    valor,
                    unidadeMedida,
                    quantidade,      
                    estoqueMinimo);  
            System.out.println("Patrimônio cadastrado com sucesso!");
        } catch (DuplicidadeException | ValidacaoException e) {
            System.out.println("Erro ao cadastrar patrimônio: " + e.getMessage());
        }
    }

    private void buscarPatrimonioPorId() {

        int id = console.lerInteiro("ID do patrimônio a ser buscado: ");

        try {
            Patrimonio patrimonio = patrimonioService.buscarPorId(id);
            System.out.println(patrimonio);
        } catch (EntidadeNaoEncontradaException e) {
            System.out.println(e.getMessage());
        }
    }

    private void listarTodosPatrimonios() {
        System.out.println("=== Lista de Patrimônios ===");
        for (Patrimonio patrimonio : patrimonioService.listarTodos()) {
            System.out.println(patrimonio);
        }
    }

    private void relatorioEstoqueBaixo() {
        System.out.println("\n=== ALERTA: ITENS COM ESTOQUE BAIXO ===");
        
        List<Patrimonio> itens = patrimonioService.listarEstoqueBaixo();
        
        if (itens.isEmpty()) {
            System.out.println("Tudo certo! Nenhum patrimônio está abaixo do estoque mínimo.");
            return;
        }

        for (Patrimonio p : itens) {
            System.out.printf("[ID: %d] %s | Quantidade Atual: %d | Estoque Mínimo: %d | Local: %s%n",
                    p.getId(),
                    p.getNome(),
                    p.getQuantidade(),
                    p.getEstoqueMinimo(),
                    p.getLocal().getNome());
        }
        System.out.println("=======================================");
    }


    private void exportarParaCsv() {
        System.out.println("\n=== Exportar para CSV ===");
        System.out.println("1 - Estoque Total (Todos os patrimônios)");
        System.out.println("2 - Somente Estoque Baixo");
        int opcaoFiltro = console.lerInteiro("Escolha o filtro desejado: ");

        List<Patrimonio> listaParaExportar;
        String tipoRelatorio;
        String nomeArquivo;

        if (opcaoFiltro == 1) {
            listaParaExportar = patrimonioService.listarTodos();
            tipoRelatorio = "Estoque Total";
            nomeArquivo = "estoque_total.csv";
        } else if (opcaoFiltro == 2) {
            listaParaExportar = patrimonioService.listarEstoqueBaixo();
            tipoRelatorio = "Estoque Baixo";
            nomeArquivo = "estoque_baixo.csv";
        } else {
            System.out.println("Opção inválida. Operação cancelada.");
            return;
        }

        // Instancia o serviço e manda gerar
        com.erp.patrimonio.service.RelatorioCsvService geradorCsv = new com.erp.patrimonio.service.RelatorioCsvService();
        try {
            // Requer que você tenha atualizado o RelatorioCsvService com os 3 parâmetros!
            geradorCsv.exportarPatrimonios(listaParaExportar, nomeArquivo, tipoRelatorio);
        } catch (IllegalArgumentException e) {
            System.out.println("Aviso: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void listarHistoricoMovimentacoes() {
        int id = console.lerInteiro("ID do patrimônio para ver o histórico: ");

        try {
            // Chama o método do Service para listar o histórico de movimentações do patrimônio
            List<HistoricoMov> historico = patrimonioService.listarHistoricoMovimentacoes(id);

            if (historico.isEmpty()) {
                System.out.println("\nNenhuma movimentação registrada para este patrimônio.");
                return;
            }

            System.out.println("\n=== Histórico de Movimentação ===");
            for (HistoricoMov mov : historico) {
                // Exibe os detalhes da movimentação, incluindo data, origem, destino e motivo
                System.out.printf("Data: %s | Origem (ID: %d) -> Destino (ID: %d) | Motivo: %s%n",
                        mov.getDataMovimentacao(),
                        mov.getLocalOrigem().getId(),
                        mov.getLocalDestino().getId(),
                        mov.getMotivo());
            }
            System.out.println("=================================");

        } catch (EntidadeNaoEncontradaException e) {
            // Se o ID do patrimônio não existir, o Service lança a exceção
            System.out.println(e.getMessage());
        }
    }

    private void atualizarPatrimonio() {

        int id = console.lerInteiro("ID do patrimônio a ser atualizado: ");

        try {
            Patrimonio patrimonioExistente = patrimonioService.buscarPorId(id);

            String novoNome = console.lerTexto(
                    "Novo nome (atual: " + patrimonioExistente.getNome() + "): ");
            String novaDescricao = console.lerTexto(
                    "Nova descrição (atual: " + patrimonioExistente.getDescricao() + "): ");
            int novaQuantidade = console.lerInteiro(
                    "Nova quantidade (atual: " + patrimonioExistente.getQuantidade() + "): ");
            int novoEstoqueMinimo = console.lerInteiro(
                    "Novo estoque mínimo (atual: " + patrimonioExistente.getEstoqueMinimo() + "): ");

            // --- LÓGICA DE TRANSFERÊNCIA DE LOCAL ---
            Local localAtual = patrimonioExistente.getLocal();
            System.out.println("Local atual: " + localAtual.getNome() + " (ID: " + localAtual.getId() + ")");
            int novoLocalId = console.lerInteiro("Novo ID do local (Digite 0 para manter o atual): ");

            Local novoLocal = localAtual;
            String motivoMovimentacao = "";

            if (novoLocalId != 0 && novoLocalId != localAtual.getId()) {
                // Busca o novo local no banco. Se não existir, vai lançar
                // EntidadeNaoEncontradaException
                novoLocal = localService.buscarPorId(novoLocalId);
                motivoMovimentacao = console.lerTexto("Motivo da transferência: ");
            }
            // --- FIM DA LÓGICA DE TRANSFERÊNCIA DE LOCAL ---

            // Chamada única ao Service para atualizar o patrimônio, incluindo a
            // movimentação se houver
            patrimonioService.atualizar(
                    id,
                    novoNome.isEmpty() ? patrimonioExistente.getNome() : novoNome,
                    novaDescricao.isEmpty() ? patrimonioExistente.getDescricao() : novaDescricao,
                    patrimonioExistente.getCategoria(),
                    novoLocal, // Passando o local (novo ou o mesmo de antes)
                    patrimonioExistente.getNumeroSerie(),
                    patrimonioExistente.getValor(),
                    patrimonioExistente.getUnidadeMedida(),
                    novaQuantidade,     // Passando a nova quantidade na ordem certa
                    novoEstoqueMinimo,  // Passando o novo estoque na ordem certa
                    motivoMovimentacao); // Novo parâmetro para o motivo da movimentação

            System.out.println("Patrimônio atualizado com sucesso!");
        } catch (EntidadeNaoEncontradaException
                | DuplicidadeException
                | ValidacaoException e) {
            System.out.println("Erro ao atualizar patrimônio: " + e.getMessage());
        }
    }

    private void removerPatrimonio() {

        int id = console.lerInteiro("ID do patrimônio a ser removido: ");

        try {
            patrimonioService.remover(id);
            System.out.println("Patrimônio removido com sucesso!");
        } catch (EntidadeNaoEncontradaException e) {
            System.out.println("Erro ao remover patrimônio: " + e.getMessage());
        }
    }
}
