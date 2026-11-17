// Paquete de configuración del ORM: aquí se crea la fábrica de EntityManager
package es.ejemplo.cinefest.orm.config;

// Imports de JPA: EntityManagerFactory es la fábrica de conexiones y Persistence sirve para crearla
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

// Map para pasar la URL de la base de datos al crear la fábrica
import java.util.Map;

/**
 * Clase de utilidad que crea el EntityManagerFactory de JPA a partir de persistence.xml.
 * Es el equivalente a tener un método que hace DriverManager.getConnection(url) en JDBC.
 * No se puede instanciar ni heredar: solo tiene un método estático.
 */
public final class OrmFactory {
    /**
     * Constructor privado: impide hacer new OrmFactory(), porque solo se usa el método estático crear.
     */
    private OrmFactory() {
    }

    /**
     * Crea la fábrica de EntityManager de la unidad de persistencia cinefestPU.
     * Si hay un error (por ejemplo, jdbcUrl es null o persistence.xml no se encuentra), la excepción sale hacia quien llama.
     *
     * @param jdbcUrl URL JDBC de la base SQLite, por ejemplo "jdbc:sqlite:datos/cinefest.sqlite3"
     * @return fábrica lista para crear EntityManager con createEntityManager()
     */
    public static EntityManagerFactory crear(String jdbcUrl) {
        // "cinefestPU" es el nombre de la unidad en persistence.xml; el Map sustituye la URL de conexión
        // así la URL no está fija en el XML y los tests pueden usar otra base de datos
        return Persistence.createEntityManagerFactory("cinefestPU",
                Map.of("jakarta.persistence.jdbc.url", jdbcUrl));
    }
}
