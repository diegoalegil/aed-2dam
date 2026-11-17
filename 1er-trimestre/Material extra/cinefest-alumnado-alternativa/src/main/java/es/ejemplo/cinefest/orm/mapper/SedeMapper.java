package es.ejemplo.cinefest.orm.mapper;

// Imports: el modelo (lo que devuelve el DAO) y la entidad JPA (lo que lee y escribe Hibernate)
import es.ejemplo.cinefest.modelo.Sede;
import es.ejemplo.cinefest.orm.entity.SedeEntity;


/**
 * Mapper de Sede: convierte entre la entidad JPA SedeEntity (tabla sedes) y el modelo Sede.
 * Clase de utilidad: solo métodos estáticos y constructor privado, no se instancia.
 * El DAO trabaja con entidades dentro de JPA, pero devuelve y recibe modelos; el mapper hace el paso de uno a otro.
 * Equivale al código JDBC que copiaba cada columna del ResultSet a un objeto (rs.getInt, rs.getString...), pero entre dos objetos.
 */
public final class SedeMapper {
    // Constructor privado: nadie puede hacer new SedeMapper(); se usa siempre SedeMapper.desdeEntidad(...)
    private SedeMapper() {}

    /**
     * Lectura: convierte la entidad SedeEntity (lo que da JPA) en el modelo Sede (lo que devuelve el DAO).
     * Si la entidad es null (por ejemplo, em.find no encontró la fila), devuelve null.
     *
     * @param source entidad leída de la base de datos; puede ser null
     * @return modelo con los mismos datos, o null si source es null
     */
    public static Sede desdeEntidad(SedeEntity source) {
        // sin entidad no hay nada que copiar: se devuelve null para que Optional.ofNullable lo convierta en vacío
        if (source == null) return null;
        // objeto de destino, vacío: se rellena campo a campo con setters
        Sede result = new Sede();
        // copia de cada atributo del origen al destino (getX del origen, setX del destino)
        result.setIdSede(source.getIdSede());
        result.setNombre(source.getNombre());
        result.setMunicipio(source.getMunicipio());
        // devuelve el objeto ya relleno
        return result;
    }

    /**
     * Escritura: convierte el modelo Sede (lo que recibe el DAO) en la entidad SedeEntity que JPA guarda en la tabla sedes.
     * Si el modelo es null, devuelve null.
     *
     * @param source modelo con los datos que se quieren guardar; puede ser null
     * @return entidad con los mismos datos, o null si source es null
     */
    public static SedeEntity aEntidad(Sede source) {
        // sin modelo no hay nada que copiar
        if (source == null) return null;
        // objeto de destino, vacío: se rellena campo a campo con setters
        SedeEntity result = new SedeEntity();
        // copia de cada atributo del origen al destino (getX del origen, setX del destino)
        result.setIdSede(source.getIdSede());
        result.setNombre(source.getNombre());
        result.setMunicipio(source.getMunicipio());
        // devuelve el objeto ya relleno
        return result;
    }
}
