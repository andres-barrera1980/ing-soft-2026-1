package co.edu.javeriana.ingsoft.vibe.code.back;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas para la operación Potencia")
class PotenciaTest {

    @Test
    @DisplayName("Potencia: base positiva, exponente positivo")
    void testPotenciaPositivos() {
        Potencia potencia = new Potencia();
        assertEquals(8, potencia.aplicar(2, 3));
    }

    @Test
    @DisplayName("Potencia: elevar a cero")
    void testPotenciaCero() {
        Potencia potencia = new Potencia();
        assertEquals(1, potencia.aplicar(5, 0));
    }

    @Test
    @DisplayName("Potencia: exponente negativo lanza excepción")
    void testPotenciaExponenteNegativo() {
        Potencia potencia = new Potencia();
        assertThrows(ArithmeticException.class, () -> {
            potencia.aplicar(2, -1);
        });
    }

    @Test
    @DisplayName("Potencia: base negativa, exponente par")
    void testPotenciaBaseNegativaPar() {
        Potencia potencia = new Potencia();
        assertEquals(4, potencia.aplicar(-2, 2));
    }

    @Test
    @DisplayName("Potencia: base negativa, exponente impar")
    void testPotenciaBaseNegativaImpar() {
        Potencia potencia = new Potencia();
        assertEquals(-8, potencia.aplicar(-2, 3));
    }

    @Test
    @DisplayName("Potencia: overflow")
    void testPotenciaOverflow() {
        Potencia potencia = new Potencia();
        assertThrows(ArithmeticException.class, () -> {
            potencia.aplicar(Integer.MAX_VALUE, Integer.MAX_VALUE);
        });
    }

    @Test
    @DisplayName("Verificar nombre de operación")
    void testNombre() {
        Potencia potencia = new Potencia();
        assertEquals("Potencia", potencia.getNombre());
    }
}
