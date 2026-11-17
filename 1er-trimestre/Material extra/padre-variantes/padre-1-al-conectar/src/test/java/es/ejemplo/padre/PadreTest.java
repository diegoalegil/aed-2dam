package es.ejemplo.padre;

import es.ejemplo.padre.dao.DaoException;
import es.ejemplo.padre.dao.LibroDao;
import es.ejemplo.padre.modelo.Libro;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas del padre AbstractDao a través de LibroDao.
 */
class PadreTest extends BaseTest {

    /** El constructor con url null no lanza nada: solo guarda la url. */
    @Test
    void nuevoConUrlNullNoLanzaTest() {
        assertDoesNotThrow(() -> new LibroDao(null));
    }

    /** Con url null, listar lanza IllegalArgumentException al conectar. */
    @Test
    void listarConUrlNullLanzaIllegalArgumentTest() {
        LibroDao dao = new LibroDao(null);
        assertThrows(IllegalArgumentException.class, dao::listar);
    }

    /** Con una url sin "jdbc:sqlite:" el new no lanza, pero listar lanza IllegalArgumentException. */
    @Test
    void prefijoMaloLanzaAlListarTest() {
        LibroDao dao = assertDoesNotThrow(() -> new LibroDao(path.toAbsolutePath().toString()));
        assertThrows(IllegalArgumentException.class, dao::listar);
    }

    /** Si el fichero no existe, listar lanza IllegalArgumentException y el fichero no se crea. */
    @Test
    void ficheroInexistenteNoSeCreaTest() {
        Path falta = carpeta.resolve("no-existe.sqlite3");
        LibroDao dao = assertDoesNotThrow(() -> new LibroDao("jdbc:sqlite:" + falta.toAbsolutePath()));
        assertThrows(IllegalArgumentException.class, dao::listar);
        assertFalse(Files.exists(falta));
    }

    /** Si el fichero no existe, insertar también lanza IllegalArgumentException y no crea el fichero. */
    @Test
    void insertarConFicheroInexistenteLanzaTest() {
        Path falta = carpeta.resolve("tampoco.sqlite3");
        LibroDao dao = new LibroDao("jdbc:sqlite:" + falta.toAbsolutePath());
        assertThrows(IllegalArgumentException.class, () -> dao.insertar(new Libro(null, "Nada", 1)));
        assertFalse(Files.exists(falta));
    }

    /** Si la ruta es una carpeta, listar lanza IllegalArgumentException. */
    @Test
    void rutaDeCarpetaLanzaIllegalArgumentTest() {
        LibroDao dao = new LibroDao("jdbc:sqlite:" + carpeta.toAbsolutePath());
        assertThrows(IllegalArgumentException.class, dao::listar);
    }

    /** Con "jdbc:sqlite:" sin ruta, listar lanza IllegalArgumentException (no abre una base en memoria). */
    @Test
    void urlSinRutaLanzaIllegalArgumentTest() {
        LibroDao dao = new LibroDao("jdbc:sqlite:");
        assertThrows(IllegalArgumentException.class, dao::listar);
    }

    /** Con una base válida, insertar devuelve el id y listar devuelve el libro. */
    @Test
    void baseValidaInsertarYListarTest() {
        LibroDao dao = new LibroDao(url);
        int id = dao.insertar(new Libro(null, "Rayuela", 1));
        assertTrue(id > 0);

        List<Libro> libros = dao.listar();
        assertEquals(1, libros.size());
        assertEquals(id, libros.get(0).getId());
        assertEquals("Rayuela", libros.get(0).getTitulo());
        assertEquals(1, libros.get(0).getIdAutor());
    }

    /** PRAGMA foreign_keys está activo: un autor que no existe lanza DaoException y no se inserta. */
    @Test
    void clavesForaneasActivasTest() {
        LibroDao dao = new LibroDao(url);
        assertThrows(DaoException.class, () -> dao.insertar(new Libro(null, "Sin autor", 999)));
        assertTrue(dao.listar().isEmpty());
    }

    /** Un error de SQL (titulo null) llega como DaoException con una SQLException como causa. */
    @Test
    void errorSqlLlegaComoDaoExceptionTest() {
        LibroDao dao = new LibroDao(url);
        DaoException e = assertThrows(DaoException.class, () -> dao.insertar(new Libro(null, null, 1)));
        assertInstanceOf(SQLException.class, e.getCause());
    }
}
