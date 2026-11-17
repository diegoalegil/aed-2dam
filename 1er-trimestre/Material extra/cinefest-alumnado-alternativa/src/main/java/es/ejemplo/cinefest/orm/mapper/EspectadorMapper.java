package es.ejemplo.cinefest.orm.mapper;

// Imports: el modelo (lo que devuelve el DAO) y la entidad JPA (lo que lee y escribe Hibernate)
import es.ejemplo.cinefest.modelo.Espectador;
import es.ejemplo.cinefest.orm.entity.EspectadorEntity;


/**
 * Mapper de Espectador: convierte entre la entidad JPA EspectadorEntity (tabla espectadores) y el modelo Espectador.
 * Clase de utilidad: solo métodos estáticos y constructor privado, no se instancia.
 * El DAO trabaja con entidades dentro de JPA, pero devuelve y recibe modelos; el mapper hace el paso de uno a otro.
 * Equivale al código JDBC que copiaba cada columna del ResultSet a un objeto (rs.getInt, rs.getString...), pero entre dos objetos.
 */
public final class EspectadorMapper {
    // Constructor privado: nadie puede hacer new EspectadorMapper(); se usa siempre EspectadorMapper.desdeEntidad(...)
    private EspectadorMapper() {}

    /**
     * Lectura: convierte la entidad EspectadorEntity (lo que da JPA) en el modelo Espectador (lo que devuelve el DAO).
     * Si la entidad es null (por ejemplo, em.find no encontró la fila), devuelve null.
     *
     * @param source entidad leída de la base de datos; puede ser null
     * @return modelo con los mismos datos, o null si source es null
     */
    public static Espectador desdeEntidad(EspectadorEntity source) {
        // sin entidad no hay nada que copiar: se devuelve null para que Optional.ofNullable lo convierta en vacío
        if (source == null) return null;
        // objeto de destino, vacío: se rellena campo a campo con setters
        Espectador result = new Espectador();
        // copia de cada atributo del origen al destino (getX del origen, setX del destino)
        result.setIdEspectador(source.getIdEspectador());
        result.setNombre(source.getNombre());
        result.setEmail(source.getEmail());
        result.setFechaAlta(source.getFechaAlta());
        result.setActivo(source.getActivo());
        // devuelve el objeto ya relleno
        return result;
    }

    /**
     * Escritura: convierte el modelo Espectador (lo que recibe el DAO) en la entidad EspectadorEntity que JPA guarda en la tabla espectadores.
     * Si el modelo es null, devuelve null.
     *
     * @param source modelo con los datos que se quieren guardar; puede ser null
     * @return entidad con los mismos datos, o null si source es null
     */
    public static EspectadorEntity aEntidad(Espectador source) {
        // sin modelo no hay nada que copiar
        if (source == null) return null;
        // objeto de destino, vacío: se rellena campo a campo con setters
        EspectadorEntity result = new EspectadorEntity();
        // copia de cada atributo del origen al destino (getX del origen, setX del destino)
        result.setIdEspectador(source.getIdEspectador());
        result.setNombre(source.getNombre());
        result.setEmail(source.getEmail());
        result.setFechaAlta(source.getFechaAlta());
        result.setActivo(source.getActivo());
        // devuelve el objeto ya relleno
        return result;
    }
}
