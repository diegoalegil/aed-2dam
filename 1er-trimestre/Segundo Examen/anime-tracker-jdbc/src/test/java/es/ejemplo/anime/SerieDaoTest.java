package es.ejemplo.anime;

import es.ejemplo.anime.config.ConexionBD;
import es.ejemplo.anime.dao.SerieDao;
import es.ejemplo.anime.exception.AplicationException;
import es.ejemplo.anime.model.Serie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SerieDaoTest extends BaseDatosTest {
    private static final Serie AOT = new Serie("AOT-001", "Shingeki no Kyojin", "shonen", 87, 9.0);
    private static final Serie KNY = new Serie("KNY-001", "Kimetsu no Yaiba", "shonen", 55, 8.5);
    private static final Serie OPM = new Serie("OPM-001", "One Punch Man", "seinen", 24, 8.7);
    private static final Serie FMA = new Serie("FMA-001", "Fullmetal Alchemist Brotherhood", "shonen", 64, 9.0);
    private static final Serie MOB = new Serie("MOB-001", "Mob Psycho 100", "seinen", 37, 8.6);
    private static final Serie REZ = new Serie("REZ-001", "Re:Zero", "isekai", 66, 8.2);

    private SerieDao dao;

    @BeforeEach
    void prepararDao() {
        new ConexionBD(url).inicializar();
        dao = new SerieDao(url);
    }

    private List<String> codigos(List<Serie> series) {
        return series.stream().map(Serie::getCodigo).toList();
    }

    @Test @Order(1)
    void listarVacioYOrdenadoPorCodigo() {
        assertTrue(dao.listar().isEmpty());
        assertTrue(dao.insertar(OPM));
        assertTrue(dao.insertar(AOT));
        assertEquals(List.of("AOT-001", "OPM-001"), codigos(dao.listar()));
    }

    @Test @Order(2)
    void buscarPorCodigo() {
        assertTrue(dao.insertar(AOT));
        Serie s = dao.buscarPorCodigo("AOT-001").orElseThrow();
        assertAll(
            () -> assertEquals("AOT-001", s.getCodigo()),
            () -> assertEquals("Shingeki no Kyojin", s.getTitulo()),
            () -> assertEquals("shonen", s.getGenero()),
            () -> assertEquals(87, s.getEpisodios()),
            () -> assertEquals(9.0, s.getNota(), 0.0001)
        );
    }

    @Test @Order(3)
    void buscarPorCodigoInexistente() {
        assertTrue(dao.buscarPorCodigo("XXX-999").isEmpty());
    }

    @Test @Order(4)
    void insertarDuplicadoLanza() {
        assertTrue(dao.insertar(AOT));
        assertThrows(AplicationException.class, () -> dao.insertar(AOT));
        assertEquals(1, dao.listar().size());
    }

    @Test @Order(5)
    void actualizar() {
        assertTrue(dao.insertar(KNY));
        assertTrue(dao.actualizar(new Serie("KNY-001", "Kimetsu no Yaiba", "shonen", 63, 8.7)));
        Serie s = dao.buscarPorCodigo("KNY-001").orElseThrow();
        assertAll(
            () -> assertEquals(63, s.getEpisodios()),
            () -> assertEquals(8.7, s.getNota(), 0.0001)
        );
    }

    @Test @Order(6)
    void actualizarInexistente() {
        assertFalse(dao.actualizar(new Serie("XXX-999", "Nada", "nada", 1, 1)));
    }

    @Test @Order(7)
    void eliminar() {
        assertTrue(dao.insertar(MOB));
        assertTrue(dao.eliminar("MOB-001"));
        assertTrue(dao.buscarPorCodigo("MOB-001").isEmpty());
    }

    @Test @Order(8)
    void eliminarInexistente() {
        assertFalse(dao.eliminar("XXX-999"));
    }

    @Test @Order(9)
    void buscarPorTituloContieneSinDistinguirMayusculas() {
        assertTrue(dao.insertar(AOT));
        assertTrue(dao.insertar(KNY));
        assertTrue(dao.insertar(OPM));
        assertEquals(List.of("KNY-001", "AOT-001"), codigos(dao.buscarPorTitulo("NO")));
        assertTrue(dao.buscarPorTitulo("dragon").isEmpty());
    }

    @Test @Order(10)
    void topPorNotaConDesempatePorCodigo() {
        assertTrue(dao.insertar(AOT));
        assertTrue(dao.insertar(KNY));
        assertTrue(dao.insertar(OPM));
        assertTrue(dao.insertar(FMA));
        assertEquals(List.of("AOT-001", "FMA-001", "OPM-001"), codigos(dao.topPorNota(3)));
    }

    @Test @Order(11)
    void topPorNotaConCantidadCeroONegativaDevuelveVacia() {
        assertTrue(dao.insertar(AOT));
        assertTrue(dao.insertar(KNY));
        assertTrue(dao.topPorNota(0).isEmpty());
        assertTrue(dao.topPorNota(-1).isEmpty());
    }

    @Test @Order(12)
    void generosConAlMenos() {
        for (Serie s : List.of(AOT, KNY, FMA, OPM, MOB, REZ)) {
            assertTrue(dao.insertar(s));
        }
        assertEquals(List.of("seinen", "shonen"), dao.generosConAlMenos(2));
        assertEquals(List.of("shonen"), dao.generosConAlMenos(3));
        assertTrue(dao.generosConAlMenos(4).isEmpty());
    }
}
