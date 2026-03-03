package co.edu.javeriana.ingsoft.vibe.old;

import java.util.Scanner;

public class Calculadora {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("\n=== CALCULADORA ENTERA ===");
            System.out.println("1. Sumar");
            System.out.println("2. Restar");
            System.out.println("3. Multiplicar");
            System.out.println("4. Dividir");
            System.out.println("5. Negar número");
            System.out.println("6. Valor absoluto");
            System.out.println("7. Incrementar");
            System.out.println("8. Decrementar");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            opcion = scanner.nextInt();

            try {
                switch (opcion) {
                    // Operaciones binarias
                    case 1 -> {
                        int[] nums = leerDosNumeros(scanner);
                        System.out.println("Resultado: " + sumar(nums[0], nums[1]));
                    }
                    case 2 -> {
                        int[] nums = leerDosNumeros(scanner);
                        System.out.println("Resultado: " + restar(nums[0], nums[1]));
                    }
                    case 3 -> {
                        int[] nums = leerDosNumeros(scanner);
                        System.out.println("Resultado: " + multiplicar(nums[0], nums[1]));
                    }
                    case 4 -> {
                        int[] nums = leerDosNumeros(scanner);
                        System.out.println("Resultado: " + dividir(nums[0], nums[1]));
                    }

                    // Operaciones unarias
                    case 5 -> {
                        int a = leerUnNumero(scanner);
                        System.out.println("Resultado: " + negar(a));
                    }
                    case 6 -> {
                        int a = leerUnNumero(scanner);
                        System.out.println("Resultado: " + valorAbsoluto(a));
                    }
                    case 7 -> {
                        int a = leerUnNumero(scanner);
                        System.out.println("Resultado: " + incrementar(a));
                    }
                    case 8 -> {
                        int a = leerUnNumero(scanner);
                        System.out.println("Resultado: " + decrementar(a));
                    }

                    case 0 -> System.out.println("Saliendo...");
                    default -> System.out.println("Opción inválida");
                }
            } catch (ArithmeticException e) {
                System.out.println("Error: " + e.getMessage());
            }

        } while (opcion != 0);

        scanner.close();
    }

    // =========================
    // Métodos de lectura
    // =========================
    private static int leerUnNumero(Scanner scanner) {
        System.out.print("Ingrese un número: ");
        return scanner.nextInt();
    }

    private static int[] leerDosNumeros(Scanner scanner) {
        System.out.print("Ingrese dos números: ");
        return new int[]{scanner.nextInt(), scanner.nextInt()};
    }

    // =========================
    // Operaciones binarias
    // =========================
    private static int sumar(int a, int b) {
        return a + b;
    }

    private static int restar(int a, int b) {
        return a - b;
    }

    private static int multiplicar(int a, int b) {
        return a * b;
    }

    private static int dividir(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("No se puede dividir por cero");
        }
        return a / b;
    }

    // =========================
    // Operaciones unarias
    // =========================
    private static int negar(int a) {
        return -a;
    }

    private static int valorAbsoluto(int a) {
        return Math.abs(a);
    }

    private static int incrementar(int a) {
        return a + 1;
    }

    private static int decrementar(int a) {
        return a - 1;
    }
}
