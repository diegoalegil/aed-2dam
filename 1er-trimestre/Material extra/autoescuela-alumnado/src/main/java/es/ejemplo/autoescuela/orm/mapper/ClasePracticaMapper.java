package es.ejemplo.autoescuela.orm.mapper;

import es.ejemplo.autoescuela.modelo.ClasePractica;
import es.ejemplo.autoescuela.orm.entity.ClasePracticaEntity;

public final class ClasePracticaMapper {
    private ClasePracticaMapper() {}
    public static ClasePractica desdeEntidad(ClasePracticaEntity source) {
        if (source == null) return null;
        ClasePractica result = new ClasePractica();
        result.setIdClase(source.getIdClase());
        result.setIdAlumno(source.getIdAlumno());
        result.setIdProfesor(source.getIdProfesor());
        result.setMatricula(source.getMatricula());
        result.setFecha(source.getFecha());
        result.setHora(source.getHora());
        result.setDuracionMin(source.getDuracionMin());
        result.setPrecio(source.getPrecio());
        result.setEstado(source.getEstado());
        return result;
    }
    public static ClasePracticaEntity aEntidad(ClasePractica source) {
        if (source == null) return null;
        ClasePracticaEntity result = new ClasePracticaEntity();
        result.setIdClase(source.getIdClase());
        result.setIdAlumno(source.getIdAlumno());
        result.setIdProfesor(source.getIdProfesor());
        result.setMatricula(source.getMatricula());
        result.setFecha(source.getFecha());
        result.setHora(source.getHora());
        result.setDuracionMin(source.getDuracionMin());
        result.setPrecio(source.getPrecio());
        result.setEstado(source.getEstado());
        return result;
    }
}
