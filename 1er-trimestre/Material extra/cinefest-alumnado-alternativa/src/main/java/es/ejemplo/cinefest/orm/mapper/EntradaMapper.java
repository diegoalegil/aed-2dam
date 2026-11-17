package es.ejemplo.cinefest.orm.mapper;

// Imports: el modelo (lo que devuelve el DAO) y la entidad JPA (lo que lee y escribe Hibernate)
import es.ejemplo.cinefest.modelo.Entrada;
import es.ejemplo.cinefest.orm.entity.EntradaEntity;


/**
 * Mapper de Entrada: convierte entre la entidad JPA EntradaEntity (tabla entradas) y el modelo Entrada.
 * Clase de utilidad: solo métodos estáticos y constructor privado, no se instancia.
 * El DAO trabaja con entidades dentro de JPA, pero devuelve y recibe modelos; el mapper hace el paso de uno a otro.
 * Equivale al código JDBC que copiaba cada columna del ResultSet a un objeto (rs.getInt, rs.getString...), pero entre dos objetos.
 */
public final class EntradaMapper {
    // Constructor privado: nadie puede hacer new EntradaMapper(); se usa siempre EntradaMapper.desdeEntidad(...)
    private EntradaMapper() {}

    /**
     * Lectura: convierte la entidad EntradaEntity (lo que da JPA) en el modelo Entrada (lo que devuelve el DAO).
     * Si la entidad es null (por ejemplo, em.find no encontró la fila), devuelve null.
     *
     * @param source entidad leída de la base de datos; puede ser null
     * @return modelo con los mismos datos, o null si source es null
     */
    public static Entrada desdeEntidad(EntradaEntity source) {
        // sin entidad no hay nada que copiar: se devuelve null para que Optional.ofNullable lo convierta en vacío
        if (source == null) return null;
        // objeto de destino, vacío: se rellena campo a campo con setters
        Entrada result = new Entrada();
        // copia de cada atributo del origen al destino (getX del origen, setX del destino)
        result.setIdEntrada(source.getIdEntrada());
        result.setIdSesion(source.getIdSesion());
        result.setIdEspectador(source.getIdEspectador());
        result.setFechaCompra(source.getFechaCompra());
        result.setEstado(source.getEstado());
        // devuelve el objeto ya relleno
        return result;
    }

    /**
     * Escritura: convierte el modelo Entrada (lo que recibe el DAO) en la entidad EntradaEntity que JPA guarda en la tabla entradas.
     * Si el modelo es null, devuelve null.
     *
     * @param source modelo con los datos que se quieren guardar; puede ser null
     * @return entidad con los mismos datos, o null si source es null
     */
    public static EntradaEntity aEntidad(Entrada source) {
        // sin modelo no hay nada que copiar
        if (source == null) return null;
        // objeto de destino, vacío: se rellena campo a campo con setters
        EntradaEntity result = new EntradaEntity();
        // copia de cada atributo del origen al destino (getX del origen, setX del destino)
        result.setIdEntrada(source.getIdEntrada());
        result.setIdSesion(source.getIdSesion());
        result.setIdEspectador(source.getIdEspectador());
        result.setFechaCompra(source.getFechaCompra());
        result.setEstado(source.getEstado());
        // devuelve el objeto ya relleno
        return result;
    }
}
