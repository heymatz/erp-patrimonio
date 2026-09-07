package com.erp.patrimonio.menu;

import java.util.Scanner;

import com.erp.patrimonio.infra.ConnectionFactory;
import com.erp.patrimonio.infra.TransactionManager;
import com.erp.patrimonio.repository.CategoriaRepository;
import com.erp.patrimonio.repository.CategoriaRepositoryJdbc;
import com.erp.patrimonio.repository.LocalRepository;
import com.erp.patrimonio.repository.LocalRepositoryJdbc;
import com.erp.patrimonio.repository.PatrimonioRepository;
import com.erp.patrimonio.repository.PatrimonioRepositoryJdbc;
import com.erp.patrimonio.service.CategoriaService;
import com.erp.patrimonio.service.LocalService;
import com.erp.patrimonio.service.PatrimonioService;
import com.erp.patrimonio.util.ConsoleUtils;

public class MenuPrincipal {

    private final Scanner scanner;
    private final ConsoleUtils console;

    private final PatrimonioMenu patrimonioMenu;
    private final LocalMenu localMenu;
    private final CategoriaMenu categoriaMenu;

    public MenuPrincipal() {
        scanner = new Scanner(System.in);
        console = new ConsoleUtils(scanner);

        // 1. Instancia a infraestrutura base de conexões e transações
        ConnectionFactory connectionFactory = new ConnectionFactory();
        TransactionManager transactionManager = new TransactionManager(connectionFactory);

        // 2. Instancia os repositórios injetando a factory e o transactionManager
        PatrimonioRepository patrimonioRepository = new PatrimonioRepositoryJdbc(connectionFactory, transactionManager);
        LocalRepository localRepository = new LocalRepositoryJdbc(connectionFactory, transactionManager);
        CategoriaRepository categoriaRepository = new CategoriaRepositoryJdbc(connectionFactory, transactionManager);

        // 3. Instancia os serviços
        PatrimonioService patrimonioService = new PatrimonioService(patrimonioRepository);
        LocalService localService = new LocalService(localRepository);
        CategoriaService categoriaService = new CategoriaService(categoriaRepository);

        // 4. Instancia os menus
        patrimonioMenu = new PatrimonioMenu(console, patrimonioService, categoriaService, localService);
        localMenu = new LocalMenu(console, localService);
        categoriaMenu = new CategoriaMenu(console, categoriaService);

    }

    public void executar() {
        int opcao;

        do {
            System.out.println("\n=== Menu Principal ===");
            System.out.println("1 - Patrimônios");
            System.out.println("2 - Locais");
            System.out.println("3 - Categorias");
            System.out.println("0 - Sair");

            opcao = console.lerInteiro("Escolha uma opção: ");

            switch (opcao) {
                case 1 -> patrimonioMenu.executar();
                case 2 -> localMenu.executar();
                case 3 -> categoriaMenu.executar();
                case 0 -> System.out.println("Sistema encerrado.");
                default -> System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }
}
