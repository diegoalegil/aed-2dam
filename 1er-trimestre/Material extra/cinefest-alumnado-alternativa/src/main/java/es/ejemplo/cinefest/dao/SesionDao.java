// Paquete dao: aquí están las clases que leen y escriben en la base de datos
package es.ejemplo.cinefest.dao;

// Imports del proyecto y de JPA: el modelo Sesion, OrmFactory, las entidades, el mapper y EntityManagerFactory
import es.ejemplo.cinefest.modelo.Sesion;
import es.ejemplo.cinefest.orm.config.OrmFactory;
import es.ejemplo.cinefest.orm.entity.EntradaEntity;
import es.ejemplo.cinefest.orm.entity.EspectadorEntity;
import es.ejemplo.cinefest.orm.entity.SalaEntity;
import es.ejemplo.cinefest.orm.entity.SesionEntity;
import es.ejemplo.cinefest.orm.mapper.SesionMapper;
import jakarta.persistence.EntityManagerFactory;

// Imports de Java: List y Optional para lo que devuelven los métodos y Logger para registrar los errores
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * DAO de la tabla sesiones: CRUD de sesiones, consultas de ventas y disponibilidad, y venta de entradas con JPA (Hibernate).
 * Cada operación abre su EntityManager, hace su trabajo y lo cierra al terminar.
 * Si hay un error, lo registra en el log y devuelve 0, Optional.empty() o List.of(), sin lanzarlo.
 */
public class SesionDao {

    // Fábrica de EntityManager: se crea una vez en el constructor y la usan los métodos del DAO
    private final EntityManagerFactory emf;

    /**
     * Crea el DAO y su EntityManagerFactory para la base de datos de esa URL.
     * Si hay un error al crear la fábrica (por ejemplo, jdbcUrl es null), la excepción sale del constructor.
     *
     * @param jdbcUrl URL JDBC de la base SQLite, por ejemplo "jdbc:sqlite:datos/cinefest.sqlite3"
     */
    public SesionDao(String jdbcUrl) {
        // OrmFactory lee persistence.xml (unidad cinefestPU) y le pone esta URL de conexión
        this.emf = OrmFactory.crear(jdbcUrl);
    }

    // Logger para registrar los errores: el README pide registrarlos dentro del DAO, sin lanzarlos
    private static final Logger LOG = Logger.getLogger(SesionDao.class.getName());

