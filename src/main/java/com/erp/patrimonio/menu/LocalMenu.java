package com.erp.patrimonio.menu;

import java.util.Scanner;

import com.erp.patrimonio.model.Local;
import com.erp.patrimonio.service.LocalService;

public class LocalMenu {

    private final Scanner scanner;
    private final LocalService localService;

    public LocalMenu(
            Scanner scanner,
            LocalService localService) {

        this.scanner = scanner;
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

            opcao = Integer.parseInt(scanner.nextLine());

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
        System.out.print("Nome do local: ");
        String nome = scanner.nextLine();
        System.out.print("Descrição do local: ");
        String descricao = scanner.nextLine();

        try {
            localService.cadastrar(nome, descricao);
            System.out.println("Local cadastrado com sucesso!");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao cadastrar local: " + e.getMessage());
        }
    }

    private void atualizarLocal() {
        System.out.print("ID do local a ser atualizado: ");
        int id = Integer.parseInt(scanner.nextLine());

        try {
            Local localExistente = localService.buscarPorId(id);

            System.out.print("Novo nome (atual: " + localExistente.getNome() + "): ");
            String novoNome = scanner.nextLine();

            System.out.print("Nova descrição (atual: " + localExistente.getDescricao() + "): ");
            String novaDescricao = scanner.nextLine();

            localService.atualizar(
                    id,
                    novoNome.isEmpty() ? localExistente.getNome() : novoNome,
                    novaDescricao.isEmpty() ? localExistente.getDescricao() : novaDescricao
            );

            System.out.println("Local atualizado com sucesso!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Erro ao atualizar local: " + e.getMessage());
        }
    }

    private void removerLocal() {
        System.out.print("ID do local a ser removido: ");
        int id = Integer.parseInt(scanner.nextLine());

        try {
            localService.remover(id);
            System.out.println("Local removido com sucesso!");
        } catch (IllegalArgumentException e) {
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
        System.out.print("ID do local a ser buscado: ");
        int id = Integer.parseInt(scanner.nextLine());

        try {
            Local local = localService.buscarPorId(id);
            System.out.println("Local encontrado: " + local);
        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao buscar local: " + e.getMessage());
        }
    }
}
