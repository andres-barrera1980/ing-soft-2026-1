package co.edu.javeriana.ingsoft.vibe.code.back;

public class Modulo implements OperacionBinaria {
    @Override
    public Integer aplicar(Integer a, Integer b) {
        if (b == 0) {
            throw new ArithmeticException("No se puede calcular módulo por cero");
        }
        return a % b;
    }

    @Override
    public String getNombre() {
        return "Módulo";
    }
}
