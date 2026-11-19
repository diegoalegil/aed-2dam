package aula.tareas.repository;

import aula.tareas.modelo.Tarea;
import aula.tareas.modelo.TareasXml;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Repository("repositoryXml")
public class TareaRepositoryXml implements ITareaRepository {

    private final AtomicLong secuencia = new AtomicLong();
    private Path path;
    private final XmlMapper mapper = new XmlMapper();
    private static final String url = "data/tarea.xml";
    private List<Tarea> lista;

    public TareaRepositoryXml() {
        this(url);
        lista = getAll();

        secuencia.set(lista.stream().mapToLong(Tarea::id).max().orElse(0));
    }

    public TareaRepositoryXml(String url) {
        path = Path.of(url);
        if (!Files.exists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public List<Tarea> getAll() {
        try {
            TareasXml caja = mapper.readValue(path.toFile(), TareasXml.class);
            return new ArrayList<>(caja.tareas);
        } catch (Exception exception) {
            return new ArrayList<>();
        }
    }

    private void saveAll(List<Tarea> items) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), new TareasXml(items));
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }


    @Override
    public Tarea create(String titulo) {
        long id = secuencia.incrementAndGet();
        Tarea tarea = new Tarea(id, titulo.strip());
        lista.add(tarea);
        saveAll(lista);
        return tarea;
    }

    @Override
    public boolean delete(long identificador) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).id() == identificador) {
                lista.remove(i);
                saveAll(lista);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Tarea> findAll() {
        return List.copyOf(lista);
    }

    @Override
    public Tarea findById(long identificador) {
        for (Tarea tarea : lista) {
            if (tarea.id() == identificador) {
                return tarea;
            }
        }
        return null;
    }

    @Override
    public Tarea update(Tarea tarea) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).id() == tarea.id()) {
                lista.set(i, tarea);
                saveAll(lista);
                return tarea;
            }
        }
        return null;
    }
}
