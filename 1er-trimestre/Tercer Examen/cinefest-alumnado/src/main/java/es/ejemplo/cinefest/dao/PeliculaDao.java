package es.ejemplo.cinefest.dao;

import es.ejemplo.cinefest.modelo.Pelicula;
import es.ejemplo.cinefest.orm.config.OrmFactory;
import es.ejemplo.cinefest.orm.entity.PeliculaEntity;
import es.ejemplo.cinefest.orm.mapper.PeliculaMapper;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;


public class PeliculaDao {

    private final EntityManagerFactory emf;

    public PeliculaDao(String jdbcUrl) {
        this.emf = OrmFactory.crear(jdbcUrl);
    }

    private static final Logger LOG = Logger.getLogger(PeliculaDao.class.getName());

    // Usar transacciones y rollback en operaciones de escritura.
    public List<Pelicula> listar() {
        try (var em = emf.createEntityManager()) {
            return em.createQuery("select p from PeliculaEntity p order by p.idPelicula", PeliculaEntity.class)
                    .getResultStream()
                    .map(PeliculaMapper::desdeEntidad)
                    .toList();
        } catch (RuntimeException ex) {
            LOG.warning("listar: " + ex);
            return List.of();
        }
    }

    public Optional<Pelicula> buscarPorId(int id) {

        if (id <= 0) return Optional.empty();

        try (var em = emf.createEntityManager()) {
            return Optional.ofNullable(em.find(PeliculaEntity.class, id))
                    .map(PeliculaMapper::desdeEntidad);
        } catch (RuntimeException ex) {
            LOG.warning("buscarPorId: " + ex);
            return Optional.empty();
        }
    }

    public int insertar(Pelicula dato) {

        if (dato == null || dato.getTitulo() == null || dato.getTitulo().isBlank()
                || dato.getGenero() == null || dato.getGenero().isBlank()
                || dato.getPais() == null || dato.getPais().isBlank()) return 0;
        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                PeliculaEntity e = PeliculaMapper.aEntidad(dato);
                e.setIdPelicula(null);
                em.persist(e);
                tx.commit();
                return e.getIdPelicula();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                LOG.warning("insertar: " + ex);
                return 0;
            }
        }
    }

    public int actualizar(Pelicula dato) {

        if (dato == null || dato.getIdPelicula() == null
                || dato.getTitulo() == null || dato.getTitulo().isBlank()
                || dato.getGenero() == null || dato.getGenero().isBlank()
                || dato.getPais() == null || dato.getPais().isBlank())
            return 0;

        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                PeliculaEntity e = em.find(PeliculaEntity.class, dato.getIdPelicula());
                if (e == null) {
                    tx.rollback();
                    return 0;
                }
                e.setTitulo(dato.getTitulo());
                e.setGenero(dato.getGenero());
                e.setDuracionMin(dato.getDuracionMin());
                e.setPais(dato.getPais());
                e.setAnio(dato.getAnio());
                tx.commit();
                return 1;
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                LOG.warning("actualizar: " + ex);
                return 0;
            }
        }
    }

    public int eliminar(int id) {

        if (id <= 0) return 0;

        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                PeliculaEntity e = em.find(PeliculaEntity.class, id);
                if (e == null) {
                    tx.rollback();
                    return 0;
                }
                em.remove(e);
                tx.commit();
                return 1;
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                LOG.warning("eliminar: " + ex);
                return 0;
            }
        }
    }

    // C03 del README
    public List<Pelicula> buscarPorGenero(String genero) {

        if (genero == null || genero.isBlank()) return List.of();

        try (var em = emf.createEntityManager()) {
            return em.createQuery("select p from PeliculaEntity p where p.genero = :genero order by p.titulo", PeliculaEntity.class)
                    .setParameter("genero", genero)
                    .getResultStream()
                    .map(PeliculaMapper::desdeEntidad)
                    .toList();
        } catch (RuntimeException ex) {
            LOG.warning("buscarPorGenero: " + ex);
            return List.of();
        }
    }

    // Sin reto en el README: las de ese año o posteriores
    public List<Pelicula> peliculasDesdeAnio(int anio) {

        try (var em = emf.createEntityManager()) {
            return em.createQuery("select p from PeliculaEntity p where p.anio >= :anio", PeliculaEntity.class)
                    .setParameter("anio", anio)
                    .getResultStream()
                    .map(PeliculaMapper::desdeEntidad)
                    .toList();
        } catch (RuntimeException ex) {
            LOG.warning("peliculasDesdeAnio: " + ex);
            return List.of();
        }
    }

    // C16 del README
    public List<Pelicula> peliculasSinSesiones() {

        try (var em = emf.createEntityManager()) {
            return em.createQuery("select p from PeliculaEntity p where not exists (select s from SesionEntity s where s.idPelicula = p.idPelicula) order by p.titulo", PeliculaEntity.class)
                    .getResultStream()
                    .map(PeliculaMapper::desdeEntidad)
                    .toList();
        } catch (RuntimeException ex) {
            LOG.warning("peliculasSinSesiones: " + ex);
            return List.of();
        }
    }

    // C26 del README
    public List<Object[]> peliculasConMediaValoraciones() {

        try (var em = emf.createEntityManager()) {
            return em.createQuery("select p.titulo, count(v.idValoracion), avg(v.puntuacion) from PeliculaEntity p left join ValoracionEntity v on v.idPelicula = p.idPelicula group by p.idPelicula, p.titulo order by p.titulo", Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            LOG.warning("peliculasConMediaValoraciones: " + ex);
            return List.of();
        }
    }

    // C14 del README
    public List<Object[]> mejorValoradas(int minimo) {

        try (var em = emf.createEntityManager()) {
            return em.createQuery("TODO", Object[].class)
                    .setParameter("minimo", minimo)
                    .getResultList();
        } catch (RuntimeException ex) {
            LOG.warning("mejorValoradas: " + ex);
            return List.of();
        }
    }

    // C19 del README
    public List<Object[]> masVendidasQueMedia() {

        try (var em = emf.createEntityManager()) {
            // 1) la media: entradas válidas entre el número de películas (dos count)
            Long validas = em.createQuery("TODO", Long.class).getSingleResult();
            Long pelis = em.createQuery("TODO", Long.class).getSingleResult();
            double media = validas * 1.0 / pelis;
            // 2) la consulta del reto, comparando con :media
            return em.createQuery("TODO", Object[].class)
                    .setParameter("media", media)
                    .getResultList();
        } catch (RuntimeException ex) {
            LOG.warning("masVendidasQueMedia: " + ex);
            return List.of();
        }
    }

    // C27 del README
    public List<Object[]> estadisticasGenero() {

        try (var em = emf.createEntityManager()) {
            return em.createQuery("TODO", Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            LOG.warning("estadisticasGenero: " + ex);
            return List.of();
        }
    }

}
