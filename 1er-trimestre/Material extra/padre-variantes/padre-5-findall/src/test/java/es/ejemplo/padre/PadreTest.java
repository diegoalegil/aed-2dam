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

class PadreTest extends BaseTest {

    /** Con una base válida, lo insertado sale en listar ordenado por id y con sus datos. */
    @Test
    void listarDevuelveEnOrdenDeIdTest() {
        LibroDao dao = new LibroDao(url);
        dao.insertar(new Libro(null, "Dune", 1));
        dao.insertar(new Libro(null, "Akira", 1));
        dao.insertar(new Libro(null, "Momo", 1));

        List<Libro> libros = dao.listar();

        assertEquals(3, libros.size());
        assertEquals("Dune", libros.get(0).getTitulo());
        assertEquals("Akira", libros.get(1).getTitulo());
        assertEquals("Momo", libros.get(2).getTitulo());
        assertTrue(libros.get(0).getId() < libros.get(1).getId());
        assertEquals(1, libros.get(0).getIdAutor());
    }

    /** insertar devuelve el id nuevo y cada id es mayor que el anterior. */
    @Test
    void insertarDevuelveIdsCrecientesTest() {
        LibroDao dao = new LibroDao(url);

        int primero = dao.insertar(new Libro(null, "Dune", 1));
        int segundo = dao.insertar(new Libro(null, "Akira", 1));

        assertTrue(primero > 0);
        assertTrue(segundo > primero);
        assertEquals(segundo, dao.listar().get(1).getId());
    }

    /** buscarPorTitulo pasa el texto como ? y encuentra coincidencias parciales ordenadas por título. */
    @Test
    void buscarPorTituloParcialTest() {
        LibroDao dao = new LibroDao(url);
        dao.insertar(new Libro(null, "Quijote ilustrado", 1));
        dao.insertar(new Libro(null, "Dune", 1));
        dao.insertar(new Libro(null, "El Quijote", 1));

        List<Libro> libros = dao.buscarPorTitulo("Quijote");

        assertEquals(2, libros.size());
        assertEquals("El Quijote", libros.get(0).getTitulo());
        assertEquals("Quijote ilustrado", libros.get(1).getTitulo());
        assertTrue(dao.buscarPorTitulo(null).isEmpty());
        assertTrue(dao.buscarPorTitulo("  ").isEmpty());
    }

    /** Un intento de inyección SQL en buscarPorTitulo no devuelve nada porque va como parámetro. */
    @Test
    void buscarPorTituloSinInyeccionTest() {
        LibroDao dao = new LibroDao(url);
        dao.insertar(new Libro(null, "Dune", 1));
        dao.insertar(new Libro(null, "Akira", 1));

        assertTrue(dao.buscarPorTitulo("x' OR 1=1 --").isEmpty());
    }

    /** eliminar devuelve 1 la primera vez y 0 cuando el libro ya no existe. */
    @Test
    void eliminarDevuelveUnoYLuegoCeroTest() {
        LibroDao dao = new LibroDao(url);
        int id = dao.insertar(new Libro(null, "Dune", 1));

        assertEquals(1, dao.eliminar(id));
        assertEquals(0, dao.eliminar(id));
        assertTrue(dao.listar().isEmpty());
    }

    /** PRAGMA foreign_keys está activo: un libro con un autor que no existe lanza DaoException. */
    @Test
    void clavesForaneasActivasTest() {
        LibroDao dao = new LibroDao(url);

        assertThrows(DaoException.class, () -> dao.insertar(new Libro(null, "Dune", 999)));
        assertTrue(dao.listar().isEmpty());
    }

    /** Un error de SQL (titulo null en una columna NOT NULL) llega como DaoException con un SQLException de causa. */
    @Test
    void errorSqlLlegaComoDaoExceptionTest() {
        LibroDao dao = new LibroDao(url);

        DaoException e = assertThrows(DaoException.class, () -> dao.insertar(new Libro(null, null, 1)));
        assertInstanceOf(SQLException.class, e.getCause());
    }

    /** Una url null o que no es de SQLite no falla en el new, falla con IllegalArgumentException al usarla. */
    @Test
    void urlNoValidaFallaAlUsarlaTest() {
        LibroDao nula = assertDoesNotThrow(() -> new LibroDao(null));
        LibroDao mysql = assertDoesNotThrow(() -> new LibroDao("jdbc:mysql://localhost/biblioteca"));

        assertThrows(IllegalArgumentException.class, nula::listar);
        assertThrows(IllegalArgumentException.class, mysql::listar);
    }

    /** Un fichero que no existe lanza IllegalArgumentException al usarlo y el fichero no se crea. */
    @Test
    void ficheroInexistenteFallaYNoSeCreaTest() {
        Path noExiste = carpeta.resolve("no-existe.sqlite3");
        LibroDao dao = assertDoesNotThrow(() -> new LibroDao("jdbc:sqlite:" + noExiste.toAbsolutePath()));

        assertThrows(IllegalArgumentException.class, dao::listar);
        assertFalse(Files.exists(noExiste));
    }

    /** Una url que apunta a una carpeta (no es un fichero normal) lanza IllegalArgumentException al usarla. */
    @Test
    void rutaQueEsCarpetaFallaTest() {
        LibroDao dao = assertDoesNotThrow(() -> new LibroDao("jdbc:sqlite:" + carpeta.toAbsolutePath()));

        assertThrows(IllegalArgumentException.class, () -> dao.insertar(new Libro(null, "Dune", 1)));
    }
}
