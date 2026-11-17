// Paquete dao: aquí están las clases que leen y escriben en la base de datos
package es.ejemplo.cinefest.dao;

// Imports del proyecto y de JPA: el modelo Sala, OrmFactory, la entidad, el mapper y EntityManagerFactory
import es.ejemplo.cinefest.modelo.Sala;
import es.ejemplo.cinefest.orm.config.OrmFactory;
import es.ejemplo.cinefest.orm.entity.SalaEntity;
import es.ejemplo.cinefest.orm.mapper.SalaMapper;
import jakarta.persistence.EntityManagerFactory;

// Imports de Java: List y Optional para lo que devuelven los métodos y Logger para registrar los errores
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * DAO de la tabla salas: CRUD de salas y consultas de salas con su sede y su ocupación con JPA (Hibernate).
 * Cada operación abre su EntityManager, hace su trabajo y lo cierra al terminar.
 * Si hay un error, lo registra en el log y devuelve 0, Optional.empty() o List.of(), sin lanzarlo.
 */
public class SalaDao {

    // Fábrica de EntityManager: se crea una vez en el constructor y la usan los métodos del DAO
    private final EntityManagerFactory emf;

    /**
     * Crea el DAO y su EntityManagerFactory para la base de datos de esa URL.
     * Si hay un error al crear la fábrica (por ejemplo, jdbcUrl es null), la excepción sale del constructor.
     *
     * @param jdbcUrl URL JDBC de la base SQLite, por ejemplo "jdbc:sqlite:datos/cinefest.sqlite3"
     */
    public SalaDao(String jdbcUrl) {
        // OrmFactory lee persistence.xml (unidad cinefestPU) y le pone esta URL de conexión
        this.emf = OrmFactory.crear(jdbcUrl);
    }

    // Logger para registrar los errores: el README pide registrarlos dentro del DAO, sin lanzarlos
    private static final Logger LOG = Logger.getLogger(SalaDao.class.getName());

