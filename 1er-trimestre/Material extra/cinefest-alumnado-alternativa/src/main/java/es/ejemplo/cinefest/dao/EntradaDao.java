// Paquete dao: aquí están las clases que leen y escriben en la base de datos
package es.ejemplo.cinefest.dao;

// Imports del proyecto y de JPA: el modelo Entrada, OrmFactory, la entidad, el mapper y EntityManagerFactory
import es.ejemplo.cinefest.modelo.Entrada;
import es.ejemplo.cinefest.orm.config.OrmFactory;
import es.ejemplo.cinefest.orm.entity.EntradaEntity;
import es.ejemplo.cinefest.orm.mapper.EntradaMapper;
import jakarta.persistence.EntityManagerFactory;

// Imports de Java: List y Optional para lo que devuelven los métodos y Logger para registrar los errores
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;


/**
 * DAO de la tabla entradas: CRUD de entradas, cancelación y consultas de ventas con JPA (Hibernate).
 * Cada operación abre su EntityManager, hace su trabajo y lo cierra al terminar.
 * Si hay un error, lo registra en el log y devuelve 0, Optional.empty() o List.of(), sin lanzarlo.
 */
public class EntradaDao {

    // Fábrica de EntityManager: se crea una vez en el constructor y la usan los métodos del DAO
    private final EntityManagerFactory emf;

    /**
     * Crea el DAO y su EntityManagerFactory para la base de datos de esa URL.
     * Si hay un error al crear la fábrica (por ejemplo, jdbcUrl es null), la excepción sale del constructor.
     *
     * @param jdbcUrl URL JDBC de la base SQLite, por ejemplo "jdbc:sqlite:datos/cinefest.sqlite3"
     */
    public EntradaDao(String jdbcUrl) {
        // OrmFactory lee persistence.xml (unidad cinefestPU) y le pone esta URL de conexión
        this.emf = OrmFactory.crear(jdbcUrl);
    }

    // Logger para registrar los errores: el README pide registrarlos dentro del DAO, sin lanzarlos
    private static final Logger LOG = Logger.getLogger(EntradaDao.class.getName());

