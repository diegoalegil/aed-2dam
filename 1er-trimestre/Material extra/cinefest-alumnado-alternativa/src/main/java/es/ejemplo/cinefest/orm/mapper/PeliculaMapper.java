package es.ejemplo.cinefest.orm.mapper;

// Imports: el modelo (lo que devuelve el DAO) y la entidad JPA (lo que lee y escribe Hibernate)
import es.ejemplo.cinefest.modelo.Pelicula;
import es.ejemplo.cinefest.orm.entity.PeliculaEntity;


/**
 * Mapper de Pelicula: convierte entre la entidad JPA PeliculaEntity (tabla peliculas) y el modelo Pelicula.
 * Clase de utilidad: solo métodos estáticos y constructor privado, no se instancia.
 * El DAO trabaja con entidades dentro de JPA, pero devuelve y recibe modelos; el mapper hace el paso de uno a otro.
 * Equivale al código JDBC que copiaba cada columna del ResultSet a un objeto (rs.getInt, rs.getString...), pero entre dos objetos.
 */
public final class PeliculaMapper {
    // Constructor privado: nadie puede hacer new PeliculaMapper(); se usa siempre PeliculaMapper.desdeEntidad(...)
    private PeliculaMapper() {}

    /**
     * Lectura: convierte la entidad PeliculaEntity (lo que da JPA) en el modelo Pelicula (lo que devuelve el DAO).
     * Si la entidad es null (por ejemplo, em.find no encontró la fila), devuelve null.
     *
     * @param source entidad leída de la base de datos; puede ser null
     * @return modelo con los mismos datos, o null si source es null
     */
    public static Pelicula desdeEntidad(PeliculaEntity source) {
        // sin entidad no hay nada que copiar: se devuelve null para que Optional.ofNullable lo convierta en vacío
        if (source == null) return null;
        // objeto de destino, vacío: se rellena campo a campo con setters
        Pelicula result = new Pelicula();
        // copia de cada atributo del origen al destino (getX del origen, setX del destino)
        result.setIdPelicula(source.getIdPelicula());
        result.setTitulo(source.getTitulo());
        result.setGenero(source.getGenero());
        result.setDuracionMin(source.getDuracionMin());
        result.setPais(source.getPais());
        result.setAnio(source.getAnio());
        // devuelve el objeto ya relleno
        return result;
    }

    /**
     * Escritura: convierte el modelo Pelicula (lo que recibe el DAO) en la entidad PeliculaEntity que JPA guarda en la tabla peliculas.
     * Si el modelo es null, devuelve null.
     *
     * @param source modelo con los datos que se quieren guardar; puede ser null
     * @return entidad con los mismos datos, o null si source es null
     */
    public static PeliculaEntity aEntidad(Pelicula source) {
        // sin modelo no hay nada que copiar
        if (source == null) return null;
        // objeto de destino, vacío: se rellena campo a campo con setters
        PeliculaEntity result = new PeliculaEntity();
        // copia de cada atributo del origen al destino (getX del origen, setX del destino)
        result.setIdPelicula(source.getIdPelicula());
        result.setTitulo(source.getTitulo());
        result.setGenero(source.getGenero());
        result.setDuracionMin(source.getDuracionMin());
        result.setPais(source.getPais());
        result.setAnio(source.getAnio());
        // devuelve el objeto ya relleno
        return result;
    }
}
