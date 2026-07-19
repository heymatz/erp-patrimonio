package com.erp.patrimonio.menu;

import com.erp.patrimonio.exception.DuplicidadeException;
import com.erp.patrimonio.exception.EntidadeNaoEncontradaException;
import com.erp.patrimonio.exception.ValidacaoException;
import com.erp.patrimonio.model.Categoria;
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

        try {
            patrimonioService.cadastrar(
                    nome,
                    descricao,
                    categoria,
                    local,
                    numeroSerie,
                    valor
            );
            System.out.println("Patrimônio cadastrado com sucesso!");
        } catch (DuplicidadeException | ValidacaoException e) {
            System.out.println("Erro ao cadastrar patrimônio: " + e.getMessage());
        }
    }

    private void atualizarPatrimonio() {

        int id = console.lerInteiro("ID do patrimônio a ser atualizado: ");

        try {
            Patrimonio patrimonioExistente = patrimonioService.buscarPorId(id);

            String novoNome = console.lerTexto(
                    "Novo nome (atual: " + patrimonioExistente.getNome() + "): "
            );
            String novaDescricao = console.lerTexto(
                    "Nova descrição (atual: " + patrimonioExistente.getDescricao() + "): "
            );
            patrimonioService.atualizar(
                    id,
                    novoNome.isEmpty() ? patrimonioExistente.getNome() : novoNome,
                    novaDescricao.isEmpty() ? patrimonioExistente.getDescricao() : novaDescricao,
                    patrimonioExistente.getCategoria(),
                    patrimonioExistente.getLocal(),
                    patrimonioExistente.getNumeroSerie(),
                    patrimonioExistente.getValor()
            );

            System.out.println("Patrimônio atualizado com sucesso!");
        } catch (EntidadeNaoEncontradaException
                | DuplicidadeException
                | ValidacaoException
                | IllegalStateException e) {
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

    private void listarTodosPatrimonios() {
        System.out.println("=== Lista de Patrimônios ===");
        for (Patrimonio patrimonio : patrimonioService.listarTodos()) {
            System.out.println(patrimonio);
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
}
