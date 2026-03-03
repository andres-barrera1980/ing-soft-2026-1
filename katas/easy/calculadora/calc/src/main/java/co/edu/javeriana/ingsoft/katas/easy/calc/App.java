package co.edu.javeriana.ingsoft.katas.easy.calc;

import co.edu.javeriana.ingsoft.katas.easy.calc.operaciones.base.Potenciacion;

import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Scanner sc = new Scanner(System.in);
        System.out.print("Base: ");     long base = sc.nextLong();
        System.out.print("Exponente: "); int exp = sc.nextInt();

        try {
            System.out.println("Resultado: " + Potenciacion.potencia(base, exp));
        } catch (ArithmeticException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


}
