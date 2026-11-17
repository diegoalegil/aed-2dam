// Paquete dao: aquí están las clases que leen y escriben en la base de datos
package es.ejemplo.cinefest.dao;

// Imports del proyecto y de JPA: el modelo Valoracion, OrmFactory, la entidad, el mapper y EntityManagerFactory
import es.ejemplo.cinefest.modelo.Valoracion;
import es.ejemplo.cinefest.orm.config.OrmFactory;
import es.ejemplo.cinefest.orm.entity.ValoracionEntity;
import es.ejemplo.cinefest.orm.mapper.ValoracionMapper;
import jakarta.persistence.EntityManagerFactory;

// Imports de Java: List y Optional para lo que devuelven los métodos y Logger para registrar los errores
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * DAO de la tabla valoraciones: CRUD de valoraciones y consultas que las unen con películas y espectadores con JPA (Hibernate).
 * Cada operación abre su EntityManager, hace su trabajo y lo cierra al terminar.
 * Si hay un error, lo registra en el log y devuelve 0, Optional.empty() o List.of(), sin lanzarlo.
 */
public class ValoracionDao {

    // Fábrica de EntityManager: se crea una vez en el constructor y la usan los métodos del DAO
    private final EntityManagerFactory emf;

    /**
     * Crea el DAO y su EntityManagerFactory para la base de datos de esa URL.
     * Si hay un error al crear la fábrica (por ejemplo, jdbcUrl es null), la excepción sale del constructor.
     *
     * @param jdbcUrl URL JDBC de la base SQLite, por ejemplo "jdbc:sqlite:datos/cinefest.sqlite3"
     */
    public ValoracionDao(String jdbcUrl) {
        // OrmFactory lee persistence.xml (unidad cinefestPU) y le pone esta URL de conexión
        this.emf = OrmFactory.crear(jdbcUrl);
    }

    // Logger para registrar los errores: el README pide registrarlos dentro del DAO, sin lanzarlos
    private static final Logger LOG = Logger.getLogger(ValoracionDao.class.getName());

