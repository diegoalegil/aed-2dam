// Paquete dao: aquí están las clases que leen y escriben en la base de datos
package es.ejemplo.cinefest.dao;

// Imports del proyecto y de JPA: el modelo Sede, OrmFactory, la entidad, el mapper y EntityManagerFactory
import es.ejemplo.cinefest.modelo.Sede;
import es.ejemplo.cinefest.orm.config.OrmFactory;
import es.ejemplo.cinefest.orm.entity.SedeEntity;
import es.ejemplo.cinefest.orm.mapper.SedeMapper;
import jakarta.persistence.EntityManagerFactory;

// Imports de Java: List y Optional para lo que devuelven los métodos y Logger para registrar los errores
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * DAO de la tabla sedes: CRUD de sedes y consultas de sus salas con JPA (Hibernate).
 * Cada operación abre su EntityManager, hace su trabajo y lo cierra al terminar.
 * Si hay un error, lo registra en el log y devuelve 0, Optional.empty() o List.of(), sin lanzarlo.
 */
public class SedeDao {

    // Fábrica de EntityManager: se crea una vez en el constructor y la usan los métodos del DAO
    private final EntityManagerFactory emf;

    /**
     * Crea el DAO y su EntityManagerFactory para la base de datos de esa URL.
     * Si hay un error al crear la fábrica (por ejemplo, jdbcUrl es null), la excepción sale del constructor.
     *
     * @param jdbcUrl URL JDBC de la base SQLite, por ejemplo "jdbc:sqlite:datos/cinefest.sqlite3"
     */
    public SedeDao(String jdbcUrl) {
        // OrmFactory lee persistence.xml (unidad cinefestPU) y le pone esta URL de conexión
        this.emf = OrmFactory.crear(jdbcUrl);
    }

    // Logger para registrar los errores: el README pide registrarlos dentro del DAO, sin lanzarlos
    private static final Logger LOG = Logger.getLogger(SedeDao.class.getName());

