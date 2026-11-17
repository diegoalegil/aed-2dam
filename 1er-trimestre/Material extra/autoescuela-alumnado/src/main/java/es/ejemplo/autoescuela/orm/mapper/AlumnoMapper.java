package es.ejemplo.autoescuela.orm.mapper;

import es.ejemplo.autoescuela.modelo.Alumno;
import es.ejemplo.autoescuela.orm.entity.AlumnoEntity;

public final class AlumnoMapper {
    private AlumnoMapper() {}
    public static Alumno desdeEntidad(AlumnoEntity source) {
        if (source == null) return null;
        Alumno result = new Alumno();
        result.setIdAlumno(source.getIdAlumno());
        result.setNombre(source.getNombre());
        result.setEmail(source.getEmail());
        result.setFechaAlta(source.getFechaAlta());
        result.setIdProfesor(source.getIdProfesor());
        result.setActivo(source.getActivo());
        return result;
    }
    public static AlumnoEntity aEntidad(Alumno source) {
        if (source == null) return null;
        AlumnoEntity result = new AlumnoEntity();
        result.setIdAlumno(source.getIdAlumno());
        result.setNombre(source.getNombre());
        result.setEmail(source.getEmail());
        result.setFechaAlta(source.getFechaAlta());
        result.setIdProfesor(source.getIdProfesor());
        result.setActivo(source.getActivo());
        return result;
    }
}
