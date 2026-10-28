package es.ejemplo.taller.orm.mapper;

import es.ejemplo.taller.modelo.Mecanico;
import es.ejemplo.taller.orm.entity.MecanicoEntity;

// Guía §11: traduce entre modelo.Mecanico y MecanicoEntity
public final class MecanicoMapper {

    private MecanicoMapper() {}

    public static MecanicoEntity toEntity(Mecanico mecanico) {
        // TODO: crear la entidad con los datos del modelo
        return null;
    }

    public static Mecanico toDomain(MecanicoEntity entidad) {
        // TODO: crear el modelo con los datos de la entidad
        return null;
    }
}
