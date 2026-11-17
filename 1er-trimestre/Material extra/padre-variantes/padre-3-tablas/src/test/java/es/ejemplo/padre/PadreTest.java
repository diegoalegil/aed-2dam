package es.ejemplo.padre;

import es.ejemplo.padre.dao.DaoException;
import es.ejemplo.padre.dao.LibroDao;
import es.ejemplo.padre.infra.AbstractDao;
import es.ejemplo.padre.modelo.Libro;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de AbstractDao a través de LibroDao. La última mira las cabeceras del padre.
 */
class PadreTest extends BaseTest {

    /** Con una base válida, insertar devuelve el id y listar devuelve el libro. */
    @Test
    void baseValidaInsertarYListarTest() {
        LibroDao dao = new LibroDao(url);
        int id = dao.insertar(new Libro(null, "Dune", 1));
        assertTrue(id > 0);
        List<Libro> libros = dao.listar();
        assertEquals(1, libros.size());
        assertEquals(Integer.valueOf(id), libros.get(0).getId());
        assertEquals("Dune", libros.get(0).getTitulo());
        assertEquals(Integer.valueOf(1), libros.get(0).getIdAutor());
    }

    /** Url nula, de otro gestor o sin prefijo: IllegalArgumentException en el new. */
    @Test
    void urlNoValidaLanzaIllegalArgumentEnElNewTest() {
        assertThrows(IllegalArgumentException.class, () -> new LibroDao(null));
        assertThrows(IllegalArgumentException.class, () -> new LibroDao("jdbc:mysql://localhost/biblioteca"));
        assertThrows(IllegalArgumentException.class, () -> new LibroDao(path.toString()));
    }

    /** Fichero que no existe: DaoException sin causa en el new y el fichero no se crea. */
    @Test
    void ficheroInexistenteLanzaDaoExceptionYNoSeCreaTest() {
        Path noExiste = carpeta.resolve("no-existe.sqlite3");
        DaoException e = assertThrows(DaoException.class, () -> new LibroDao(urlDe(noExiste)));
        assertNull(e.getCause());
        assertTrue(Files.notExists(noExiste));
    }

    /** Ruta que es una carpeta: DaoException sin causa en el new. */
    @Test
    void rutaQueEsCarpetaLanzaDaoExceptionEnElNewTest() {
        DaoException e = assertThrows(DaoException.class, () -> new LibroDao(urlDe(carpeta)));
        assertNull(e.getCause());
    }

    /** Fichero vacío de 0 bytes (sin tablas): DaoException en el new. */
    @Test
    void ficheroVacioLanzaDaoExceptionEnElNewTest() throws Exception {
        Path vacio = Files.createFile(carpeta.resolve("vacio.sqlite3"));
        assertThrows(DaoException.class, () -> new LibroDao(urlDe(vacio)));
    }

    /** Fichero de texto que no es SQLite: DaoException en el new con la SQLException como causa. */
    @Test
    void ficheroDeTextoLanzaDaoExceptionConCausaEnElNewTest() throws Exception {
        Path texto = carpeta.resolve("notas.sqlite3");
        Files.writeString(texto, "Esto no es una base de datos.\n".repeat(50));
        DaoException e = assertThrows(DaoException.class, () -> new LibroDao(urlDe(texto)));
        assertInstanceOf(SQLException.class, e.getCause());
    }

    /** Base de otro dominio (solo clientes) o con solo autores: DaoException en el new. */
    @Test
    void baseSinLasTablasLanzaDaoExceptionEnElNewTest() throws Exception {
        Path tienda = carpeta.resolve("tienda.sqlite3");
        ejecutar(urlDe(tienda), "CREATE TABLE clientes (id INTEGER PRIMARY KEY, nombre TEXT)");
        assertThrows(DaoException.class, () -> new LibroDao(urlDe(tienda)));

        Path soloAutores = carpeta.resolve("solo-autores.sqlite3");
        ejecutar(urlDe(soloAutores), "CREATE TABLE autores (id INTEGER PRIMARY KEY, nombre TEXT)");
        assertThrows(DaoException.class, () -> new LibroDao(urlDe(soloAutores)));
    }

    /** Una tabla de más (auditoria) no impide construir el DAO ni usarlo. */
    @Test
    void tablaExtraNoImpideConstruirTest() throws Exception {
        ejecutar(url, "CREATE TABLE auditoria (id INTEGER PRIMARY KEY, accion TEXT)");
        LibroDao dao = assertDoesNotThrow(() -> new LibroDao(url));
        assertTrue(dao.listar().isEmpty());
    }

    /** Claves foráneas activas: un libro con el autor 999 lanza DaoException al insertar. */
    @Test
    void clavesForaneasActivasTest() {
        LibroDao dao = new LibroDao(url);
        assertThrows(DaoException.class, () -> dao.insertar(new Libro(null, "Sin autor", 999)));
        assertTrue(dao.listar().isEmpty());
    }

    /** Error de SQL (titulo nulo, NOT NULL): DaoException con la SQLException como causa. */
    @Test
    void errorSqlLlegaComoDaoExceptionConCausaTest() {
        LibroDao dao = new LibroDao(url);
        DaoException e = assertThrows(DaoException.class, () -> dao.insertar(new Libro(null, null, 1)));
        assertInstanceOf(SQLException.class, e.getCause());
    }

    /** Ningún método del padre lleva throws: constructor, obtenerConexion y map. */
    @Test
    void padreSinThrowsTest() throws Exception {
        assertEquals(0, AbstractDao.class.getDeclaredConstructor(String.class).getExceptionTypes().length);
        assertEquals(0, AbstractDao.class.getDeclaredMethod("obtenerConexion").getExceptionTypes().length);
        assertEquals(0, AbstractDao.class.getDeclaredMethod("map", ResultSet.class).getExceptionTypes().length);
    }
}
