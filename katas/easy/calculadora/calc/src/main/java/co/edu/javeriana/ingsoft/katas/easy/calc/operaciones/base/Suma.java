package co.edu.javeriana.ingsoft.katas.easy.calc.operaciones.base;

public class Suma extends OperacionBinaria {

    @Override
    public Integer realizarOperacion(Integer num1, Integer num2) {
        if (num1 == null || num2 == null) {
            throw new IllegalArgumentException("Los números no pueden ser nulos");
        }

        int r = num1 + num2;
        // Logic from Math.addExact(int x, int y): 
        // HD 2-12 Overflow occurs if both arguments have the same sign 
        // and the result has a different sign.
        if (((num1 ^ r) & (num2 ^ r)) < 0) {
            throw new ArithmeticException("integer overflow");
        }
        return r;
    }
}
