package es.ejemplo.cinefest.dao;

import es.ejemplo.cinefest.modelo.Espectador;
import es.ejemplo.cinefest.orm.config.OrmFactory;
import es.ejemplo.cinefest.orm.entity.EspectadorEntity;
import es.ejemplo.cinefest.orm.mapper.EspectadorMapper;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;


public class EspectadorDao {

    private final EntityManagerFactory emf;

    public EspectadorDao(String jdbcUrl) {
        this.emf = OrmFactory.crear(jdbcUrl);
    }

    private static final Logger LOG = Logger.getLogger(EspectadorDao.class.getName());

    // Usar transacciones y rollback en operaciones de escritura.

    public List<Espectador> listar() {
        try (var em = emf.createEntityManager()) {
            return em.createQuery("select es from EspectadorEntity es order by es.idEspectador", EspectadorEntity.class)
                    .getResultStream()
                    .map(EspectadorMapper::desdeEntidad)
                    .toList();
        } catch (RuntimeException ex) {
            LOG.warning("listar: " + ex);
            return List.of();
        }
    }

    public Optional<Espectador> buscarPorId(int id) {

        if (id <= 0) return Optional.empty();

        try (var em = emf.createEntityManager()) {
            return Optional.ofNullable(em.find(EspectadorEntity.class, id))
                    .map(EspectadorMapper::desdeEntidad);
        } catch (RuntimeException ex) {
            LOG.warning("buscarPorId: " + ex);
            return Optional.empty();
        }
    }

    public int insertar(Espectador dato) {

        if (dato == null || dato.getNombre() == null || dato.getNombre().isBlank()
                || dato.getEmail() == null || dato.getEmail().isBlank()
                || dato.getFechaAlta() == null || dato.getFechaAlta().isBlank()
                || (dato.getActivo() != null && dato.getActivo() != 0 && dato.getActivo() != 1)) return 0;
        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                EspectadorEntity e = EspectadorMapper.aEntidad(dato);
                e.setIdEspectador(null);
                if (e.getActivo() == null) e.setActivo(1);
                em.persist(e);
                tx.commit();
                return e.getIdEspectador();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                LOG.warning("insertar: " + ex);
                return 0;
            }
        }
    }

    public int actualizar(Espectador dato) {

        if (dato == null || dato.getIdEspectador() == null
                || dato.getNombre() == null || dato.getNombre().isBlank()
                || dato.getEmail() == null || dato.getEmail().isBlank()
                || dato.getFechaAlta() == null || dato.getFechaAlta().isBlank()
                || dato.getActivo() == null || (dato.getActivo() != 0 && dato.getActivo() != 1))
            return 0;

        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                EspectadorEntity e = em.find(EspectadorEntity.class, dato.getIdEspectador());
                if (e == null) {
                    tx.rollback();
                    return 0;
                }
                e.setNombre(dato.getNombre());
                e.setEmail(dato.getEmail());
                e.setFechaAlta(dato.getFechaAlta());
                e.setActivo(dato.getActivo());
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
                EspectadorEntity e = em.find(EspectadorEntity.class, id);
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

    public Optional<Espectador> buscarPorEmail(String email) {

        if (email == null || email.isBlank()) return Optional.empty();

        try (var em = emf.createEntityManager()) {
            return em.createQuery("select es from EspectadorEntity es where es.email = :email", EspectadorEntity.class)
                    .setParameter("email", email)
                    .getResultStream()
                    .findFirst()
                    .map(EspectadorMapper::desdeEntidad);
        } catch (RuntimeException ex) {
            LOG.warning("buscarPorEmail: " + ex);
            return Optional.empty();
        }
    }

    // C09 del README
    public List<Espectador> espectadoresSinEntradas() {

        try (var em = emf.createEntityManager()) {
            return em.createQuery("select es from EspectadorEntity es where not exists "
                            + "(select e from EntradaEntity e where e.idEspectador = es.idEspectador) "
                            + "order by es.nombre", EspectadorEntity.class)
                    .getResultStream()
                    .map(EspectadorMapper::desdeEntidad)
                    .toList();
        } catch (RuntimeException ex) {
            LOG.warning("espectadoresSinEntradas: " + ex);
            return List.of();
        }
    }

    // C13 del README
    public List<Object[]> rankingEspectadores() {

        try (var em = emf.createEntityManager()) {
            return em.createQuery("select es.nombre, count(e.idEntrada) "
                            + "from EspectadorEntity es left join EntradaEntity e on e.idEspectador = es.idEspectador "
                            + "and e.estado in ('PAGADA','USADA') "
                            + "group by es.idEspectador, es.nombre order by count(e.idEntrada) desc, es.nombre", Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            LOG.warning("rankingEspectadores: " + ex);
            return List.of();
        }
    }

    // C20 del README
    public List<Object[]> espectadoresVariasSedes(int minimo) {

        try (var em = emf.createEntityManager()) {
            return em.createQuery("select es.nombre, count(distinct sa.idSede) "
                            + "from EspectadorEntity es join EntradaEntity e on e.idEspectador = es.idEspectador "
                            + "join SesionEntity s on s.idSesion = e.idSesion "
                            + "join SalaEntity sa on sa.idSala = s.idSala "
                            + "where e.estado in ('PAGADA','USADA') "
                            + "group by es.idEspectador, es.nombre having count(distinct sa.idSede) >= :minimo "
                            + "order by count(distinct sa.idSede) desc, es.nombre", Object[].class)
                    .setParameter("minimo", minimo)
                    .getResultList();
        } catch (RuntimeException ex) {
            LOG.warning("espectadoresVariasSedes: " + ex);
            return List.of();
        }
    }

}
