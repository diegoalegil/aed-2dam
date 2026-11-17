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

    /** Con una base válida se inserta un libro y listar lo devuelve. */
    @Test
    void insertarYListarConBaseValidaTest() {
        LibroDao dao = new LibroDao(url);

        int id = dao.insertar(new Libro(null, "Rayuela", 1));
        List<Libro> libros = dao.listar();

        assertEquals(1, id);
        assertEquals(1, libros.size());
        assertEquals("Rayuela", libros.get(0).getTitulo());
        assertEquals(1, libros.get(0).getIdAutor());
    }

    /** Dos inserciones devuelven los ids 1 y 2 y listar las da ordenadas por id. */
    @Test
    void insertarDevuelveIdsSeguidosTest() {
        LibroDao dao = new LibroDao(url);

        assertEquals(1, dao.insertar(new Libro(null, "Primero", 1)));
        assertEquals(2, dao.insertar(new Libro(null, "Segundo", 1)));

        List<Libro> libros = dao.listar();
        assertEquals(2, libros.size());
        assertEquals(1, libros.get(0).getId());
        assertEquals(2, libros.get(1).getId());
    }

    /** PRAGMA foreign_keys está activo: un libro con un autor que no existe da DaoException. */
    @Test
    void claveForaneaActivaTest() {
        LibroDao dao = new LibroDao(url);

        assertThrows(DaoException.class, () -> dao.insertar(new Libro(null, "Sin autor", 999)));
        assertTrue(dao.listar().isEmpty());
    }

    /** Un error de SQL (titulo null, NOT NULL) llega como DaoException con la SQLException de causa. */
    @Test
    void errorSqlLlegaComoDaoExceptionConCausaTest() {
        LibroDao dao = new LibroDao(url);

        DaoException e = assertThrows(DaoException.class, () -> dao.insertar(new Libro(null, null, 1)));
        assertInstanceOf(SQLException.class, e.getCause());
    }

    /** Una url null lanza IllegalArgumentException en el propio new. */
    @Test
    void urlNullFallaEnElNewTest() {
        assertThrows(IllegalArgumentException.class, () -> new LibroDao(null));
    }

    /** Una url sin el prefijo jdbc:sqlite: lanza IllegalArgumentException en el propio new. */
    @Test
    void urlSinPrefijoFallaEnElNewTest() {
        assertThrows(IllegalArgumentException.class, () -> new LibroDao("sqlite:" + path.toAbsolutePath()));
        assertThrows(IllegalArgumentException.class, () -> new LibroDao("jdbc:mysql:" + path.toAbsolutePath()));
    }

    /** Un fichero que no existe lanza DaoException en el new, con causa null. */
    @Test
    void ficheroInexistenteFallaEnElNewTest() {
        Path noExiste = carpeta.resolve("no-existe.sqlite3");

        DaoException e = assertThrows(DaoException.class, () -> new LibroDao("jdbc:sqlite:" + noExiste.toAbsolutePath()));
        assertNull(e.getCause());
    }

    /** Si el fichero no existe, el new no lo crea. */
    @Test
    void ficheroInexistenteNoSeCreaTest() {
        Path noExiste = carpeta.resolve("no-existe.sqlite3");

        assertThrows(DaoException.class, () -> new LibroDao("jdbc:sqlite:" + noExiste.toAbsolutePath()));
        assertTrue(Files.notExists(noExiste));
    }

    /** Una ruta que es una carpeta (no un fichero normal) lanza DaoException en el new. */
    @Test
    void carpetaFallaEnElNewTest() {
        DaoException e = assertThrows(DaoException.class, () -> new LibroDao("jdbc:sqlite:" + carpeta.toAbsolutePath()));
        assertNull(e.getCause());
    }
}
