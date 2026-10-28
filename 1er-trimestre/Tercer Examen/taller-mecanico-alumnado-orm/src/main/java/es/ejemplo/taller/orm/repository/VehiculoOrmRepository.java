package es.ejemplo.taller.orm.repository;

import es.ejemplo.taller.modelo.Vehiculo;
import es.ejemplo.taller.orm.entity.VehiculoEntity;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

// Guía §15-§16: repositorio ORM de Vehiculo, con su CRUD heredado y sus consultas específicas
public final class VehiculoOrmRepository extends AbstractOrmRepository<Vehiculo, VehiculoEntity, String> {

    public VehiculoOrmRepository(EntityManagerFactory factory) {
        // TODO: pasar la clase de la entidad, las funciones del mapper, cómo sacar el id y cómo copiar el id generado
        super(factory, VehiculoEntity.class, null, null, null, null);
    }

    public List<Vehiculo> buscarPorCliente(Integer idCliente) {
        // TODO
        return List.of();
    }

    public List<Vehiculo> listarPorMarca(String marca) {
        // TODO
        return List.of();
    }
}
