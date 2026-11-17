package es.ejemplo.banco;

import es.ejemplo.banco.orm.config.OrmFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas del nivel 0 (montaje): persistence.xml, OrmFactory y claves foráneas. */
class OrmFactoryTest extends BaseOrmTest {

    /** OrmFactory.crear devuelve una fábrica abierta. */
    @Test
    void crearDevuelveFabricaAbiertaTest() {
        assertNotNull(factory);
        assertTrue(factory.isOpen());
    }

    /** La fábrica abre un EntityManager utilizable. */
    @Test
    void fabricaAbreEntityManagerTest() {
        try (var em = factory.createEntityManager()) {
            assertTrue(em.isOpen());
        }
    }

    /** Una consulta nativa sobre sqlite_master encuentra las 6 tablas del esquema. */
    @Test
    void consultaNativaCuentaSeisTablasTest() {
        try (var em = factory.createEntityManager()) {
            Number tablas = (Number) em.createNativeQuery(
                            "select count(*) from sqlite_master where type = 'table' and name not like 'sqlite_%'")
                    .getSingleResult();
            assertEquals(6, tablas.intValue());
        }
    }

    /** Las conexiones de Hibernate tienen PRAGMA foreign_keys activado. */
    @Test
    void pragmaForeignKeysActivoTest() {
        try (var em = factory.createEntityManager()) {
            Number valor = (Number) em.createNativeQuery("PRAGMA foreign_keys").getSingleResult();
            assertEquals(1, valor.intValue());
        }
    }

    /** Insertar un empleado con una sucursal inexistente es rechazado por la clave foránea. */
    @Test
    void claveForaneaInexistenteRechazadaTest() throws Exception {
        try (var em = factory.createEntityManager()) {
            var tx = em.getTransaction();
            assertThrows(RuntimeException.class, () -> {
                tx.begin();
                em.createNativeQuery("insert into empleados (dni, nombre, puesto, salario, id_sucursal) "
                        + "values ('11111111H', 'Elena Navarro', 'CAJERO', 1650, 999)").executeUpdate();
                tx.commit();
            });
            if (tx.isActive()) {
                tx.rollback();
            }
        }
        assertEquals(0, contarFilas("empleados"));
    }

    /** La fábrica usa la URL recibida: otra base distinta no ve las filas de la primera. */
    @Test
    void crearUsaLaUrlRecibidaTest() throws Exception {
        try (Connection cn = abrir(url); Statement st = cn.createStatement()) {
            st.executeUpdate("insert into sucursales (codigo, nombre, ciudad) values ('SUC-001', 'Centro', 'Madrid')");
        }
        Path otro = Files.createTempFile("banco-junit-otra-", ".sqlite3");
        String otraUrl = "jdbc:sqlite:" + otro.toAbsolutePath();
        try {
            ejecutarSchema(otraUrl);
            try (var otraFactory = OrmFactory.crear(otraUrl); var em = otraFactory.createEntityManager()) {
                Number filas = (Number) em.createNativeQuery("select count(*) from sucursales").getSingleResult();
                assertEquals(0, filas.intValue());
            }
            try (var em = factory.createEntityManager()) {
                Number filas = (Number) em.createNativeQuery("select count(*) from sucursales").getSingleResult();
                assertEquals(1, filas.intValue());
            }
        } finally {
            Files.deleteIfExists(otro);
        }
    }

    /** La unidad de persistencia se llama bancoPU. */
    @Test
    void unidadBancoPUExisteTest() {
        try (var otraFactory = Persistence.createEntityManagerFactory("bancoPU",
                Map.of("jakarta.persistence.jdbc.url", url))) {
            assertTrue(otraFactory.isOpen());
        }
    }
}
