package co.edu.javeriana.ingsoft.vibe.code.back;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas para la operación División")
class DivisionTest {

    @Test
    @DisplayName("Dividir dos números positivos")
    void testDivisionPositivos() {
        Division division = new Division();
        assertEquals(2, division.aplicar(6, 3));
    }

    @Test
    @DisplayName("Dividir números negativos")
    void testDivisionNegativos() {
        Division division = new Division();
        assertEquals(2, division.aplicar(-6, -3));
    }

    @Test
    @DisplayName("Dividir positivo entre negativo")
    void testDivisionMixto() {
        Division division = new Division();
        assertEquals(-2, division.aplicar(6, -3));
    }

    @Test
    @DisplayName("Dividir entre cero lanza excepción")
    void testDivisionPorCero() {
        Division division = new Division();
        assertThrows(ArithmeticException.class, () -> {
            division.aplicar(5, 0);
        });
    }

    @Test
    @DisplayName("División truncada hacia cero")
    void testDivisionTruncada() {
        Division division = new Division();
        assertEquals(2, division.aplicar(7, 3));
    }

    @Test
    @DisplayName("Verificar nombre de operación")
    void testNombre() {
        Division division = new Division();
        assertEquals("División", division.getNombre());
    }
}
