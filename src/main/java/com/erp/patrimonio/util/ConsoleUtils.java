package com.erp.patrimonio.util;

import java.util.Scanner;

public class ConsoleUtils {

    private final Scanner scanner;

    public ConsoleUtils(Scanner scanner) {
        this.scanner = scanner;
    }

    public String lerTexto(String mensagem) {

        while (true) {

            System.out.print(mensagem);

            String texto = scanner.nextLine().trim();

            if (!texto.isEmpty()) {
                return texto;
            }

            System.out.println("O texto não pode ficar vazio.");
        }
    }

    public int lerInteiro(String mensagem) {

        while (true) {

            System.out.print(mensagem);

            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Digite um número inteiro.");
            }
        }
    }

    public double lerDouble(String mensagem) {

        while (true) {

            System.out.print(mensagem);

            try {
                return Double.parseDouble(
                        scanner.nextLine().replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Digite um número.");
            }
        }
    }
}
