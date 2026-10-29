package es.ejemplo.academia;

import es.ejemplo.academia.dao.*;
import es.ejemplo.academia.modelo.*;
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
 * Preparación común de las pruebas: crea un fichero SQLite temporal por test, ejecuta el esquema
 * de src/test/resources/schema.sql, crea los tres DAO y borra el fichero al terminar.
 * No añade casos de prueba y no toca las bases de datos de la carpeta datos/.
 */
abstract class BaseDaoTest {

    protected Path archivoBaseDatos;
    protected String url;
    protected IAlumnoDao alumnoDao;
    protected ICursoDao cursoDao;
    protected IMatriculaDao matriculaDao;

    @BeforeEach
    void prepararBaseDatosDePrueba() throws Exception {
        archivoBaseDatos = Files.createTempFile("academia-junit-", ".sqlite3");
        url = "jdbc:sqlite:" + archivoBaseDatos.toAbsolutePath();
        ejecutarSql(archivoBaseDatos, esquema());
        alumnoDao = new AlumnoDao(url);
        cursoDao = new CursoDao(url);
        matriculaDao = new MatriculaDao(url);
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

    /** Ejecuta sentencias SQL sobre un fichero SQLite sin pasar por los DAO. */
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

    /** Inserta un alumno y devuelve su id. */
    protected int nuevoAlumno(String nombre, String email, String telefono) {
        return alumnoDao.insertar(new Alumno(null, nombre, email, telefono));
    }

    /** Inserta un curso y devuelve su id. El precio va como texto para crear el BigDecimal exacto. */
    protected int nuevoCurso(String titulo, String nivel, int horas, String precio) {
        return cursoDao.insertar(new Curso(null, titulo, nivel, horas, new BigDecimal(precio)));
    }

    /** Inserta una matrícula y devuelve su id. */
    protected int nuevaMatricula(int idAlumno, int idCurso, String fecha, String importe) {
        return matriculaDao.insertar(new Matricula(null, idAlumno, idCurso, fecha, new BigDecimal(importe)));
    }

    /** Alumno de ejemplo: Ana Ruiz, ana@academia.es, 600111222. */
    protected int insertarAlumnoDePrueba() {
        return nuevoAlumno("Ana Ruiz", "ana@academia.es", "600111222");
    }

    /** Curso de ejemplo: Java desde cero, BASICO, 40 horas, 120.50. */
    protected int insertarCursoDePrueba() {
        return nuevoCurso("Java desde cero", "BASICO", 40, "120.50");
    }
}
