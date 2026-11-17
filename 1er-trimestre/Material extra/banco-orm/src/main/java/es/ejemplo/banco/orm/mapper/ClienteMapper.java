package es.ejemplo.banco.orm.mapper;

import es.ejemplo.banco.modelo.Cliente;
import es.ejemplo.banco.orm.entity.ClienteEntity;

// README §4: traduce entre modelo.Cliente y ClienteEntity
public final class ClienteMapper {

    private ClienteMapper() {}

    public static ClienteEntity toEntity(Cliente cliente) {
        // TODO: crear la entidad con los datos del dominio
        return null;
    }

    public static Cliente toDomain(ClienteEntity entidad) {
        // TODO: crear el dominio con los datos de la entidad
        return null;
    }
}
