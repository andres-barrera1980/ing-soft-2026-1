package co.edu.javeriana.ingsoft.vibe.code.back;

public class Resta implements OperacionBinaria {
    @Override
    public Integer aplicar(Integer a, Integer b) {
        if (a > 0 && b < Integer.MIN_VALUE + a) {
            throw new ArithmeticException("Overflow: la resta excede Integer.MAX_VALUE");
        }
        if (a < 0 && b > Integer.MAX_VALUE + a) {
            throw new ArithmeticException("Underflow: la resta es menor que Integer.MIN_VALUE");
        }
        return a - b;
    }

    @Override
    public String getNombre() {
        return "Resta";
    }
}
