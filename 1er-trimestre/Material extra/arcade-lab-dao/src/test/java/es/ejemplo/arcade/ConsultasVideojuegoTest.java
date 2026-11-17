package es.ejemplo.arcade;

import es.ejemplo.arcade.dao.VideojuegoDao;
import es.ejemplo.arcade.support.BaseDatosTest;
import es.ejemplo.arcade.support.Semilla;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ConsultasVideojuegoTest extends BaseDatosTest {
    private VideojuegoDao dao;

    @BeforeEach
    void prepararDao() {
        Semilla.cargar(url);
        dao = new VideojuegoDao(url);
    }

    @Test
    void contarConDatosYConLaTablaVacia() {
        assertEquals(7, dao.contar());
        ejecutar("DELETE FROM videojuego");
        assertEquals(0, dao.contar());
    }

    @Test
    void mediaPrecio() {
        assertEquals(39.99143, dao.mediaPrecio(), 0.0001);
    }

    @Test
    void mediaPrecioConUnoYConLaTablaVacia() {
        ejecutar("DELETE FROM videojuego WHERE id <> 2");
        assertEquals(49.99, dao.mediaPrecio(), 0.0001);
        ejecutar("DELETE FROM videojuego");
        assertEquals(0.0, dao.mediaPrecio(), 0.0001);
    }

    @Test
    void contarPorPlataformaValores() {
        assertEquals(Map.of("PC", 3, "PS5", 2, "SWITCH", 2), dao.contarPorPlataforma());
    }

    @Test
    void contarPorPlataformaOrdenadoPorCodigo() {
        assertEquals(List.of("PC", "PS5", "SWITCH"), new ArrayList<>(dao.contarPorPlataforma().keySet()));
    }

    @Test
    void plataformasConAlMenos() {
        assertEquals(List.of("PC"), dao.plataformasConAlMenos(3));
        assertEquals(List.of("PC", "PS5", "SWITCH"), dao.plataformasConAlMenos(2));
        assertEquals(List.of(), dao.plataformasConAlMenos(4));
    }

    @Test
    void titulosDeFabricanteOrdenadosPorTitulo() {
        assertEquals(List.of("Assassin's Creed Shadows", "Astro Bot"), dao.titulosDeFabricante("Sony"));
        assertEquals(List.of(), dao.titulosDeFabricante("Microsoft"));
    }
}
