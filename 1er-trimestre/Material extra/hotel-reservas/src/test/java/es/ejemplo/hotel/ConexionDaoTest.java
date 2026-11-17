package es.ejemplo.hotel;

import es.ejemplo.hotel.dao.*;
import es.ejemplo.hotel.modelo.Huesped;
import es.ejemplo.hotel.modelo.Reserva;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas de la conexion y de las comprobaciones que hace AbstractDao al construir un DAO. */
class ConexionDaoTest extends BaseDaoTest {

    @TempDir
    Path carpeta;

    /** Una url sin el prefijo jdbc:sqlite: no es valida, aunque el fichero exista. */
    @Test
    void urlSinPrefijoLanzaIllegalArgumentExceptionTest() {
        String sinPrefijo = archivoBaseDatos.toAbsolutePath().toString();
        assertThrows(IllegalArgumentException.class, () -> new HuespedDao(sinPrefijo));
    }

    /** Si el fichero no existe, el DAO no se puede crear. */
    @Test
    void ficheroInexistenteLanzaDaoExceptionTest() {
        Path noExiste = carpeta.resolve("no-existe.sqlite3");
        assertThrows(DaoException.class, () -> new HabitacionDao("jdbc:sqlite:" + noExiste));
    }

    /** Rechazar un fichero que no existe no puede crearlo (SQLite lo crearia vacio al conectar). */
    @Test
    void ficheroInexistenteNoSeCreaTest() {
        Path noExiste = carpeta.resolve("no-existe.sqlite3");
        assertThrows(RuntimeException.class, () -> new ReservaDao("jdbc:sqlite:" + noExiste));
        assertFalse(Files.exists(noExiste));
    }

    /** Un fichero vacio (0 bytes) no es una base de datos del hotel. */
    @Test
    void ficheroVacioLanzaDaoExceptionTest() throws Exception {
        Path vacio = Files.createFile(carpeta.resolve("vacio.sqlite3"));
        assertThrows(DaoException.class, () -> new ConsumoDao("jdbc:sqlite:" + vacio));
    }

    /** Una base de datos SQLite de otro dominio (sin las tablas del hotel) no es valida. */
    @Test
    void baseDatosSinLasTablasDelHotelLanzaDaoExceptionTest() throws Exception {
        Path otra = carpeta.resolve("clinica.sqlite3");
        ejecutarSql(otra,
                "CREATE TABLE clientes (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT NOT NULL)",
                "CREATE TABLE mascotas (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT NOT NULL)");
        assertThrows(DaoException.class, () -> new ReservaDao("jdbc:sqlite:" + otra));
    }

    /** Tener tablas de mas (sqlite_sequence, auditoria...) no impide usar la base de datos. */
    @Test
    void baseDatosConTablasDeMasFuncionaTest() throws Exception {
        ejecutarSql(archivoBaseDatos,
                "CREATE TABLE auditoria (id INTEGER PRIMARY KEY AUTOINCREMENT, texto TEXT NOT NULL)");
        IHuespedDao otroDao = new HuespedDao(url);
        assertTrue(otroDao.insertar(new Huesped(null, "Ana Ruiz", "11111111A", "ana@hotel.es", "ES")) > 0);
    }

    /** Las claves foraneas tienen que estar activadas en la conexion que hace el INSERT. */
    @Test
    void clavesForaneasActivadasAlInsertarTest() {
        assertThrows(DaoException.class,
                () -> reservaDao.insertar(new Reserva(null, 999, 999, "2026-11-02", 2, "CONFIRMADA")));
    }

    /** DaoException es no comprobada y conserva el mensaje y la causa. */
    @Test
    void daoExceptionEsNoComprobadaYGuardaLaCausaTest() {
        SQLException causa = new SQLException("fallo de prueba");
        DaoException excepcion = new DaoException("mensaje de prueba", causa);
        assertInstanceOf(RuntimeException.class, excepcion);
        assertEquals("mensaje de prueba", excepcion.getMessage());
        assertSame(causa, excepcion.getCause());
    }

    /** Un error de SQLite llega como DaoException con la SQLException original como causa. */
    @Test
    void errorSqlConservaLaSqlExceptionComoCausaTest() {
        insertarHuespedDePrueba();
        DaoException excepcion = assertThrows(DaoException.class,
                () -> huespedDao.insertar(new Huesped(null, "Otra Ana", "11111111A", "otra@hotel.es", "ES")));
        assertInstanceOf(SQLException.class, excepcion.getCause());
    }
}
