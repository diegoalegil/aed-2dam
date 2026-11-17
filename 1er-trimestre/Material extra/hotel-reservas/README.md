# Hotel: reservas con JDBC y SQLite

Proyecto Java 21 / Maven / JDBC / SQLite / JUnit 5. Hay que implementar el acceso a datos de un hotel: **4 DAO con 31 métodos** (20 CRUD y 11 consultas) más la clase padre `AbstractDao`.

**Tiempo: 100 minutos.** Individual. Se pueden usar apuntes escritos a mano.

## Requisitos y ejecución

- JDK 21 y Maven 3.9+.
- Dependencias: `org.xerial:sqlite-jdbc:3.46.1.0` y `org.junit.jupiter:junit-jupiter:5.11.4`.
- Desde la raíz del proyecto: `mvn clean test`.
- Autoevaluación: `python3 -I calcular_nota.py` (ejecuta `mvn -o clean test` y calcula la nota con los tests visibles).
- Ejemplo de uso:

```java
IHuespedDao dao = new HuespedDao("jdbc:sqlite:datos/hotel.sqlite3");
List<Huesped> huespedes = dao.listar();
```

- La ruta relativa se resuelve desde el directorio de trabajo: en IntelliJ abre **la carpeta del proyecto** (la que tiene el `pom.xml`).

## Modelo de datos

El esquema completo está en `src/main/resources/schema.sql` (y una copia en `src/test/resources/schema.sql`, que es la que usan los tests). Todas las claves primarias son `INTEGER PRIMARY KEY AUTOINCREMENT`.

| Tabla | Columnas | Restricciones |
|---|---|---|
| `huespedes` | `id`, `nombre`, `dni`, `email`, `pais` | `dni` y `email` `UNIQUE`; todo `NOT NULL` |
| `habitaciones` | `id`, `numero`, `tipo`, `planta`, `precio_noche` | `numero` `UNIQUE`; `tipo IN ('INDIVIDUAL','DOBLE','SUITE')`; `planta >= 0`; `precio_noche > 0` |
| `reservas` | `id`, `id_huesped`, `id_habitacion`, `fecha_entrada`, `noches`, `estado` | FK a `huespedes` y a `habitaciones` (`ON DELETE RESTRICT`); `noches > 0`; `estado IN ('CONFIRMADA','CANCELADA','FINALIZADA')` |
| `consumos` | `id`, `id_reserva`, `concepto`, `cantidad`, `precio_unitario`, `fecha` | FK a `reservas` (`ON DELETE RESTRICT`); `cantidad > 0`; `precio_unitario >= 0` |

Relaciones 1:N: un huésped tiene muchas reservas, una habitación tiene muchas reservas y una reserva tiene muchos consumos.

- Los importes (`precio_noche`, `precio_unitario`) son `NUMERIC` en SQLite y `BigDecimal` en Java.
- Las fechas son texto ISO `AAAA-MM-DD`, así que se pueden comparar y ordenar como texto.
- `datos/hotel.sqlite3` es una base de datos de trabajo con datos de ejemplo, para probar las consultas en IntelliJ (pestaña Database) antes de escribirlas en Java. Los tests **no** la usan.

## Diseño y estructura

Paquetes bajo `es.ejemplo.hotel`:

- `modelo`: `Huesped`, `Habitacion`, `Reserva` y `Consumo`. Clases mutables con constructores, getters y setters. **Ya están hechas.**
- `dao`:
  - las interfaces `IHuespedDao`, `IHabitacionDao`, `IReservaDao` e `IConsumoDao` (ya hechas, **no se cambian**);
  - los DAO `HuespedDao`, `HabitacionDao`, `ReservaDao` y `ConsumoDao`: vienen con todos sus métodos lanzando `UnsupportedOperationException`;
  - `DaoException`: la excepción del proyecto, no comprobada, con el constructor `(String mensaje, Throwable causa)`. **Ya viene incluida**: úsala tal cual.
- `infra`: `AbstractDao`. **Viene vacío y hay que implementarlo.**

### AbstractDao y la conexión

`AbstractDao<T>` es el padre de los cuatro DAO y centraliza todo el JDBC:

- recibe la `url` en su constructor (cada DAO hace `super(url)`);
- abre las conexiones, rellena las `?` de los `PreparedStatement`, ejecuta y convierte las `SQLException` en `DaoException`;
- no conoce las columnas de ninguna tabla: obliga a cada hijo a convertir el `ResultSet` con `protected abstract List<T> map(ResultSet rs)`;
- para los `SELECT`, un método `findAll(String query, Object... valores)`. Los demás métodos auxiliares (escrituras, id generado, sumas, agrupaciones...) quedan a tu criterio.

