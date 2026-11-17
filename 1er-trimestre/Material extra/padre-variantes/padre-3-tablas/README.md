# Padre 3: constructor + debe tener las tablas

Proyecto Java 21 / Maven / JDBC / SQLite / JUnit 5. Un solo DAO (`LibroDao`) sobre `libros`, con FK a `autores`. Lo que se practica es la clase padre `AbstractDao`. Individual. Se pueden usar apuntes escritos a mano.

## Qué viene dado

- `modelo/Libro` (`id`, `titulo`, `idAutor`) con constructores, getters y setters. **Ya está hecho.**
- `dao/DaoException`: no comprobada, constructor `(String mensaje, Throwable causa)`. **Úsala tal cual.**
- `dao/LibroDao`: **completo** (`listar()` e `insertar(Libro)`). No se cambia: usa `super(url)`, `obtenerConexion()` y `map`.
- Los tests: `BaseTest` crea con `schema.sql` una base nueva en una carpeta temporal para cada test, con el autor 1 'Ana'.
- Tablas (`src/main/resources/schema.sql`): `autores(id, nombre NOT NULL)` y `libros(id, titulo NOT NULL, id_autor NOT NULL REFERENCES autores(id))`.
- `infra/AbstractDao` viene vacío y hay que implementarlo: sin él no compila nada.

### AbstractDao y la conexión

`public abstract class AbstractDao<T>` con `private final String url`, `protected AbstractDao(String url)`, `protected Connection obtenerConexion()` y `protected abstract List<T> map(ResultSet rs)`. Ningún método lleva `throws`: toda `SQLException` se convierte en `DaoException`.

**Comprobaciones en el constructor** (en el `new`, no al conectar), por este orden; solo si todo va bien se guarda la `url`:

1. `url` nula o que no empieza por `jdbc:sqlite:` → `IllegalArgumentException`.
2. La ruta (lo que va detrás de `jdbc:sqlite:`) no existe o no es un fichero normal (por ejemplo, una carpeta) → `DaoException` **sin causa** (`null`). El fichero **no** se debe crear.
3. Abrir una conexión, consultar `SELECT name FROM sqlite_master WHERE type = 'table'` y guardar los nombres en una `List<String>`. Si falta `autores` o falta `libros` → `DaoException`. Las tablas de más (`sqlite_sequence`, `auditoria`...) no son un problema.
4. Cualquier `SQLException` del paso 3 (por ejemplo, el fichero no es una base SQLite) → `DaoException` con la `SQLException` como causa.

**`obtenerConexion()`** solo conecta y ejecuta `PRAGMA foreign_keys = ON`; devuelve la conexión abierta (la cierra el hijo con su try-with-resources). Si falla → `DaoException` con la causa.

## Cómo empezar

1. `mvn clean test`: no compila hasta que `AbstractDao` tenga lo de arriba.
2. Implementa el padre hasta que `PadreTest` salga en verde (11 tests).

## Pruebas

| Test | Qué comprueba |
|---|---|
| `baseValidaInsertarYListarTest` | base válida: `insertar` devuelve el id y `listar` devuelve el libro |
| `urlNoValidaLanzaIllegalArgumentEnElNewTest` | url nula, `jdbc:mysql:` o sin prefijo → `IllegalArgumentException` en el `new` |
| `ficheroInexistenteLanzaDaoExceptionYNoSeCreaTest` | fichero que no existe → `DaoException` sin causa en el `new`; no se crea |
| `rutaQueEsCarpetaLanzaDaoExceptionEnElNewTest` | la ruta es una carpeta → `DaoException` sin causa en el `new` |
| `ficheroVacioLanzaDaoExceptionEnElNewTest` | fichero de 0 bytes (sin tablas) → `DaoException` en el `new` |
| `ficheroDeTextoLanzaDaoExceptionConCausaEnElNewTest` | fichero de texto → `DaoException` en el `new` con `SQLException` como causa |
| `baseSinLasTablasLanzaDaoExceptionEnElNewTest` | base solo con `clientes` o solo con `autores` → `DaoException` en el `new` |
| `tablaExtraNoImpideConstruirTest` | una tabla `auditoria` de más no impide construir ni listar |
| `clavesForaneasActivasTest` | libro con autor 999 → `DaoException` al insertar (FK activas) |
| `errorSqlLlegaComoDaoExceptionConCausaTest` | `titulo` nulo → `DaoException` con `SQLException` como causa |
| `padreSinThrowsTest` | ni el constructor, ni `obtenerConexion()`, ni `map` llevan `throws` |
