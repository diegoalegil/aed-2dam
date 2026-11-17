package es.ejemplo.arcade;

import es.ejemplo.arcade.dao.AbstractDao;
import es.ejemplo.arcade.dao.PlataformaDao;
import es.ejemplo.arcade.exception.AplicationException;
import es.ejemplo.arcade.model.Plataforma;
import es.ejemplo.arcade.support.BaseDatosTest;
import es.ejemplo.arcade.support.Semilla;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PlataformaDaoTest extends BaseDatosTest {
    private PlataformaDao dao;

    @BeforeEach
    void prepararDao() {
        Semilla.cargar(url);
        dao = new PlataformaDao(url);
    }

    @Test
    void heredaDeAbstractDaoDePlataforma() {
        Type padre = PlataformaDao.class.getGenericSuperclass();
        assertInstanceOf(ParameterizedType.class, padre, "PlataformaDao tiene que heredar de AbstractDao<Plataforma>");
        ParameterizedType generico = (ParameterizedType) padre;
        assertEquals(AbstractDao.class, generico.getRawType());
        assertEquals(Plataforma.class, generico.getActualTypeArguments()[0]);
    }

    @Test
    void listarOrdenadoPorCodigo() {
        assertEquals(List.of("PC", "PS5", "SWITCH", "XSX"),
                dao.listar().stream().map(Plataforma::getCodigo).toList());
    }

    @Test
    void buscarPorCodigoDevuelveTodosLosCampos() {
        Plataforma p = dao.buscarPorCodigo("SWITCH").orElseThrow();
        assertAll(() -> assertEquals("SWITCH", p.getCodigo()),
                  () -> assertEquals("Nintendo Switch", p.getNombre()),
                  () -> assertEquals("Nintendo", p.getFabricante()),
                  () -> assertEquals(2017, p.getAnio()));
    }

    @Test
    void buscarPorCodigoInexistenteDevuelveVacio() {
        assertEquals(Optional.empty(), dao.buscarPorCodigo("N64"));
    }

    @Test
    void insertarYLeer() {
        assertTrue(dao.insertar(new Plataforma("GB", "Game Boy", "Nintendo", 1989)));
        Plataforma p = dao.buscarPorCodigo("GB").orElseThrow();
        assertAll(() -> assertEquals("Game Boy", p.getNombre()),
                  () -> assertEquals("Nintendo", p.getFabricante()),
                  () -> assertEquals(1989, p.getAnio()));
        assertEquals(5, contar("SELECT COUNT(*) FROM plataforma"));
    }

    @Test
    void insertarDuplicadoLanza() {
        assertThrows(AplicationException.class,
                () -> dao.insertar(new Plataforma("PS5", "PlayStation 5 Pro", "Sony", 2024)));
        assertEquals(4, contar("SELECT COUNT(*) FROM plataforma"));
    }

    @Test
    void actualizarExistenteYNoExistente() {
        assertTrue(dao.actualizar(new Plataforma("PC", "PC Gaming", "Valve", 2003)));
        Plataforma p = dao.buscarPorCodigo("PC").orElseThrow();
        assertAll(() -> assertEquals("PC Gaming", p.getNombre()),
                  () -> assertEquals("Valve", p.getFabricante()),
                  () -> assertEquals(2003, p.getAnio()));
        assertFalse(dao.actualizar(new Plataforma("N64", "Nintendo 64", "Nintendo", 1996)));
        assertEquals(4, contar("SELECT COUNT(*) FROM plataforma"));
    }

    @Test
    void eliminarExistenteYNoExistente() {
        assertTrue(dao.eliminar("XSX"));
        assertEquals(0, contar("SELECT COUNT(*) FROM plataforma WHERE codigo = 'XSX'"));
        assertFalse(dao.eliminar("XSX"));
        assertFalse(dao.eliminar("N64"));
        assertEquals(3, contar("SELECT COUNT(*) FROM plataforma"));
    }
}