    // Usar transacciones y rollback en operaciones de escritura.
    /**
     * Devuelve las salas de la tabla salas ordenadas por id.
     * Si hay un error, lo registra en el log y devuelve List.of().
     *
     * @return lista de salas; List.of() (lista vacía) si no hay ninguna
     */
    public List<Sala> listar() {
        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // JPQL: se escribe con la clase SalaEntity y su atributo idSala, no con la tabla ni la columna
            // SalaEntity.class: cada fila llega como una SalaEntity; es solo lectura, no hace falta transacción
            return em.createQuery("select sa from SalaEntity sa order by sa.idSala", SalaEntity.class)
                    // recorre las filas una a una (como el while (rs.next()) de JDBC)
                    .getResultStream()
                    // desdeEntidad: pasa cada SalaEntity (entidad) a Sala (modelo), que es lo que devuelve el DAO
                    .map(SalaMapper::desdeEntidad)
                    // junta las salas en una lista
                    .toList();
        } catch (RuntimeException ex) {
            // JPA lanza RuntimeException (no SQLException como JDBC): se registra y se devuelve la lista vacía
            LOG.warning("listar: " + ex);
            return List.of();
        }
    }

    /**
     * Busca una sala por su id (clave primaria).
     * Si hay un error, lo registra en el log y devuelve Optional.empty().
     *
     * @param id id de la sala; si es 0 o negativo no se busca
     * @return Optional con la sala si existe; Optional.empty() si no existe o el id no es válido
     */
    public Optional<Sala> buscarPorId(int id) {

        // un id 0 o negativo no existe: se devuelve vacío sin ir a la base de datos
        if (id <= 0) return Optional.empty();

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // em.find busca por clave primaria (el SELECT ... WHERE id_sala = ? de JDBC) y da null si no está
            // Optional.ofNullable: si find da null, queda Optional.empty()
            return Optional.ofNullable(em.find(SalaEntity.class, id))
                    // si la encontró, desdeEntidad la pasa de SalaEntity (entidad) a Sala (modelo)
                    .map(SalaMapper::desdeEntidad);
        } catch (RuntimeException ex) {
            // error de base de datos: se registra y se devuelve vacío
            LOG.warning("buscarPorId: " + ex);
            return Optional.empty();
        }
    }

    /**
     * Inserta una sala nueva y devuelve el id que le da la base de datos.
     * Si hay un error (por ejemplo, la sede no existe), hace rollback, lo registra y devuelve 0.
     *
     * @param dato sala que se inserta; nombre no vacío, aforo mayor que 0 e idSede obligatorios
     * @return el id generado (mayor que 0) si se inserta; 0 si el dato no es válido o hay un error
     */
    public int insertar(Sala dato) {

        // validación: sin sala, con nombre vacío, aforo nulo o menor o igual que 0, o sin sede, no se inserta y devuelve 0
        if (dato == null || dato.getNombre() == null || dato.getNombre().isBlank()
                || dato.getAforo() == null || dato.getAforo() <= 0
                || dato.getIdSede() == null) return 0;
        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // es una escritura: hace falta transacción (en JDBC era setAutoCommit(false) y luego commit o rollback)
            var tx = em.getTransaction();
            try {
                // empieza la transacción: hasta el commit no se guarda nada
                tx.begin();
                // aEntidad: pasa la Sala (modelo) a SalaEntity (entidad), que es lo que guarda JPA
                SalaEntity e = SalaMapper.aEntidad(dato);
                // id a null para que lo genere la base de datos (AUTOINCREMENT); con un id puesto, persist da error
                e.setIdSala(null);
                // persist hace el INSERT (como el ps.executeUpdate() de JDBC)
                // si la sede no existe, la clave foránea lo rechaza y salta al catch (las claves foráneas están activadas)
                em.persist(e);
                // commit: confirma el INSERT en la base de datos (como con.commit() en JDBC)
                tx.commit();
                // después del persist la entidad ya tiene el id generado: se devuelve (mayor que 0)
                return e.getIdSala();
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
     * Actualiza el nombre, el aforo y la sede de una sala que ya existe, buscándola por su id.
     * Si hay un error (por ejemplo, la sede nueva no existe), hace rollback, lo registra y devuelve 0.
     *
     * @param dato sala con el id y los datos nuevos; id, nombre, aforo (mayor que 0) e idSede son obligatorios
     * @return 1 si se actualiza; 0 si el dato no es válido, la sala no existe o hay un error
     */
    public int actualizar(Sala dato) {

        // validación: hace falta el id (para saber cuál se cambia), nombre no vacío, aforo mayor que 0 y sede
        if (dato == null || dato.getIdSala() == null
                || dato.getNombre() == null || dato.getNombre().isBlank()
                || dato.getAforo() == null || dato.getAforo() <= 0
                || dato.getIdSede() == null)
            return 0;

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // es una escritura: hace falta transacción (en JDBC era setAutoCommit(false) y luego commit o rollback)
            var tx = em.getTransaction();
            try {
                // empieza la transacción: hasta el commit no se guarda nada
                tx.begin();
                // busca la sala guardada por su id; da null si no existe
                SalaEntity e = em.find(SalaEntity.class, dato.getIdSala());
                // si no existe, no hay nada que actualizar
                if (e == null) {
                    // rollback antes del return 0: si no, la transacción queda abierta y SQLite bloquea la base
                    // (SQLITE_BUSY), así que las siguientes escrituras devolverían 0
                    tx.rollback();
                    return 0;
                }
                // se cambian los campos de la entidad con los datos nuevos (el id no se toca)
                e.setNombre(dato.getNombre());
                e.setAforo(dato.getAforo());
                e.setIdSede(dato.getIdSede());
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
     * Elimina la sala con ese id.
     * Si hay un error (por ejemplo, la sala tiene sesiones), hace rollback, lo registra y devuelve 0.
     *
     * @param id id de la sala que se elimina; si es 0 o negativo no se elimina
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
                SalaEntity e = em.find(SalaEntity.class, id);
                // si no existe, no hay nada que borrar
                if (e == null) {
                    // rollback antes del return 0: si no, la transacción queda abierta y SQLite bloquea la base
                    tx.rollback();
                    return 0;
                }
                // remove marca la entidad para borrarla (el DELETE de JDBC)
                em.remove(e);
                // commit: aquí se hace el DELETE; si otra tabla depende de esta sala, la clave foránea lo rechaza
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

    // C06 del README
    /**
     * Consulta C06: lista cada sala con el nombre de su sede y su aforo, ordenada por sede y luego por sala.
     * Si hay un error, lo registra en el log y devuelve List.of().
     *
     * @return lista de filas Object[] con {nombre de la sede, nombre de la sala, aforo}; List.of() si no hay ninguna
     */
    public List<Object[]> salasConSede() {

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // JPQL con join: SalaEntity y SedeEntity se unen con "on" por idSede (como el JOIN ... ON de SQL)
            // Object[].class: se piden columnas sueltas, así que cada fila llega como un array {sede, sala, aforo}
            return em.createQuery("select se.nombre, sa.nombre, sa.aforo from SalaEntity sa "
                            + "join SedeEntity se on se.idSede = sa.idSede order by se.nombre, sa.nombre", Object[].class)
                    // getResultList: devuelve todas las filas de golpe (no hay entidades que pasar por el mapper)
                    .getResultList();
        } catch (RuntimeException ex) {
            // error de base de datos: se registra y se devuelve la lista vacía
            LOG.warning("salasConSede: " + ex);
            return List.of();
        }
    }

    // C28 del README
    /**
     * Consulta C28: lista las salas que no tienen ninguna sesión en estado PROGRAMADA, ordenadas por sede y sala.
     * Si hay un error, lo registra en el log y devuelve List.of().
     *
     * @return lista de salas sin sesiones programadas; List.of() si no hay ninguna
     */
    public List<Sala> salasSinSesiones() {

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // not exists: se queda con la sala solo si la subconsulta no encuentra ninguna sesión PROGRAMADA suya
            // SalaEntity.class: se devuelven salas completas, así que luego se pasan por el mapper
            return em.createQuery("select sa from SalaEntity sa join SedeEntity se on se.idSede = sa.idSede "
                            + "where not exists (select s from SesionEntity s where s.idSala = sa.idSala and s.estado = 'PROGRAMADA') "
                            + "order by se.nombre, sa.nombre", SalaEntity.class)
                    // recorre las filas una a una
                    .getResultStream()
                    // desdeEntidad: pasa cada SalaEntity (entidad) a Sala (modelo)
                    .map(SalaMapper::desdeEntidad)
                    // junta las salas en una lista
                    .toList();
        } catch (RuntimeException ex) {
            // error de base de datos: se registra y se devuelve la lista vacía
            LOG.warning("salasSinSesiones: " + ex);
            return List.of();
        }
    }

    /**
     * Calcula, por cada sala, cuántas entradas PAGADA o USADA tiene entre todas sus sesiones.
     * Las salas sin entradas salen con 0. Si hay un error, lo registra en el log y devuelve List.of().
     *
     * @return lista de filas Object[] con {nombre de la sala, aforo, número de entradas}; List.of() si no hay salas
     */
    public List<Object[]> ocupacionPorSala() {

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // left join: la sala sale aunque no tenga sesiones ni entradas (con inner join desaparecería)
            // el filtro de estado va en el "on" del último left join, no en un where, para no perder esas salas
            // group by + count: cuenta las entradas de cada sala; Object[].class porque devuelve columnas sueltas
            return em.createQuery("select sa.nombre, sa.aforo, count(e.idEntrada) from SalaEntity sa "
                            + "left join SesionEntity s on s.idSala = sa.idSala "
                            + "left join EntradaEntity e on e.idSesion = s.idSesion and e.estado in ('PAGADA','USADA') "
                            + "group by sa.idSala, sa.nombre, sa.aforo order by sa.idSala", Object[].class)
                    // getResultList: devuelve todas las filas de golpe
                    .getResultList();
        } catch (RuntimeException ex) {
            // error de base de datos: se registra y se devuelve la lista vacía
            LOG.warning("ocupacionPorSala: " + ex);
            return List.of();
        }
    }

}
