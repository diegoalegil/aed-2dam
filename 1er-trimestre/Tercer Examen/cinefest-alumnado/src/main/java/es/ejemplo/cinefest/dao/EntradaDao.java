package es.ejemplo.cinefest.dao;

import es.ejemplo.cinefest.modelo.Entrada;
import es.ejemplo.cinefest.orm.config.OrmFactory;
import es.ejemplo.cinefest.orm.entity.EntradaEntity;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;


public class EntradaDao {

    private final EntityManagerFactory emf;

    public EntradaDao(String jdbcUrl) {
        this.emf = OrmFactory.crear(jdbcUrl);
    }

    private static final Logger LOG = Logger.getLogger(EntradaDao.class.getName());

    // Usar transacciones y rollback en operaciones de escritura.
    public List<Entrada> listar() {
        // TODO: implementar conforme al contrato del README.md.
        return List.of();
    }

    public Optional<Entrada> buscarPorId(int id) {
        // TODO: implementar conforme al contrato del README.md.
        return Optional.empty();
    }

    public int insertar(Entrada dato) {
        // TODO: implementar conforme al contrato del README.md.
        return 0;
    }

    public int actualizar(Entrada dato) {
        // TODO: implementar conforme al contrato del README.md.
        return 0;
    }

    public int eliminar(int id) {
        // TODO: implementar conforme al contrato del README.md.
        return 0;
    }

    public List<Object[]> entradasConDetalles() {
        // TODO: implementar conforme al contrato del README.md.
        return List.of();
    }

    public List<Object[]> entradasPorEspectador(int idEspectador) {
        // TODO: implementar conforme al contrato del README.md.
        return List.of();
    }

    public List<Object[]> ventasPorDia() {
        // TODO: implementar conforme al contrato del README.md.
        return List.of();
    }

    public int cancelarEntrada(int idEntrada) {

        if (idEntrada <= 0) return 0;

        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                EntradaEntity e = em.find(EntradaEntity.class, idEntrada);
                if (e == null) {
                    tx.rollback();
                    return 0;
                }
                if ("CANCELADA".equals(e.getEstado())) {
                    tx.rollback();
                    return 0;
                }
                e.setEstado("CANCELADA");
                tx.commit();
                return 1;
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                LOG.warning("cancelarEntrada: " + ex);
                return 0;
            }
        }
    }

}
