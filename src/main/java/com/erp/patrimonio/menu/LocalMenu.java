package com.erp.patrimonio.menu;

import com.erp.patrimonio.exception.DuplicidadeException;
import com.erp.patrimonio.exception.EntidadeNaoEncontradaException;
import com.erp.patrimonio.exception.ValidacaoException;
import com.erp.patrimonio.model.Local;
import com.erp.patrimonio.service.LocalService;
import com.erp.patrimonio.util.ConsoleUtils;

public class LocalMenu {

    private final ConsoleUtils console;
    private final LocalService localService;

    public LocalMenu(
            ConsoleUtils console,
            LocalService localService) {

        this.console = console;
        this.localService = localService;
    }

    public void executar() {

        int opcao;

        do {

            System.out.println("\n=== Menu Locais ===");
            System.out.println("1 - Cadastrar");
            System.out.println("2 - Atualizar");
            System.out.println("3 - Remover");
            System.out.println("4 - Listar");
            System.out.println("5 - Buscar por ID");
            System.out.println("0 - Voltar");

            opcao = console.lerInteiro("Escolha uma opção: ");

            switch (opcao) {

                case 1 ->
                    cadastrarLocal();
                case 2 ->
                    atualizarLocal();
                case 3 ->
                    removerLocal();
                case 4 ->
                    listarTodosLocais();
                case 5 ->
                    buscarLocalPorId();
                case 0 ->
                    System.out.println("Voltando...");
                default ->
                    System.out.println("Opção inválida.");

            }

        } while (opcao != 0);
    }

    private void cadastrarLocal() {

        String nome = console.lerTexto("Nome do local: ");
        String descricao = console.lerTexto("Descrição do local: ");

        try {
            localService.cadastrar(nome, descricao);
            System.out.println("Local cadastrado com sucesso!");
        } catch (DuplicidadeException | ValidacaoException e) {
            System.out.println("Erro ao cadastrar local: " + e.getMessage());
        }
    }

    private void atualizarLocal() {

        int id = console.lerInteiro("ID do local a ser atualizado: ");

        try {
            Local localExistente = localService.buscarPorId(id);

            String novoNome = console.lerTexto(
                    "Novo nome (atual: " + localExistente.getNome() + "): "
            );

            String novaDescricao = console.lerTexto(
                    "Nova descrição (atual: " + localExistente.getDescricao() + "): "
            );

            localService.atualizar(
                    id,
                    novoNome.isEmpty() ? localExistente.getNome() : novoNome,
                    novaDescricao.isEmpty() ? localExistente.getDescricao() : novaDescricao
            );

            System.out.println("Local atualizado com sucesso!");
        } catch (EntidadeNaoEncontradaException
                | DuplicidadeException
                | ValidacaoException e) {
            System.out.println("Erro ao atualizar local: " + e.getMessage());
        }
    }

    private void removerLocal() {

        int id = console.lerInteiro("ID do local a ser removido: ");

        try {
            localService.remover(id);
            System.out.println("Local removido com sucesso!");
        } catch (EntidadeNaoEncontradaException e) {
            System.out.println("Erro ao remover local: " + e.getMessage());
        }
    }

    private void listarTodosLocais() {
        System.out.println("=== Lista de Locais ===");
        for (Local local : localService.listarTodos()) {
            System.out.println(local);
        }
    }

    private void buscarLocalPorId() {

        int id = console.lerInteiro("ID do local a ser buscado: ");

        try {
            Local local = localService.buscarPorId(id);
            System.out.println("Local encontrado: " + local);
        } catch (EntidadeNaoEncontradaException e) {
            System.out.println("Erro ao buscar local: " + e.getMessage());
        }
    }
}