    // Usar transacciones y rollback en operaciones de escritura.
    /**
     * Devuelve las sedes de la tabla sedes ordenadas por id.
     * Si hay un error, lo registra en el log y devuelve List.of().
     *
     * @return lista de sedes; List.of() (lista vacía) si no hay ninguna
     */
    public List<Sede> listar() {
        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // JPQL: se escribe con la clase SedeEntity y su atributo idSede, no con la tabla ni la columna
            // SedeEntity.class: cada fila llega como una SedeEntity; es solo lectura, no hace falta transacción
            return em.createQuery("select s from SedeEntity s order by s.idSede", SedeEntity.class)
                    // recorre las filas una a una (como el while (rs.next()) de JDBC)
                    .getResultStream()
                    // desdeEntidad: pasa cada SedeEntity (entidad) a Sede (modelo), que es lo que devuelve el DAO
                    .map(SedeMapper::desdeEntidad)
                    // junta las sedes en una lista
                    .toList();
        } catch (RuntimeException ex) {
            // JPA lanza RuntimeException (no SQLException como JDBC): se registra y se devuelve la lista vacía
            LOG.warning("listar: " + ex);
            return List.of();
        }
    }

    /**
     * Busca una sede por su id (clave primaria).
     * Si hay un error, lo registra en el log y devuelve Optional.empty().
     *
     * @param id id de la sede; si es 0 o negativo no se busca
     * @return Optional con la sede si existe; Optional.empty() si no existe o el id no es válido
     */
    public Optional<Sede> buscarPorId(int id) {

        // un id 0 o negativo no existe: se devuelve vacío sin ir a la base de datos
        if (id <= 0) return Optional.empty();

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // em.find busca por clave primaria (el SELECT ... WHERE id_sede = ? de JDBC) y da null si no está
            // Optional.ofNullable: si find da null, queda Optional.empty()
            return Optional.ofNullable(em.find(SedeEntity.class, id))
                    // si la encontró, desdeEntidad la pasa de SedeEntity (entidad) a Sede (modelo)
                    .map(SedeMapper::desdeEntidad);
        } catch (RuntimeException ex) {
            // error de base de datos: se registra y se devuelve vacío
            LOG.warning("buscarPorId: " + ex);
            return Optional.empty();
        }
    }

    /**
     * Inserta una sede nueva y devuelve el id que le da la base de datos.
     * Si hay un error (por ejemplo, ya existe una sede con ese nombre), hace rollback, lo registra y devuelve 0.
     *
     * @param dato sede que se inserta; nombre y municipio no pueden estar vacíos
     * @return el id generado (mayor que 0) si se inserta; 0 si el dato no es válido o hay un error
     */
    public int insertar(Sede dato) {

        // validación: sin sede, o con nombre o municipio vacíos, no se inserta y devuelve 0
        if (dato == null || dato.getNombre() == null || dato.getNombre().isBlank()
                || dato.getMunicipio() == null || dato.getMunicipio().isBlank()) return 0;
        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // es una escritura: hace falta transacción (en JDBC era setAutoCommit(false) y luego commit o rollback)
            var tx = em.getTransaction();
            try {
                // empieza la transacción: hasta el commit no se guarda nada
                tx.begin();
                // aEntidad: pasa la Sede (modelo) a SedeEntity (entidad), que es lo que guarda JPA
                SedeEntity e = SedeMapper.aEntidad(dato);
                // id a null para que lo genere la base de datos (AUTOINCREMENT); con un id puesto, persist da error
                e.setIdSede(null);
                // persist hace el INSERT (como el ps.executeUpdate() de JDBC)
                // si el nombre ya existe (nombre es UNIQUE en schema.sql), la base de datos lo rechaza y salta al catch
                em.persist(e);
                // commit: confirma el INSERT en la base de datos (como con.commit() en JDBC)
                tx.commit();
                // después del persist la entidad ya tiene el id generado: se devuelve (mayor que 0)
                return e.getIdSede();
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
     * Actualiza el nombre y el municipio de una sede que ya existe, buscándola por su id.
     * Si hay un error (por ejemplo, el nombre nuevo ya es de otra sede), hace rollback, lo registra y devuelve 0.
     *
     * @param dato sede con el id y los datos nuevos; id, nombre y municipio son obligatorios
     * @return 1 si se actualiza; 0 si el dato no es válido, la sede no existe o hay un error
     */
    public int actualizar(Sede dato) {

        // validación: hace falta el id (para saber cuál se cambia) y que nombre y municipio no estén vacíos
        if (dato == null || dato.getIdSede() == null || dato.getNombre() == null || dato.getNombre().isBlank() || dato.getMunicipio() == null || dato.getMunicipio().isBlank())
            return 0;

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // es una escritura: hace falta transacción (en JDBC era setAutoCommit(false) y luego commit o rollback)
            var tx = em.getTransaction();
            try {
                // empieza la transacción: hasta el commit no se guarda nada
                tx.begin();
                // busca la sede guardada por su id; da null si no existe
                SedeEntity e = em.find(SedeEntity.class, dato.getIdSede());
                // si no existe, no hay nada que actualizar
                if (e == null) {
                    // rollback antes del return 0: si no, la transacción queda abierta y SQLite bloquea la base
                    // (SQLITE_BUSY), así que las siguientes escrituras devolverían 0
                    tx.rollback();
                    return 0;
                }
                // se cambian los campos de la entidad con los datos nuevos (el id no se toca)
                e.setNombre(dato.getNombre());
                e.setMunicipio(dato.getMunicipio());
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
     * Elimina la sede con ese id.
     * Si hay un error (por ejemplo, la sede tiene salas), hace rollback, lo registra y devuelve 0.
     *
     * @param id id de la sede que se elimina; si es 0 o negativo no se elimina
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
                SedeEntity e = em.find(SedeEntity.class, id);
                // si no existe, no hay nada que borrar
                if (e == null) {
                    // rollback antes del return 0: si no, la transacción queda abierta y SQLite bloquea la base
                    // (SQLITE_BUSY), así que las siguientes escrituras devolverían 0
                    tx.rollback();
                    return 0;
                }
                // remove marca la entidad para borrarla (el DELETE de JDBC)
                em.remove(e);
                // commit: hace el DELETE; si la sede tiene salas, la clave foránea lo impide y salta al catch
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

    // Sin reto numerado en el README: método propio de este DAO
    /**
     * Devuelve el nombre y el aforo de las salas de una sede, ordenadas por nombre de sala.
     * Si hay un error, lo registra en el log y devuelve List.of().
     *
     * @param idSede id de la sede; si es 0 o negativo no se busca
     * @return filas Object[] con [0] nombre de la sala (String) y [1] aforo (Integer); List.of() si no tiene salas
     */
    public List<Object[]> salasPorSede(int idSede) {
        // un id 0 o negativo no existe: lista vacía sin ir a la base de datos
        if (idSede <= 0) return List.of();
        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // solo hace falta la clase SalaEntity: su atributo idSede es la FK que apunta a la sede
            // select: nombre y aforo de cada sala; Object[].class porque cada fila trae dos columnas sueltas
            return em.createQuery("select sa.nombre, sa.aforo from SalaEntity sa "
                            // se queda con las salas de esa sede (:id es el ? de JDBC, pero con nombre) y ordena por nombre
                            + "where sa.idSede = :id order by sa.nombre", Object[].class)
                    // da valor a :id (como el ps.setInt(1, idSede) de JDBC)
                    .setParameter("id", idSede)
                    // devuelve las filas en una lista: [0] nombre, [1] aforo (Integer, porque es un atributo y no un count)
                    .getResultList();
        } catch (RuntimeException ex) {
            // si falla la consulta: se registra el error y se devuelve la lista vacía
            LOG.warning("salasPorSede: " + ex);
            return List.of();
        }
    }

    // C12 del README
    /**
     * Devuelve, por cada sede, cuántas salas tiene y la suma de su aforo, también las sedes sin salas (reto C12).
     * Si hay un error, lo registra en el log y devuelve List.of().
     *
     * @return filas Object[] ordenadas por sede con [0] nombre de la sede (String), [1] número de salas (Long)
     *         y [2] aforo total (Long, 0 si no tiene salas); List.of() si no hay sedes
     */
    public List<Object[]> aforoTotalPorSede() {
        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // select: nombre de la sede, número de salas y aforo total (count y sum dan Long)
            // count(sa.idSala) cuenta solo salas reales: una sede sin salas da 0
            // coalesce(sum(sa.aforo), 0): una sede sin salas da sum null; coalesce lo cambia por 0
            return em.createQuery("select se.nombre, count(sa.idSala), coalesce(sum(sa.aforo), 0) "
                            // "incluso sin salas" del README: left join, así salen también las sedes sin salas
                            + "from SedeEntity se left join SalaEntity sa on sa.idSede = se.idSede "
                            // "por sede": group by (una fila por sede); "Orden: sede ASC": order by se.nombre
                            + "group by se.idSede, se.nombre order by se.nombre", Object[].class)
                    // devuelve las filas Object[] en una lista: [0] sede, [1] salas, [2] aforo total
                    .getResultList();
        } catch (RuntimeException ex) {
            // si falla la consulta: se registra el error y se devuelve la lista vacía
            LOG.warning("aforoTotalPorSede: " + ex);
            return List.of();
        }
    }

}
