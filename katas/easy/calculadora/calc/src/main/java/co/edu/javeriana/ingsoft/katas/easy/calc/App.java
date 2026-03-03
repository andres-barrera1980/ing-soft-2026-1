package co.edu.javeriana.ingsoft.katas.easy.calc;

import co.edu.javeriana.ingsoft.katas.easy.calc.operaciones.base.Potenciacion;

import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        System.out.print("Base: ");
        int base = sc.nextInt();

        System.out.print("Exponente: ");
        int exp = sc.nextInt();

        try {
            Potenciacion p = new Potenciacion();
            Integer resultado = p.realizarOperacion(base, exp);
            System.out.println("Resultado: " + resultado);
        } catch (ArithmeticException | IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }

        sc.close();
    }


}
