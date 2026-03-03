package co.edu.javeriana.ingsoft.vibe.code.back;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas para la operación Módulo")
class ModuloTest {

    @Test
    @DisplayName("Módulo de dos números positivos")
    void testModuloPositivos() {
        Modulo modulo = new Modulo();
        assertEquals(1, modulo.aplicar(7, 3));
    }

    @Test
    @DisplayName("Módulo con números negativos")
    void testModuloNegativos() {
        Modulo modulo = new Modulo();
        assertEquals(-1, modulo.aplicar(-7, 3));
    }

    @Test
    @DisplayName("Módulo cuando dividendo es múltiplo")
    void testModuloMultiplo() {
        Modulo modulo = new Modulo();
        assertEquals(0, modulo.aplicar(6, 3));
    }

    @Test
    @DisplayName("Módulo por cero lanza excepción")
    void testModuloPorCero() {
        Modulo modulo = new Modulo();
        assertThrows(ArithmeticException.class, () -> {
            modulo.aplicar(5, 0);
        });
    }

    @Test
    @DisplayName("Módulo cuando dividendo es menor")
    void testModuloMenor() {
        Modulo modulo = new Modulo();
        assertEquals(2, modulo.aplicar(2, 5));
    }

    @Test
    @DisplayName("Verificar nombre de operación")
    void testNombre() {
        Modulo modulo = new Modulo();
        assertEquals("Módulo", modulo.getNombre());
    }
}