**Comprobaciones al construir cualquier DAO** (en el constructor de `AbstractDao`, no al conectar): un DAO mal construido no debe llegar a existir.

1. Si la `url` es `null` o no empieza por `jdbc:sqlite:` → `IllegalArgumentException`.
2. El fichero de la base de datos tiene que existir → si no, `DaoException`. **No se puede crear**: si se conecta con `DriverManager` a una ruta que no existe, SQLite crea un fichero vacío sin avisar.
3. Tiene que ser una base de datos **del hotel**: tienen que estar las **cuatro** tablas `huespedes`, `habitaciones`, `reservas` y `consumos`. Si falta alguna, o el fichero no es una base de datos SQLite → `DaoException`. Puede haber tablas de más (por ejemplo `sqlite_sequence`, que SQLite crea solo con `AUTOINCREMENT`).

Además, **cada conexión** que se abra tiene que ejecutar `PRAGMA foreign_keys = ON`: en SQLite las claves foráneas vienen apagadas y el `PRAGMA` solo vale para la conexión en la que se ejecuta.

## Contratos comunes a los cuatro DAO

- `listar()` devuelve todas las filas **ordenadas por `id`**. Si no hay filas, una lista vacía (nunca `null`).
- `buscarPorId(Integer id)` devuelve un `Optional`. Si el `id` es `null` o no existe → `Optional.empty()`. Con `null` no se llega a consultar la base de datos.
- `insertar(T)` devuelve el `id` que asigna SQLite **y además lo guarda en el objeto** (`setId`).
- `actualizar(T)` devuelve `true` si ha modificado una fila y `false` si el `id` no existe.
- `eliminar(Integer id)` devuelve `true` si ha borrado una fila y `false` si el `id` no existe. `eliminar(null)` lanza `NullPointerException`, igual que en la clínica.
- Programación defensiva **antes de tocar la base de datos**:
  - `insertar(null)` y `actualizar(null)` → `IllegalArgumentException`;
  - `actualizar` de un objeto sin `id` → `IllegalArgumentException`;
  - los **textos obligatorios** a `null` o en blanco → `IllegalArgumentException`. Son: `nombre`, `dni`, `email` y `pais` (huésped); `numero` y `tipo` (habitación); `fechaEntrada` y `estado` (reserva); `concepto` y `fecha` (consumo).
- El resto de reglas (números y claves foráneas obligatorios, `UNIQUE`, `CHECK`, `FOREIGN KEY`) las controla la base de datos. Cualquier error de SQLite → `DaoException`, **conservando la `SQLException` original como causa**.
- Sin `throws` en las cabeceras: `try/catch` dentro y `throw new DaoException(mensaje, e)`.
- Nunca se concatenan valores en el SQL: siempre `?`.
- Las consultas nunca devuelven `null`: lista vacía, `Optional.empty()` o `0`, también cuando algún parámetro llega a `null`.
- No hay borrado en cascada: para borrar, primero las dependencias (consumos → reservas → huéspedes/habitaciones).

## Interfaces y consultas

Además de las cinco operaciones CRUD, cada interfaz declara sus consultas. Todos los empates de los `ORDER BY` están indicados: respétalos.

**`IHuespedDao`**
- `listarSinReservas()`: huéspedes que no tienen **ninguna** reserva (cuenta cualquier estado, también `CANCELADA`), ordenados por `id`.
- `contarReservasPorHuesped()`: `Map` (un `LinkedHashMap`, que conserva el orden) de **email → número de reservas no canceladas**. Salen **todos** los huéspedes: los que no tienen ninguna reserva válida salen con `0`. Orden: número de reservas de mayor a menor y, en empate, email ascendente.

**`IHabitacionDao`**
- `buscarPorTipoYPrecioMaximo(String tipo, BigDecimal precioMaximo)`: habitaciones de ese tipo con `precio_noche` menor o igual que `precioMaximo`. Orden: `precio_noche` descendente y, en empate, `numero` ascendente.
- `listarMasCarasQueLaMedia()`: habitaciones cuyo `precio_noche` es mayor que la media de todas las habitaciones. Mismo orden que la anterior.
- `nochesPorTipo(int minimoNoches)`: `Map` de **tipo → suma de noches** de las reservas no canceladas. Solo salen los tipos cuya suma llega a `minimoNoches` (incluido). Orden: suma de mayor a menor y, en empate, tipo ascendente.

