package co.edu.javeriana.ingsoft.katas.easy.calc.operaciones.base;

import java.util.Scanner;

public class RaizCuadrada {

    public Double realizarOperacion(Double numero) {

        if (numero == null) {
            throw new IllegalArgumentException("El número no puede ser nulo");
        }

        if (numero < 0) {
            throw new IllegalArgumentException("No se puede calcular la raíz de un número negativo");
        }

        return Math.sqrt(numero);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingresa un número: ");
        double numero = scanner.nextDouble();

        RaizCuadrada raizCuadrada = new RaizCuadrada();

        try {
            Double resultado = raizCuadrada.realizarOperacion(numero);
            System.out.println("La raíz cuadrada de " + numero + " es: " + resultado);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        scanner.close();
    }
}