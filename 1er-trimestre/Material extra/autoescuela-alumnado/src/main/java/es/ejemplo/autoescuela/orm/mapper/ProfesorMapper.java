package es.ejemplo.autoescuela.orm.mapper;

import es.ejemplo.autoescuela.modelo.Profesor;
import es.ejemplo.autoescuela.orm.entity.ProfesorEntity;

public final class ProfesorMapper {
    private ProfesorMapper() {}
    public static Profesor desdeEntidad(ProfesorEntity source) {
        if (source == null) return null;
        Profesor result = new Profesor();
        result.setIdProfesor(source.getIdProfesor());
        result.setNombre(source.getNombre());
        result.setDni(source.getDni());
        result.setTelefono(source.getTelefono());
        result.setActivo(source.getActivo());
        return result;
    }
    public static ProfesorEntity aEntidad(Profesor source) {
        if (source == null) return null;
        ProfesorEntity result = new ProfesorEntity();
        result.setIdProfesor(source.getIdProfesor());
        result.setNombre(source.getNombre());
        result.setDni(source.getDni());
        result.setTelefono(source.getTelefono());
        result.setActivo(source.getActivo());
        return result;
    }
}