**`IReservaDao`**
- `buscarEntreFechas(String desde, String hasta)`: reservas con `fecha_entrada` entre las dos fechas, **ambas incluidas**. Orden: `fecha_entrada` ascendente y, en empate, `id`.
- `buscarPorPaisYTipo(String pais, String tipo)`: reservas (de cualquier estado) de huéspedes de ese país en habitaciones de ese tipo. Orden: `fecha_entrada` ascendente y, en empate, `id`.
- `buscarMasLarga()`: la reserva **no cancelada** con más noches. En empate, la de `fecha_entrada` más antigua y, si sigue el empate, la de menor `id`. Si no hay ninguna → `Optional.empty()`.
- `calcularImporteTotal(Integer idReserva)`: `noches × precio_noche` de su habitación **más** la suma de `cantidad × precio_unitario` de sus consumos. Si la reserva no existe o el id es `null` → `0`.

**`IConsumoDao`**
- `listarPorReserva(Integer idReserva)`: consumos de esa reserva. Orden: `fecha` ascendente y, en empate, `id`.
- `calcularTotalReserva(Integer idReserva)`: suma de `cantidad × precio_unitario` de los consumos de la reserva. Sin consumos → `0` (nunca `null`).

## Orden recomendado

1. **Compila antes de nada**: `mvn clean test`. Repítelo cada 20 minutos y antes de entregar.
2. Lee `schema.sql`, los modelos, las interfaces y **los tests** (`BaseDaoTest` prepara una base de datos temporal por cada test con el esquema de `src/test/resources`).
3. `AbstractDao<T>`: constructor con las comprobaciones, conexión con el `PRAGMA`, `findAll(String query, Object... valores)` y `map`. Sin él no funciona ningún DAO.
4. `HuespedDao` (CRUD) → ejecuta `HuespedDaoTest` y `ConexionDaoTest`.
5. `HabitacionDao` (CRUD) → `ReservaDao` (CRUD) → `ConsumoDao` (CRUD), ejecutando sus tests después de cada uno.
6. Consultas, de menos a más difícil: `listarPorReserva`, `buscarEntreFechas`, `buscarPorTipoYPrecioMaximo`, `calcularTotalReserva`, `listarSinReservas`, `buscarPorPaisYTipo`, `listarMasCarasQueLaMedia`, `buscarMasLarga`, `nochesPorTipo`, `contarReservasPorHuesped` y `calcularImporteTotal`.
7. Los últimos 10 minutos: JavaDoc y `mvn clean test` final.

## Pruebas

| Fichero | Tests | Qué cubre |
|---|---:|---|
| `ConexionDaoTest` | 9 | comprobaciones de la url y del fichero, tablas, claves foráneas, `DaoException` |
| `HuespedDaoTest` | 14 | CRUD, validaciones, `UNIQUE`, anti-join y recuento con ceros |
| `HabitacionDaoTest` | 12 | CRUD, `BigDecimal`, `CHECK`, filtro con desempate, inyección SQL, subconsulta, `GROUP BY` + `HAVING` |
| `ReservaDaoTest` | 12 | CRUD, `CHECK`, FK, fechas, `JOIN` de tres tablas, máximo con desempate, importe total |
| `ConsumoDaoTest` | 9 | CRUD, validaciones, orden, sumas |
| `IntegracionDaoTest` | 5 | varios DAO juntos, borrados con dependencias |
| **Total visibles** | **61** | |

Para ejecutar una sola clase: `mvn -Dtest=HuespedDaoTest test`.

## Criterios de corrección

- **Si el proyecto no compila (código o tests), la nota es 0.**
- Tests: **9 × (tests superados / tests totales)**. En la corrección se añaden **tests ocultos** (unos 40) con casos límite: nulos en todos los parámetros, ids que no existen, textos en blanco, duplicados, claves foráneas, empates, ceros y sumas sin filas.
- JavaDoc: **1 punto**, que **solo cuenta si se supera más del 85 % de los tests**. Se comprueba que **todo método y todo constructor `public` de `src/main/java`** tenga un comentario `/** ... */` justo encima (puede haber anotaciones como `@Override` entre el comentario y el método). Se puntúa la parte proporcional, pero si **cualquier** JavaDoc de `src/main` contiene la palabra «todo» o «pendiente» (en mayúsculas o minúsculas), el JavaDoc vale 0.
- Se revisa además a mano: el JDBC centralizado en `AbstractDao`, columnas leídas por **nombre**, sin `throws` en las cabeceras y sin valores concatenados en el SQL.
- **No se modifican** las firmas de las interfaces, los constructores de los DAO, los modelos, los tests, el `pom.xml` ni los `schema.sql`.

## Entrega

Comprime la carpeta del proyecto sin `target/` con el nombre `hotel-nombre-apellido`.
