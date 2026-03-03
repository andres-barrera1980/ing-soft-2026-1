package co.edu.javeriana.ingsoft.vibe.code.back;

public class Division implements OperacionBinaria {
    @Override
    public Integer aplicar(Integer a, Integer b) {
        if (b == 0) {
            throw new ArithmeticException("No se puede dividir por cero");
        }
        return a / b;
    }

    @Override
    public String getNombre() {
        return "División";
    }
}
