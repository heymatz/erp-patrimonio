package com.erp.patrimonio.menu;

import java.util.Scanner;

import com.erp.patrimonio.model.Categoria;
import com.erp.patrimonio.model.Local;
import com.erp.patrimonio.model.Patrimonio;
import com.erp.patrimonio.service.CategoriaService;
import com.erp.patrimonio.service.LocalService;
import com.erp.patrimonio.service.PatrimonioService;

public class PatrimonioMenu {

    private final Scanner scanner;

    private final PatrimonioService patrimonioService;
    private final CategoriaService categoriaService;
    private final LocalService localService;

    public PatrimonioMenu(
            Scanner scanner,
            PatrimonioService patrimonioService,
            CategoriaService categoriaService,
            LocalService localService) {

        this.scanner = scanner;
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

            opcao = Integer.parseInt(scanner.nextLine());

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
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Descrição: ");
        String descricao = scanner.nextLine();
        Categoria categoria = null;
        while (categoria == null) {
            try {
                System.out.println("=== Categorias ===");
                for (Categoria c : categoriaService.listarTodos()) {
                    System.out.println(c);
                }

                System.out.print("ID da categoria: ");
                int categoriaId = Integer.parseInt(scanner.nextLine());

                categoria = categoriaService.buscarPorId(categoriaId);

            } catch (IllegalArgumentException e) {
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

                System.out.print("ID do local: ");
                int localId = Integer.parseInt(scanner.nextLine());

                local = localService.buscarPorId(localId);

            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.print("Número de série: ");
        String numeroSerie = scanner.nextLine();
        System.out.print("Valor: ");
        double valor = Double.parseDouble(scanner.nextLine());
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
        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao cadastrar patrimônio: " + e.getMessage());
        }
    }

    private void atualizarPatrimonio() {
        System.out.print("ID do patrimônio a ser atualizado: ");
        int id = Integer.parseInt(scanner.nextLine());

        try {
            Patrimonio patrimonioExistente = patrimonioService.buscarPorId(id);

            System.out.print("Novo nome (atual: " + patrimonioExistente.getNome() + "): ");
            String novoNome = scanner.nextLine();

            System.out.print("Nova descrição (atual: " + patrimonioExistente.getDescricao() + "): ");
            String novaDescricao = scanner.nextLine();

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
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Erro ao atualizar patrimônio: " + e.getMessage());
        }
    }

    private void removerPatrimonio() {
        System.out.print("ID do patrimônio a ser removido: ");
        int id = Integer.parseInt(scanner.nextLine());

        try {
            patrimonioService.remover(id);
            System.out.println("Patrimônio removido com sucesso!");
        } catch (IllegalArgumentException e) {
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
        System.out.print("ID do patrimônio a ser buscado: ");
        int id = Integer.parseInt(scanner.nextLine());

        try {
            Patrimonio patrimonio = patrimonioService.buscarPorId(id);
            System.out.println(patrimonio);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
