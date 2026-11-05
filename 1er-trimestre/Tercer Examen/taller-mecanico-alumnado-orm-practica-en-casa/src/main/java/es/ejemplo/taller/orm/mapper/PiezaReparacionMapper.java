package es.ejemplo.taller.orm.mapper;

import es.ejemplo.taller.modelo.PiezaReparacion;
import es.ejemplo.taller.orm.entity.PiezaReparacionEntity;

public class PiezaReparacionMapper {

    public static PiezaReparacionEntity toEntity(PiezaReparacion pieza) {
        return new PiezaReparacionEntity(pieza.getIdPieza(), pieza.getIdReparacion(),
                pieza.getNombre(), pieza.getCantidad(), pieza.getPrecioUnitario());
    }

    public static PiezaReparacion toDomain(PiezaReparacionEntity entidad) {
        return new PiezaReparacion(entidad.getIdPieza(), entidad.getIdReparacion(),
                entidad.getNombre(), entidad.getCantidad(), entidad.getPrecioUnitario());
    }
}
