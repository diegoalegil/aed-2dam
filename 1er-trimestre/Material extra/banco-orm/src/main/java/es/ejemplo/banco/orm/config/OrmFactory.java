package es.ejemplo.banco.orm.config;

import jakarta.persistence.EntityManagerFactory;

// README §3: fábrica del EntityManagerFactory de la unidad bancoPU
public final class OrmFactory {

    private OrmFactory() {}

    public static EntityManagerFactory crear(String jdbcUrl) {
        // TODO: crear la fábrica de la unidad con la URL recibida y las claves foráneas activas en cada conexión
        throw new UnsupportedOperationException("Sin implementar");
    }
}
