package co.edu.javeriana.ingsoft.vibe.code.back;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas para la operación Valor Absoluto")
class ValorAbsolutoTest {

    @Test
    @DisplayName("Valor absoluto de número positivo")
    void testValorAbsolutoPositivo() {
        ValorAbsoluto abs = new ValorAbsoluto();
        assertEquals(5, abs.aplicar(5));
    }

    @Test
    @DisplayName("Valor absoluto de número negativo")
    void testValorAbsolutoNegativo() {
        ValorAbsoluto abs = new ValorAbsoluto();
        assertEquals(5, abs.aplicar(-5));
    }

    @Test
    @DisplayName("Valor absoluto de cero")
    void testValorAbsolutoCero() {
        ValorAbsoluto abs = new ValorAbsoluto();
        assertEquals(0, abs.aplicar(0));
    }

    @Test
    @DisplayName("Valor absoluto de número grande")
    void testValorAbsolutoGrande() {
        ValorAbsoluto abs = new ValorAbsoluto();
        assertEquals(Integer.MAX_VALUE, abs.aplicar(Integer.MAX_VALUE));
    }

    @Test
    @DisplayName("Valor absoluto de Integer.MIN_VALUE lanza excepción")
    void testValorAbsolutoMinValue() {
        ValorAbsoluto abs = new ValorAbsoluto();
        assertThrows(ArithmeticException.class, () -> {
            abs.aplicar(Integer.MIN_VALUE);
        });
    }

    @Test
    @DisplayName("Verificar nombre de operación")
    void testNombre() {
        ValorAbsoluto abs = new ValorAbsoluto();
        assertEquals("Valor Absoluto", abs.getNombre());
    }
}