    // Usar transacciones y rollback en operaciones de escritura.
    /**
     * Devuelve las valoraciones de la tabla valoraciones ordenadas por id.
     * Si hay un error, lo registra en el log y devuelve List.of().
     *
     * @return lista de valoraciones; List.of() (lista vacía) si no hay ninguna
     */
    public List<Valoracion> listar() {
        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // JPQL: se escribe con la clase ValoracionEntity y su atributo idValoracion, no con la tabla ni la columna
            // ValoracionEntity.class: cada fila llega como una ValoracionEntity; es solo lectura, no hace falta transacción
            return em.createQuery("select v from ValoracionEntity v order by v.idValoracion", ValoracionEntity.class)
                    // recorre las filas una a una (como el while (rs.next()) de JDBC)
                    .getResultStream()
                    // desdeEntidad: pasa cada ValoracionEntity (entidad) a Valoracion (modelo), que es lo que devuelve el DAO
                    .map(ValoracionMapper::desdeEntidad)
                    // junta las valoraciones en una lista
                    .toList();
        } catch (RuntimeException ex) {
            // JPA lanza RuntimeException (no SQLException como JDBC): se registra y se devuelve la lista vacía
            LOG.warning("listar: " + ex);
            return List.of();
        }
    }

    /**
     * Busca una valoración por su id (clave primaria).
     * Si hay un error, lo registra en el log y devuelve Optional.empty().
     *
     * @param id id de la valoración; si es 0 o negativo no se busca
     * @return Optional con la valoración si existe; Optional.empty() si no existe o el id no es válido
     */
    public Optional<Valoracion> buscarPorId(int id) {

        // un id 0 o negativo no existe: se devuelve vacío sin ir a la base de datos
        if (id <= 0) return Optional.empty();

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // em.find busca por clave primaria (el SELECT ... WHERE id_valoracion = ? de JDBC) y da null si no está
            // Optional.ofNullable: si find da null, queda Optional.empty()
            return Optional.ofNullable(em.find(ValoracionEntity.class, id))
                    // si la encontró, desdeEntidad la pasa de ValoracionEntity (entidad) a Valoracion (modelo)
                    .map(ValoracionMapper::desdeEntidad);
        } catch (RuntimeException ex) {
            // error de base de datos: se registra y se devuelve vacío
            LOG.warning("buscarPorId: " + ex);
            return Optional.empty();
        }
    }

    /**
     * Inserta una valoración nueva y devuelve el id que le da la base de datos.
     * Si hay un error (por ejemplo, la película o el espectador no existen), hace rollback, lo registra y devuelve 0.
     *
     * @param dato valoración que se inserta; fecha no vacía, idPelicula, idEspectador y puntuación entre 1 y 5 obligatorios
     * @return el id generado (mayor que 0) si se inserta; 0 si el dato no es válido o hay un error
     */
    public int insertar(Valoracion dato) {

        // validación: sin valoración, sin fecha, sin película ni espectador, o con puntuación fuera de 1 a 5, devuelve 0
        // (el comentario puede ser null: no se valida)
        if (dato == null || dato.getFecha() == null || dato.getFecha().isBlank()
                || dato.getIdPelicula() == null || dato.getIdEspectador() == null
                || dato.getPuntuacion() == null || dato.getPuntuacion() < 1 || dato.getPuntuacion() > 5) return 0;
        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // es una escritura: hace falta transacción (en JDBC era setAutoCommit(false) y luego commit o rollback)
            var tx = em.getTransaction();
            try {
                // empieza la transacción: hasta el commit no se guarda nada
                tx.begin();
                // aEntidad: pasa la Valoracion (modelo) a ValoracionEntity (entidad), que es lo que guarda JPA
                ValoracionEntity e = ValoracionMapper.aEntidad(dato);
                // id a null para que lo genere la base de datos (AUTOINCREMENT); con un id puesto, persist da error
                e.setIdValoracion(null);
                // persist hace el INSERT (como el ps.executeUpdate() de JDBC)
                // si la película o el espectador no existen, la clave foránea lo rechaza y salta al catch
                em.persist(e);
                // commit: confirma el INSERT en la base de datos (como con.commit() en JDBC)
                tx.commit();
                // después del persist la entidad ya tiene el id generado: se devuelve (mayor que 0)
                return e.getIdValoracion();
            } catch (RuntimeException ex) {
                // deshace los cambios; solo si la transacción sigue activa (si falló el commit puede estar cerrada)
                if (tx.isActive()) tx.rollback();
                // registra el error con el nombre del método, para saber dónde falló
                LOG.warning("insertar: " + ex);
                // 0 = no se ha insertado
                return 0;
            }
        }
    }

    /**
     * Actualiza la película, el espectador, la puntuación, el comentario y la fecha de una valoración que ya existe.
     * Si hay un error (por ejemplo, la película nueva no existe), hace rollback, lo registra y devuelve 0.
     *
     * @param dato valoración con el id y los datos nuevos; todo es obligatorio salvo el comentario
     * @return 1 si se actualiza; 0 si el dato no es válido, la valoración no existe o hay un error
     */
    public int actualizar(Valoracion dato) {

        // validación: hace falta el id (para saber cuál se cambia), fecha, película, espectador y puntuación entre 1 y 5
        if (dato == null || dato.getIdValoracion() == null
                || dato.getFecha() == null || dato.getFecha().isBlank()
                || dato.getIdPelicula() == null || dato.getIdEspectador() == null
                || dato.getPuntuacion() == null || dato.getPuntuacion() < 1 || dato.getPuntuacion() > 5)
            return 0;

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // es una escritura: hace falta transacción (en JDBC era setAutoCommit(false) y luego commit o rollback)
            var tx = em.getTransaction();
            try {
                // empieza la transacción: hasta el commit no se guarda nada
                tx.begin();
                // busca la valoración guardada por su id; da null si no existe
                ValoracionEntity e = em.find(ValoracionEntity.class, dato.getIdValoracion());
                // si no existe, no hay nada que actualizar
                if (e == null) {
                    // rollback antes del return 0: si no, la transacción queda abierta y SQLite bloquea la base
                    // (SQLITE_BUSY), así que las siguientes escrituras devolverían 0
                    tx.rollback();
                    return 0;
                }
                // se cambian los campos de la entidad con los datos nuevos (el id no se toca)
                e.setIdPelicula(dato.getIdPelicula());
                e.setIdEspectador(dato.getIdEspectador());
                e.setPuntuacion(dato.getPuntuacion());
                e.setComentario(dato.getComentario());
                e.setFecha(dato.getFecha());
                // commit: Hibernate ve los cambios y hace el UPDATE solo (en JDBC: UPDATE ... SET y ps.setString)
                tx.commit();
                // 1 = se ha actualizado
                return 1;
            } catch (RuntimeException ex) {
                // deshace los cambios; solo si la transacción sigue activa (si falló el commit puede estar cerrada)
                if (tx.isActive()) tx.rollback();
                // registra el error con el nombre del método, para saber dónde falló
                LOG.warning("actualizar: " + ex);
                // 0 = no se ha actualizado
                return 0;
            }
        }
    }

    /**
     * Elimina la valoración con ese id.
     * Si hay un error, hace rollback, lo registra y devuelve 0.
     *
     * @param id id de la valoración que se elimina; si es 0 o negativo no se elimina
     * @return 1 si se elimina; 0 si no existe, el id no es válido o hay un error
     */
    public int eliminar(int id) {

        // un id 0 o negativo no existe: devuelve 0 sin ir a la base de datos
        if (id <= 0) return 0;

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // es una escritura: hace falta transacción (en JDBC era setAutoCommit(false) y luego commit o rollback)
            var tx = em.getTransaction();
            try {
                // empieza la transacción: hasta el commit no se guarda nada
                tx.begin();
                // para borrar con JPA primero hay que buscar la entidad por su id; da null si no existe
                ValoracionEntity e = em.find(ValoracionEntity.class, id);
                // si no existe, no hay nada que borrar
                if (e == null) {
                    // rollback antes del return 0: si no, la transacción queda abierta y SQLite bloquea la base
                    tx.rollback();
                    return 0;
                }
                // remove marca la entidad para borrarla (el DELETE de JDBC)
                em.remove(e);
                // commit: aquí se hace el DELETE
                tx.commit();
                // 1 = se ha eliminado
                return 1;
            } catch (RuntimeException ex) {
                // deshace los cambios; solo si la transacción sigue activa (si falló el commit puede estar cerrada)
                if (tx.isActive()) tx.rollback();
                // registra el error con el nombre del método, para saber dónde falló
                LOG.warning("eliminar: " + ex);
                // 0 = no se ha eliminado
                return 0;
            }
        }
    }

    // C10 del README
    /**
     * Consulta C10: lista cada valoración con el título de la película, el nombre del espectador y la puntuación.
     * Va ordenada por título y luego por nombre. Si hay un error, lo registra en el log y devuelve List.of().
     *
     * @return lista de filas Object[] con {título, nombre del espectador, puntuación}; List.of() si no hay ninguna
     */
    public List<Object[]> valoracionesConDetalles() {

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // dos joins con "on": se unen las entidades por sus ids (como el JOIN ... ON de SQL)
            // Object[].class: se piden columnas sueltas, así que cada fila llega como un array {título, nombre, puntuación}
            return em.createQuery("select p.titulo, es.nombre, v.puntuacion from ValoracionEntity v "
                            + "join PeliculaEntity p on p.idPelicula = v.idPelicula "
                            + "join EspectadorEntity es on es.idEspectador = v.idEspectador "
                            + "order by p.titulo, es.nombre", Object[].class)
                    // getResultList: devuelve todas las filas de golpe (no hay entidades que pasar por el mapper)
                    .getResultList();
        } catch (RuntimeException ex) {
            // error de base de datos: se registra y se devuelve la lista vacía
            LOG.warning("valoracionesConDetalles: " + ex);
            return List.of();
        }
    }

    /**
     * Lista las películas que no tienen ninguna valoración, ordenadas por id.
     * Si hay un error, lo registra en el log y devuelve List.of().
     *
     * @return lista de filas Object[] con {id de la película, título}; List.of() si todas tienen valoraciones
     */
    public List<Object[]> peliculasSinValoraciones() {

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // not exists: se queda con la película solo si la subconsulta no encuentra ninguna valoración suya
            // Object[].class: se piden dos columnas sueltas, así que cada fila llega como un array {id, título}
            return em.createQuery("select p.idPelicula, p.titulo from PeliculaEntity p "
                            + "where not exists (select v from ValoracionEntity v where v.idPelicula = p.idPelicula) "
                            + "order by p.idPelicula", Object[].class)
                    // getResultList: devuelve todas las filas de golpe
                    .getResultList();
        } catch (RuntimeException ex) {
            // error de base de datos: se registra y se devuelve la lista vacía
            LOG.warning("peliculasSinValoraciones: " + ex);
            return List.of();
        }
    }

    /**
     * Lista las valoraciones de una película con el nombre del espectador, la puntuación y el comentario.
     * Va ordenada por nombre. Si hay un error, lo registra en el log y devuelve List.of().
     *
     * @param idPelicula id de la película; si es 0 o negativo no se busca
     * @return lista de filas Object[] con {nombre del espectador, puntuación, comentario}; List.of() si no hay ninguna o el id no es válido
     */
    public List<Object[]> valoracionesPorPelicula(int idPelicula) {

        // un id 0 o negativo no existe: se devuelve la lista vacía sin ir a la base de datos
        if (idPelicula <= 0) return List.of();

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // join con el espectador para sacar su nombre; where filtra por la película con el parámetro :id
            return em.createQuery("select es.nombre, v.puntuacion, v.comentario from ValoracionEntity v "
                            + "join EspectadorEntity es on es.idEspectador = v.idEspectador "
                            + "where v.idPelicula = :id order by es.nombre", Object[].class)
                    // setParameter: pone el valor en :id (como ps.setInt en JDBC)
                    .setParameter("id", idPelicula)
                    // getResultList: devuelve todas las filas de golpe
                    .getResultList();
        } catch (RuntimeException ex) {
            // error de base de datos: se registra y se devuelve la lista vacía
            LOG.warning("valoracionesPorPelicula: " + ex);
            return List.of();
        }
    }

    /**
     * Cuenta cuántas valoraciones hay de cada puntuación, ordenadas por puntuación.
     * Solo salen las puntuaciones que existen. Si hay un error, lo registra en el log y devuelve List.of().
     *
     * @return lista de filas Object[] con {puntuación, número de valoraciones}; List.of() si no hay valoraciones
     */
    public List<Object[]> estadisticasPuntuaciones() {

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // group by + count: agrupa por puntuación y cuenta cuántas valoraciones tiene cada una
            // Object[].class: se piden columnas sueltas, así que cada fila llega como un array {puntuación, total}
            return em.createQuery("select v.puntuacion, count(v.idValoracion) from ValoracionEntity v "
                            + "group by v.puntuacion order by v.puntuacion", Object[].class)
                    // getResultList: devuelve todas las filas de golpe
                    .getResultList();
        } catch (RuntimeException ex) {
            // error de base de datos: se registra y se devuelve la lista vacía
            LOG.warning("estadisticasPuntuaciones: " + ex);
            return List.of();
        }
    }

}
