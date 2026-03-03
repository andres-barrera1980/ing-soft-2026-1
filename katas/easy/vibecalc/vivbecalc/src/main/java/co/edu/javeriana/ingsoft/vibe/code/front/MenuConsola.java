package co.edu.javeriana.ingsoft.vibe.code.front;

import java.util.Scanner;
import co.edu.javeriana.ingsoft.vibe.code.back.*;

public class MenuConsola {
    private Scanner scanner;
    private boolean ejecutando;

    public MenuConsola() {
        this.scanner = new Scanner(System.in);
        this.ejecutando = true;
    }

    public void iniciar() {
        while (ejecutando) {
            mostrarMenuPrincipal();
            int opcion = leerOpcion();
            procesarOpcionPrincipal(opcion);
        }
        scanner.close();
        System.out.println("\n*** Gracias por usar VibeCalc ***\n");
    }

    private void mostrarMenuPrincipal() {
        limpiarPantalla();
        System.out.println("\n");
        System.out.println("*".repeat(50));
        System.out.println("*" + " ".repeat(48) + "*");
        System.out.println("*" + centrar("CALCULADORA VIBECALC", 48) + "*");
        System.out.println("*" + " ".repeat(48) + "*");
        System.out.println("*".repeat(50));
        System.out.println();
        
        System.out.println("*** OPERACIONES BINARIAS (2 números) ***");
        System.out.println("  1. Suma");
        System.out.println("  2. Resta");
        System.out.println("  3. Multiplicación");
        System.out.println("  4. División");
        System.out.println("  5. Módulo");
        System.out.println("  6. Potencia");
        System.out.println();
        
        System.out.println("*** OPERACIONES UNARIAS (1 número) ***");
        System.out.println("  7. Raíz Cuadrada");
        System.out.println("  8. Factorial");
        System.out.println("  9. Fibonacci");
        System.out.println("  10. Valor Absoluto");
        System.out.println();
        
        System.out.println("*".repeat(50));
        System.out.println("  0. Salir");
        System.out.println("*".repeat(50));
        System.out.print("\nSeleccione una opción: ");
    }

    private void procesarOpcionPrincipal(int opcion) {
        try {
            switch (opcion) {
                case 1 -> ejecutarOperacionBinaria(new Suma(), "Suma");
                case 2 -> ejecutarOperacionBinaria(new Resta(), "Resta");
                case 3 -> ejecutarOperacionBinaria(new Multiplicacion(), "Multiplicación");
                case 4 -> ejecutarOperacionBinaria(new Division(), "División");
                case 5 -> ejecutarOperacionBinaria(new Modulo(), "Módulo");
                case 6 -> ejecutarOperacionBinaria(new Potencia(), "Potencia");
                case 7 -> ejecutarOperacionUnaria(new RaizCuadrada(), "Raíz Cuadrada");
                case 8 -> ejecutarOperacionUnaria(new Factorial(), "Factorial");
                case 9 -> ejecutarOperacionUnaria(new Fibonacci(), "Fibonacci");
                case 10 -> ejecutarOperacionUnaria(new ValorAbsoluto(), "Valor Absoluto");
                case 0 -> ejecutando = false;
                default -> System.out.println("\n*** Opción inválida. Por favor, intente de nuevo ***");
            }
        } catch (Exception e) {
            System.out.println("\n*** Error: " + e.getMessage() + " ***");
        }
        
        if (ejecutando && opcion != 0) {
            pausa();
        }
    }

    private void ejecutarOperacionBinaria(OperacionBinaria operacion, String nombre) {
        limpiarPantalla();
        System.out.println("\n*".repeat(25));
        System.out.println("* " + nombre.toUpperCase());
        System.out.println("*".repeat(25));
        System.out.println();
        
        System.out.print("Ingrese el primer número: ");
        Integer a = leerNumero();
        
        System.out.print("Ingrese el segundo número: ");
        Integer b = leerNumero();
        
        try {
            Integer resultado = operacion.aplicar(a, b);
            mostrarResultado(a, nombre, b, resultado);
        } catch (ArithmeticException e) {
            System.out.println("\n*** ERROR: " + e.getMessage() + " ***");
        }
    }

    private void ejecutarOperacionUnaria(OperacionUnaria operacion, String nombre) {
        limpiarPantalla();
        System.out.println("\n*".repeat(25));
        System.out.println("* " + nombre.toUpperCase());
        System.out.println("*".repeat(25));
        System.out.println();
        
        System.out.print("Ingrese el número: ");
        Integer a = leerNumero();
        
        try {
            Integer resultado = operacion.aplicar(a);
            mostrarResultadoUnario(a, nombre, resultado);
        } catch (ArithmeticException e) {
            System.out.println("\n*** ERROR: " + e.getMessage() + " ***");
        }
    }

    private void mostrarResultado(Integer a, String operacion, Integer b, Integer resultado) {
        System.out.println("\n*".repeat(40));
        System.out.println("* RESULTADO");
        System.out.println("*".repeat(40));
        System.out.printf("* %d %s %d = %d%n", a, obtenerSimbolo(operacion), b, resultado);
        System.out.println("*".repeat(40));
    }

    private void mostrarResultadoUnario(Integer a, String operacion, Integer resultado) {
        System.out.println("\n*".repeat(40));
        System.out.println("* RESULTADO");
        System.out.println("*".repeat(40));
        System.out.printf("* %s(%d) = %d%n", operacion, a, resultado);
        System.out.println("*".repeat(40));
    }

    private String obtenerSimbolo(String operacion) {
        return switch (operacion) {
            case "Suma" -> "+";
            case "Resta" -> "-";
            case "Multiplicación" -> "*";
            case "División" -> "/";
            case "Módulo" -> "%";
            case "Potencia" -> "^";
            default -> "?";
        };
    }

    private Integer leerNumero() {
        try {
            return scanner.nextInt();
        } catch (Exception e) {
            scanner.nextLine(); // Limpiar buffer
            throw new ArithmeticException("Entrada inválida. Por favor ingrese un número entero.");
        }
    }

    private int leerOpcion() {
        try {
            return scanner.nextInt();
        } catch (Exception e) {
            scanner.nextLine(); // Limpiar buffer
            return -1;
        }
    }

    private String centrar(String texto, int ancho) {
        int espacios = (ancho - texto.length()) / 2;
        return " ".repeat(Math.max(0, espacios)) + texto;
    }

    private void pausa() {
        System.out.print("\nPresione ENTER para continuar...");
        scanner.nextLine(); // Consume el \n del número anterior
        scanner.nextLine(); // Espera a que presione ENTER
    }

    private void limpiarPantalla() {
        // Intenta limpiar la pantalla (funciona en Linux/Mac/Windows con ANSI)
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void main(String[] args) {
        MenuConsola menu = new MenuConsola();
        menu.iniciar();
    }
}
