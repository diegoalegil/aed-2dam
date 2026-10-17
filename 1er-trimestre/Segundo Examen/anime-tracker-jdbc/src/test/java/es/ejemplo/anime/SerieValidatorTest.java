package es.ejemplo.anime;

import es.ejemplo.anime.exception.AplicationException;
import es.ejemplo.anime.model.Serie;
import es.ejemplo.anime.validation.SerieValidator;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SerieValidatorTest {
    private final SerieValidator validator = new SerieValidator();

    private Serie conCodigo(String codigo) {
        return new Serie(codigo, "Naruto", "shonen", 220, 8.0);
    }

    @Test @Order(1)
    void codigoValido() {
        assertTrue(validator.codigoValido("NAR-001"));
        assertTrue(validator.codigoValido("AOT-123"));
    }

    @Test @Order(2)
    void codigoEnMinusculasNoVale() {
        assertFalse(validator.codigoValido("nar-001"));
    }

    @Test @Order(3)
    void codigoConFormatoIncorrectoNoVale() {
        assertFalse(validator.codigoValido("NARU-01"));
        assertFalse(validator.codigoValido("NAR001"));
        assertFalse(validator.codigoValido("NA-0011"));
        assertFalse(validator.codigoValido("NAR-01A"));
    }

    @Test @Order(4)
    void codigoNuloNoVale() {
        assertFalse(validator.codigoValido(null));
    }

    @Test @Order(5)
    void serieCorrectaNoLanza() {
        assertDoesNotThrow(() -> validator.validar(conCodigo("NAR-001")));
        assertDoesNotThrow(() -> validator.validar(new Serie("NAR-002", "Naruto", "shonen", 1, 0)));
        assertDoesNotThrow(() -> validator.validar(new Serie("NAR-003", "Naruto", "shonen", 1, 10)));
    }

    @Test @Order(6)
    void serieNulaLanza() {
        assertThrows(AplicationException.class, () -> validator.validar(null));
    }

    @Test @Order(7)
    void codigoIncorrectoLanza() {
        assertThrows(AplicationException.class, () -> validator.validar(conCodigo("naruto")));
    }

    @Test @Order(8)
    void tituloOGeneroVacioLanza() {
        assertThrows(AplicationException.class,
            () -> validator.validar(new Serie("NAR-001", "   ", "shonen", 220, 8.0)));
        assertThrows(AplicationException.class,
            () -> validator.validar(new Serie("NAR-001", "Naruto", "", 220, 8.0)));
    }

    @Test @Order(9)
    void episodiosCeroLanza() {
        assertThrows(AplicationException.class,
            () -> validator.validar(new Serie("NAR-001", "Naruto", "shonen", 0, 8.0)));
    }

    @Test @Order(10)
    void notaFueraDeRangoLanza() {
        assertThrows(AplicationException.class,
            () -> validator.validar(new Serie("NAR-001", "Naruto", "shonen", 220, -0.5)));
        assertThrows(AplicationException.class,
            () -> validator.validar(new Serie("NAR-001", "Naruto", "shonen", 220, 10.5)));
    }
}
