package es.ejemplo.autoescuela.dao;

import es.ejemplo.autoescuela.modelo.ClasePractica;
import es.ejemplo.autoescuela.orm.config.OrmFactory;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;

public class ClasePracticaDao {

    public ClasePracticaDao(String jdbcUrl) {  }

    // Usar transacciones y rollback en operaciones de escritura.
    public List<ClasePractica> listar() {
        // TODO: implementar conforme al contrato del README.md.
        return List.of();
    }
    public Optional<ClasePractica> buscarPorId(int id) {
        // TODO: implementar conforme al contrato del README.md.
        return Optional.empty();
    }
    public int insertar(ClasePractica dato) {
        // TODO: implementar conforme al contrato del README.md.
        return 0;
    }
    public int actualizar(ClasePractica dato) {
        // TODO: implementar conforme al contrato del README.md.
        return 0;
    }
    public int eliminar(int id) {
        // TODO: implementar conforme al contrato del README.md.
        return 0;
    }
    public List<Object[]> agendaDelDia(String fecha) {
        // TODO: implementar conforme al contrato del README.md.
        return List.of();
    }
    public List<Object[]> clasesEntreFechas(String desde, String hasta) {
        // TODO: implementar conforme al contrato del README.md.
        return List.of();
    }
    public List<Object[]> historialAlumno(int idAlumno) {
        // TODO: implementar conforme al contrato del README.md.
        return List.of();
    }
    public int reservarClase(int idAlumno, int idProfesor, String matricula, String fecha, String hora) {
        // TODO: implementar conforme al contrato del README.md.
        return 0;
    }
    public int cancelarClase(int idClase) {
        // TODO: implementar conforme al contrato del README.md.
        return 0;
    }
    
}
