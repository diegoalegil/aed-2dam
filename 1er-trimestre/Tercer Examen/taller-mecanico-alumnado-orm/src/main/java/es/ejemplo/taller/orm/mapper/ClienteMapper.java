package es.ejemplo.taller.orm.mapper;

import es.ejemplo.taller.modelo.Cliente;

// Guía §11: traduce entre modelo.Cliente y ClienteEntity
public final class ClienteMapper {

    private ClienteMapper() {}

    public static es.ejemplo.taller.orm.entity.ClienteEntity toEntity(Cliente cliente) {
        // TODO: crear la entidad con los datos del modelo
        return null;
    }

    public static Cliente toDomain(es.ejemplo.taller.orm.entity.ClienteEntity entidad) {
        // TODO: crear el modelo con los datos de la entidad
        return null;
    }
}
