package es.ejemplo.anime;

import es.ejemplo.anime.config.ConexionBD;
import es.ejemplo.anime.dao.SerieDao;
import es.ejemplo.anime.exception.AplicationException;
import es.ejemplo.anime.model.Serie;
import es.ejemplo.anime.service.SerieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SerieServiceTest extends BaseDatosTest {
    private SerieService service;

    @BeforeEach
    void prepararService() {
        new ConexionBD(url).inicializar();
        service = new SerieService(new SerieDao(url));
    }

    @Test @Order(1)
    void registrarSerieValida() {
        assertTrue(service.registrar(new Serie("AOT-001", "Shingeki no Kyojin", "shonen", 87, 9.0)));
        assertEquals("Shingeki no Kyojin", service.obtener("AOT-001").getTitulo());
    }

    @Test @Order(2)
    void registrarSerieInvalidaLanzaYNoGuardaNada() {
        assertThrows(AplicationException.class,
            () -> service.registrar(new Serie("aot-1", "Shingeki no Kyojin", "shonen", 87, 9.0)));
        assertTrue(new SerieDao(url).listar().isEmpty());
    }

    @Test @Order(3)
    void registrarDuplicadaLanza() {
        assertTrue(service.registrar(new Serie("AOT-001", "Shingeki no Kyojin", "shonen", 87, 9.0)));
        assertThrows(AplicationException.class,
            () -> service.registrar(new Serie("AOT-001", "Otra", "seinen", 10, 5.0)));
    }

    @Test @Order(4)
    void obtenerInexistenteLanza() {
        assertThrows(AplicationException.class, () -> service.obtener("XXX-999"));
    }

    @Test @Order(5)
    void sumarEpisodios() {
        assertTrue(service.registrar(new Serie("KNY-001", "Kimetsu no Yaiba", "shonen", 55, 8.5)));
        assertTrue(service.sumarEpisodios("KNY-001", 8));
        assertEquals(63, service.obtener("KNY-001").getEpisodios());
    }

    @Test @Order(6)
    void sumarEpisodiosNoPositivosOInexistenteLanza() {
        assertTrue(service.registrar(new Serie("KNY-001", "Kimetsu no Yaiba", "shonen", 55, 8.5)));
        assertThrows(AplicationException.class, () -> service.sumarEpisodios("KNY-001", 0));
        assertThrows(AplicationException.class, () -> service.sumarEpisodios("XXX-999", 5));
        assertEquals(55, service.obtener("KNY-001").getEpisodios());
    }
}
