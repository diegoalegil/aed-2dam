package es.ejemplo.taller.modelo.Dao;

import es.ejemplo.taller.modelo.Cliente;
import es.ejemplo.taller.orm.config.OrmFactory;
import es.ejemplo.taller.orm.entity.ClienteEntity;
import es.ejemplo.taller.orm.mapper.ClienteMapper;
import jakarta.persistence.EntityManagerFactory;

public class ClienteDao {

    private final String url;
    private final EntityManagerFactory managerFactory;

    public ClienteDao(String url) {
        this.url = url;
        this.managerFactory = OrmFactory.crear(url);
    }

    public int insertar(Cliente dato) {
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                ClienteEntity entidad = ClienteMapper.toEntity(dato);
                em.persist(entidad);
                em.flush(); // Necesario aquí para leer inmediatamente el ID generado
                dato.setIdCliente(entidad.getIdCliente());
                tx.commit();
                return entidad.getIdCliente();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                throw ex;
            }
        }
    }


}
