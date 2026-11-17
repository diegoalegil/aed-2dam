package es.ejemplo.arcade;

import es.ejemplo.arcade.dao.AbstractDao;
import es.ejemplo.arcade.dao.VideojuegoDao;
import es.ejemplo.arcade.exception.AplicationException;
import es.ejemplo.arcade.model.Videojuego;
import es.ejemplo.arcade.support.BaseDatosTest;
import es.ejemplo.arcade.support.Semilla;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class VideojuegoDaoTest extends BaseDatosTest {
    private VideojuegoDao dao;

    @BeforeEach
    void prepararDao() {
        Semilla.cargar(url);
        dao = new VideojuegoDao(url);
    }

    private static List<Integer> ids(List<Videojuego> videojuegos) {
        return videojuegos.stream().map(Videojuego::getId).toList();
    }

    @Test
    void heredaDeAbstractDaoDeVideojuego() {
        Type padre = VideojuegoDao.class.getGenericSuperclass();
        assertInstanceOf(ParameterizedType.class, padre, "VideojuegoDao tiene que heredar de AbstractDao<Videojuego>");
        ParameterizedType generico = (ParameterizedType) padre;
        assertEquals(AbstractDao.class, generico.getRawType());
        assertEquals(Videojuego.class, generico.getActualTypeArguments()[0]);
    }

    @Test
    void listarOrdenadoPorId() {
        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7), ids(dao.listar()));
    }

    @Test
    void buscarPorIdDevuelveTodosLosCampos() {
        Videojuego baldur = dao.buscarPorId(1).orElseThrow();
        assertAll(() -> assertEquals(1, baldur.getId()),
                  () -> assertEquals("Baldur's Gate 3", baldur.getTitulo()),
                  () -> assertEquals(59.99, baldur.getPrecio(), 0.0001),
                  () -> assertTrue(baldur.isMultijugador()),
                  () -> assertEquals(96, baldur.getNota()),
                  () -> assertEquals("PC", baldur.getPlataforma()));
        Videojuego assassins = dao.buscarPorId(5).orElseThrow();
        assertAll(() -> assertEquals("Assassin's Creed Shadows", assassins.getTitulo()),
                  () -> assertFalse(assassins.isMultijugador()),
                  () -> assertEquals(81, assassins.getNota()),
                  () -> assertEquals("PS5", assassins.getPlataforma()));
    }

    @Test
    void buscarPorIdInexistenteDevuelveVacio() {
        assertEquals(Optional.empty(), dao.buscarPorId(99));
    }

    @Test
    void insertarYLeer() {
        assertTrue(dao.insertar(new Videojuego(8, "Tom Clancy's Rainbow Six Siege", 19.99, true, 85, "PC")));
        Videojuego v = dao.buscarPorId(8).orElseThrow();
        assertAll(() -> assertEquals("Tom Clancy's Rainbow Six Siege", v.getTitulo()),
                  () -> assertEquals(19.99, v.getPrecio(), 0.0001),
                  () -> assertTrue(v.isMultijugador()),
                  () -> assertEquals(85, v.getNota()),
                  () -> assertEquals("PC", v.getPlataforma()));
        assertEquals(8, contar("SELECT COUNT(*) FROM videojuego"));
    }

    @Test
    void insertarDuplicadoLanza() {
        assertThrows(AplicationException.class,
                () -> dao.insertar(new Videojuego(3, "Otro juego", 10.0, false, 50, "PS5")));
        assertEquals("Astro Bot", leerTexto("SELECT titulo FROM videojuego WHERE id = 3"));
    }

    @Test
    void actualizarExistenteYNoExistente() {
        assertTrue(dao.actualizar(new Videojuego(4, "Hollow Knight: Silksong GOTY", 24.99, true, 91, "PC")));
        Videojuego v = dao.buscarPorId(4).orElseThrow();
        assertAll(() -> assertEquals("Hollow Knight: Silksong GOTY", v.getTitulo()),
                  () -> assertEquals(24.99, v.getPrecio(), 0.0001),
                  () -> assertTrue(v.isMultijugador()),
                  () -> assertEquals(91, v.getNota()),
                  () -> assertEquals("PC", v.getPlataforma()));
        assertFalse(dao.actualizar(new Videojuego(99, "No existe", 1.0, false, 1, "PC")));
        assertEquals(7, contar("SELECT COUNT(*) FROM videojuego"));
    }

    @Test
    void eliminarExistenteYNoExistente() {
        assertTrue(dao.eliminar(6));
        assertEquals(Optional.empty(), dao.buscarPorId(6));
        assertFalse(dao.eliminar(6));
        assertEquals(6, contar("SELECT COUNT(*) FROM videojuego"));
    }

    @Test
    void buscarPorPlataformaOrdenadoPorId() {
        assertEquals(List.of(2, 4), ids(dao.buscarPorPlataforma("SWITCH")));
        assertEquals(List.of(), dao.buscarPorPlataforma("XSX"));
    }

    @Test
    void multijugadorHastaIncluyeElPrecioMaximo() {
        assertEquals(List.of(6, 2), ids(dao.multijugadorHasta(49.99)));
        assertEquals(List.of(6), ids(dao.multijugadorHasta(49.98)));
    }

    @Test
    void masCarosOrdenadoYEmpatePorId() {
        assertEquals(List.of(5, 1, 2), ids(dao.masCaros(3)));
        assertEquals(List.of(5, 1, 2, 3, 7), ids(dao.masCaros(5)));
    }

    @Test
    void masCarosConCeroONegativoDevuelveVacio() {
        assertEquals(List.of(), dao.masCaros(0));
        assertEquals(List.of(), dao.masCaros(-1));
    }

    @Test
    void masCarosMayorQueElTotalDevuelveTodos() {
        assertEquals(List.of(5, 1, 2, 3, 7, 4, 6), ids(dao.masCaros(100)));
    }
}
