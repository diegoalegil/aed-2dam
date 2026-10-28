package es.ejemplo.taller.orm.mapper;

import es.ejemplo.taller.modelo.PiezaReparacion;
import es.ejemplo.taller.orm.entity.PiezaReparacionEntity;

// Guía §11: traduce entre modelo.PiezaReparacion y PiezaReparacionEntity
public final class PiezaReparacionMapper {

    private PiezaReparacionMapper() {}

    public static PiezaReparacionEntity toEntity(PiezaReparacion piezaReparacion) {
        // TODO: crear la entidad con los datos del modelo
        return null;
    }

    public static PiezaReparacion toDomain(PiezaReparacionEntity entidad) {
        // TODO: crear el modelo con los datos de la entidad
        return null;
    }
}
