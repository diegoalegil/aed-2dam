package aula.tareas.repository;

import aula.tareas.api.TareaRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Repository("repositoryJson")
public class TareaRepositoryJson implements ITareaRepository {
    private final AtomicLong secuencia = new AtomicLong();
    private Path path;
    private final ObjectMapper mapper = new ObjectMapper();
    private static final String url = "data/tarea.json";
    private List<TareaRequest> lista;

    public TareaRepositoryJson() {
        //path = Path.of("data", "tarea.json");
        //String url = "data/test.json";
        /**
         if (!Files.exists(path)) {
         try {
         Files.createFile(path);
         } catch (IOException e) {
         throw new RuntimeException(e);
         }
         }
         **/
        //this(data/tareas.json;
        //Otra manera de hacerlo "" pregunta de examen
        this(url);
        lista = getAll();

        //Esto es lo que esta comentando con respecto al anterior examen que se queja de nuestro nivel y tal y esta comentando
        //Aqui pregunta si esto sabremos hacerlo, nadie responde
        //Luego esta preguntando si esto lo podemos poner en el metodo de abajo que habria que hace replace para que funcione, la linea 51 mio al 61 mio no la de el que tenemos diferente, el cambio ahora el String url por String jdbcUrl
        //Terminar codigo para mañana tanto CSV, JSON y agregar XML para mañana todo entendido e importante entender el tema de las conexiones

        //String jdbcUrl = "jdbc:sqlite:data/tarea.sqlite3";
        //ormFactory.crear(jdbcUrl);

        //Esto lo pongo yo, no esta en el codigo del profe:
        //el contador empieza en el id mas alto del fichero, porque si no al reiniciar la app vuelve a 0 y repite ids
        secuencia.set(lista.stream().mapToLong(TareaRequest::id).max().orElse(0));
    }

    public TareaRepositoryJson(String url) {
        path = Path.of(url);
        if (!Files.exists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public TareaRequest create(String titulo) {
        long id = secuencia.incrementAndGet();
        var tarea = new TareaRequest(id, titulo.strip());
        lista.add(tarea);
        saveAll(lista);
        return tarea;
    }

    @Override
    public boolean delete(long identificador) {
        boolean removed = lista.removeIf(t -> t.id() == identificador);
        if (removed) {
            saveAll(lista);
        }
        return removed;
    }

    @Override
    public List<TareaRequest> findAll() {
        return List.copyOf(lista);
    }

    @Override
    public TareaRequest findById(long identificador) {
        return findAll().stream().filter(t -> t.id() == identificador).findFirst().orElse(null);
    }

    @Override
    public TareaRequest update(TareaRequest tarea) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).id() == tarea.id()) {
                lista.set(i, tarea);
                saveAll(lista);
                return tarea;
            }
        }
        return null;
    }

    private void saveAll(List<TareaRequest> items) {
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            mapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), items);
        } catch (Exception exception) {
            //Logger
            throw new RuntimeException(exception);
        }
    }

    public List<TareaRequest> getAll() {
        try {
            return new ArrayList<>(mapper.readValue(
                    path.toFile(), new TypeReference<List<TareaRequest>>() {
                    }));
        } catch (Exception exception) {
            //Logger
            return new ArrayList<>();
        }
    }
}
