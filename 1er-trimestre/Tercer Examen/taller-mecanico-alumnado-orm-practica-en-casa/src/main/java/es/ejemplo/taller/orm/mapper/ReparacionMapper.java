package es.ejemplo.taller.orm.mapper;

import es.ejemplo.taller.modelo.Reparacion;
import es.ejemplo.taller.orm.entity.ReparacionEntity;

public class ReparacionMapper {

    public static ReparacionEntity toEntity(Reparacion reparacion) {
        return new ReparacionEntity(reparacion.getIdReparacion(), reparacion.getMatricula(),
                reparacion.getIdMecanico(), reparacion.getDescripcion(), reparacion.getFecha(),
                reparacion.getHorasTrabajo(), reparacion.getEstado());
    }

    public static Reparacion toDomain(ReparacionEntity entidad) {
        return new Reparacion(entidad.getIdReparacion(), entidad.getMatricula(),
                entidad.getIdMecanico(), entidad.getDescripcion(), entidad.getFecha(),
                entidad.getHorasTrabajo(), entidad.getEstado());
    }
}
