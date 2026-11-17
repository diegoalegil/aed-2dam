// Paquete dao: aquí están las clases que leen y escriben en la base de datos
package es.ejemplo.cinefest.dao;

// Imports del proyecto y de JPA: el modelo Espectador, OrmFactory, la entidad, el mapper y EntityManagerFactory
import es.ejemplo.cinefest.modelo.Espectador;
import es.ejemplo.cinefest.orm.config.OrmFactory;
import es.ejemplo.cinefest.orm.entity.EspectadorEntity;
import es.ejemplo.cinefest.orm.mapper.EspectadorMapper;
import jakarta.persistence.EntityManagerFactory;

// Imports de Java: List y Optional para lo que devuelven los métodos y Logger para registrar los errores
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * DAO de la tabla espectadores: CRUD de espectadores, búsqueda por email y consultas sobre sus entradas con JPA (Hibernate).
 * Cada operación abre su EntityManager, hace su trabajo y lo cierra al terminar.
 * Si hay un error, lo registra en el log y devuelve 0, Optional.empty() o List.of(), sin lanzarlo.
 */
public class EspectadorDao {

    // Fábrica de EntityManager: se crea una vez en el constructor y la usan los métodos del DAO
    private final EntityManagerFactory emf;

    /**
     * Crea el DAO y su EntityManagerFactory para la base de datos de esa URL.
     * Si hay un error al crear la fábrica (por ejemplo, jdbcUrl es null), la excepción sale del constructor.
     *
     * @param jdbcUrl URL JDBC de la base SQLite, por ejemplo "jdbc:sqlite:datos/cinefest.sqlite3"
     */
    public EspectadorDao(String jdbcUrl) {
        // OrmFactory lee persistence.xml (unidad cinefestPU) y le pone esta URL de conexión
        this.emf = OrmFactory.crear(jdbcUrl);
    }

    // Logger para registrar los errores: el README pide registrarlos dentro del DAO, sin lanzarlos
    private static final Logger LOG = Logger.getLogger(EspectadorDao.class.getName());

    // Usar transacciones y rollback en operaciones de escritura.

