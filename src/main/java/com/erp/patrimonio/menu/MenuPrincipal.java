package com.erp.patrimonio.menu;

import java.util.Scanner;

import com.erp.patrimonio.repository.CategoriaRepository;
import com.erp.patrimonio.repository.LocalRepository;
import com.erp.patrimonio.repository.PatrimonioRepository;
import com.erp.patrimonio.service.CategoriaService;
import com.erp.patrimonio.service.LocalService;
import com.erp.patrimonio.service.PatrimonioService;

public class MenuPrincipal {

    private final Scanner scanner;

    private final PatrimonioMenu patrimonioMenu;
    private final LocalMenu localMenu;
    private final CategoriaMenu categoriaMenu;

    public MenuPrincipal() {

        scanner = new Scanner(System.in);

        PatrimonioRepository patrimonioRepository = new PatrimonioRepository();
        LocalRepository localRepository = new LocalRepository();
        CategoriaRepository categoriaRepository = new CategoriaRepository();

        PatrimonioService patrimonioService
                = new PatrimonioService(patrimonioRepository);

        LocalService localService
                = new LocalService(localRepository);

        CategoriaService categoriaService
                = new CategoriaService(categoriaRepository);

        patrimonioMenu = new PatrimonioMenu(
                scanner,
                patrimonioService,
                categoriaService,
                localService
        );

        localMenu = new LocalMenu(
                scanner,
                localService
        );

        categoriaMenu = new CategoriaMenu(
                scanner,
                categoriaService
        );
    }

    public void executar() {
        int opcao;

        do {
            System.out.println("\n=== Menu Principal ===");
            System.out.println("1 - Patrimônios");
            System.out.println("2 - Locais");
            System.out.println("3 - Categorias");
            System.out.println("0 - Sair");

            opcao = Integer.parseInt(scanner.nextLine());

            switch (opcao) {
                case 1 ->
                    patrimonioMenu.executar();

                case 2 ->
                    localMenu.executar();

                case 3 ->
                    categoriaMenu.executar();

                case 0 ->
                    System.out.println("Sistema encerrado.");

                default ->
                    System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }
}
