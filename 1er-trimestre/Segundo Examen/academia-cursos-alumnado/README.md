# Academia: cursos y alumnado con JDBC y SQLite

Proyecto Java 17 / Maven / JDBC / SQLite / JUnit 5. Hay que implementar el acceso a datos de una academia: **3 DAO con 22 métodos** (15 CRUD y 7 consultas) sobre la clase padre `AbstractDao<T>`.

**Tiempo: 90 minutos.** Individual. Se pueden usar apuntes escritos a mano.

## Objetivo

Practicar el diseño que se usa en clase: un padre `AbstractDao<T>` que centraliza la conexión y todo el JDBC, y unos DAO hijos que solo aportan su SQL y su conversión `ResultSet` → entidad (`map`). Se trabaja con `PreparedStatement` y `?`, `try-with-resources`, `Optional`, `BigDecimal`, claves foráneas, `JOIN`, `LEFT JOIN` con `GROUP BY` y sumas con `COALESCE`.

## Requisitos y ejecución

- JDK 17 o superior y Maven 3.9+.
- Dependencias: `org.xerial:sqlite-jdbc:3.46.1.0` y `org.junit.jupiter:junit-jupiter:5.11.4`.
- Desde la raíz del proyecto: `mvn clean test`.
- Autoevaluación: `python3 -I calcular_nota.py` (ejecuta `mvn -o clean test` sin pararse en los tests que fallan y calcula la nota con los tests visibles).
- Ejemplo de uso:

```java
IAlumnoDao dao = new AlumnoDao("jdbc:sqlite:datos/academia.sqlite3");
List<Alumno> alumnos = dao.listar();
```

- La ruta relativa se resuelve desde el directorio de trabajo: en IntelliJ abre **la carpeta del proyecto** (la que tiene el `pom.xml`).

## Modelo de datos

El esquema completo está en `src/main/resources/schema.sql` (y una copia en `src/test/resources/schema.sql`, que es la que usan los tests). Todas las claves primarias son `INTEGER PRIMARY KEY AUTOINCREMENT`.

| Tabla | Columnas | Restricciones |
|---|---|---|
| `alumnos` | `id`, `nombre`, `email`, `telefono` | `nombre` y `email` `NOT NULL`; `email` `UNIQUE`; `telefono` puede ser `NULL` |
| `cursos` | `id`, `titulo`, `nivel`, `horas`, `precio` | todo `NOT NULL`; `titulo` `UNIQUE`; `nivel IN ('BASICO','MEDIO','AVANZADO')`; `horas > 0`; `precio >= 0` |
| `matriculas` | `id`, `id_alumno`, `id_curso`, `fecha`, `importe` | todo `NOT NULL`; FK `id_alumno` → `alumnos(id)` y `id_curso` → `cursos(id)`; `importe >= 0`; `UNIQUE (id_alumno, id_curso)` |

Relaciones 1:N: un alumno tiene muchas matrículas y un curso tiene muchas matrículas (una matrícula = un alumno apuntado a un curso; no puede repetirse la pareja alumno-curso).

- **No hay borrado en cascada**: para borrar un alumno o un curso con matrículas, primero se borran sus matrículas.
- Los importes (`precio`, `importe`) son `NUMERIC` en SQLite y `BigDecimal` en Java. SQLite guarda `120.50` como `120.5`: compara siempre con `compareTo`, nunca con `equals`.
- Las fechas son texto ISO `AAAA-MM-DD`, así que se pueden comparar y ordenar como texto.
- `datos/academia.sqlite3` tiene datos de ejemplo para probar las consultas en IntelliJ (pestaña Database) antes de escribirlas en Java; `datos/academia-test.sqlite3` es una copia para pruebas manuales. Los tests **no** las usan.

| Clase del modelo | Campos (en el orden del constructor) | Tabla |
|---|---|---|
| `Alumno` | `Integer id`, `String nombre`, `String email`, `String telefono` | `alumnos` |
| `Curso` | `Integer id`, `String titulo`, `String nivel`, `Integer horas`, `BigDecimal precio` | `cursos` |
| `Matricula` | `Integer id`, `Integer idAlumno`, `Integer idCurso`, `String fecha`, `BigDecimal importe` | `matriculas` |

## Diseño y estructura

Paquetes bajo `es.ejemplo.academia`:

- `modelo`: `Alumno`, `Curso` y `Matricula`. Clases mutables con constructores, getters, setters y `toString`. **Ya están hechas.**
- `dao`:
  - las interfaces `IAlumnoDao`, `ICursoDao` e `IMatriculaDao` (ya hechas, **no se cambian**);
  - `DaoException`: la excepción del proyecto, no comprobada (`extends RuntimeException`), con el constructor `(String mensaje, Throwable causa)`. **Ya está hecha**: úsala tal cual;
  - los DAO `AlumnoDao`, `CursoDao` y `MatriculaDao`: vienen con el constructor vacío y todos sus métodos lanzando `UnsupportedOperationException`. **Todavía no heredan de `AbstractDao`**: tienes que añadir `extends AbstractDao<Alumno>` (o `<Curso>`, `<Matricula>`), llamar a `super(url)` en el constructor e implementar `map`.