    /**
     * Devuelve los espectadores de la tabla espectadores ordenados por id.
     * Si hay un error, lo registra en el log y devuelve List.of().
     *
     * @return lista de espectadores; List.of() (lista vacía) si no hay ninguno
     */
    public List<Espectador> listar() {
        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // JPQL: se escribe con la clase EspectadorEntity y su atributo idEspectador, no con la tabla ni la columna
            // EspectadorEntity.class: cada fila llega como una EspectadorEntity; es solo lectura, no hace falta transacción
            return em.createQuery("select es from EspectadorEntity es order by es.idEspectador", EspectadorEntity.class)
                    // recorre las filas una a una (como el while (rs.next()) de JDBC)
                    .getResultStream()
                    // desdeEntidad: pasa cada EspectadorEntity (entidad) a Espectador (modelo), que es lo que devuelve el DAO
                    .map(EspectadorMapper::desdeEntidad)
                    // junta los espectadores en una lista
                    .toList();
        } catch (RuntimeException ex) {
            // JPA lanza RuntimeException (no SQLException como JDBC): se registra y se devuelve la lista vacía
            LOG.warning("listar: " + ex);
            return List.of();
        }
    }

    /**
     * Busca un espectador por su id (clave primaria).
     * Si hay un error, lo registra en el log y devuelve Optional.empty().
     *
     * @param id id del espectador; si es 0 o negativo no se busca
     * @return Optional con el espectador si existe; Optional.empty() si no existe o el id no es válido
     */
    public Optional<Espectador> buscarPorId(int id) {

        // un id 0 o negativo no existe: se devuelve vacío sin ir a la base de datos
        if (id <= 0) return Optional.empty();

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // em.find busca por clave primaria (el SELECT ... WHERE id_espectador = ? de JDBC) y da null si no está
            // Optional.ofNullable: si find da null, queda Optional.empty()
            return Optional.ofNullable(em.find(EspectadorEntity.class, id))
                    // si lo encontró, desdeEntidad lo pasa de EspectadorEntity (entidad) a Espectador (modelo)
                    .map(EspectadorMapper::desdeEntidad);
        } catch (RuntimeException ex) {
            // error de base de datos: se registra y se devuelve vacío
            LOG.warning("buscarPorId: " + ex);
            return Optional.empty();
        }
    }

    /**
     * Inserta un espectador nuevo y devuelve el id que le da la base de datos.
     * Si no trae valor de activo, se guarda como activo (1).
     * Si hay un error (por ejemplo, el email ya existe), hace rollback, lo registra y devuelve 0.
     *
     * @param dato espectador que se inserta; nombre, email y fechaAlta no pueden estar vacíos; activo puede ser null, 0 o 1
     * @return el id generado (mayor que 0) si se inserta; 0 si el dato no es válido o hay un error
     */
    public int insertar(Espectador dato) {

        // validación: sin espectador, con nombre, email o fecha vacíos, o con activo distinto de null, 0 y 1, devuelve 0
        // (activo puede venir a null: más abajo se pone a 1)
        if (dato == null || dato.getNombre() == null || dato.getNombre().isBlank()
                || dato.getEmail() == null || dato.getEmail().isBlank()
                || dato.getFechaAlta() == null || dato.getFechaAlta().isBlank()
                || (dato.getActivo() != null && dato.getActivo() != 0 && dato.getActivo() != 1)) return 0;
        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // es una escritura: hace falta transacción (en JDBC era setAutoCommit(false) y luego commit o rollback)
            var tx = em.getTransaction();
            try {
                // empieza la transacción: hasta el commit no se guarda nada
                tx.begin();
                // aEntidad: pasa el Espectador (modelo) a EspectadorEntity (entidad), que es lo que guarda JPA
                EspectadorEntity e = EspectadorMapper.aEntidad(dato);
                // id a null para que lo genere la base de datos (AUTOINCREMENT); con un id puesto, persist da error
                e.setIdEspectador(null);
                // valor por defecto: si no se indicó activo, el espectador se guarda como activo (1)
                if (e.getActivo() == null) e.setActivo(1);
                // persist hace el INSERT (como el ps.executeUpdate() de JDBC)
                // si el email ya existe (email es UNIQUE en schema.sql), la base de datos lo rechaza y salta al catch
                em.persist(e);
                // commit: confirma el INSERT en la base de datos (como con.commit() en JDBC)
                tx.commit();
                // después del persist la entidad ya tiene el id generado: se devuelve (mayor que 0)
                return e.getIdEspectador();
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
     * Actualiza el nombre, el email, la fecha de alta y el activo de un espectador que ya existe, buscándolo por su id.
     * Si hay un error (por ejemplo, el email nuevo ya es de otro espectador), hace rollback, lo registra y devuelve 0.
     *
     * @param dato espectador con el id y los datos nuevos; todo es obligatorio y activo tiene que ser 0 o 1
     * @return 1 si se actualiza; 0 si el dato no es válido, el espectador no existe o hay un error
     */
    public int actualizar(Espectador dato) {

        // validación: hace falta el id (para saber cuál se cambia), textos no vacíos y activo con valor 0 o 1
        // (aquí activo no puede ser null, a diferencia de insertar)
        if (dato == null || dato.getIdEspectador() == null
                || dato.getNombre() == null || dato.getNombre().isBlank()
                || dato.getEmail() == null || dato.getEmail().isBlank()
                || dato.getFechaAlta() == null || dato.getFechaAlta().isBlank()
                || dato.getActivo() == null || (dato.getActivo() != 0 && dato.getActivo() != 1))
            return 0;

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // es una escritura: hace falta transacción (en JDBC era setAutoCommit(false) y luego commit o rollback)
            var tx = em.getTransaction();
            try {
                // empieza la transacción: hasta el commit no se guarda nada
                tx.begin();
                // busca el espectador guardado por su id; da null si no existe
                EspectadorEntity e = em.find(EspectadorEntity.class, dato.getIdEspectador());
                // si no existe, no hay nada que actualizar
                if (e == null) {
                    // rollback antes del return 0: si no, la transacción queda abierta y SQLite bloquea la base
                    // (SQLITE_BUSY), así que las siguientes escrituras devolverían 0
                    tx.rollback();
                    return 0;
                }
                // se cambian los campos de la entidad con los datos nuevos (el id no se toca)
                e.setNombre(dato.getNombre());
                e.setEmail(dato.getEmail());
                e.setFechaAlta(dato.getFechaAlta());
                e.setActivo(dato.getActivo());
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
     * Elimina el espectador con ese id.
     * Si hay un error (por ejemplo, el espectador tiene entradas), hace rollback, lo registra y devuelve 0.
     *
     * @param id id del espectador que se elimina; si es 0 o negativo no se elimina
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
                EspectadorEntity e = em.find(EspectadorEntity.class, id);
                // si no existe, no hay nada que borrar
                if (e == null) {
                    // rollback antes del return 0: si no, la transacción queda abierta y SQLite bloquea la base
                    tx.rollback();
                    return 0;
                }
                // remove marca la entidad para borrarla (el DELETE de JDBC)
                em.remove(e);
                // commit: aquí se hace el DELETE; si otra tabla depende de este espectador, la clave foránea lo rechaza
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

    /**
     * Busca un espectador por su email.
     * Si hay un error, lo registra en el log y devuelve Optional.empty().
     *
     * @param email email del espectador; si es null o está vacío no se busca
     * @return Optional con el espectador si existe; Optional.empty() si no existe o el email no es válido
     */
    public Optional<Espectador> buscarPorEmail(String email) {

        // un email null o vacío no puede existir: se devuelve vacío sin ir a la base de datos
        if (email == null || email.isBlank()) return Optional.empty();

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // em.find solo sirve para la clave primaria; para buscar por otro campo se usa JPQL con where
            // :email es un parámetro con nombre (como el ? del PreparedStatement, pero con nombre)
            return em.createQuery("select es from EspectadorEntity es where es.email = :email", EspectadorEntity.class)
                    // setParameter: pone el valor en :email (como ps.setString en JDBC); evita la inyección SQL
                    .setParameter("email", email)
                    // recorre las filas
                    .getResultStream()
                    // findFirst: se queda con la primera fila (el email es único) y da Optional.empty() si no hay ninguna
                    .findFirst()
                    // si la encontró, desdeEntidad la pasa de EspectadorEntity (entidad) a Espectador (modelo)
                    .map(EspectadorMapper::desdeEntidad);
        } catch (RuntimeException ex) {
            // error de base de datos: se registra y se devuelve vacío
            LOG.warning("buscarPorEmail: " + ex);
            return Optional.empty();
        }
    }

    // C09 del README
    /**
     * Consulta C09: lista los espectadores que no tienen ninguna entrada (de cualquier estado), ordenados por nombre.
     * Si hay un error, lo registra en el log y devuelve List.of().
     *
     * @return lista de espectadores sin entradas; List.of() si no hay ninguno
     */
    public List<Espectador> espectadoresSinEntradas() {

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // not exists: se queda con el espectador solo si la subconsulta no encuentra ninguna entrada suya
            // EspectadorEntity.class: se devuelven espectadores completos, así que luego se pasan por el mapper
            return em.createQuery("select es from EspectadorEntity es where not exists "
                            + "(select e from EntradaEntity e where e.idEspectador = es.idEspectador) "
                            + "order by es.nombre", EspectadorEntity.class)
                    // recorre las filas una a una
                    .getResultStream()
                    // desdeEntidad: pasa cada EspectadorEntity (entidad) a Espectador (modelo)
                    .map(EspectadorMapper::desdeEntidad)
                    // junta los espectadores en una lista
                    .toList();
        } catch (RuntimeException ex) {
            // error de base de datos: se registra y se devuelve la lista vacía
            LOG.warning("espectadoresSinEntradas: " + ex);
            return List.of();
        }
    }

    // C13 del README
    /**
     * Consulta C13: ranking de espectadores por número de entradas PAGADA o USADA, de más a menos.
     * Los espectadores sin entradas salen con 0. Si hay un error, lo registra en el log y devuelve List.of().
     *
     * @return lista de filas Object[] con {nombre del espectador, número de entradas}; List.of() si no hay espectadores
     */
    public List<Object[]> rankingEspectadores() {

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // left join: el espectador sale aunque no tenga entradas (con inner join desaparecería)
            // el filtro de estado va en el "on", no en un where, para no perder a los que tienen 0 entradas
            // group by + count: cuenta las entradas de cada espectador; Object[].class porque devuelve columnas sueltas
            // order by count desc: el que más entradas tiene va primero; si empatan, por nombre
            return em.createQuery("select es.nombre, count(e.idEntrada) "
                            + "from EspectadorEntity es left join EntradaEntity e on e.idEspectador = es.idEspectador "
                            + "and e.estado in ('PAGADA','USADA') "
                            + "group by es.idEspectador, es.nombre order by count(e.idEntrada) desc, es.nombre", Object[].class)
                    // getResultList: devuelve todas las filas de golpe
                    .getResultList();
        } catch (RuntimeException ex) {
            // error de base de datos: se registra y se devuelve la lista vacía
            LOG.warning("rankingEspectadores: " + ex);
            return List.of();
        }
    }

    // C20 del README
    /**
     * Consulta C20: lista los espectadores con entradas PAGADA o USADA en al menos "minimo" sedes distintas.
     * Si hay un error, lo registra en el log y devuelve List.of().
     *
     * @param minimo número mínimo de sedes distintas que tiene que haber visitado el espectador
     * @return lista de filas Object[] con {nombre del espectador, número de sedes distintas}; List.of() si no hay ninguno
     */
    public List<Object[]> espectadoresVariasSedes(int minimo) {

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // la cadena de joins va de espectador a entrada, de entrada a sesión, de sesión a sala y la sala da la sede
            // count(distinct ...): cuenta cada sede una sola vez aunque el espectador haya ido varias veces
            // having: filtra los grupos ya contados (el where filtra filas antes de agrupar; having, después)
            // :minimo es un parámetro con nombre, como el ? del PreparedStatement
            return em.createQuery("select es.nombre, count(distinct sa.idSede) "
                            + "from EspectadorEntity es join EntradaEntity e on e.idEspectador = es.idEspectador "
                            + "join SesionEntity s on s.idSesion = e.idSesion "
                            + "join SalaEntity sa on sa.idSala = s.idSala "
                            + "where e.estado in ('PAGADA','USADA') "
                            + "group by es.idEspectador, es.nombre having count(distinct sa.idSede) >= :minimo "
                            + "order by count(distinct sa.idSede) desc, es.nombre", Object[].class)
                    // setParameter: pone el valor en :minimo (como ps.setInt en JDBC)
                    .setParameter("minimo", minimo)
                    // getResultList: devuelve todas las filas de golpe
                    .getResultList();
        } catch (RuntimeException ex) {
            // error de base de datos: se registra y se devuelve la lista vacía
            LOG.warning("espectadoresVariasSedes: " + ex);
            return List.of();
        }
    }

}
