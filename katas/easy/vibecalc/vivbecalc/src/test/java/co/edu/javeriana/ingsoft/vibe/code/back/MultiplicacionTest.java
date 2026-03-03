package co.edu.javeriana.ingsoft.vibe.code.back;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas para la operación Multiplicación")
class MultiplicacionTest {

    @Test
    @DisplayName("Multiplicar dos números positivos")
    void testMultiplicacionPositivos() {
        Multiplicacion mult = new Multiplicacion();
        assertEquals(6, mult.aplicar(2, 3));
    }

    @Test
    @DisplayName("Multiplicar números negativos")
    void testMultiplicacionNegativos() {
        Multiplicacion mult = new Multiplicacion();
        assertEquals(6, mult.aplicar(-2, -3));
    }

    @Test
    @DisplayName("Multiplicar positivo y negativo")
    void testMultiplicacionMixto() {
        Multiplicacion mult = new Multiplicacion();
        assertEquals(-6, mult.aplicar(2, -3));
    }

    @Test
    @DisplayName("Multiplicar por cero")
    void testMultiplicacionCero() {
        Multiplicacion mult = new Multiplicacion();
        assertEquals(0, mult.aplicar(5, 0));
    }

    @Test
    @DisplayName("Overflow en multiplicación")
    void testMultiplicacionOverflow() {
        Multiplicacion mult = new Multiplicacion();
        assertThrows(ArithmeticException.class, () -> {
            mult.aplicar(Integer.MAX_VALUE, 2);
        });
    }

    @Test
    @DisplayName("Verificar nombre de operación")
    void testNombre() {
        Multiplicacion mult = new Multiplicacion();
        assertEquals("Multiplicación", mult.getNombre());
    }
}
