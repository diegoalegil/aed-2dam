package aula.tareas.orm.mapper;

import aula.tareas.api.TareaRequest;
import aula.tareas.orm.entity.TareaEntity;

public final class TareaMapper {
    private TareaMapper() {
    }

    public static TareaRequest desdeEntidad(TareaEntity entidad) {
        return new TareaRequest(entidad.getId(), entidad.getTitulo());
    }
}
