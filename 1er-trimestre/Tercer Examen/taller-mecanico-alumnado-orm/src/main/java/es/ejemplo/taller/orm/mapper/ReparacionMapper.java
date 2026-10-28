package es.ejemplo.taller.orm.mapper;

import es.ejemplo.taller.modelo.Reparacion;
import es.ejemplo.taller.orm.entity.ReparacionEntity;

// Guía §11: traduce entre modelo.Reparacion y ReparacionEntity
public final class ReparacionMapper {

    private ReparacionMapper() {}

    public static ReparacionEntity toEntity(Reparacion reparacion) {
        // TODO: crear la entidad con los datos del modelo
        return null;
    }

    public static Reparacion toDomain(ReparacionEntity entidad) {
        // TODO: crear el modelo con los datos de la entidad
        return null;
    }
}
