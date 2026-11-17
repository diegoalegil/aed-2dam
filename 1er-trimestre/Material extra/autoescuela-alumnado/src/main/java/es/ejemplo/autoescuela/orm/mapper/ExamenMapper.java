package es.ejemplo.autoescuela.orm.mapper;

import es.ejemplo.autoescuela.modelo.Examen;
import es.ejemplo.autoescuela.orm.entity.ExamenEntity;

public final class ExamenMapper {
    private ExamenMapper() {}
    public static Examen desdeEntidad(ExamenEntity source) {
        if (source == null) return null;
        Examen result = new Examen();
        result.setIdExamen(source.getIdExamen());
        result.setIdAlumno(source.getIdAlumno());
        result.setTipo(source.getTipo());
        result.setFecha(source.getFecha());
        result.setResultado(source.getResultado());
        result.setErrores(source.getErrores());
        return result;
    }
    public static ExamenEntity aEntidad(Examen source) {
        if (source == null) return null;
        ExamenEntity result = new ExamenEntity();
        result.setIdExamen(source.getIdExamen());
        result.setIdAlumno(source.getIdAlumno());
        result.setTipo(source.getTipo());
        result.setFecha(source.getFecha());
        result.setResultado(source.getResultado());
        result.setErrores(source.getErrores());
        return result;
    }
}
