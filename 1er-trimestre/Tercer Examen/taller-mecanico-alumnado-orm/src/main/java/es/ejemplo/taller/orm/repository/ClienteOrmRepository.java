package es.ejemplo.taller.orm.repository;

import es.ejemplo.taller.modelo.Cliente;
import jakarta.persistence.EntityManagerFactory;

// Guía §15-§16: repositorio ORM de Cliente, con su CRUD heredado y sus consultas específicas
public final class ClienteOrmRepository extends AbstractOrmRepository<Cliente, es.ejemplo.taller.orm.entity.ClienteEntity, Integer> {

    public ClienteOrmRepository(EntityManagerFactory factory) {
        // TODO: pasar la clase de la entidad, las funciones del mapper, cómo sacar el id y cómo copiar el id generado
        super(factory, es.ejemplo.taller.orm.entity.ClienteEntity.class, null, null, null, null);
    }
}
