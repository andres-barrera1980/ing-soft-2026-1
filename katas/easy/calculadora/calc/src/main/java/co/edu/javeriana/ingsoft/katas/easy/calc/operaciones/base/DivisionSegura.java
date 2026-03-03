package co.edu.javeriana.ingsoft.katas.easy.calc.operaciones.base;

public class DivisionSegura extends OperacionBinaria {

    @Override
    public Integer realizarOperacion(Integer num1, Integer num2) {

        if (num2 == 0) {
            throw new ArithmeticException("No es posible dividir por cero");
        }


        if (num1 == Integer.MIN_VALUE && num2 == -1) {
            throw new IllegalArgumentException("Valor fuera del rango de 32 bits (Overflow)");
        }

        return num1 / num2;
    }
}