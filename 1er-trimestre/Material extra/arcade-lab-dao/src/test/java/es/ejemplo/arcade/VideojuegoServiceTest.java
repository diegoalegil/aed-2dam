package es.ejemplo.arcade;

import es.ejemplo.arcade.dao.PlataformaDao;
import es.ejemplo.arcade.dao.VideojuegoDao;
import es.ejemplo.arcade.exception.AplicationException;
import es.ejemplo.arcade.model.Videojuego;
import es.ejemplo.arcade.service.VideojuegoService;
import es.ejemplo.arcade.support.BaseDatosTest;
import es.ejemplo.arcade.support.Semilla;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VideojuegoServiceTest extends BaseDatosTest {
    private VideojuegoService service;

    @BeforeEach
    void prepararService() {
        Semilla.cargar(url);
        service = new VideojuegoService(new VideojuegoDao(url), new PlataformaDao(url));
    }

    private int totalVideojuegos() {
        return contar("SELECT COUNT(*) FROM videojuego");
    }

    @Test
    void codigoPlataformaValidoAceptaMayusculasYNumeros() {
        assertTrue(VideojuegoService.codigoPlataformaValido("PS5"));
        assertTrue(VideojuegoService.codigoPlataformaValido("SWITCH"));
        assertTrue(VideojuegoService.codigoPlataformaValido("PC"));
    }

    @Test
    void codigoPlataformaValidoRechazaLosIncorrectos() {
        assertTrue(VideojuegoService.codigoPlataformaValido("XSX"));
        assertFalse(VideojuegoService.codigoPlataformaValido("ps5"));
        assertFalse(VideojuegoService.codigoPlataformaValido("P"));
        assertFalse(VideojuegoService.codigoPlataformaValido("ABCDEFG"));
        assertFalse(VideojuegoService.codigoPlataformaValido("PS-5"));
        assertFalse(VideojuegoService.codigoPlataformaValido(null));
    }

    @Test
    void registrarValidoLoGuarda() {
        service.registrar(new Videojuego(8, "Halo Infinite", 29.99, true, 87, "XSX"));
        assertEquals(8, totalVideojuegos());
        assertEquals("Halo Infinite", leerTexto("SELECT titulo FROM videojuego WHERE id = 8"));
    }

    @Test
    void registrarNuloOSinTituloLanzaYNoGuarda() {
        assertThrows(AplicationException.class, () -> service.registrar(null));
        assertThrows(AplicationException.class,
                () -> service.registrar(new Videojuego(8, "   ", 29.99, true, 87, "XSX")));
        assertThrows(AplicationException.class,
                () -> service.registrar(new Videojuego(9, null, 29.99, true, 87, "XSX")));
        assertEquals(7, totalVideojuegos());
    }

    @Test
    void registrarPrecioONotaFueraDeRangoLanzaYNoGuarda() {
        assertThrows(AplicationException.class,
                () -> service.registrar(new Videojuego(8, "Halo Infinite", -1.0, true, 87, "XSX")));
        assertThrows(AplicationException.class,
                () -> service.registrar(new Videojuego(8, "Halo Infinite", 29.99, true, 101, "XSX")));
        assertThrows(AplicationException.class,
                () -> service.registrar(new Videojuego(8, "Halo Infinite", 29.99, true, -1, "XSX")));
        assertEquals(7, totalVideojuegos());
    }

    @Test
    void registrarPlataformaInvalidaOInexistenteLanzaYNoGuarda() {
        assertThrows(AplicationException.class,
                () -> service.registrar(new Videojuego(8, "Halo Infinite", 29.99, true, 87, "xsx")));
        assertThrows(AplicationException.class,
                () -> service.registrar(new Videojuego(8, "Pokémon Rojo", 29.99, false, 89, "GB")));
        assertEquals(7, totalVideojuegos());
    }

    @Test
    void registrarIdRepetidoLanza() {
        assertThrows(AplicationException.class,
                () -> service.registrar(new Videojuego(1, "Halo Infinite", 29.99, true, 87, "XSX")));
        assertEquals("Baldur's Gate 3", leerTexto("SELECT titulo FROM videojuego WHERE id = 1"));
        assertEquals(7, totalVideojuegos());
    }

    @Test
    void obtenerExistenteEInexistente() {
        assertEquals("Mario Kart 8 Deluxe", service.obtener(2).getTitulo());
        assertThrows(AplicationException.class, () -> service.obtener(99));
    }

    @Test
    void rebajarCambiaElPrecio() {
        service.rebajar(2, 20);
        assertEquals(39.992, leerDecimal("SELECT precio FROM videojuego WHERE id = 2"), 0.0001);
    }

    @Test
    void rebajarPorcentajeInvalidoOIdInexistenteLanza() {
        assertThrows(AplicationException.class, () -> service.rebajar(2, 0));
        assertThrows(AplicationException.class, () -> service.rebajar(2, 95));
        assertThrows(AplicationException.class, () -> service.rebajar(99, 10));
        assertEquals(49.99, leerDecimal("SELECT precio FROM videojuego WHERE id = 2"), 0.0001);
    }
}
