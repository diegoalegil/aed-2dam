package es.ejemplo.cinefest.orm.mapper;

// Imports: el modelo (lo que devuelve el DAO) y la entidad JPA (lo que lee y escribe Hibernate)
import es.ejemplo.cinefest.modelo.Valoracion;
import es.ejemplo.cinefest.orm.entity.ValoracionEntity;


/**
 * Mapper de Valoracion: convierte entre la entidad JPA ValoracionEntity (tabla valoraciones) y el modelo Valoracion.
 * Clase de utilidad: solo métodos estáticos y constructor privado, no se instancia.
 * El DAO trabaja con entidades dentro de JPA, pero devuelve y recibe modelos; el mapper hace el paso de uno a otro.
 * Equivale al código JDBC que copiaba cada columna del ResultSet a un objeto (rs.getInt, rs.getString...), pero entre dos objetos.
 */
public final class ValoracionMapper {
    // Constructor privado: nadie puede hacer new ValoracionMapper(); se usa siempre ValoracionMapper.desdeEntidad(...)
    private ValoracionMapper() {}

    /**
     * Lectura: convierte la entidad ValoracionEntity (lo que da JPA) en el modelo Valoracion (lo que devuelve el DAO).
     * Si la entidad es null (por ejemplo, em.find no encontró la fila), devuelve null.
     *
     * @param source entidad leída de la base de datos; puede ser null
     * @return modelo con los mismos datos, o null si source es null
     */
    public static Valoracion desdeEntidad(ValoracionEntity source) {
        // sin entidad no hay nada que copiar: se devuelve null para que Optional.ofNullable lo convierta en vacío
        if (source == null) return null;
        // objeto de destino, vacío: se rellena campo a campo con setters
        Valoracion result = new Valoracion();
        // copia de cada atributo del origen al destino (getX del origen, setX del destino)
        result.setIdValoracion(source.getIdValoracion());
        result.setIdPelicula(source.getIdPelicula());
        result.setIdEspectador(source.getIdEspectador());
        result.setPuntuacion(source.getPuntuacion());
        result.setComentario(source.getComentario());
        result.setFecha(source.getFecha());
        // devuelve el objeto ya relleno
        return result;
    }

    /**
     * Escritura: convierte el modelo Valoracion (lo que recibe el DAO) en la entidad ValoracionEntity que JPA guarda en la tabla valoraciones.
     * Si el modelo es null, devuelve null.
     *
     * @param source modelo con los datos que se quieren guardar; puede ser null
     * @return entidad con los mismos datos, o null si source es null
     */
    public static ValoracionEntity aEntidad(Valoracion source) {
        // sin modelo no hay nada que copiar
        if (source == null) return null;
        // objeto de destino, vacío: se rellena campo a campo con setters
        ValoracionEntity result = new ValoracionEntity();
        // copia de cada atributo del origen al destino (getX del origen, setX del destino)
        result.setIdValoracion(source.getIdValoracion());
        result.setIdPelicula(source.getIdPelicula());
        result.setIdEspectador(source.getIdEspectador());
        result.setPuntuacion(source.getPuntuacion());
        result.setComentario(source.getComentario());
        result.setFecha(source.getFecha());
        // devuelve el objeto ya relleno
        return result;
    }
}
