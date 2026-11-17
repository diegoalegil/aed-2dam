# Padre 1: comprobar al conectar

Proyecto Java / Maven / JDBC / SQLite / JUnit 5. Hay que implementar la clase padre `AbstractDao<T>` de los DAO. Individual.
Se pueden usar apuntes escritos a mano.

## Qué viene hecho

- `modelo/Libro`: `Integer id`, `String titulo`, `Integer idAutor`, con constructores, getters, setters y `toString`.
- `dao/DaoException`: no comprobada (`extends RuntimeException`), constructor `(String mensaje, Throwable causa)`. Úsala tal cual.
- `dao/LibroDao`: **completo**. Hereda de `AbstractDao<Libro>`, llama a `super(url)`, implementa `map` y usa `this.obtenerConexion()` en `listar()` e `insertar(Libro)`. No se cambia.
- Los tests (`BaseTest` y `PadreTest`) y el esquema en `src/test/resources/schema.sql` (tablas `autores` y `libros`, con FK `libros.id_autor` → `autores(id)`).
- `infra/AbstractDao` viene vacío y hay que implementarlo: sin él no compila nada.

### AbstractDao y la conexión

- Clase genérica y abstracta: `AbstractDao<T>`, con el atributo `private final String url`.
- Constructor `protected AbstractDao(String url)`: **solo guarda la url**. No comprueba nada y no lanza nada (`new LibroDao(null)` funciona).
- `protected Connection obtenerConexion()`, sin `throws` en la cabecera. Hace las comprobaciones en este orden, cada vez que se llama:
  1. Si la url es `null` o no empieza por `jdbc:sqlite:` → `IllegalArgumentException`.
  2. La ruta es la url sin el prefijo `jdbc:sqlite:`. Si esa ruta no es un fichero normal (no existe, es una carpeta, o la ruta queda vacía como en `jdbc:sqlite:` sin nada detrás) → `IllegalArgumentException`. **No se puede crear el fichero**: después de fallar, sigue sin existir.
  3. Abre la conexión con `DriverManager`, activa las claves foráneas con `PRAGMA foreign_keys = ON` y devuelve la conexión abierta.
  4. Cualquier `SQLException` al abrir o al activar el PRAGMA → `DaoException` con un mensaje y la `SQLException` como causa.
- Las excepciones de los pasos 1 y 2 salen **al usar el DAO** (`listar`, `insertar`), nunca en el `new`.
- `protected abstract List<T> map(ResultSet rs)`: la implementa cada DAO hijo.

## Cómo empezar

1. Abre en IntelliJ la carpeta del proyecto (la que tiene el `pom.xml`).
2. Ejecuta `mvn clean test`: no compila porque `LibroDao` necesita el padre.
3. Implementa `infra/AbstractDao` hasta que compile y `PadreTest` salga en verde.

## Pruebas

| Test | Qué comprueba |
|---|---|
| `nuevoConUrlNullNoLanzaTest` | `new LibroDao(null)` no lanza |
| `listarConUrlNullLanzaIllegalArgumentTest` | url `null` → `IllegalArgumentException` en `listar()` |
| `prefijoMaloLanzaAlListarTest` | url sin `jdbc:sqlite:` → el `new` no lanza, `listar()` lanza `IllegalArgumentException` |
| `ficheroInexistenteNoSeCreaTest` | fichero que no existe → `IllegalArgumentException` y el fichero no se crea |
| `insertarConFicheroInexistenteLanzaTest` | lo mismo con `insertar` |
| `rutaDeCarpetaLanzaIllegalArgumentTest` | la ruta es una carpeta → `IllegalArgumentException` |
| `urlSinRutaLanzaIllegalArgumentTest` | `jdbc:sqlite:` sin ruta → `IllegalArgumentException` |
| `baseValidaInsertarYListarTest` | con una base válida, `insertar` devuelve el id y `listar` lo devuelve |
| `clavesForaneasActivasTest` | autor 999 → `DaoException` (las FK están activas) |
| `errorSqlLlegaComoDaoExceptionTest` | `titulo` null → `DaoException` con `SQLException` como causa |
