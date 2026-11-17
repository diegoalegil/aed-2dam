package es.ejemplo.autoescuela.orm.mapper;

import es.ejemplo.autoescuela.modelo.Vehiculo;
import es.ejemplo.autoescuela.orm.entity.VehiculoEntity;

public final class VehiculoMapper {
    private VehiculoMapper() {}
    public static Vehiculo desdeEntidad(VehiculoEntity source) {
        if (source == null) return null;
        Vehiculo result = new Vehiculo();
        result.setMatricula(source.getMatricula());
        result.setMarca(source.getMarca());
        result.setModelo(source.getModelo());
        result.setTipo(source.getTipo());
        result.setAnio(source.getAnio());
        return result;
    }
    public static VehiculoEntity aEntidad(Vehiculo source) {
        if (source == null) return null;
        VehiculoEntity result = new VehiculoEntity();
        result.setMatricula(source.getMatricula());
        result.setMarca(source.getMarca());
        result.setModelo(source.getModelo());
        result.setTipo(source.getTipo());
        result.setAnio(source.getAnio());
        return result;
    }
}
