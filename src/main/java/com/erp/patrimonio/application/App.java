package com.erp.patrimonio.application;

import java.util.Scanner;

import com.erp.patrimonio.model.Categoria;
import com.erp.patrimonio.model.Local;
import com.erp.patrimonio.model.Patrimonio;
import com.erp.patrimonio.repository.CategoriaRepository;
import com.erp.patrimonio.repository.LocalRepository;
import com.erp.patrimonio.repository.PatrimonioRepository;
import com.erp.patrimonio.service.CategoriaService;
import com.erp.patrimonio.service.LocalService;
import com.erp.patrimonio.service.PatrimonioService;

public class App {

    private final Scanner scanner;
    private final PatrimonioService patrimonioService;
    private final LocalService localService;
    private final CategoriaService categoriaService;

    public App() {
        scanner = new Scanner(System.in);

        PatrimonioRepository repository = new PatrimonioRepository();
        patrimonioService = new PatrimonioService(repository);

        LocalRepository localRepository = new LocalRepository();
        localService = new LocalService(localRepository);

        CategoriaRepository categoriaRepository = new CategoriaRepository();
        categoriaService = new CategoriaService(categoriaRepository);
    }

    public static void main(String[] args) {
        App app = new App();
        app.executar();
    }

    public void executar() {
        int opcao;

        do {
            System.out.println("\n=== Menu Principal ===");
            System.out.println("1 - Cadastrar Patrimônios");
            System.out.println("2 - Cadastrar Locais");
            System.out.println("3 - Cadastrar Categorias");
            System.out.println("0 - Sair");

            opcao = Integer.parseInt(scanner.nextLine());

            switch (opcao) {
                case 1 ->
                    menuPatrimonios();
                case 2 ->
                    menuLocais();
                case 3 ->
                    menuCategorias();
                case 0 ->
                    System.out.println("O sistema foi encerrado.");
                default ->
                    System.out.println("Opção inválida.");
            }

        } while (opcao != 0);
    }

    private void menuPatrimonios() {

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
                    System.out.println("Voltar ao menu principal.");
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

            Patrimonio atualizado = patrimonioService.atualizar(
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

    private void menuLocais() {
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
                    System.out.println("Voltar ao menu principal.");
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

            Local atualizado = localService.atualizar(
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

    private void menuCategorias() {
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
                    System.out.println("Voltar ao menu principal.");
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

            Categoria atualizado = categoriaService.atualizar(
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
