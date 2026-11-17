package es.ejemplo.hotel;

import es.ejemplo.hotel.dao.*;
import es.ejemplo.hotel.modelo.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

/**
 * Preparacion comun de las pruebas: crea un fichero SQLite temporal por test, ejecuta el esquema,
 * crea los cuatro DAO y borra el fichero al terminar. No toca la base de datos de datos/.
 */
abstract class BaseDaoTest {

    protected Path archivoBaseDatos;
    protected String url;
    protected IHuespedDao huespedDao;
    protected IHabitacionDao habitacionDao;
    protected IReservaDao reservaDao;
    protected IConsumoDao consumoDao;

    @BeforeEach
    void prepararBaseDatosDePrueba() throws Exception {
        archivoBaseDatos = Files.createTempFile("hotel-junit-", ".sqlite3");
        url = "jdbc:sqlite:" + archivoBaseDatos.toAbsolutePath();
        ejecutarSql(archivoBaseDatos, esquema());
        huespedDao = new HuespedDao(url);
        habitacionDao = new HabitacionDao(url);
        reservaDao = new ReservaDao(url);
        consumoDao = new ConsumoDao(url);
    }

    @AfterEach
    void borrarBaseDatosDePrueba() throws Exception {
        Files.deleteIfExists(archivoBaseDatos);
    }

    /** Sentencias de src/test/resources/schema.sql, separadas por punto y coma. */
    protected static String[] esquema() throws Exception {
        String sql = new String(Objects.requireNonNull(
                BaseDaoTest.class.getResourceAsStream("/schema.sql")).readAllBytes());
        return sql.split(";");
    }

    /** Ejecuta sentencias SQL sobre un fichero SQLite (lo crea si no existe) sin pasar por los DAO. */
    protected static void ejecutarSql(Path fichero, String... sentencias) throws SQLException {
        try (Connection conexion = DriverManager.getConnection("jdbc:sqlite:" + fichero.toAbsolutePath());
             Statement st = conexion.createStatement()) {
            for (String sentencia : sentencias) {
                if (!sentencia.isBlank()) {
                    st.execute(sentencia);
                }
            }
        }
    }

    /** Inserta un huesped y devuelve su id. */
    protected int nuevoHuesped(String nombre, String dni, String email, String pais) {
        return huespedDao.insertar(new Huesped(null, nombre, dni, email, pais));
    }

    /** Inserta una habitacion y devuelve su id. */
    protected int nuevaHabitacion(String numero, String tipo, int planta, String precioNoche) {
        return habitacionDao.insertar(new Habitacion(null, numero, tipo, planta, new BigDecimal(precioNoche)));
    }

    /** Inserta una reserva y devuelve su id. */
    protected int nuevaReserva(int idHuesped, int idHabitacion, String fechaEntrada, int noches, String estado) {
        return reservaDao.insertar(new Reserva(null, idHuesped, idHabitacion, fechaEntrada, noches, estado));
    }

    /** Inserta un consumo y devuelve su id. */
    protected int nuevoConsumo(int idReserva, String concepto, int cantidad, String precioUnitario, String fecha) {
        return consumoDao.insertar(new Consumo(null, idReserva, concepto, cantidad,
                new BigDecimal(precioUnitario), fecha));
    }

    /** Huesped de ejemplo: Ana Ruiz, de ES. */
    protected int insertarHuespedDePrueba() {
        return nuevoHuesped("Ana Ruiz", "11111111A", "ana@hotel.es", "ES");
    }

    /** Habitacion de ejemplo: la 101, DOBLE, planta 1, 80.00 la noche. */
    protected int insertarHabitacionDePrueba() {
        return nuevaHabitacion("101", "DOBLE", 1, "80.00");
    }

    /** Reserva CONFIRMADA de 3 noches de un huesped nuevo en una habitacion nueva (101, 80.00). */
    protected int insertarReservaDePrueba() {
        return nuevaReserva(insertarHuespedDePrueba(), insertarHabitacionDePrueba(), "2026-11-02", 3, "CONFIRMADA");
    }
}
