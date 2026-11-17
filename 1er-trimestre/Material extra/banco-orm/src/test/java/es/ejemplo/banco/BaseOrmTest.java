package es.ejemplo.banco;

import es.ejemplo.banco.orm.config.OrmFactory;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Preparación común: base SQLite temporal aislada por prueba, creada con schema.sql.
 * Nunca toca datos/banco.sqlite3. Los helpers JDBC permiten comprobar la tabla física.
 */
abstract class BaseOrmTest {

    // Silencia los mensajes INFO/WARN de Hibernate en la consola (cambia a Level.INFO para verlos)
    private static final Logger LOG_HIBERNATE = Logger.getLogger("org.hibernate");
    static {
        LOG_HIBERNATE.setLevel(Level.SEVERE);
    }

    protected Path archivo;
    protected String url;
    protected EntityManagerFactory factory;
    private int contador;

    @BeforeEach
    void prepararBaseDatosDePrueba() throws Exception {
        contador = 0;
        archivo = Files.createTempFile("banco-junit-", ".sqlite3");
        url = "jdbc:sqlite:" + archivo.toAbsolutePath();
        ejecutarSchema(url);
        factory = OrmFactory.crear(url);
    }

    @AfterEach
    void eliminarBaseDatosTemporal() throws Exception {
        // Primero se cierra la fábrica: su pool mantiene conexiones abiertas al fichero
        if (factory != null && factory.isOpen()) {
            factory.close();
        }
        Files.deleteIfExists(archivo);
    }

    /** Ejecuta /schema.sql del classpath sobre la base indicada. */
    protected void ejecutarSchema(String urlBase) throws Exception {
        try (Connection cn = abrir(urlBase); Statement st = cn.createStatement()) {
            String sql = new String(Objects.requireNonNull(
                    getClass().getResourceAsStream("/schema.sql"), "Falta schema.sql en el classpath")
                    .readAllBytes(), StandardCharsets.UTF_8);
            for (String s : sql.split(";")) {
                if (!s.isBlank()) {
                    st.execute(s);
                }
            }
        }
    }

    /** Conexión JDBC directa (sin Hibernate) con las claves foráneas activas. */
    protected Connection abrir(String urlBase) throws SQLException {
        Connection cn = DriverManager.getConnection(urlBase);
        try (Statement st = cn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        }
        return cn;
    }

    /** Cuenta las filas de una tabla física leyendo con JDBC directo. */
    protected long contarFilas(String tabla) throws SQLException {
        try (Connection cn = abrir(url); Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery("select count(*) from " + tabla)) {
            rs.next();
            return rs.getLong(1);
        }
    }

    /** Lee una columna de un cliente directamente de la tabla clientes (null si no hay fila). */
    protected String columnaCliente(String columna, int idCliente) throws SQLException {
        try (Connection cn = abrir(url);
             PreparedStatement ps = cn.prepareStatement(
                     "select " + columna + " from clientes where id_cliente = ?")) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }

    /** Inserta con JDBC una sucursal de prueba y devuelve su id. */
    protected int insertarSucursalDePrueba() throws SQLException {
        contador++;
        return insertar("insert into sucursales (codigo, nombre, ciudad) values ('SUC-T" + contador
                + "', 'Sucursal de prueba', 'Madrid')");
    }

    /** Inserta con JDBC un gestor (con su sucursal) y devuelve su id_empleado. */
    protected int insertarGestorDePrueba() throws SQLException {
        int idSucursal = insertarSucursalDePrueba();
        contador++;
        return insertar("insert into empleados (dni, nombre, puesto, salario, id_sucursal) values ('9000000"
                + contador + "X', 'Javier Moreno', 'GESTOR', 2100.00, " + idSucursal + ")");
    }

    private int insertar(String sql) throws SQLException {
        try (Connection cn = abrir(url); Statement st = cn.createStatement()) {
            st.executeUpdate(sql);
            try (ResultSet rs = st.executeQuery("select last_insert_rowid()")) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }
}
