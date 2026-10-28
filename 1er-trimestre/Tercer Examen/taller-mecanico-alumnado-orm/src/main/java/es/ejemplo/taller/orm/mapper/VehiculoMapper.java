package es.ejemplo.taller.orm.mapper;

import es.ejemplo.taller.modelo.Vehiculo;
import es.ejemplo.taller.orm.entity.VehiculoEntity;

// Guía §11: traduce entre modelo.Vehiculo y VehiculoEntity
public final class VehiculoMapper {

    private VehiculoMapper() {}

    public static VehiculoEntity toEntity(Vehiculo vehiculo) {
        // TODO: crear la entidad con los datos del modelo
        return null;
    }

    public static Vehiculo toDomain(VehiculoEntity entidad) {
        // TODO: crear el modelo con los datos de la entidad
        return null;
    }
}
