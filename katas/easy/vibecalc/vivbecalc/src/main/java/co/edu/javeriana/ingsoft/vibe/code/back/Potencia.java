package co.edu.javeriana.ingsoft.vibe.code.back;

public class Potencia implements OperacionBinaria {
    @Override
    public Integer aplicar(Integer base, Integer exponente) {
        if (exponente < 0) {
            throw new ArithmeticException("El exponente no puede ser negativo");
        }
        
        if (exponente == 0) {
            return 1;
        }
        
        if (base == 0) {
            return 0;
        }
        
        if (base == 1) {
            return 1;
        }
        
        if (base == -1) {
            return exponente % 2 == 0 ? 1 : -1;
        }
        
        long resultado = 1;
        long baseL = base;
        
        for (int i = 0; i < exponente; i++) {
            resultado *= baseL;
            
            // Verificar overflow
            if (resultado > Integer.MAX_VALUE || resultado < Integer.MIN_VALUE) {
                throw new ArithmeticException("Overflow: la potencia excede los límites de Integer");
            }
        }
        
        return (int) resultado;
    }

    @Override
    public String getNombre() {
        return "Potencia";
    }
}
