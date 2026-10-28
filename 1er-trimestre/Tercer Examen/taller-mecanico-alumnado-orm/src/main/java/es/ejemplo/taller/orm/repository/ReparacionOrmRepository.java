package es.ejemplo.taller.orm.repository;

import es.ejemplo.taller.modelo.Reparacion;
import es.ejemplo.taller.orm.entity.ReparacionEntity;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

// Guía §15-§16: repositorio ORM de Reparacion, con su CRUD heredado y sus consultas específicas
public final class ReparacionOrmRepository extends AbstractOrmRepository<Reparacion, ReparacionEntity, Integer> {

    public ReparacionOrmRepository(EntityManagerFactory factory) {
        // TODO: pasar la clase de la entidad, las funciones del mapper, cómo sacar el id y cómo copiar el id generado
        super(factory, ReparacionEntity.class, null, null, null, null);
    }

    public List<Reparacion> buscarPorVehiculo(String matricula) {
        // TODO
        return List.of();
    }

    public List<Reparacion> buscarPorMecanico(Integer idMecanico) {
        // TODO
        return List.of();
    }

    public List<Reparacion> listarPendientes() {
        // TODO
        return List.of();
    }
}
