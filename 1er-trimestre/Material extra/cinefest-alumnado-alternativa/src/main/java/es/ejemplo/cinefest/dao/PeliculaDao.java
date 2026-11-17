// Paquete dao: aquí están las clases que leen y escriben en la base de datos
package es.ejemplo.cinefest.dao;

// Imports del proyecto y de JPA: el modelo Pelicula, OrmFactory, la entidad, el mapper y EntityManagerFactory
import es.ejemplo.cinefest.modelo.Pelicula;
import es.ejemplo.cinefest.orm.config.OrmFactory;
import es.ejemplo.cinefest.orm.entity.PeliculaEntity;
import es.ejemplo.cinefest.orm.mapper.PeliculaMapper;
import jakarta.persistence.EntityManagerFactory;

// Imports de Java: List y Optional para lo que devuelven los métodos y Logger para registrar los errores
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;


/**
 * DAO de la tabla peliculas: CRUD y consultas de películas con JPA (Hibernate).
 * Cada operación abre su EntityManager, hace su trabajo y lo cierra al terminar.
 * Si hay un error, lo registra en el log y devuelve 0, Optional.empty() o List.of(), sin lanzarlo.
 */
public class PeliculaDao {

    // Fábrica de EntityManager: se crea una vez en el constructor y la usan los métodos del DAO
    private final EntityManagerFactory emf;

    /**
     * Crea el DAO y su EntityManagerFactory para la base de datos de esa URL.
     * Si hay un error al crear la fábrica (por ejemplo, jdbcUrl es null), la excepción sale del constructor.
     *
     * @param jdbcUrl URL JDBC de la base SQLite, por ejemplo "jdbc:sqlite:datos/cinefest.sqlite3"
     */
    public PeliculaDao(String jdbcUrl) {
        // OrmFactory lee persistence.xml (unidad cinefestPU) y le pone esta URL de conexión
        this.emf = OrmFactory.crear(jdbcUrl);
    }

    // Logger para registrar los errores: el README pide registrarlos dentro del DAO, sin lanzarlos
    private static final Logger LOG = Logger.getLogger(PeliculaDao.class.getName());