- `infra`: `AbstractDao<T>`. Viene como guía: el atributo `url`, el constructor y `protected abstract List<T> map(ResultSet rs)` están hechos; `obtenerConexion`, `findAll`, `ejecutar`, `insertarYDevolverId` y `consultarDecimal` tienen la firma y los pasos en comentarios, y lanzan `UnsupportedOperationException`. Puedes añadir los métodos `protected` que necesites (por ejemplo, uno para el `GROUP BY`).

## Contrato

### Conexión (dentro de `AbstractDao`, una conexión nueva en cada operación)

- La comprobación se hace **al ejecutar una operación** (`listar`, `insertar`...), **no en el constructor**: `new AlumnoDao(null)` se puede crear, y falla al usarlo.
- `url` `null` o que no empieza por `jdbc:sqlite:` → `IllegalArgumentException`.
- El fichero de la base de datos no existe, o es una carpeta → `IllegalArgumentException`, y **el fichero no se puede crear**: compruébalo con `Files` antes de llamar a `DriverManager`, porque si SQLite conecta a una ruta que no existe crea un fichero vacío sin avisar.
- Cada conexión ejecuta `PRAGMA foreign_keys = ON` (en SQLite las claves foráneas vienen apagadas y el `PRAGMA` solo vale para esa conexión). Si el `PRAGMA` falla, se cierra la conexión antes de lanzar el error.
- La conexión se cierra al terminar cada operación (`try-with-resources`): no se guarda una conexión compartida.

### Errores

- Cualquier fallo de SQL (`UNIQUE`, `CHECK`, `NOT NULL`, `FOREIGN KEY`, SQL mal escrito) → `DaoException`, **conservando la `SQLException` original como causa**.
- **Sin `throws` en las cabeceras** de `src/main`: `try/catch` dentro y `throw new DaoException(mensaje, e)`.
- Los textos `null` **no se validan en Java**: llegan a la base de datos y el `NOT NULL` los convierte en `DaoException`.

### CRUD (los tres DAO)

- `listar()`: todas las filas **ordenadas por `id`**. Si no hay filas, lista vacía (nunca `null`).
- `buscarPorId(Integer id)`: `Optional` con la fila. Si el `id` es `null` o no existe → `Optional.empty()`.
- `insertar(T)`: devuelve el **id que genera SQLite**. `insertar(null)` → `NullPointerException`. (No hace falta guardar el id en el objeto con `setId`: no se comprueba.)
- `actualizar(T)`: actualiza todas las columnas menos el `id` y devuelve las **filas afectadas** (`1`, o `0` si el `id` no existe). `actualizar(null)` → `NullPointerException`; un objeto con `id` `null` → `IllegalArgumentException`.
- `eliminar(Integer id)`: devuelve las **filas afectadas** (`1`, o `0` si no existe). `eliminar(null)` → `NullPointerException`. Borrar una fila que tiene matrículas → `DaoException` (clave foránea).

### Consultas

Todos los empates de los `ORDER BY` están indicados: respétalos. Siempre con `?`, nunca concatenando valores en el SQL.

| Método | Qué devuelve | Orden | Parámetro `null` (o en blanco) |
|---|---|---|---|
| `IAlumnoDao.buscarPorNombre(String texto)` | alumnos cuyo nombre **contiene** el texto sin distinguir mayúsculas: `lower(nombre) LIKE lower('%' \|\| ? \|\| '%')` | `nombre`, `id` | lista vacía |
| `ICursoDao.listarPorNivel(String nivel)` | cursos con `nivel = ?` | `precio`, `id` | lista vacía |
| `ICursoDao.listarConPrecioHasta(BigDecimal maximo)` | cursos con `precio <= ?` (**el máximo se incluye**) | `precio DESC`, `id` | lista vacía |
| `ICursoDao.listarDeAlumno(Integer idAlumno)` | cursos en los que está matriculado el alumno (`cursos JOIN matriculas`), con el mismo `map` que `Curso` | `titulo` | lista vacía |
| `IMatriculaDao.listarPorAlumno(Integer idAlumno)` | matrículas con `id_alumno = ?` | `fecha`, `id` | lista vacía |
| `IMatriculaDao.calcularTotalPagado(Integer idAlumno)` | `COALESCE(SUM(importe), 0)` como `BigDecimal`, **nunca `null`** | — | `BigDecimal.ZERO` (también si no tiene matrículas) |
| `IMatriculaDao.contarAlumnosPorCurso()` | `LinkedHashMap` **título → número de matrículas**: `cursos LEFT JOIN matriculas`, `COUNT(m.id)`, `GROUP BY c.id`. Salen **también los cursos sin matrículas, con 0** | `c.id` | Sin cursos: mapa vacío (nunca `null`) |

Los tests comparan los `BigDecimal` con `compareTo`.

## Orden recomendado

