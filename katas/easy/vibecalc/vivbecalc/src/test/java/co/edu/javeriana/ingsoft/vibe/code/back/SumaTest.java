package co.edu.javeriana.ingsoft.vibe.code.back;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas para la operación Suma")
class SumaTest {

    @Test
    @DisplayName("Sumar dos números positivos")
    void testSumarPositivos() {
        Suma suma = new Suma();
        assertEquals(5, suma.aplicar(2, 3));
    }

    @Test
    @DisplayName("Sumar números negativos")
    void testSumarNegativos() {
        Suma suma = new Suma();
        assertEquals(-5, suma.aplicar(-2, -3));
    }

    @Test
    @DisplayName("Sumar positivo y negativo")
    void testSumarMixto() {
        Suma suma = new Suma();
        assertEquals(1, suma.aplicar(5, -4));
    }

    @Test
    @DisplayName("Sumar cero")
    void testSumarCero() {
        Suma suma = new Suma();
        assertEquals(5, suma.aplicar(5, 0));
    }

    @Test
    @DisplayName("Overflow en suma")
    void testSumaOverflow() {
        Suma suma = new Suma();
        assertThrows(ArithmeticException.class, () -> {
            suma.aplicar(Integer.MAX_VALUE, 1);
        });
    }

    @Test
    @DisplayName("Underflow en suma")
    void testSumaUnderflow() {
        Suma suma = new Suma();
        assertThrows(ArithmeticException.class, () -> {
            suma.aplicar(Integer.MIN_VALUE, -1);
        });
    }

    @Test
    @DisplayName("Verificar nombre de operación")
    void testNombre() {
        Suma suma = new Suma();
        assertEquals("Suma", suma.getNombre());
    }
}
