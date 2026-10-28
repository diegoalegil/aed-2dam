package es.ejemplo.taller.orm.repository;

import es.ejemplo.taller.modelo.Mecanico;
import es.ejemplo.taller.orm.entity.MecanicoEntity;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

// Guía §15-§16: repositorio ORM de Mecanico, con su CRUD heredado y sus consultas específicas
public final class MecanicoOrmRepository extends AbstractOrmRepository<Mecanico, MecanicoEntity, Integer> {

    public MecanicoOrmRepository(EntityManagerFactory factory) {
        // TODO: pasar la clase de la entidad, las funciones del mapper, cómo sacar el id y cómo copiar el id generado
        super(factory, MecanicoEntity.class, null, null, null, null);
    }

    public List<Mecanico> buscarPorEspecialidad(String especialidad) {
        // TODO
        return List.of();
    }
}
