package co.edu.javeriana.ingsoft.katas.easy.calc.operaciones.base;

public abstract class DivisionSegura extends OperacionBinaria {

    public double dividir(double numerador, double divisor) {

        if (numerador < Integer.MIN_VALUE || numerador > Integer.MAX_VALUE ||
                divisor < Integer.MIN_VALUE || divisor > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Los valores deben estar dentro del rango de 32 bits.");
        }

        if (divisor == 0) {
            throw new ArithmeticException("No se puede dividir por cero.");
        }

        return (double) numerador / divisor;
    }
}