    // Usar transacciones y rollback en operaciones de escritura.
    /**
     * Devuelve las sesiones de la tabla sesiones ordenadas por id.
     * Si hay un error, lo registra en el log y devuelve List.of().
     *
     * @return lista de sesiones; List.of() (lista vacía) si no hay ninguna
     */
    public List<Sesion> listar() {
        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // JPQL: se escribe con la clase SesionEntity y su atributo idSesion, no con la tabla ni la columna
            // SesionEntity.class: cada fila llega como una SesionEntity; es solo lectura, no hace falta transacción
            return em.createQuery("select s from SesionEntity s order by s.idSesion", SesionEntity.class)
                    // recorre las filas una a una (como el while (rs.next()) de JDBC)
                    .getResultStream()
                    // desdeEntidad: pasa cada SesionEntity (entidad) a Sesion (modelo), que es lo que devuelve el DAO
                    .map(SesionMapper::desdeEntidad)
                    // junta las sesiones en una lista
                    .toList();
        } catch (RuntimeException ex) {
            // JPA lanza RuntimeException (no SQLException como JDBC): se registra y se devuelve la lista vacía
            LOG.warning("listar: " + ex);
            return List.of();
        }
    }

    /**
     * Busca una sesión por su id (clave primaria).
     * Si hay un error, lo registra en el log y devuelve Optional.empty().
     *
     * @param id id de la sesión; si es 0 o negativo no se busca
     * @return Optional con la sesión si existe; Optional.empty() si no existe o el id no es válido
     */
    public Optional<Sesion> buscarPorId(int id) {

        // un id 0 o negativo no existe: se devuelve vacío sin ir a la base de datos
        if (id <= 0) return Optional.empty();

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // em.find busca por clave primaria (el SELECT ... WHERE id_sesion = ? de JDBC) y da null si no está
            // Optional.ofNullable: si find da null, queda Optional.empty()
            return Optional.ofNullable(em.find(SesionEntity.class, id))
                    // si la encontró, desdeEntidad la pasa de SesionEntity (entidad) a Sesion (modelo)
                    .map(SesionMapper::desdeEntidad);
        } catch (RuntimeException ex) {
            // error de base de datos: se registra y se devuelve vacío
            LOG.warning("buscarPorId: " + ex);
            return Optional.empty();
        }
    }

    /**
     * Inserta una sesión nueva y devuelve el id que le da la base de datos.
     * Si el estado viene a null se guarda como PROGRAMADA.
     * Si hay un error (por ejemplo, la película o la sala no existen), hace rollback, lo registra y devuelve 0.
     *
     * @param dato sesión que se inserta; película, sala, fecha, hora y precio son obligatorios (precio no negativo);
     *             el estado, si viene, debe ser PROGRAMADA, CANCELADA o FINALIZADA
     * @return el id generado (mayor que 0) si se inserta; 0 si el dato no es válido o hay un error
     */
    public int insertar(Sesion dato) {

        // validación: sin datos obligatorios, con precio negativo o con un estado que no sea válido, devuelve 0
        // el estado puede ser null (entonces se pone PROGRAMADA más abajo), por eso se comprueba con != null
        if (dato == null || dato.getIdPelicula() == null || dato.getIdSala() == null
                || dato.getFecha() == null || dato.getFecha().isBlank()
                || dato.getHora() == null || dato.getHora().isBlank()
                || dato.getPrecio() == null || dato.getPrecio() < 0
                || (dato.getEstado() != null && !"PROGRAMADA".equals(dato.getEstado()) && !"CANCELADA".equals(dato.getEstado()) && !"FINALIZADA".equals(dato.getEstado()))) return 0;
        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // es una escritura: hace falta transacción (en JDBC era setAutoCommit(false) y luego commit o rollback)
            var tx = em.getTransaction();
            try {
                // empieza la transacción: hasta el commit no se guarda nada
                tx.begin();
                // aEntidad: pasa la Sesion (modelo) a SesionEntity (entidad), que es lo que guarda JPA
                SesionEntity e = SesionMapper.aEntidad(dato);
                // id a null para que lo genere la base de datos (AUTOINCREMENT); con un id puesto, persist da error
                e.setIdSesion(null);
                // si no viene estado, se guarda como PROGRAMADA (el valor por defecto de una sesión nueva)
                if (e.getEstado() == null) e.setEstado("PROGRAMADA");
                // persist hace el INSERT (como el ps.executeUpdate() de JDBC)
                // si la película o la sala no existen, la clave foránea lo rechaza y salta al catch
                em.persist(e);
                // commit: confirma el INSERT en la base de datos (como con.commit() en JDBC)
                tx.commit();
                // después del persist la entidad ya tiene el id generado: se devuelve (mayor que 0)
                return e.getIdSesion();
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
     * Actualiza la película, la sala, la fecha, la hora, el precio y el estado de una sesión que ya existe.
     * Si hay un error (por ejemplo, la sala nueva no existe), hace rollback, lo registra y devuelve 0.
     *
     * @param dato sesión con el id y los datos nuevos; todos los campos son obligatorios (precio no negativo)
     *             y el estado debe ser PROGRAMADA, CANCELADA o FINALIZADA
     * @return 1 si se actualiza; 0 si el dato no es válido, la sesión no existe o hay un error
     */
    public int actualizar(Sesion dato) {

        // validación: hace falta el id (para saber cuál se cambia), todos los campos y un estado válido
        // aquí el estado no puede ser null: al actualizar no hay valor por defecto
        if (dato == null || dato.getIdSesion() == null
                || dato.getIdPelicula() == null || dato.getIdSala() == null
                || dato.getFecha() == null || dato.getFecha().isBlank()
                || dato.getHora() == null || dato.getHora().isBlank()
                || dato.getPrecio() == null || dato.getPrecio() < 0
                || (!"PROGRAMADA".equals(dato.getEstado()) && !"CANCELADA".equals(dato.getEstado()) && !"FINALIZADA".equals(dato.getEstado())))
            return 0;

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // es una escritura: hace falta transacción (en JDBC era setAutoCommit(false) y luego commit o rollback)
            var tx = em.getTransaction();
            try {
                // empieza la transacción: hasta el commit no se guarda nada
                tx.begin();
                // busca la sesión guardada por su id; da null si no existe
                SesionEntity e = em.find(SesionEntity.class, dato.getIdSesion());
                // si no existe, no hay nada que actualizar
                if (e == null) {
                    // rollback antes del return 0: si no, la transacción queda abierta y SQLite bloquea la base
                    // (SQLITE_BUSY), así que las siguientes escrituras devolverían 0
                    tx.rollback();
                    return 0;
                }
                // se cambian los campos de la entidad con los datos nuevos (el id no se toca)
                e.setIdPelicula(dato.getIdPelicula());
                e.setIdSala(dato.getIdSala());
                e.setFecha(dato.getFecha());
                e.setHora(dato.getHora());
                e.setPrecio(dato.getPrecio());
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
     * Elimina la sesión con ese id.
     * Si hay un error (por ejemplo, la sesión tiene entradas), hace rollback, lo registra y devuelve 0.
     *
     * @param id id de la sesión que se elimina; si es 0 o negativo no se elimina
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
                SesionEntity e = em.find(SesionEntity.class, id);
                // si no existe, no hay nada que borrar
                if (e == null) {
                    // rollback antes del return 0: si no, la transacción queda abierta y SQLite bloquea la base
                    // (SQLITE_BUSY), así que las siguientes escrituras devolverían 0
                    tx.rollback();
                    return 0;
                }
                // remove marca la entidad para borrarla (el DELETE de JDBC)
                em.remove(e);
                // commit: hace el DELETE; si la sesión tiene entradas, la clave foránea lo impide y salta al catch
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

    // C07 del README
    /**
     * Devuelve cada sesión con el título de la película, la sede, la sala, la fecha y la hora (reto C07).
     * Si hay un error, lo registra en el log y devuelve List.of().
     *
     * @return filas Object[] ordenadas por id de sesión con [0] id de la sesión (Integer), [1] título de la película,
     *         [2] nombre de la sede, [3] nombre de la sala, [4] fecha y [5] hora (todos String);
     *         List.of() si no hay sesiones
     */
    public List<Object[]> sesionesConDetalles() {

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // select: columnas sueltas de cuatro tablas; Object[].class porque cada fila trae seis valores
            return em.createQuery("select s.idSesion, p.titulo, se.nombre, sa.nombre, s.fecha, s.hora "
                            // join con on: se unen las clases por sus atributos id (como el JOIN ... ON de SQL)
                            // la película sale directamente de la sesión
                            + "from SesionEntity s join PeliculaEntity p on p.idPelicula = s.idPelicula "
                            // la sala sale de la sesión
                            + "join SalaEntity sa on sa.idSala = s.idSala "
                            // la sede no está en la sesión: se llega a ella sesión -> sala -> sede
                            + "join SedeEntity se on se.idSede = sa.idSede "
                            // orden por id de sesión
                            + "order by s.idSesion", Object[].class)
                    // devuelve las filas Object[] en una lista
                    .getResultList();
        } catch (RuntimeException ex) {
            // si falla la consulta: se registra el error y se devuelve la lista vacía
            LOG.warning("sesionesConDetalles: " + ex);
            return List.of();
        }
    }

    // C04 del README
    /**
     * Devuelve las sesiones de una fecha con su hora, id y precio (reto C04).
     * Si hay un error, lo registra en el log y devuelve List.of().
     *
     * @param fecha fecha que se busca, en el mismo formato que se guarda (por ejemplo "2026-11-12"); si es null o está vacía no se busca
     * @return filas Object[] ordenadas por hora y id con [0] fecha (String), [1] hora (String), [2] id de la sesión (Integer)
     *         y [3] precio (Double); List.of() si no hay sesiones ese día
     */
    public List<Object[]> sesionesPorFecha(String fecha) {

        // una fecha null o vacía no encuentra nada: lista vacía sin ir a la base de datos
        if (fecha == null || fecha.isBlank()) return List.of();

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // select: columnas sueltas (no la entidad entera); Object[].class porque cada fila trae cuatro valores
            return em.createQuery("select s.fecha, s.hora, s.idSesion, s.precio from SesionEntity s "
                            // se queda con las sesiones de esa fecha (:fecha es el ? de JDBC, pero con nombre) y ordena
                            + "where s.fecha = :fecha order by s.fecha, s.hora, s.idSesion", Object[].class)
                    // da valor a :fecha (como el ps.setString(1, fecha) de JDBC)
                    .setParameter("fecha", fecha)
                    // devuelve las filas Object[] en una lista
                    .getResultList();
        } catch (RuntimeException ex) {
            // si falla la consulta: se registra el error y se devuelve la lista vacía
            LOG.warning("sesionesPorFecha: " + ex);
            return List.of();
        }
    }

    // C17 del README
    /**
     * Devuelve las sesiones programadas que todavía no han vendido ninguna entrada (reto C17).
     * Una entrada vendida es la que está PAGADA o USADA; las canceladas no cuentan.
     * Si hay un error, lo registra en el log y devuelve List.of().
     *
     * @return filas Object[] ordenadas por id de sesión con [0] id de la sesión (Integer), [1] título de la película
     *         (String) y [2] fecha (String); List.of() si todas las sesiones tienen ventas
     */
    public List<Object[]> sesionesSinVentas() {

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // select: id de la sesión, título de la película y fecha
            return em.createQuery("select s.idSesion, p.titulo, s.fecha "
                            // join con la película para sacar el título
                            + "from SesionEntity s join PeliculaEntity p on p.idPelicula = s.idPelicula "
                            // solo sesiones programadas; "sin ventas" = not exists: no existe ninguna entrada vendida
                            // (el not exists es la forma de decir "ninguna"; un join normal no encuentra lo que no existe)
                            + "where s.estado = 'PROGRAMADA' and not exists "
                            // subconsulta: entradas de esa misma sesión que estén pagadas o usadas
                            + "(select e from EntradaEntity e where e.idSesion = s.idSesion and e.estado in ('PAGADA','USADA')) "
                            // orden por id de sesión
                            + "order by s.idSesion", Object[].class)
                    // devuelve las filas Object[] en una lista
                    .getResultList();
        } catch (RuntimeException ex) {
            // si falla la consulta: se registra el error y se devuelve la lista vacía
            LOG.warning("sesionesSinVentas: " + ex);
            return List.of();
        }
    }

    // C15 del README
    /**
     * Devuelve, por cada sesión, el aforo de su sala, las entradas vendidas y las plazas libres (reto C15).
     * Solo cuentan como vendidas las entradas PAGADA o USADA; las sesiones sin ventas salen con 0 vendidas.
     * Si hay un error, lo registra en el log y devuelve List.of().
     *
     * @return filas Object[] ordenadas por id de sesión con [0] id de la sesión (Integer), [1] aforo (Integer),
     *         [2] entradas vendidas (Long) y [3] plazas libres (Long); List.of() si no hay sesiones
     */
    public List<Object[]> disponibilidadSesiones() {

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // select: id, aforo, entradas vendidas (count da Long) y libres (aforo menos vendidas)
            return em.createQuery("select s.idSesion, sa.aforo, count(e.idEntrada), sa.aforo - count(e.idEntrada) "
                            // join con la sala para sacar el aforo
                            + "from SesionEntity s join SalaEntity sa on sa.idSala = s.idSala "
                            // left join: salen también las sesiones sin entradas (count(e.idEntrada) da 0, no se pierden)
                            // el filtro del estado va en el on y no en un where: así el left join no pierde esas sesiones
                            + "left join EntradaEntity e on e.idSesion = s.idSesion and e.estado in ('PAGADA','USADA') "
                            // "por sesión": group by; el aforo también va en el group by porque no es una función de agregado
                            + "group by s.idSesion, sa.aforo order by s.idSesion", Object[].class)
                    // devuelve las filas Object[] en una lista
                    .getResultList();
        } catch (RuntimeException ex) {
            // si falla la consulta: se registra el error y se devuelve la lista vacía
            LOG.warning("disponibilidadSesiones: " + ex);
            return List.of();
        }
    }

    // C18 del README
    /**
     * Devuelve las sesiones agotadas: aquellas con tantas entradas vendidas como aforo tiene la sala (reto C18).
     * Solo cuentan como vendidas las entradas PAGADA o USADA.
     * Si hay un error, lo registra en el log y devuelve List.of().
     *
     * @return filas Object[] ordenadas por id de sesión con [0] id de la sesión (Integer), [1] título de la película
     *         (String) y [2] aforo de la sala (Integer); List.of() si no hay ninguna agotada
     */
    public List<Object[]> sesionesAgotadas() {

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // select: id de la sesión, título de la película y aforo de la sala
            return em.createQuery("select s.idSesion, p.titulo, sa.aforo "
                            // join con la película (título) y con la sala (aforo)
                            + "from SesionEntity s join PeliculaEntity p on p.idPelicula = s.idPelicula "
                            + "join SalaEntity sa on sa.idSala = s.idSala "
                            // join normal con las entradas: una sesión sin entradas no puede estar agotada
                            + "join EntradaEntity e on e.idSesion = s.idSesion "
                            // where filtra las filas antes de agrupar: solo entradas vendidas (pagadas o usadas)
                            + "where e.estado in ('PAGADA','USADA') "
                            // having filtra después de agrupar (con funciones de agregado como count, el where no vale)
                            // agotada = entradas vendidas >= aforo de la sala
                            + "group by s.idSesion, p.titulo, sa.aforo having count(e.idEntrada) >= sa.aforo "
                            // orden por id de sesión
                            + "order by s.idSesion", Object[].class)
                    // devuelve las filas Object[] en una lista
                    .getResultList();
        } catch (RuntimeException ex) {
            // si falla la consulta: se registra el error y se devuelve la lista vacía
            LOG.warning("sesionesAgotadas: " + ex);
            return List.of();
        }
    }

    // C21 del README
    /**
     * Devuelve el ranking de las 3 películas que más han recaudado, con sus entradas vendidas (reto C21).
     * Solo cuentan las entradas PAGADA o USADA; cada entrada suma el precio de su sesión.
     * Si hay un error, lo registra en el log y devuelve List.of().
     *
     * @return como máximo 3 filas Object[] de mayor a menor recaudación (y por título si empatan) con
     *         [0] título (String), [1] entradas vendidas (Long) y [2] recaudación (Double); List.of() si no hay ventas
     */
    public List<Object[]> rankingRecaudacion() {

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // select: título, número de entradas (count da Long) y recaudación (suma de los precios)
            return em.createQuery("select p.titulo, count(e.idEntrada), sum(s.precio) "
                            // de la película se llega a sus sesiones (precio) y de ellas a sus entradas
                            + "from PeliculaEntity p join SesionEntity s on s.idPelicula = p.idPelicula "
                            + "join EntradaEntity e on e.idSesion = s.idSesion "
                            // solo cuentan las entradas vendidas (pagadas o usadas)
                            + "where e.estado in ('PAGADA','USADA') "
                            // una fila por película; order by sum(...) desc pone primero la que más recauda
                            + "group by p.idPelicula, p.titulo order by sum(s.precio) desc, p.titulo", Object[].class)
                    // JPQL no tiene LIMIT: setMaxResults(3) se queda con las 3 primeras filas (el "top 3")
                    .setMaxResults(3)
                    // devuelve las filas Object[] en una lista
                    .getResultList();
        } catch (RuntimeException ex) {
            // si falla la consulta: se registra el error y se devuelve la lista vacía
            LOG.warning("rankingRecaudacion: " + ex);
            return List.of();
        }
    }

    // C22 del README
    /**
     * Devuelve lo recaudado por cada sede, también las sedes que no han vendido nada, que salen con 0 (reto C22).
     * Solo cuentan las entradas PAGADA o USADA; cada entrada suma el precio de su sesión.
     * Si hay un error, lo registra en el log y devuelve List.of().
     *
     * @return filas Object[] de mayor a menor recaudación (y por nombre si empatan) con [0] nombre de la sede (String)
     *         y [1] recaudación (Double, o 0 si no ha vendido); List.of() si no hay sedes
     */
    public List<Object[]> ingresosPorSede() {

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // select: nombre de la sede y su recaudación
            // el case when suma el precio solo si hay entrada (e.idEntrada no es null): evita sumar el precio de sesiones sin ventas
            // coalesce(..., 0): una sede sin ventas da sum null; coalesce lo cambia por 0
            return em.createQuery("select se.nombre, coalesce(sum(case when e.idEntrada is not null then s.precio end), 0) "
                            // todo con left join para no perder las sedes sin salas, las salas sin sesiones ni las sesiones sin entradas
                            // se baja de la sede a sus salas, de ellas a sus sesiones y de ellas a sus entradas
                            + "from SedeEntity se left join SalaEntity sa on sa.idSede = se.idSede "
                            + "left join SesionEntity s on s.idSala = sa.idSala "
                            // el filtro del estado va en el on (no en el where) para que el left join no pierda filas
                            + "left join EntradaEntity e on e.idSesion = s.idSesion and e.estado in ('PAGADA','USADA') "
                            // una fila por sede
                            + "group by se.idSede, se.nombre "
                            // mayor recaudación primero; en JPQL se repite la expresión en el order by (no vale el alias)
                            + "order by coalesce(sum(case when e.idEntrada is not null then s.precio end), 0) desc, se.nombre", Object[].class)
                    // devuelve las filas Object[] en una lista
                    .getResultList();
        } catch (RuntimeException ex) {
            // si falla la consulta: se registra el error y se devuelve la lista vacía
            LOG.warning("ingresosPorSede: " + ex);
            return List.of();
        }
    }

    // C25 del README
    /**
     * Devuelve el informe diario: por cada fecha, las sesiones programadas, las entradas vendidas y lo recaudado (reto C25).
     * Solo cuentan como vendidas las entradas PAGADA o USADA.
     * Si hay un error, lo registra en el log y devuelve List.of().
     *
     * @return filas Object[] ordenadas por fecha con [0] fecha (String), [1] sesiones programadas (Long),
     *         [2] entradas vendidas (Long) y [3] recaudación (Double, o 0 si no hay ventas); List.of() si no hay sesiones
     */
    public List<Object[]> informeDiario() {

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // select: la fecha y tres cantidades que se calculan a la vez
            // count(distinct case ...): cuenta cada sesión programada una sola vez; sin distinct se repetiría por cada entrada
            return em.createQuery("select s.fecha, count(distinct case when s.estado = 'PROGRAMADA' then s.idSesion end), "
                            // count(e.idEntrada): entradas vendidas; coalesce(sum(...), 0): recaudación, 0 si no hay ventas
                            + "count(e.idEntrada), coalesce(sum(case when e.idEntrada is not null then s.precio end), 0) "
                            + "from SesionEntity s "
                            // left join: las fechas con sesiones pero sin ventas también salen (con 0 entradas)
                            // el filtro del estado va en el on (no en el where) para que el left join no pierda filas
                            + "left join EntradaEntity e on e.idSesion = s.idSesion and e.estado in ('PAGADA','USADA') "
                            // "por día": una fila por fecha, ordenada por fecha
                            + "group by s.fecha order by s.fecha", Object[].class)
                    // devuelve las filas Object[] en una lista
                    .getResultList();
        } catch (RuntimeException ex) {
            // si falla la consulta: se registra el error y se devuelve la lista vacía
            LOG.warning("informeDiario: " + ex);
            return List.of();
        }
    }

    // C29 del README
    /**
     * Vende una entrada de una sesión a un espectador, con todas las comprobaciones dentro de una transacción (reto C29).
     * No se vende si la sesión no existe o no está PROGRAMADA, si el espectador no existe o no está activo,
     * si ya tiene entrada para esa sesión o si la sala está llena.
     * Si hay un error, hace rollback, lo registra y devuelve 0.
     *
     * @param idSesion id de la sesión; debe ser mayor que 0
     * @param idEspectador id del espectador; debe ser mayor que 0
     * @param fechaCompra fecha de la compra; no puede ser null ni estar vacía
     * @return 1 si se vende la entrada (se guarda como PAGADA); 0 si algún dato no es válido, no se cumple
     *         alguna comprobación o hay un error
     */
    public int venderEntrada(int idSesion, int idEspectador, String fechaCompra) {

        // validación: ids 0 o negativos o fecha vacía no valen: devuelve 0 sin ir a la base de datos
        if (idSesion <= 0 || idEspectador <= 0 || fechaCompra == null || fechaCompra.isBlank()) return 0;

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // es una escritura con varias comprobaciones: todo va en una sola transacción
            var tx = em.getTransaction();
            try {
                // empieza la transacción: hasta el commit no se guarda nada
                tx.begin();
                // update "vacío" (estado = estado): no cambia nada, pero en SQLite toma el bloqueo de escritura desde ya
                // así dos ventas a la vez no pueden vender la última plaza de la sala a la vez
                em.createQuery("update SesionEntity s set s.estado = s.estado where s.idSesion = :id")
                        // da valor a :id (como el ps.setInt de JDBC)
                        .setParameter("id", idSesion)
                        // executeUpdate lanza el UPDATE (como el ps.executeUpdate() de JDBC)
                        .executeUpdate();
                // comprobación 1: la sesión existe (find da null si no) y está PROGRAMADA
                SesionEntity s = em.find(SesionEntity.class, idSesion);
                if (s == null || !"PROGRAMADA".equals(s.getEstado())) {
                    // rollback antes del return 0: si no, la transacción queda abierta y SQLite bloquea la base
                    // (SQLITE_BUSY), así que las siguientes escrituras devolverían 0
                    tx.rollback();
                    return 0;
                }
                // comprobación 2: el espectador existe y está activo (activo vale 1)
                EspectadorEntity es = em.find(EspectadorEntity.class, idEspectador);
                if (es == null || es.getActivo() != 1) {
                    // mismo rollback antes del return para no dejar la transacción abierta
                    tx.rollback();
                    return 0;
                }
                // comprobación 3: el espectador no tiene ya una entrada para esta sesión
                // count da Long (por eso la variable es Long y no int)
                Long ya = em.createQuery("select count(e) from EntradaEntity e "
                                + "where e.idSesion = :sesion and e.idEspectador = :espectador", Long.class)
                        // da valor a :sesion y :espectador (como los ps.setInt de JDBC)
                        .setParameter("sesion", idSesion)
                        .setParameter("espectador", idEspectador)
                        // getSingleResult: la consulta devuelve una sola fila (el count)
                        .getSingleResult();
                if (ya > 0) {
                    // mismo rollback antes del return para no dejar la transacción abierta
                    tx.rollback();
                    return 0;
                }
                // comprobación 4: quedan plazas; el aforo está en la sala de la sesión
                SalaEntity sa = em.find(SalaEntity.class, s.getIdSala());
                // entradas ya vendidas de esa sesión (pagadas o usadas; las canceladas no ocupan plaza)
                Long vendidas = em.createQuery("select count(e) from EntradaEntity e "
                                + "where e.idSesion = :sesion and e.estado in ('PAGADA','USADA')", Long.class)
                        .setParameter("sesion", idSesion)
                        .getSingleResult();
                // si ya hay tantas entradas como aforo, la sala está llena
                if (vendidas >= sa.getAforo()) {
                    // mismo rollback antes del return para no dejar la transacción abierta
                    tx.rollback();
                    return 0;
                }
                // pasadas todas las comprobaciones: se crea la entrada nueva (sin id: lo genera la base de datos)
                EntradaEntity e = new EntradaEntity();
                e.setIdSesion(idSesion);
                e.setIdEspectador(idEspectador);
                e.setFechaCompra(fechaCompra);
                // una entrada recién vendida queda como PAGADA
                e.setEstado("PAGADA");
                // persist hace el INSERT (como el ps.executeUpdate() de JDBC)
                em.persist(e);
                // commit: confirma el INSERT en la base de datos (como con.commit() en JDBC)
                tx.commit();
                // 1 = entrada vendida
                return 1;
            } catch (RuntimeException ex) {
                // deshace los cambios; solo si la transacción sigue activa (si falló el commit puede estar cerrada)
                if (tx.isActive()) tx.rollback();
                // registra el error con el nombre del método, para saber dónde falló
                LOG.warning("venderEntrada: " + ex);
                // 0 = no se ha vendido
                return 0;
            }
        }
    }

}
