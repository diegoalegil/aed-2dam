package es.ejemplo.cinefest.orm.mapper;

// Imports: el modelo (lo que devuelve el DAO) y la entidad JPA (lo que lee y escribe Hibernate)
import es.ejemplo.cinefest.modelo.Sesion;
import es.ejemplo.cinefest.orm.entity.SesionEntity;


/**
 * Mapper de Sesion: convierte entre la entidad JPA SesionEntity (tabla sesiones) y el modelo Sesion.
 * Clase de utilidad: solo métodos estáticos y constructor privado, no se instancia.
 * El DAO trabaja con entidades dentro de JPA, pero devuelve y recibe modelos; el mapper hace el paso de uno a otro.
 * Equivale al código JDBC que copiaba cada columna del ResultSet a un objeto (rs.getInt, rs.getString...), pero entre dos objetos.
 */
public final class SesionMapper {
    // Constructor privado: nadie puede hacer new SesionMapper(); se usa siempre SesionMapper.desdeEntidad(...)
    private SesionMapper() {}

    /**
     * Lectura: convierte la entidad SesionEntity (lo que da JPA) en el modelo Sesion (lo que devuelve el DAO).
     * Si la entidad es null (por ejemplo, em.find no encontró la fila), devuelve null.
     *
     * @param source entidad leída de la base de datos; puede ser null
     * @return modelo con los mismos datos, o null si source es null
     */
    public static Sesion desdeEntidad(SesionEntity source) {
        // sin entidad no hay nada que copiar: se devuelve null para que Optional.ofNullable lo convierta en vacío
        if (source == null) return null;
        // objeto de destino, vacío: se rellena campo a campo con setters
        Sesion result = new Sesion();
        // copia de cada atributo del origen al destino (getX del origen, setX del destino)
        result.setIdSesion(source.getIdSesion());
        result.setIdPelicula(source.getIdPelicula());
        result.setIdSala(source.getIdSala());
        result.setFecha(source.getFecha());
        result.setHora(source.getHora());
        result.setPrecio(source.getPrecio());
        result.setEstado(source.getEstado());
        // devuelve el objeto ya relleno
        return result;
    }

    /**
     * Escritura: convierte el modelo Sesion (lo que recibe el DAO) en la entidad SesionEntity que JPA guarda en la tabla sesiones.
     * Si el modelo es null, devuelve null.
     *
     * @param source modelo con los datos que se quieren guardar; puede ser null
     * @return entidad con los mismos datos, o null si source es null
     */
    public static SesionEntity aEntidad(Sesion source) {
        // sin modelo no hay nada que copiar
        if (source == null) return null;
        // objeto de destino, vacío: se rellena campo a campo con setters
        SesionEntity result = new SesionEntity();
        // copia de cada atributo del origen al destino (getX del origen, setX del destino)
        result.setIdSesion(source.getIdSesion());
        result.setIdPelicula(source.getIdPelicula());
        result.setIdSala(source.getIdSala());
        result.setFecha(source.getFecha());
        result.setHora(source.getHora());
        result.setPrecio(source.getPrecio());
        result.setEstado(source.getEstado());
        // devuelve el objeto ya relleno
        return result;
    }
}
