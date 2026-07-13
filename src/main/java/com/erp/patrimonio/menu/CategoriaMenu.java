package com.erp.patrimonio.menu;

import java.util.Scanner;

import com.erp.patrimonio.model.Categoria;
import com.erp.patrimonio.service.CategoriaService;

public class CategoriaMenu {

    private final Scanner scanner;
    private final CategoriaService categoriaService;

    public CategoriaMenu(
            Scanner scanner,
            CategoriaService categoriaService) {

        this.scanner = scanner;
        this.categoriaService = categoriaService;
    }

    public void executar() {

        int opcao;

        do {
            System.out.println("\n=== Menu Categorias ===");
            System.out.println("1 - Cadastrar");
            System.out.println("2 - Atualizar");
            System.out.println("3 - Remover");
            System.out.println("4 - Listar");
            System.out.println("5 - Buscar por ID");
            System.out.println("0 - Voltar");

            opcao = Integer.parseInt(scanner.nextLine());

            switch (opcao) {
                case 1 ->
                    cadastrarCategoria();
                case 2 ->
                    atualizarCategoria();
                case 3 ->
                    removerCategoria();
                case 4 ->
                    listarTodasCategorias();
                case 5 ->
                    buscarCategoriaPorId();
                case 0 ->
                    System.out.println("Voltando...");
                default ->
                    System.out.println("Opção inválida.");
            }

        } while (opcao != 0);
    }

    private void cadastrarCategoria() {
        System.out.print("Nome da categoria: ");
        String nome = scanner.nextLine();
        System.out.print("Descrição da categoria: ");
        String descricao = scanner.nextLine();

        try {
            categoriaService.cadastrar(nome, descricao);
            System.out.println("Categoria cadastrada com sucesso!");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao cadastrar categoria: " + e.getMessage());
        }
    }

    private void atualizarCategoria() {
        System.out.print("ID da categoria a ser atualizada: ");
        int id = Integer.parseInt(scanner.nextLine());

        try {
            Categoria categoriaExistente = categoriaService.buscarPorId(id);

            System.out.print("Novo nome (atual: " + categoriaExistente.getNome() + "): ");
            String novoNome = scanner.nextLine();

            System.out.print("Nova descrição (atual: " + categoriaExistente.getDescricao() + "): ");
            String novaDescricao = scanner.nextLine();

            categoriaService.atualizar(
                    id,
                    novoNome.isEmpty() ? categoriaExistente.getNome() : novoNome,
                    novaDescricao.isEmpty() ? categoriaExistente.getDescricao() : novaDescricao
            );

            System.out.println("Categoria atualizada com sucesso!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Erro ao atualizar categoria: " + e.getMessage());
        }
    }

    private void removerCategoria() {
        System.out.print("ID da categoria a ser removida: ");
        int id = Integer.parseInt(scanner.nextLine());

        try {
            categoriaService.remover(id);
            System.out.println("Categoria removida com sucesso!");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao remover categoria: " + e.getMessage());
        }
    }

    private void listarTodasCategorias() {
        System.out.println("=== Lista de Categorias ===");
        for (Categoria categoria : categoriaService.listarTodos()) {
            System.out.println(categoria);
        }
    }

    private void buscarCategoriaPorId() {
        System.out.print("ID da categoria a ser buscada: ");
        int id = Integer.parseInt(scanner.nextLine());

        try {
            Categoria categoria = categoriaService.buscarPorId(id);
            System.out.println("Categoria encontrada: " + categoria);
        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao buscar categoria: " + e.getMessage());
        }
    }
}
