package es.ejemplo.arcade.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/** TODO: documenta la clase */
public abstract class AbstractDao<T> {

    public AbstractDao(String url) {
    }

    /** TODO: documenta mapRsToClass */
    protected abstract T mapRsToClass(ResultSet rs) throws SQLException;

    /** TODO: documenta consultar */
    public List<T> consultar(PreparedStatement ps) {
        return null;
    }

    /** TODO: documenta actualizar */
    public int actualizar(PreparedStatement ps) {
        return 0;
    }
}
