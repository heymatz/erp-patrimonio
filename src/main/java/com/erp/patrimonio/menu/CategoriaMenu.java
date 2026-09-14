package com.erp.patrimonio.menu;

import com.erp.patrimonio.enums.TipoItem;
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
        TipoItem tipoItem = lerTipoItemCadastro();

        try {
            categoriaService.cadastrar(nome, descricao, tipoItem);
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
            TipoItem novoTipo = lerTipoItemAtualizacao(categoriaExistente.getTipoItem());

            categoriaService.atualizar(
                    id,
                    novoNome.isEmpty() ? categoriaExistente.getNome() : novoNome,
                    novaDescricao.isEmpty() ? categoriaExistente.getDescricao() : novaDescricao,
                    novoTipo);

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

    private TipoItem lerTipoItemCadastro() {
        while (true) {
            int opcao = console.lerInteiro("Tipo de Item (1 - Patrimônio, 2 - Estoque): ");
            if (opcao == 1)
                return TipoItem.PATRIMONIO;
            if (opcao == 2)
                return TipoItem.ESTOQUE;
            System.out.println("Opção inválida. Digite 1 ou 2.");
        }
    }

    private TipoItem lerTipoItemAtualizacao(TipoItem tipoAtual) {
        while (true) {
            int opcao = console
                    .lerInteiro("Novo Tipo (1 - Patrimônio, 2 - Estoque, 0 - Manter [" + tipoAtual.name() + "]): ");
            if (opcao == 0)
                return tipoAtual;
            if (opcao == 1)
                return TipoItem.PATRIMONIO;
            if (opcao == 2)
                return TipoItem.ESTOQUE;
            System.out.println("Opção inválida. Digite 0, 1 ou 2.");
        }
    }
}