    // Usar transacciones y rollback en operaciones de escritura.
    /**
     * Devuelve las entradas de la tabla entradas ordenadas por id.
     * Si hay un error, lo registra en el log y devuelve List.of().
     *
     * @return lista de entradas; List.of() (lista vacía) si no hay ninguna
     */
    public List<Entrada> listar() {
        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // JPQL: se escribe con la clase EntradaEntity y su atributo idEntrada, no con la tabla ni la columna
            // EntradaEntity.class: cada fila llega como una EntradaEntity; es solo lectura, no hace falta transacción
            return em.createQuery("select e from EntradaEntity e order by e.idEntrada", EntradaEntity.class)
                    // recorre las filas una a una (como el while (rs.next()) de JDBC)
                    .getResultStream()
                    // desdeEntidad: pasa cada EntradaEntity (entidad) a Entrada (modelo), que es lo que devuelve el DAO
                    .map(EntradaMapper::desdeEntidad)
                    // junta las entradas en una lista
                    .toList();
        } catch (RuntimeException ex) {
            // JPA lanza RuntimeException (no SQLException como JDBC): se registra y se devuelve la lista vacía
            LOG.warning("listar: " + ex);
            return List.of();
        }
    }

    /**
     * Busca una entrada por su id (clave primaria).
     * Si hay un error, lo registra en el log y devuelve Optional.empty().
     *
     * @param id id de la entrada; si es 0 o negativo no se busca
     * @return Optional con la entrada si existe; Optional.empty() si no existe o el id no es válido
     */
    public Optional<Entrada> buscarPorId(int id) {

        // un id 0 o negativo no existe: se devuelve vacío sin ir a la base de datos
        if (id <= 0) return Optional.empty();

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // em.find busca por clave primaria (el SELECT ... WHERE id_entrada = ? de JDBC) y da null si no está
            // Optional.ofNullable: si find da null, queda Optional.empty()
            return Optional.ofNullable(em.find(EntradaEntity.class, id))
                    // si la encontró, desdeEntidad la pasa de EntradaEntity (entidad) a Entrada (modelo)
                    .map(EntradaMapper::desdeEntidad);
        } catch (RuntimeException ex) {
            // error de base de datos: se registra y se devuelve vacío
            LOG.warning("buscarPorId: " + ex);
            return Optional.empty();
        }
    }

    /**
     * Inserta una entrada nueva y devuelve el id que le da la base de datos.
     * Si el estado viene a null se guarda como PAGADA.
     * Si hay un error (por ejemplo, la sesión o el espectador no existen), hace rollback, lo registra y devuelve 0.
     *
     * @param dato entrada que se inserta; sesión, espectador y fecha de compra son obligatorios;
     *             el estado, si viene, debe ser PAGADA, USADA o CANCELADA
     * @return el id generado (mayor que 0) si se inserta; 0 si el dato no es válido o hay un error
     */
    public int insertar(Entrada dato) {

        // validación: sin datos obligatorios, o con un estado que no sea PAGADA, USADA o CANCELADA, devuelve 0
        // el estado puede ser null (entonces se pone PAGADA más abajo), por eso se comprueba con != null
        if (dato == null || dato.getIdSesion() == null || dato.getIdEspectador() == null
                || dato.getFechaCompra() == null || dato.getFechaCompra().isBlank()
                || (dato.getEstado() != null && !"PAGADA".equals(dato.getEstado()) && !"USADA".equals(dato.getEstado()) && !"CANCELADA".equals(dato.getEstado()))) return 0;
        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // es una escritura: hace falta transacción (en JDBC era setAutoCommit(false) y luego commit o rollback)
            var tx = em.getTransaction();
            try {
                // empieza la transacción: hasta el commit no se guarda nada
                tx.begin();
                // aEntidad: pasa la Entrada (modelo) a EntradaEntity (entidad), que es lo que guarda JPA
                EntradaEntity e = EntradaMapper.aEntidad(dato);
                // id a null para que lo genere la base de datos (AUTOINCREMENT); con un id puesto, persist da error
                e.setIdEntrada(null);
                // si no viene estado, se guarda como PAGADA (el valor por defecto de una entrada nueva)
                if (e.getEstado() == null) e.setEstado("PAGADA");
                // persist hace el INSERT (como el ps.executeUpdate() de JDBC)
                // si la sesión o el espectador no existen, la clave foránea lo rechaza y salta al catch
                em.persist(e);
                // commit: confirma el INSERT en la base de datos (como con.commit() en JDBC)
                tx.commit();
                // después del persist la entidad ya tiene el id generado: se devuelve (mayor que 0)
                return e.getIdEntrada();
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
     * Actualiza la sesión, el espectador, la fecha de compra y el estado de una entrada que ya existe.
     * Si hay un error (por ejemplo, la sesión nueva no existe), hace rollback, lo registra y devuelve 0.
     *
     * @param dato entrada con el id y los datos nuevos; todos los campos son obligatorios
     *             y el estado debe ser PAGADA, USADA o CANCELADA
     * @return 1 si se actualiza; 0 si el dato no es válido, la entrada no existe o hay un error
     */
    public int actualizar(Entrada dato) {

        // validación: hace falta el id (para saber cuál se cambia), todos los campos y un estado válido
        // aquí el estado no puede ser null: al actualizar no hay valor por defecto
        if (dato == null || dato.getIdEntrada() == null
                || dato.getIdSesion() == null || dato.getIdEspectador() == null
                || dato.getFechaCompra() == null || dato.getFechaCompra().isBlank()
                || (!"PAGADA".equals(dato.getEstado()) && !"USADA".equals(dato.getEstado()) && !"CANCELADA".equals(dato.getEstado())))
            return 0;

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // es una escritura: hace falta transacción (en JDBC era setAutoCommit(false) y luego commit o rollback)
            var tx = em.getTransaction();
            try {
                // empieza la transacción: hasta el commit no se guarda nada
                tx.begin();
                // busca la entrada guardada por su id; da null si no existe
                EntradaEntity e = em.find(EntradaEntity.class, dato.getIdEntrada());
                // si no existe, no hay nada que actualizar
                if (e == null) {
                    // rollback antes del return 0: si no, la transacción queda abierta y SQLite bloquea la base
                    // (SQLITE_BUSY), así que las siguientes escrituras devolverían 0
                    tx.rollback();
                    return 0;
                }
                // se cambian los campos de la entidad con los datos nuevos (el id no se toca)
                e.setIdSesion(dato.getIdSesion());
                e.setIdEspectador(dato.getIdEspectador());
                e.setFechaCompra(dato.getFechaCompra());
                e.setEstado(dato.getEstado());
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
     * Elimina la entrada con ese id.
     * Si hay un error, hace rollback, lo registra y devuelve 0.
     *
     * @param id id de la entrada que se elimina; si es 0 o negativo no se elimina
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
                EntradaEntity e = em.find(EntradaEntity.class, id);
                // si no existe, no hay nada que borrar
                if (e == null) {
                    // rollback antes del return 0: si no, la transacción queda abierta y SQLite bloquea la base
                    // (SQLITE_BUSY), así que las siguientes escrituras devolverían 0
                    tx.rollback();
                    return 0;
                }
                // remove marca la entidad para borrarla (el DELETE de JDBC)
                em.remove(e);
                // commit: hace el DELETE en la base de datos
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

    // C08 del README
    /**
     * Devuelve cada entrada con el nombre del espectador, el título de la película y el estado (reto C08).
     * Si hay un error, lo registra en el log y devuelve List.of().
     *
     * @return filas Object[] ordenadas por id de entrada con [0] id de la entrada (Integer), [1] nombre del
     *         espectador (String), [2] título de la película (String) y [3] estado (String); List.of() si no hay entradas
     */
    public List<Object[]> entradasConDetalles() {

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // select: columnas sueltas de varias tablas; Object[].class porque cada fila trae cuatro valores
            // el espectador sale del join con EspectadorEntity y la película hay que buscarla pasando por la sesión
            return em.createQuery("select e.idEntrada, es.nombre, p.titulo, e.estado "
                            // join con on: se unen las clases por sus atributos id (como el JOIN ... ON de SQL)
                            + "from EntradaEntity e join EspectadorEntity es on es.idEspectador = e.idEspectador "
                            // la entrada no guarda la película: se llega a ella entrada -> sesión -> película
                            + "join SesionEntity s on s.idSesion = e.idSesion "
                            + "join PeliculaEntity p on p.idPelicula = s.idPelicula "
                            // orden por id de entrada
                            + "order by e.idEntrada", Object[].class)
                    // devuelve las filas Object[] en una lista
                    .getResultList();
        } catch (RuntimeException ex) {
            // si falla la consulta: se registra el error y se devuelve la lista vacía
            LOG.warning("entradasConDetalles: " + ex);
            return List.of();
        }
    }

    // C24 del README
    /**
     * Devuelve las entradas de un espectador con el título de la película, la fecha, el estado y el precio (reto C24).
     * Si hay un error, lo registra en el log y devuelve List.of().
     *
     * @param idEspectador id del espectador; si es 0 o negativo no se busca
     * @return filas Object[] ordenadas por fecha y título con [0] título (String), [1] fecha de la sesión (String),
     *         [2] estado de la entrada (String) y [3] precio de la sesión (Double); List.of() si no tiene entradas
     */
    public List<Object[]> entradasPorEspectador(int idEspectador) {

        // un id 0 o negativo no existe: lista vacía sin ir a la base de datos
        if (idEspectador <= 0) return List.of();

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // select: título, fecha y precio vienen de otras clases, así que hay que hacer join con sesión y película
            return em.createQuery("select p.titulo, s.fecha, e.estado, s.precio "
                            // de la entrada se pasa a su sesión (fecha y precio) y de la sesión a su película (título)
                            + "from EntradaEntity e join SesionEntity s on s.idSesion = e.idSesion "
                            + "join PeliculaEntity p on p.idPelicula = s.idPelicula "
                            // se queda con las entradas de ese espectador (:id es el ? de JDBC, pero con nombre) y ordena
                            + "where e.idEspectador = :id order by s.fecha, p.titulo", Object[].class)
                    // da valor a :id (como el ps.setInt(1, idEspectador) de JDBC)
                    .setParameter("id", idEspectador)
                    // devuelve las filas Object[] en una lista
                    .getResultList();
        } catch (RuntimeException ex) {
            // si falla la consulta: se registra el error y se devuelve la lista vacía
            LOG.warning("entradasPorEspectador: " + ex);
            return List.of();
        }
    }

    // C23 del README
    /**
     * Devuelve, por cada día de compra, cuántas entradas se vendieron y cuánto se recaudó (reto C23).
     * Solo cuenta las entradas PAGADA o USADA; las canceladas no son una venta.
     * Si hay un error, lo registra en el log y devuelve List.of().
     *
     * @return filas Object[] ordenadas por fecha con [0] fecha de compra (String), [1] número de entradas (Long)
     *         y [2] suma de precios (Double); List.of() si no hay ventas
     */
    public List<Object[]> ventasPorDia() {

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // select: fecha de compra, número de entradas (count da Long) y suma de precios de sus sesiones
            return em.createQuery("select e.fechaCompra, count(e.idEntrada), sum(s.precio) "
                            // el precio está en la sesión, por eso se hace join entrada -> sesión
                            + "from EntradaEntity e join SesionEntity s on s.idSesion = e.idSesion "
                            // solo cuentan como venta las entradas pagadas o usadas (no las canceladas)
                            + "where e.estado in ('PAGADA','USADA') "
                            // "por día": group by la fecha de compra (una fila por día) y order by para ordenar
                            + "group by e.fechaCompra order by e.fechaCompra", Object[].class)
                    // devuelve las filas Object[] en una lista: [0] fecha, [1] entradas, [2] recaudación
                    .getResultList();
        } catch (RuntimeException ex) {
            // si falla la consulta: se registra el error y se devuelve la lista vacía
            LOG.warning("ventasPorDia: " + ex);
            return List.of();
        }
    }

    // Sin reto numerado en el README: método propio de este DAO
    /**
     * Cancela una entrada: cambia su estado a CANCELADA.
     * Si hay un error, hace rollback, lo registra y devuelve 0.
     *
     * @param idEntrada id de la entrada que se cancela; si es 0 o negativo no se cancela
     * @return 1 si se cancela; 0 si no existe, ya estaba cancelada, el id no es válido o hay un error
     */
    public int cancelarEntrada(int idEntrada) {

        // un id 0 o negativo no existe: devuelve 0 sin ir a la base de datos
        if (idEntrada <= 0) return 0;

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // es una escritura: hace falta transacción (en JDBC era setAutoCommit(false) y luego commit o rollback)
            var tx = em.getTransaction();
            try {
                // empieza la transacción: hasta el commit no se guarda nada
                tx.begin();
                // busca la entrada guardada por su id; da null si no existe
                EntradaEntity e = em.find(EntradaEntity.class, idEntrada);
                // si no existe, no hay nada que cancelar
                if (e == null) {
                    // rollback antes del return 0: si no, la transacción queda abierta y SQLite bloquea la base
                    // (SQLITE_BUSY), así que las siguientes escrituras devolverían 0
                    tx.rollback();
                    return 0;
                }
                // una entrada ya cancelada no se vuelve a cancelar: devuelve 0
                if ("CANCELADA".equals(e.getEstado())) {
                    // mismo rollback antes del return para no dejar la transacción abierta
                    tx.rollback();
                    return 0;
                }
                // se cambia el estado en la entidad; no hace falta persist porque ya está gestionada
                e.setEstado("CANCELADA");
                // commit: Hibernate ve el cambio y hace el UPDATE solo (en JDBC: UPDATE ... SET estado = ?)
                tx.commit();
                // 1 = se ha cancelado
                return 1;
            } catch (RuntimeException ex) {
                // deshace los cambios; solo si la transacción sigue activa (si falló el commit puede estar cerrada)
                if (tx.isActive()) tx.rollback();
                // registra el error con el nombre del método, para saber dónde falló
                LOG.warning("cancelarEntrada: " + ex);
                // 0 = no se ha cancelado
                return 0;
            }
        }
    }

}
