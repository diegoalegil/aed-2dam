package repository.file.csv;


import com.ejemplo.catalogo.model.Producto;

import java.util.List;
import java.util.Optional;

public interface RepositoryInterface {

    /**
     * Funcion que devuelve todos los productos
     * @return lista con todos los productos, vacia si no hay ninguno
     */
    List<Producto> findAll();

    /**
     * Funcion que busca un producto por su id
     * @param id identificador del producto a buscar
     * @return el producto si existe, o Optional.empty() si no existe
     */
    Optional<Producto> findById(long id);

    /**
     * Funcion que guarda un producto nuevo
     * @param producto producto a guardar
     */
    void create(Producto producto);

    /**
     * Funcion que sustituye un producto existente por otro con el mismo id
     * @param entity producto con los datos nuevos
     * @return true si se ha actualizado, false si no existia ningun producto con ese id
     */
    boolean update(Producto entity);

    /**
     * Funcion que borra un producto por su id
     * @param id identificador del producto a borrar
     * @return true si se ha borrado, false si no existia
     */
    boolean delete(long id);
}
