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
 * Pruebas del padre AbstractDao (variante 4: si no existe, se crea con sus tablas),
 * siempre a través de LibroDao.
 */
class PadreTest extends BaseTest {

    /** Con una base de datos válida, lo insertado aparece en listar(). */
    @Test
    void insertarYListarFuncionaTest() {
        LibroDao dao = new LibroDao(url);

        int id = dao.insertar(new Libro(null, "Dune", 1));

        assertTrue(id > 0);
        List<Libro> libros = dao.listar();
        assertEquals(1, libros.size());
        assertEquals(id, libros.get(0).getId());
        assertEquals("Dune", libros.get(0).getTitulo());
        assertEquals(1, libros.get(0).getIdAutor());
    }

    /** El new con una ruta que no existe crea el fichero y listar() devuelve una lista vacía. */
    @Test
    void ficheroQueNoExisteSeCreaEnElNewTest() {
        Path nueva = carpeta.resolve("nueva.sqlite3");
        assertFalse(Files.exists(nueva));

        LibroDao dao = new LibroDao("jdbc:sqlite:" + nueva.toAbsolutePath());

        assertTrue(Files.exists(nueva));
        assertTrue(dao.listar().isEmpty());
    }

    /** Las dos tablas quedan creadas en el new, antes de llamar a ningún método del DAO. */
    @Test
    void tablasSeCreanEnElNewTest() throws Exception {
        String urlNueva = "jdbc:sqlite:" + carpeta.resolve("tablas.sqlite3").toAbsolutePath();

        new LibroDao(urlNueva);

        assertEquals(2, consultarNumero(urlNueva,
                "SELECT COUNT(*) FROM sqlite_master WHERE type = 'table' AND name IN ('autores', 'libros')"));
    }

    /** Construir otra vez sobre una base de datos con datos no da error y conserva los datos (IF NOT EXISTS). */
    @Test
    void construirOtraVezConservaLosDatosTest() {
        new LibroDao(url).insertar(new Libro(null, "Dune", 1));

        LibroDao otro = new LibroDao(url);

        List<Libro> libros = otro.listar();
        assertEquals(1, libros.size());
        assertEquals("Dune", libros.get(0).getTitulo());
    }

    /** Una url null lanza IllegalArgumentException en el new. */
    @Test
    void urlNullLanzaIllegalArgumentEnElNewTest() {
        assertThrows(IllegalArgumentException.class, () -> new LibroDao(null));
    }

    /** Una url sin el prefijo jdbc:sqlite: lanza IllegalArgumentException en el new. */
    @Test
    void urlSinPrefijoLanzaIllegalArgumentEnElNewTest() {
        String sinPrefijo = ruta.toAbsolutePath().toString();
        assertThrows(IllegalArgumentException.class, () -> new LibroDao(sinPrefijo));
        assertThrows(IllegalArgumentException.class, () -> new LibroDao("jdbc:mysql://localhost/libros"));
    }

    /** Si la carpeta del fichero no existe, el new lanza DaoException con la SQLException como causa. */
    @Test
    void carpetaQueNoExisteLanzaDaoExceptionEnElNewTest() {
        String urlMala = "jdbc:sqlite:" + carpeta.resolve("no-existe").resolve("libros.sqlite3").toAbsolutePath();

        DaoException e = assertThrows(DaoException.class, () -> new LibroDao(urlMala));

        assertInstanceOf(SQLException.class, e.getCause());
    }

    /** Las claves foráneas están activas en obtenerConexion: un idAutor que no existe lanza DaoException. */
    @Test
    void clavesForaneasActivasTest() throws Exception {
        String urlNueva = "jdbc:sqlite:" + carpeta.resolve("fk.sqlite3").toAbsolutePath();
        LibroDao dao = new LibroDao(urlNueva);
        ejecutarSql(urlNueva, "INSERT INTO autores (id, nombre) VALUES (1, 'Luis')");

        assertTrue(dao.insertar(new Libro(null, "Valido", 1)) > 0);
        DaoException e = assertThrows(DaoException.class, () -> dao.insertar(new Libro(null, "Huerfano", 999)));

        assertInstanceOf(SQLException.class, e.getCause());
        assertEquals(1, dao.listar().size());
    }

    /** Un error de SQL (titulo null, NOT NULL) llega como DaoException cuya causa es una SQLException. */
    @Test
    void errorSqlLlegaComoDaoExceptionConCausaTest() {
        LibroDao dao = new LibroDao(url);

        DaoException e = assertThrows(DaoException.class, () -> dao.insertar(new Libro(null, null, 1)));

        assertInstanceOf(SQLException.class, e.getCause());
    }
}
