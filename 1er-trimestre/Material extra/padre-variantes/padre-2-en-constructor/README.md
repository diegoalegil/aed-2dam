# Padre 2: comprobar en el constructor

Proyecto Maven con JDBC y SQLite (tablas `autores` y `libros`): hay que implementar el padre de los DAO, `AbstractDao`.

## Qué viene dado

- `modelo/Libro`: completo (id, titulo, idAutor).
- `dao/DaoException`: completa, no comprobada, con el constructor `(String mensaje, Throwable causa)`.
- `dao/LibroDao`: completo (`map`, `listar`, `insertar`). No se toca.
- `schema.sql` y las pruebas (`BaseTest`, `PadreTest`). No se tocan.
- `infra/AbstractDao` viene vacío y hay que implementarlo: sin él no compila nada.

### AbstractDao y la conexión

- Es `public abstract class AbstractDao<T>` y guarda la url en `private final String url`.
- Constructor `protected AbstractDao(String url)`. Aquí van TODAS las comprobaciones, antes de guardar la url:
  1. url `null` o que no empieza por `jdbc:sqlite:` → `IllegalArgumentException`. Sale en el propio `new`.
  2. La ruta es lo que va detrás de `jdbc:sqlite:`. Si ese fichero no existe o no es un fichero normal (por ejemplo, una carpeta) → `DaoException`, también en el `new`. Como no hay SQLException, la causa es null.
  3. El fichero NO se debe crear: el `new` nunca abre una conexión.
  4. Solo si las dos comprobaciones pasan se hace `this.url = url`.
- `protected Connection obtenerConexion()`: no comprueba nada más. Solo abre la conexión con la url y ejecuta `PRAGMA foreign_keys = ON`. Si salta una `SQLException` → `DaoException` con esa `SQLException` como causa.
- `protected abstract List<T> map(ResultSet rs);` (lo implementa `LibroDao`).
- Ningún método lleva `throws` en la cabecera.

## Cómo empezar

1. Abre el proyecto en IntelliJ y ejecuta `mvn clean test`: al principio no compila porque `LibroDao` necesita el padre.
2. Implementa `infra/AbstractDao` siguiendo las reglas de arriba.
3. Repite `mvn clean test` hasta que `PadreTest` salga en verde.

Se pueden usar apuntes escritos a mano.

## Pruebas

| Prueba | Qué comprueba |
|---|---|
| insertarYListarConBaseValidaTest | con una base válida se inserta y `listar` lo devuelve |
| insertarDevuelveIdsSeguidosTest | `insertar` devuelve 1 y 2, `listar` ordena por id |
| claveForaneaActivaTest | autor 999 → `DaoException` (claves foráneas activas) |
| errorSqlLlegaComoDaoExceptionConCausaTest | titulo null → `DaoException` con causa `SQLException` |
| urlNullFallaEnElNewTest | `new LibroDao(null)` → `IllegalArgumentException` en el `new` |
| urlSinPrefijoFallaEnElNewTest | url sin `jdbc:sqlite:` → `IllegalArgumentException` en el `new` |
| ficheroInexistenteFallaEnElNewTest | fichero que no existe → `DaoException` en el `new`, causa null |
| ficheroInexistenteNoSeCreaTest | después de ese `new`, el fichero sigue sin existir |
| carpetaFallaEnElNewTest | ruta a una carpeta → `DaoException` en el `new`, causa null |
