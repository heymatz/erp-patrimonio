package com.erp.patrimonio.menu;

import com.erp.patrimonio.exception.DuplicidadeException;
import com.erp.patrimonio.exception.EntidadeNaoEncontradaException;
import com.erp.patrimonio.exception.ValidacaoException;
import com.erp.patrimonio.model.Categoria;
import com.erp.patrimonio.service.CategoriaService;
import com.erp.patrimonio.util.ConsoleUtils;

public class CategoriaMenu {

    private final ConsoleUtils console;
    private final CategoriaService categoriaService;

    public CategoriaMenu(
            ConsoleUtils console,
            CategoriaService categoriaService) {

        this.console = console;
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

            opcao = console.lerInteiro("Escolha uma opção: ");

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

        String nome = console.lerTexto("Nome da categoria: ");
        String descricao = console.lerTexto("Descrição da categoria: ");

        try {
            categoriaService.cadastrar(nome, descricao);
            System.out.println("Categoria cadastrada com sucesso!");
        } catch (DuplicidadeException | ValidacaoException e) {
            System.out.println("Erro ao cadastrar categoria: " + e.getMessage());
        }
    }

    private void atualizarCategoria() {

        int id = console.lerInteiro("ID da categoria a ser atualizada: ");

        try {
            Categoria categoriaExistente = categoriaService.buscarPorId(id);
            String novoNome = console.lerTexto(
                    "Novo nome (atual: " + categoriaExistente.getNome() + "): ");
            String novaDescricao = console.lerTexto(
                    "Nova descrição (atual: " + categoriaExistente.getDescricao() + "): ");

            categoriaService.atualizar(
                    id,
                    novoNome.isEmpty() ? categoriaExistente.getNome() : novoNome,
                    novaDescricao.isEmpty() ? categoriaExistente.getDescricao() : novaDescricao
            );

            System.out.println("Categoria atualizada com sucesso!");
        } catch (EntidadeNaoEncontradaException
                | DuplicidadeException
                | ValidacaoException e) {
            System.out.println("Erro ao atualizar categoria: " + e.getMessage());
        }
    }

    private void removerCategoria() {

        int id = console.lerInteiro("ID da categoria a ser removida: ");

        try {
            categoriaService.remover(id);
            System.out.println("Categoria removida com sucesso!");
        } catch (EntidadeNaoEncontradaException e) {
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

        int id = console.lerInteiro("ID da categoria a ser buscada: ");

        try {
            Categoria categoria = categoriaService.buscarPorId(id);
            System.out.println("Categoria encontrada: " + categoria);
        } catch (EntidadeNaoEncontradaException e) {
            System.out.println("Erro ao buscar categoria: " + e.getMessage());
        }
    }
}