    // Usar transacciones y rollback en operaciones de escritura.
    /**
     * Devuelve las películas de la tabla peliculas ordenadas por id.
     * Si hay un error, lo registra en el log y devuelve List.of().
     *
     * @return lista de películas; List.of() (lista vacía) si no hay ninguna
     */
    public List<Pelicula> listar() {
        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // JPQL: se escribe con la clase PeliculaEntity y su atributo idPelicula, no con la tabla ni la columna
            // PeliculaEntity.class: cada fila llega como una PeliculaEntity; es solo lectura, no hace falta transacción
            return em.createQuery("select p from PeliculaEntity p order by p.idPelicula", PeliculaEntity.class)
                    // recorre las filas una a una (como el while (rs.next()) de JDBC)
                    .getResultStream()
                    // desdeEntidad: pasa cada PeliculaEntity (entidad) a Pelicula (modelo), que es lo que devuelve el DAO
                    .map(PeliculaMapper::desdeEntidad)
                    // junta las películas en una lista
                    .toList();
        } catch (RuntimeException ex) {
            // JPA lanza RuntimeException (no SQLException como JDBC): se registra y se devuelve la lista vacía
            LOG.warning("listar: " + ex);
            return List.of();
        }
    }

    /**
     * Busca una película por su id (clave primaria).
     * Si hay un error, lo registra en el log y devuelve Optional.empty().
     *
     * @param id id de la película; si es 0 o negativo no se busca
     * @return Optional con la película si existe; Optional.empty() si no existe o el id no es válido
     */
    public Optional<Pelicula> buscarPorId(int id) {

        // un id 0 o negativo no existe: se devuelve vacío sin ir a la base de datos
        if (id <= 0) return Optional.empty();

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // em.find busca por clave primaria (el SELECT ... WHERE id_pelicula = ? de JDBC) y da null si no está
            // Optional.ofNullable: si find da null, queda Optional.empty()
            return Optional.ofNullable(em.find(PeliculaEntity.class, id))
                    // si la encontró, desdeEntidad la pasa de PeliculaEntity (entidad) a Pelicula (modelo)
                    .map(PeliculaMapper::desdeEntidad);
        } catch (RuntimeException ex) {
            // error de base de datos: se registra y se devuelve vacío
            LOG.warning("buscarPorId: " + ex);
            return Optional.empty();
        }
    }

    /**
     * Inserta una película nueva y devuelve el id que le da la base de datos.
     * Si hay un error (por ejemplo, la base de datos rechaza la fila), hace rollback, lo registra y devuelve 0.
     *
     * @param dato película que se inserta; título, género y país no pueden estar vacíos
     * @return el id generado (mayor que 0) si se inserta; 0 si el dato no es válido o hay un error
     */
    public int insertar(Pelicula dato) {

        // validación: sin película, o con título, género o país vacíos, no se inserta y devuelve 0
        // duración y año no se miran aquí: si vienen null, la base de datos rechaza el INSERT (NOT NULL)
        if (dato == null || dato.getTitulo() == null || dato.getTitulo().isBlank()
                || dato.getGenero() == null || dato.getGenero().isBlank()
                || dato.getPais() == null || dato.getPais().isBlank()) return 0;
        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // es una escritura: hace falta transacción (en JDBC era setAutoCommit(false) y luego commit o rollback)
            var tx = em.getTransaction();
            try {
                // empieza la transacción: hasta el commit no se guarda nada
                tx.begin();
                // aEntidad: pasa la Pelicula (modelo) a PeliculaEntity (entidad), que es lo que guarda JPA
                PeliculaEntity e = PeliculaMapper.aEntidad(dato);
                // id a null para que lo genere la base de datos (AUTOINCREMENT); con un id puesto, persist da error
                e.setIdPelicula(null);
                // persist hace el INSERT (como el ps.executeUpdate() de JDBC)
                em.persist(e);
                // commit: confirma el INSERT en la base de datos (como con.commit() en JDBC)
                tx.commit();
                // después del persist la entidad ya tiene el id generado: se devuelve (mayor que 0)
                return e.getIdPelicula();
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
     * Actualiza los datos de una película que ya existe, buscándola por su id.
     * Si hay un error (por ejemplo, la base de datos rechaza los datos), hace rollback, lo registra y devuelve 0.
     *
     * @param dato película con el id y los datos nuevos; id, título, género y país son obligatorios
     * @return 1 si se actualiza; 0 si el dato no es válido, la película no existe o hay un error
     */
    public int actualizar(Pelicula dato) {

        // validación: hace falta el id (para saber cuál se cambia) y que título, género y país no estén vacíos
        if (dato == null || dato.getIdPelicula() == null
                || dato.getTitulo() == null || dato.getTitulo().isBlank()
                || dato.getGenero() == null || dato.getGenero().isBlank()
                || dato.getPais() == null || dato.getPais().isBlank())
            return 0;

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // es una escritura: hace falta transacción (en JDBC era setAutoCommit(false) y luego commit o rollback)
            var tx = em.getTransaction();
            try {
                // empieza la transacción: hasta el commit no se guarda nada
                tx.begin();
                // busca la película guardada por su id; da null si no existe
                PeliculaEntity e = em.find(PeliculaEntity.class, dato.getIdPelicula());
                // si no existe, no hay nada que actualizar
                if (e == null) {
                    // rollback antes del return 0: si no, la transacción queda abierta y SQLite bloquea la base
                    // (SQLITE_BUSY), así que las siguientes escrituras devolverían 0
                    tx.rollback();
                    return 0;
                }
                // se cambian los campos de la entidad con los datos nuevos (el id no se toca)
                e.setTitulo(dato.getTitulo());
                e.setGenero(dato.getGenero());
                e.setDuracionMin(dato.getDuracionMin());
                e.setPais(dato.getPais());
                e.setAnio(dato.getAnio());
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
     * Elimina la película con ese id.
     * Si hay un error (por ejemplo, la película tiene sesiones o valoraciones), hace rollback, lo registra y devuelve 0.
     *
     * @param id id de la película que se elimina; si es 0 o negativo no se elimina
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
                PeliculaEntity e = em.find(PeliculaEntity.class, id);
                // si no existe, no hay nada que borrar
                if (e == null) {
                    // rollback antes del return 0: si no, la transacción queda abierta y SQLite bloquea la base
                    // (SQLITE_BUSY), así que las siguientes escrituras devolverían 0
                    tx.rollback();
                    return 0;
                }
                // remove marca la entidad para borrarla (el DELETE de JDBC)
                em.remove(e);
                // commit: hace el DELETE; si tiene sesiones o valoraciones, la clave foránea lo impide y salta al catch
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

    // C03 del README
    /**
     * Devuelve las películas de un género, ordenadas por título (reto C03 del README).
     * Si hay un error, lo registra en el log y devuelve List.of().
     *
     * @param genero género que se busca, por ejemplo "Drama"
     * @return lista de películas de ese género; List.of() si no hay ninguna o el género está vacío
     */
    public List<Pelicula> buscarPorGenero(String genero) {

        // sin género no hay nada que buscar: lista vacía
        if (genero == null || genero.isBlank()) return List.of();

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // "Parámetros: genero" del README: where p.genero = :genero (:genero es el ? de JDBC, pero con nombre)
            // "Orden: titulo ASC" del README: order by p.titulo
            return em.createQuery("select p from PeliculaEntity p where p.genero = :genero order by p.titulo", PeliculaEntity.class)
                    // da valor a :genero (como el ps.setString(1, genero) de JDBC)
                    .setParameter("genero", genero)
                    // recorre las filas una a una (como el while (rs.next()) de JDBC)
                    .getResultStream()
                    // desdeEntidad: pasa cada PeliculaEntity (entidad) a Pelicula (modelo)
                    .map(PeliculaMapper::desdeEntidad)
                    // junta las películas en una lista
                    .toList();
        } catch (RuntimeException ex) {
            // si falla la consulta: se registra el error y se devuelve la lista vacía
            LOG.warning("buscarPorGenero: " + ex);
            return List.of();
        }
    }

    // Sin reto en el README: las de ese año o posteriores
    /**
     * Devuelve las películas de ese año o posteriores, ordenadas por id.
     * Si hay un error, lo registra en el log y devuelve List.of().
     *
     * @param anio año desde el que se buscan, por ejemplo 2025 (las de 2025 también salen)
     * @return lista de películas con año mayor o igual que el indicado; List.of() si no hay ninguna
     */
    public List<Pelicula> peliculasDesdeAnio(int anio) {

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // "ese año o posteriores": p.anio >= :anio (con el igual, entra también ese año); ordenadas por id
            return em.createQuery("select p from PeliculaEntity p where p.anio >= :anio order by p.idPelicula", PeliculaEntity.class)
                    // da valor a :anio (como el ps.setInt(1, anio) de JDBC)
                    .setParameter("anio", anio)
                    // recorre las filas una a una (como el while (rs.next()) de JDBC)
                    .getResultStream()
                    // desdeEntidad: pasa cada PeliculaEntity (entidad) a Pelicula (modelo)
                    .map(PeliculaMapper::desdeEntidad)
                    // junta las películas en una lista
                    .toList();
        } catch (RuntimeException ex) {
            // si falla la consulta: se registra el error y se devuelve la lista vacía
            LOG.warning("peliculasDesdeAnio: " + ex);
            return List.of();
        }
    }

    // C16 del README
    /**
     * Devuelve las películas que no tienen ninguna sesión, ordenadas por título (reto C16 del README).
     * Si hay un error, lo registra en el log y devuelve List.of().
     *
     * @return lista de películas sin sesiones; List.of() si todas tienen alguna sesión
     */
    public List<Pelicula> peliculasSinSesiones() {

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // "Películas sin sesiones" del README: "sin" lleva a not exists
            // la subconsulta busca sesiones de esa película; not exists se queda con la película si no encuentra ninguna
            // "Orden: titulo ASC" del README: order by p.titulo
            return em.createQuery("select p from PeliculaEntity p where not exists (select s from SesionEntity s where s.idPelicula = p.idPelicula) order by p.titulo", PeliculaEntity.class)
                    // recorre las filas una a una (como el while (rs.next()) de JDBC)
                    .getResultStream()
                    // desdeEntidad: pasa cada PeliculaEntity (entidad) a Pelicula (modelo)
                    .map(PeliculaMapper::desdeEntidad)
                    // junta las películas en una lista
                    .toList();
        } catch (RuntimeException ex) {
            // si falla la consulta: se registra el error y se devuelve la lista vacía
            LOG.warning("peliculasSinSesiones: " + ex);
            return List.of();
        }
    }

    // C26 del README
    /**
     * Devuelve, por cada película, cuántas valoraciones tiene y su media, también las que no tienen (reto C26).
     * Si hay un error, lo registra en el log y devuelve List.of().
     *
     * @return filas Object[] ordenadas por título con [0] título (String), [1] número de valoraciones (Long)
     *         y [2] media con 2 decimales (Double, null si no tiene valoraciones); List.of() si no hay películas
     */
    public List<Object[]> peliculasConMediaValoraciones() {

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // "incluyendo películas sin valorar" del README: left join, así salen también las que no tienen valoraciones
            // el join se escribe con la clase y el atributo de la FK (v.idPelicula = p.idPelicula) en el on, como en SQL
            // count(v.idValoracion): cuenta solo valoraciones reales (los null del left join dan 0); count siempre da Long
            // round(avg(v.puntuacion), 2): la media con 2 decimales (Double); si no hay valoraciones queda null
            // "por película": group by p.idPelicula, p.titulo (una fila por película)
            // "Orden: pelicula ASC" del README: order by p.titulo
            // Object[].class: cada fila es un array con las columnas del select en su orden ([0], [1], [2])
            return em.createQuery("select p.titulo, count(v.idValoracion), round(avg(v.puntuacion), 2) from PeliculaEntity p left join ValoracionEntity v on v.idPelicula = p.idPelicula group by p.idPelicula, p.titulo order by p.titulo", Object[].class)
                    // devuelve las filas en una lista (no hay mapper: son columnas sueltas, no entidades)
                    .getResultList();
        } catch (RuntimeException ex) {
            // si falla la consulta: se registra el error y se devuelve la lista vacía
            LOG.warning("peliculasConMediaValoraciones: " + ex);
            return List.of();
        }
    }

    // C14 del README
    /**
     * Devuelve las películas con al menos ese número de valoraciones y su media (reto C14 del README).
     * Si hay un error, lo registra en el log y devuelve List.of().
     *
     * @param minimo número mínimo de valoraciones que debe tener la película, por ejemplo 2
     * @return filas Object[] con [0] título (String), [1] número de valoraciones (Long) y [2] media con 2 decimales
     *         (Double), ordenadas por media de mayor a menor y por título; List.of() si no hay ninguna
     */
    public List<Object[]> mejorValoradas(int minimo) {

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // mismo select y left join que C26: título, número de valoraciones (Long) y media con 2 decimales (Double)
            // group by p.idPelicula, p.titulo: una fila por película
            // "con al menos dos valoraciones" del README: having count(v.idValoracion) >= :minimo
            // having filtra los grupos ya contados: el count solo existe después del group by
            // "Orden: media DESC, pelicula ASC" del README: order by avg(v.puntuacion) desc, p.titulo
            return em.createQuery("select p.titulo, count(v.idValoracion), round(avg(v.puntuacion), 2) from PeliculaEntity p left join ValoracionEntity v on v.idPelicula = p.idPelicula group by p.idPelicula, p.titulo having count(v.idValoracion) >= :minimo order by avg(v.puntuacion) desc, p.titulo", Object[].class)
                    // da valor a :minimo (como el ps.setInt(1, minimo) de JDBC)
                    .setParameter("minimo", minimo)
                    // devuelve las filas Object[] en una lista
                    .getResultList();
        } catch (RuntimeException ex) {
            // si falla la consulta: se registra el error y se devuelve la lista vacía
            LOG.warning("mejorValoradas: " + ex);
            return List.of();
        }
    }

    // C19 del README
    /**
     * Devuelve las películas con más entradas válidas (PAGADA o USADA) que la media (reto C19 del README).
     * La media es: entradas válidas entre el número de películas, contando también las de 0 ventas.
     * Si hay un error, lo registra en el log y devuelve List.of().
     *
     * @return filas Object[] con [0] título (String) y [1] entradas válidas (Long), ordenadas por entradas
     *         válidas de mayor a menor y por título; List.of() si no hay ninguna
     */
    public List<Object[]> masVendidasQueMedia() {

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // 1) la media: entradas válidas entre el número de películas (dos count)
            // entrada válida = PAGADA o USADA (convenciones del README): las CANCELADA no cuentan
            // count siempre devuelve Long (por eso Long.class); getSingleResult porque la consulta da un solo número
            Long validas = em.createQuery("select count(e) from EntradaEntity e where e.estado in ('PAGADA','USADA')", Long.class).getSingleResult();
            // número de películas: cuenta también las que tienen 0 ventas, como pide el README
            Long pelis = em.createQuery("select count(p) from PeliculaEntity p", Long.class).getSingleResult();
            // * 1.0 para dividir con decimales: Long entre Long es división entera (20 / 9 daría 2 y no 2.22)
            double media = validas * 1.0 / pelis;
            // 2) la consulta del reto, comparando con :media
            // select: título y número de entradas válidas de cada película (count da Long)
            return em.createQuery("select p.titulo, count(e.idEntrada) "
                            // "incluidas las de cero ventas": left join, así salen también las películas sin sesiones
                            + "from PeliculaEntity p left join SesionEntity s on s.idPelicula = p.idPelicula "
                            // left join con el filtro de estado en el on: en el where se perderían las películas con 0
                            + "left join EntradaEntity e on e.idSesion = s.idSesion and e.estado in ('PAGADA','USADA') "
                            // una fila por película; having: su count contra la media (* 1.0 para comparar con decimales)
                            + "group by p.idPelicula, p.titulo having count(e.idEntrada) * 1.0 > :media "
                            // "Orden: entradas_validas DESC, pelicula ASC" del README
                            + "order by count(e.idEntrada) desc, p.titulo", Object[].class)
                    // da valor a :media (como un ps.setDouble de JDBC)
                    .setParameter("media", media)
                    // devuelve las filas Object[] en una lista: [0] título, [1] entradas válidas
                    .getResultList();
        } catch (RuntimeException ex) {
            // si falla la consulta: se registra el error y se devuelve la lista vacía
            LOG.warning("masVendidasQueMedia: " + ex);
            return List.of();
        }
    }

    // C27 del README
    /**
     * Devuelve, por cada género, cuántas películas, sesiones y entradas válidas tiene (reto C27 del README).
     * Si hay un error, lo registra en el log y devuelve List.of().
     *
     * @return filas Object[] ordenadas por género con [0] género (String), [1] títulos (Long),
     *         [2] sesiones (Long) y [3] entradas válidas (Long); List.of() si no hay películas
     */
    public List<Object[]> estadisticasGenero() {

        // abre el EntityManager (var = tipo EntityManager); el try (...) lo cierra solo, como la Connection en JDBC
        try (var em = emf.createEntityManager()) {
            // select: género, títulos, sesiones y entradas válidas (los count dan Long)
            // distinct porque los join repiten la película y la sesión una vez por cada entrada (si no, se contarían de más)
            return em.createQuery("select p.genero, count(distinct p.idPelicula), count(distinct s.idSesion), count(e.idEntrada) "
                            // left join: las películas sin sesiones también cuentan como títulos de su género
                            + "from PeliculaEntity p left join SesionEntity s on s.idPelicula = p.idPelicula "
                            // left join con el filtro de estado en el on: así no se pierden las películas sin ventas válidas
                            + "left join EntradaEntity e on e.idSesion = s.idSesion and e.estado in ('PAGADA','USADA') "
                            // "por género" del README: group by p.genero (una fila por género); "Orden: genero ASC"
                            + "group by p.genero order by p.genero", Object[].class)
                    // devuelve las filas Object[] en una lista
                    .getResultList();
        } catch (RuntimeException ex) {
            // si falla la consulta: se registra el error y se devuelve la lista vacía
            LOG.warning("estadisticasGenero: " + ex);
            return List.of();
        }
    }

}
