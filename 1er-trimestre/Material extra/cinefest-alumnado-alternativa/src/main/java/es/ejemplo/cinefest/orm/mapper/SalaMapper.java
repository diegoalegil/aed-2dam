package es.ejemplo.cinefest.orm.mapper;

// Imports: el modelo (lo que devuelve el DAO) y la entidad JPA (lo que lee y escribe Hibernate)
import es.ejemplo.cinefest.modelo.Sala;
import es.ejemplo.cinefest.orm.entity.SalaEntity;


/**
 * Mapper de Sala: convierte entre la entidad JPA SalaEntity (tabla salas) y el modelo Sala.
 * Clase de utilidad: solo métodos estáticos y constructor privado, no se instancia.
 * El DAO trabaja con entidades dentro de JPA, pero devuelve y recibe modelos; el mapper hace el paso de uno a otro.
 * Equivale al código JDBC que copiaba cada columna del ResultSet a un objeto (rs.getInt, rs.getString...), pero entre dos objetos.
 */
public final class SalaMapper {
    // Constructor privado: nadie puede hacer new SalaMapper(); se usa siempre SalaMapper.desdeEntidad(...)
    private SalaMapper() {}

    /**
     * Lectura: convierte la entidad SalaEntity (lo que da JPA) en el modelo Sala (lo que devuelve el DAO).
     * Si la entidad es null (por ejemplo, em.find no encontró la fila), devuelve null.
     *
     * @param source entidad leída de la base de datos; puede ser null
     * @return modelo con los mismos datos, o null si source es null
     */
    public static Sala desdeEntidad(SalaEntity source) {
        // sin entidad no hay nada que copiar: se devuelve null para que Optional.ofNullable lo convierta en vacío
        if (source == null) return null;
        // objeto de destino, vacío: se rellena campo a campo con setters
        Sala result = new Sala();
        // copia de cada atributo del origen al destino (getX del origen, setX del destino)
        result.setIdSala(source.getIdSala());
        result.setNombre(source.getNombre());
        result.setAforo(source.getAforo());
        result.setIdSede(source.getIdSede());
        // devuelve el objeto ya relleno
        return result;
    }

    /**
     * Escritura: convierte el modelo Sala (lo que recibe el DAO) en la entidad SalaEntity que JPA guarda en la tabla salas.
     * Si el modelo es null, devuelve null.
     *
     * @param source modelo con los datos que se quieren guardar; puede ser null
     * @return entidad con los mismos datos, o null si source es null
     */
    public static SalaEntity aEntidad(Sala source) {
        // sin modelo no hay nada que copiar
        if (source == null) return null;
        // objeto de destino, vacío: se rellena campo a campo con setters
        SalaEntity result = new SalaEntity();
        // copia de cada atributo del origen al destino (getX del origen, setX del destino)
        result.setIdSala(source.getIdSala());
        result.setNombre(source.getNombre());
        result.setAforo(source.getAforo());
        result.setIdSede(source.getIdSede());
        // devuelve el objeto ya relleno
        return result;
    }
}