1. **Compila antes de nada**: `mvn clean test`. El proyecto compila y los tests se ejecutan (casi todos en rojo). Repítelo a menudo y antes de entregar.
2. Lee `schema.sql`, los modelos, las interfaces y **los tests** (`BaseDaoTest` prepara una base de datos temporal por cada test con el esquema de `src/test/resources`).
3. Practica las consultas de `consultas.sql` sobre `datos/academia.sqlite3` (no puntúan, pero luego solo hay que copiarlas).
4. `AbstractDao.obtenerConexion()`: comprobaciones de la url y del fichero, conexión y `PRAGMA`.
5. `AbstractDao.findAll(String sql, Object... valores)`: el `for` con `setObject` y `return map(rs)`. Después `ejecutar`, `insertarYDevolverId` y `consultarDecimal`.
6. `AlumnoDao`: `extends AbstractDao<Alumno>`, `super(url)`, `map` (con el `while` y las columnas por nombre) y el CRUD → ejecuta `AlumnoDaoTest`. De `ConexionDaoTest` ya tienen que pasar `urlNulaLanzaIllegalArgumentAlUsarElDaoTest`, `carpetaEnLugarDeFicheroLanzaIllegalArgumentTest` y `daoExceptionEsRuntimeYGuardaLaCausaTest`; los otros 5 usan `CursoDao` o `MatriculaDao` y se ponen en verde en el paso 7.
7. `CursoDao` (CRUD) → `MatriculaDao` (CRUD), ejecutando sus tests después de cada uno. Al terminar `MatriculaDao`, ejecuta otra vez `ConexionDaoTest` e `IntegracionDaoTest`.
8. Consultas, de menos a más difícil: `listarPorNivel`, `listarConPrecioHasta`, `listarPorAlumno`, `buscarPorNombre`, `calcularTotalPagado`, `listarDeAlumno` y `contarAlumnosPorCurso` (este necesita un método nuevo en el padre que devuelva un `LinkedHashMap`).
9. Los últimos 10 minutos: JavaDoc de los métodos y constructores `public` de los DAO y `mvn clean test` final.

## Pruebas

| Fichero | Tests | Qué cubre |
|---|---:|---|
| `ConexionDaoTest` | 8 | url `null` y de otro gestor, fichero inexistente (sin crearlo), carpeta, claves foráneas al insertar y al borrar, `DaoException`, varias operaciones seguidas |
| `AlumnoDaoTest` | 14 | CRUD, nulos, `NOT NULL` con la causa, teléfono opcional, `buscarPorNombre` |
| `CursoDaoTest` | 13 | CRUD, `BigDecimal`, `CHECK`, `listarPorNivel`, `listarConPrecioHasta`, `listarDeAlumno` |
| `MatriculaDaoTest` | 12 | CRUD, `CHECK`, `listarPorAlumno`, `calcularTotalPagado`, `contarAlumnosPorCurso` |
| `IntegracionDaoTest` | 5 | varios DAO juntos, borrados con dependencias, datos de dos alumnos sin mezclar |
| **Total visibles** | **52** | |

`BaseDaoTest.java` solo contiene la preparación y limpieza comunes (`@BeforeEach` / `@AfterEach`) y unos métodos para insertar datos de prueba; no añade casos. Cada test lleva un comentario JavaDoc que explica el escenario y su nombre sigue la convención **lowerCamelCase descriptivo con sufijo `Test`**.

Para ejecutar una sola clase: `mvn -Dtest=AlumnoDaoTest test`. Para una única prueba: `mvn -Dtest=AlumnoDaoTest#insertarDevuelveIdPositivoTest test`. Para toda la suite: `mvn clean test`. El informe queda en `target/surefire-reports/`.

## Criterios de corrección

- **Si el proyecto no compila (código o tests), la nota es 0.**
- Tests: **nota = 9 × (tests superados / tests totales)**. En la corrección se añaden **17 tests ocultos** con casos límite del contrato de este README: inyección SQL, mayúsculas, ceros, `null`, duplicados, `CHECK` e ids que no existen.
- JavaDoc: **1 punto**, que **solo cuenta si pasan más del 85 % de los tests**. Se comprueba que todo método y todo constructor `public` de `src/main/java` tenga un comentario `/** ... */` justo encima (puede haber anotaciones como `@Override` entre el comentario y el método). Se puntúa la parte proporcional de los documentados, pero **si cualquier JavaDoc de `src/main` contiene la palabra «todo», el JavaDoc vale 0** (el corrector también anula «pendiente», «rellenar» y «documentar»). Los `// TODO` de línea no son JavaDoc. Basta un comentario de una línea, por ejemplo `/** Lista los alumnos ordenados por id. */`.
- Se revisa además a mano (el corrector lo avisa, pero no cambia la nota): el JDBC centralizado en `AbstractDao` y los DAO heredando de él, columnas leídas por **nombre** en `map`, sin `throws` en las cabeceras y sin valores concatenados en el SQL.
- **No se modifican** las firmas de las interfaces, los constructores de los DAO (`public AlumnoDao(String url)`...), los modelos, `DaoException`, los tests, el `pom.xml` ni los `schema.sql`.

## Entrega

Comprime la carpeta del proyecto sin `target/` con el nombre `academia-nombre-apellido`.
