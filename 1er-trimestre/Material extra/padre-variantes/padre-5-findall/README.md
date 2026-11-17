# Padre 5: todo centralizado en el padre

Biblioteca con SQLite (tablas `autores` y `libros`). El DAO hijo no abre conexiones ni escribe JDBC: le pasa la SQL y los valores al padre y el padre hace el resto.

## Qué viene dado

- `modelo/Libro` (id, titulo, idAutor) y `dao/DaoException` (no comprobada): completos.
- `dao/LibroDao`: DAO completo (`listar`, `buscarPorTitulo`, `insertar`, `eliminar`). No se toca.
- Los tests de `src/test` y `schema.sql`.

`infra/AbstractDao` viene vacío y hay que implementarlo: sin él no compila nada.

### AbstractDao y la conexión

- `public abstract class AbstractDao<T>` con un atributo `private final String url`. Es abstracta porque tiene un método abstracto (`map`): nunca se hace `new AbstractDao`, solo se usa a través del hijo (`LibroDao extends AbstractDao<Libro>`).
- Constructor `protected AbstractDao(String url)`: solo guarda la url. No comprueba nada ni lanza nada. Ningún método del padre lleva `throws`.
- `protected abstract List<T> map(ResultSet rs)`: la implementa cada hijo.
- `protected Connection obtenerConexion()` abre una conexión nueva en cada llamada:
  1. url null o que no empieza por `jdbc:sqlite:` → `IllegalArgumentException`.
  2. El fichero de la url no existe o no es un fichero normal (por ejemplo, una carpeta) → `IllegalArgumentException`. El fichero nunca se crea.
  3. Activa `PRAGMA foreign_keys = ON` antes de devolver la conexión.
  4. Un `SQLException` al conectar → `DaoException` con el error original como causa.
- Las comprobaciones saltan en el primer uso (al llamar a un método del DAO), nunca en el `new`.
- El padre centraliza el JDBC con estos tres métodos:
  - `protected List<T> findAll(String query, Object... valores)`: SELECT, devuelve las filas convertidas con `map`.
  - `protected int ejecutar(String sql, Object... valores)`: UPDATE o DELETE, devuelve las filas afectadas.
  - `protected int insertarYDevolverId(String sql, Object... valores)`: INSERT, devuelve el id nuevo que crea SQLite (0 si no hay).
- Los tres abren y cierran su propia conexión, ponen los valores en los `?` en el mismo orden en que llegan (nunca pegados a la SQL) y convierten cualquier `SQLException` en `DaoException` con la causa.

## Cómo empezar

Lanza `mvn clean test` (al principio no compila) e implementa `infra/AbstractDao` hasta que `PadreTest` salga en verde.

Se pueden usar apuntes escritos a mano.

## Pruebas

| Test | Qué comprueba |
|---|---|
| `listarDevuelveEnOrdenDeIdTest` | lo insertado sale en `listar`, ordenado por id |
| `insertarDevuelveIdsCrecientesTest` | `insertar` devuelve el id nuevo y cada id es mayor |
| `buscarPorTituloParcialTest` / `buscarPorTituloSinInyeccionTest` | el `?` funciona con LIKE y no admite inyección |
| `eliminarDevuelveUnoYLuegoCeroTest` | `eliminar` devuelve 1 y después 0 |
| `clavesForaneasActivasTest` / `errorSqlLlegaComoDaoExceptionTest` | PRAGMA activo y `DaoException` con causa `SQLException` |
| `urlNoValidaFallaAlUsarlaTest` / `ficheroInexistenteFallaYNoSeCreaTest` / `rutaQueEsCarpetaFallaTest` | `IllegalArgumentException` en el primer uso |
