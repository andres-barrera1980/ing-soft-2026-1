package co.edu.javeriana.ingsoft.vibe.code.back;

public class ValorAbsoluto implements OperacionUnaria {
    @Override
    public Integer aplicar(Integer a) {
        // Integer.MIN_VALUE no tiene valor absoluto representable en Integer
        if (a == Integer.MIN_VALUE) {
            throw new ArithmeticException("No se puede calcular valor absoluto de Integer.MIN_VALUE");
        }
        
        return Math.abs(a);
    }

    @Override
    public String getNombre() {
        return "Valor Absoluto";
    }
}
