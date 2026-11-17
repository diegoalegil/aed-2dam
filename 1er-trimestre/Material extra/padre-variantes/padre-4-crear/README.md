# Padre 4: si no existe, se crea con sus tablas

Proyecto Java 21 / Maven / JDBC / SQLite / JUnit 5. Se practica **solo la clase padre** `AbstractDao<T>`. Individual. Se pueden usar apuntes escritos a mano.

## Qué viene dado (paquete `es.ejemplo.padre`)

- `modelo`: `Libro` (`id`, `titulo`, `idAutor`). **Ya está hecho.**
- `dao`: `DaoException` (no comprobada, constructor `(String mensaje, Throwable causa)`) y `LibroDao` **completo** (`listar()` e `insertar(Libro)`). No se cambian.
- Los tests (`BaseTest` y `PadreTest`) y el esquema `schema.sql` (tablas `autores` y `libros`, `libros.id_autor` es FK a `autores`).
- `infra/AbstractDao` viene vacío y hay que implementarlo: sin él no compila nada.

### AbstractDao y la conexión

- `public abstract class AbstractDao<T>`, con la url en un atributo `private final String url`.
- Constructor `protected AbstractDao(String url)`. Comprobaciones **en el constructor** (en el `new` del DAO, no al conectar):
  - url `null` o que no empieza por `jdbc:sqlite:` → `IllegalArgumentException`.
- **No** se comprueba si el fichero existe: si no existe, SQLite lo crea al conectar.
- Después, también en el constructor, se dejan creadas las tablas si faltan. En **una sola conexión** (cerrada al terminar) se ejecuta primero `PRAGMA foreign_keys = ON` y luego estas dos sentencias, tal cual:
  - `CREATE TABLE IF NOT EXISTS autores (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT NOT NULL)`
  - `CREATE TABLE IF NOT EXISTS libros (id INTEGER PRIMARY KEY AUTOINCREMENT, titulo TEXT NOT NULL, id_autor INTEGER NOT NULL REFERENCES autores(id))`
- Si falla esa conexión o esas sentencias (por ejemplo, la carpeta del fichero no existe) → `DaoException` con la `SQLException` como causa, también en el `new`.
- Construir otra vez sobre una base de datos que ya tiene tablas y datos no da error y no borra nada.
- `protected Connection obtenerConexion()`: abre una conexión nueva con la url y activa `PRAGMA foreign_keys = ON` antes de devolverla. `SQLException` → `DaoException` con la `SQLException` como causa. Quien la pide la cierra (try-with-resources en `LibroDao`).
- `protected abstract List<T> map(ResultSet rs)`: cada hijo convierte las filas.
- Ningún método lleva `throws` en la cabecera.

## Cómo empezar

1. `mvn clean test`: al principio **no compila** (`LibroDao` usa `super(url)`, `obtenerConexion()` y `map`).
2. Implementa `AbstractDao` hasta que compile y `PadreTest` salga en verde.

## Pruebas

| Test | Qué comprueba |
|---|---|
| `insertarYListarFuncionaTest` | Con una base de datos válida, lo insertado sale en `listar()` |
| `ficheroQueNoExisteSeCreaEnElNewTest` | Tras el `new` el fichero existe y `listar()` está vacío |
| `tablasSeCreanEnElNewTest` | Las dos tablas existen justo después del `new` |
| `construirOtraVezConservaLosDatosTest` | Un segundo `new` no falla y los datos siguen |
| `urlNullLanzaIllegalArgumentEnElNewTest` | url `null` → `IllegalArgumentException` en el `new` |
| `urlSinPrefijoLanzaIllegalArgumentEnElNewTest` | url sin `jdbc:sqlite:` → `IllegalArgumentException` en el `new` |
| `carpetaQueNoExisteLanzaDaoExceptionEnElNewTest` | Carpeta inexistente → `DaoException` (causa `SQLException`) en el `new` |
| `clavesForaneasActivasTest` | `idAutor` 999 → `DaoException`: las FK están activas al conectar |
| `errorSqlLlegaComoDaoExceptionConCausaTest` | `titulo` null → `DaoException` con causa `SQLException` |
