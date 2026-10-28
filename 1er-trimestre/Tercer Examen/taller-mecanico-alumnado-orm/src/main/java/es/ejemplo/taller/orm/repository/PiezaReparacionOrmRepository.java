package es.ejemplo.taller.orm.repository;

import es.ejemplo.taller.modelo.PiezaReparacion;
import es.ejemplo.taller.orm.entity.PiezaReparacionEntity;
import jakarta.persistence.EntityManagerFactory;

import java.math.BigDecimal;
import java.util.List;

// Guía §15-§16: repositorio ORM de PiezaReparacion, con su CRUD heredado y sus consultas específicas
public final class PiezaReparacionOrmRepository extends AbstractOrmRepository<PiezaReparacion, PiezaReparacionEntity, Integer> {

    public PiezaReparacionOrmRepository(EntityManagerFactory factory) {
        // TODO: pasar la clase de la entidad, las funciones del mapper, cómo sacar el id y cómo copiar el id generado
        super(factory, PiezaReparacionEntity.class, null, null, null, null);
    }

    public List<PiezaReparacion> listarPorReparacion(Integer idReparacion) {
        // TODO
        return List.of();
    }

    public BigDecimal calcularCostePiezas(Integer idReparacion) {
        // TODO
        return BigDecimal.ZERO;
    }
}
