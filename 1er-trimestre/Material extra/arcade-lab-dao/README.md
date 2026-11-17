# ArcadeLab DAO — Evaluación práctica: AbstractDao con JDBC y SQLite

**Duración:** **90 minutos**  
**Modalidad:** Individual  
**Puntuación máxima:** **10 puntos** (9 de funcionalidad + 1 de JavaDoc condicionado)  
**Tecnologías:** Java 17, Maven, JUnit 5, SQLite y JDBC (`sqlite-jdbc`).

## 1. Situación y objetivo

Un salón recreativo guarda en una base de datos **SQLite** su catálogo de **videojuegos** y las **plataformas** en las que se juegan. La aplicación ya tiene la conexión, las tablas y los modelos, pero cada DAO repetía el mismo código JDBC en todos sus métodos: abrir la conexión, preparar la sentencia, ejecutarla, recorrer el `ResultSet` y construir los objetos.

Tu tarea es **refactorizar la capa de acceso a datos**: escribir una clase padre genérica `AbstractDao<T>` con la lógica común, hacer que `PlataformaDao` y `VideojuegoDao` hereden de ella y solo definan su propio mapeo, y completar un `VideojuegoService` que valida antes de guardar.

Los bloques son **independientes en su calificación**: puedes hacerlos en cualquier orden y obtener puntuación en uno aunque otro no funcione (salvo que el proyecto deje de compilar). La base de datos de trabajo es `datos/arcade.sqlite3`. Los ejemplos y contratos de este documento son los que prevalecen para esta evaluación.

## 2. Material que recibes

- Proyecto Maven con dependencias y tests preparados.
- `config/ConexionBD`: abre la conexión y crea las tablas. **La completas tú**: no tiene puntos propios, pero sin ella no funciona nada. Tiene sus propios tests en `ConexionBDTest`.
- `exception/AplicationException`: excepción de la aplicación, con los constructores `(String mensaje)` y `(String mensaje, Throwable causa)`. **Resuelta**.
- `model/Plataforma` y `model/Videojuego`: clases con atributos `final`, constructor y getters. **Resueltas**.
- `Main`: arranque de la aplicación. No evaluable.
- Esqueletos de las clases evaluables: `dao/AbstractDao`, `dao/PlataformaDao`, `dao/VideojuegoDao` y `service/VideojuegoService`.
- `sql/schema.sql` (esquema de referencia) y `calcular_nota.py` (autoevaluación).

### Organización del proyecto

```text
arcade-lab-dao/
├── pom.xml
├── calcular_nota.py
├── sql/schema.sql
├── datos/arcade.sqlite3                     # Se crea al ejecutar Main
└── src/
    ├── main/java/es/ejemplo/arcade/
    │   ├── Main.java                        # No evaluable
    │   ├── config/ConexionBD.java           # La completas tú (sin puntos propios)
    │   ├── exception/AplicationException.java   # Entregada
    │   ├── model/
    │   │   ├── Plataforma.java              # Entregada
    │   │   └── Videojuego.java              # Entregada
    │   ├── dao/
    │   │   ├── AbstractDao.java             # Evaluable
    │   │   ├── PlataformaDao.java           # Evaluable
    │   │   └── VideojuegoDao.java           # Evaluable
    │   └── service/VideojuegoService.java   # Evaluable
    └── test/java/es/ejemplo/arcade/         # Pruebas por bloque
        └── support/                         # BaseDatosTest y Semilla
```

> Respeta siempre los paquetes, los nombres y las firmas exactas de los esqueletos proporcionados.

### Modelo de datos

```java
public class Plataforma {
    private final String codigo;
    private final String nombre;
    private final String fabricante;
    private final int anio;
    public Plataforma(String codigo, String nombre, String fabricante, int anio) { ... }
    // getCodigo(), getNombre(), getFabricante(), getAnio()
}

public class Videojuego {
    private final int id;
    private final String titulo;
    private final double precio;
    private final boolean multijugador;
    private final int nota;
    private final String plataforma;
    public Videojuego(int id, String titulo, double precio, boolean multijugador, int nota, String plataforma) { ... }
    // getId(), getTitulo(), getPrecio(), isMultijugador(), getNota(), getPlataforma()
}
```

El identificador de un videojuego es `id` (`int`) y el de una plataforma es `codigo` (`String`). La columna `plataforma` de un videojuego guarda el `codigo` de su plataforma.

### Esquema (`sql/schema.sql`)

```sql
CREATE TABLE IF NOT EXISTS plataforma (
    codigo     TEXT    PRIMARY KEY NOT NULL,
    nombre     TEXT    NOT NULL,
    fabricante TEXT    NOT NULL,
    anio       INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS videojuego (
    id           INTEGER PRIMARY KEY NOT NULL,
    titulo       TEXT    NOT NULL,
    precio       REAL    NOT NULL,
    multijugador INTEGER NOT NULL,
    nota         INTEGER NOT NULL,
    plataforma   TEXT    NOT NULL
);
```

SQLite no tiene tipo booleano: `multijugador` guarda `1` (true) o `0` (false). No hay claves foráneas activas: la base no comprueba que la plataforma de un videojuego exista (eso lo hace el Service).

### Datos iniciales

| Código | Nombre | Fabricante | Año |
|---|---|---|---:|
| PS5 | PlayStation 5 | Sony | 2020 |
| SWITCH | Nintendo Switch | Nintendo | 2017 |
| PC | PC | Varios | 1981 |
| XSX | Xbox Series X | Microsoft | 2020 |

| Id | Título | Precio | Multijugador | Nota | Plataforma |
|---:|---|---:|---|---:|---|
| 1 | Baldur's Gate 3 | 59.99 | sí | 96 | PC |
| 2 | Mario Kart 8 Deluxe | 49.99 | sí | 92 | SWITCH |
| 3 | Astro Bot | 39.99 | no | 94 | PS5 |
| 4 | Hollow Knight: Silksong | 19.99 | no | 88 | SWITCH |
| 5 | Assassin's Creed Shadows | 69.99 | no | 81 | PS5 |
| 6 | Fortnite | 0.00 | sí | 72 | PC |
| 7 | Hades II | 39.99 | no | 90 | PC |

Cada test trabaja con una base nueva en una carpeta temporal y carga estos datos con JDBC propio (`support/Semilla`), así que una escritura no afecta a las pruebas siguientes.

## 3. Infraestructura y contrato

### `ConexionBD` — la completas tú (sin puntos propios)

```java
public class ConexionBD {
    public ConexionBD(String url)
    public Connection obtenerConexion()   // crea la carpeta de la base si no existe y conecta
    public void inicializar()             // CREATE TABLE IF NOT EXISTS de las dos tablas
}
```

`obtenerConexion()` devuelve una conexión **abierta**: quien la pide la cierra (try-with-resources). Cualquier error se lanza como `AplicationException`. La url tiene siempre la forma `jdbc:sqlite:<ruta del fichero>`.

### `AbstractDao<T>` — la clase padre que tienes que completar

Antes, cada método de cada DAO repetía los mismos pasos:

```java
try (Connection connection = this.obtenerConexion();
     PreparedStatement preparedStatement = connection.prepareStatement("SELECT ...")) {
    // set de los ?
    ResultSet rs = preparedStatement.executeQuery();
    while (rs.next()) {
        // leer columnas, construir el objeto y añadirlo a la lista
    }
}
```

Ahora el código común se escribe **una sola vez** en una clase padre genérica, según este diagrama:

```text
                      AbstractDao<T>  (extends ConexionBD)
      + consultar(PreparedStatement ps): List<T>
      + actualizar(PreparedStatement ps): int
      # mapRsToClass(ResultSet rs): T            «abstracto»
                    /                         \
   PlataformaDao                                VideojuegoDao
   extends AbstractDao<Plataforma>              extends AbstractDao<Videojuego>
   + mapRsToClass(rs): Plataforma               + mapRsToClass(rs): Videojuego
```

En el diagrama, `+` es `public` y `#` es `protected`.

```java
public abstract class AbstractDao<T> extends ConexionBD {
    public AbstractDao(String url)
    protected abstract T mapRsToClass(ResultSet rs) throws SQLException;
    public List<T> consultar(PreparedStatement ps)
    public int actualizar(PreparedStatement ps)
}
```

- `consultar` **recibe la sentencia ya preparada**: el DAO hijo abre la conexión, prepara el `SELECT`, hace los `set` de los `?` y se la pasa al padre. El padre la ejecuta, recorre el `ResultSet` y convierte cada fila llamando a `mapRsToClass`.
- `actualizar` hace lo mismo con un `INSERT`, `UPDATE` o `DELETE` ya preparado y devuelve **cuántas filas ha cambiado**.
- `mapRsToClass` es **abstracto**: cada DAO hijo escribe cómo se convierte **su** fila en **su** objeto.
- Quien abre la conexión y el `PreparedStatement` (el hijo) es quien los cierra.

## 4. Bloques evaluables y puntuación

Se dispone de **90 minutos** en total. La distribución temporal es orientativa.

| Bloque | Entrega | Puntos | Tiempo sugerido |
|---|---|---:|---:|
| 1. AbstractDao | `consultar`, `actualizar` y el mapeo abstracto | **2,0** | 20 min |
| 2. Plataformas | `PlataformaDao`: CRUD con id `String` usando el padre | **1,5** | 15 min |
| 3. Videojuegos | `VideojuegoDao`: herencia, CRUD y búsquedas que devuelven `Videojuego` | **2,5** | 25 min |
| 4. Consultas | `VideojuegoDao`: consultas que no devuelven `Videojuego` | **1,5** | 15 min |
| 5. Service | `VideojuegoService`: validaciones y reglas de negocio | **1,5** | 10 min |
| **Subtotal funcional** | **Pruebas JUnit ponderadas por bloques** | **9,0** | **85 min** |
| Documentación JavaDoc | Documentación de `AbstractDao`, solo si supera el umbral de tests | **1,0** | **5 min** |
| **Total** | | **10,0** | **90 min** |

### Bloque 1 — `AbstractDao<T>` (2 puntos)

1. `consultar` recibe la sentencia SQL del `SELECT`, la ejecuta y devuelve una lista con un objeto `T` por cada fila, **en el mismo orden** en que las devuelve la consulta.
2. Cada fila se convierte con `mapRsToClass(rs)`: el padre no sabe qué columnas tiene la tabla; lo sabe el hijo.
3. Si la consulta no devuelve filas, la lista está **vacía**, nunca es `null`.
4. Si la sentencia ya trae parámetros (`?`) rellenos, se respetan: el padre no prepara nada ni hace ningún `set`.
5. `actualizar` ejecuta la sentencia y devuelve el número de filas cambiadas: `2` si un `UPDATE` afecta a dos filas, `0` si no afecta a ninguna, `1` tras un `INSERT`.
6. Cualquier `SQLException` (sentencia cerrada, clave primaria repetida, columna que no existe...) se lanza como `AplicationException`, con la excepción original como causa.

### Bloque 2 — `PlataformaDao` (1,5 puntos)

`PlataformaDao extends AbstractDao<Plataforma>`. Todos los métodos preparan su sentencia y usan `consultar` o `actualizar` del padre.

```java
List<Plataforma> listar();
Optional<Plataforma> buscarPorCodigo(String codigo);
boolean insertar(Plataforma plataforma);
boolean actualizar(Plataforma plataforma);
boolean eliminar(String codigo);
```

1. `listar()` devuelve las plataformas **ordenadas por código**.
2. `buscarPorCodigo(codigo)` busca el código **exacto**; `Optional.empty()` si no existe.
3. `insertar` devuelve `true` si inserta la fila. Si el código ya existe, se lanza `AplicationException` (la clave primaria hace fallar a SQLite).
4. `actualizar` cambia `nombre`, `fabricante` y `anio` de la plataforma con ese código; devuelve `false` si no existe.
5. `eliminar` devuelve `true` si borra la fila y `false` si no existe.
6. Atención al orden de `nombre` y `fabricante`: los dos son `String`.

### Bloque 3 — `VideojuegoDao`: lo que devuelve `Videojuego` (2,5 puntos)

El esqueleto de `VideojuegoDao` **todavía no hereda** de nadie. Tiene que quedar como `PlataformaDao`: `extends AbstractDao<Videojuego>`, constructor que llama al del padre y su propio `mapRsToClass`. Todos los métodos de este bloque usan `consultar` o `actualizar` del padre.

```java
List<Videojuego> listar();
Optional<Videojuego> buscarPorId(int id);
boolean insertar(Videojuego videojuego);
boolean actualizar(Videojuego videojuego);
boolean eliminar(int id);
List<Videojuego> buscarPorPlataforma(String codigo);
List<Videojuego> multijugadorHasta(double precioMaximo);
List<Videojuego> masCaros(int n);
```

**Semántica obligatoria:**

- `listar()`: todos los videojuegos **ordenados por id**.
- `buscarPorId(id)`: el videojuego con ese id; `Optional.empty()` si no existe.
- `insertar`: `true` si inserta. Si el id ya existe, `AplicationException`.
- `actualizar`: cambia `titulo`, `precio`, `multijugador`, `nota` y `plataforma` del videojuego con ese id; `false` si no existe.
- `eliminar`: `true` si borra y `false` si no existe.
- `buscarPorPlataforma(codigo)`: los videojuegos de esa plataforma ordenados por id. `buscarPorPlataforma("SWITCH")` devuelve los ids 2 y 4.
- `multijugadorHasta(precioMaximo)`: videojuegos multijugador con precio **menor o igual** que el máximo, ordenados por precio y, si empatan, por id. Con 49.99 devuelve Fortnite y Mario Kart (ids 6 y 2).
- `masCaros(n)`: los `n` videojuegos más caros, de mayor a menor precio y, si empatan, por id **de menor a mayor**. Con 5 devuelve los ids 5, 1, 2, 3 y 7. Si `n` es mayor que el número de videojuegos, devuelve todos; **si `n` es 0 o negativo, una lista vacía**.
- Sin coincidencias, las listas se devuelven **vacías**, nunca `null`.

### Bloque 4 — `VideojuegoDao`: consultas que no devuelven `Videojuego` (1,5 puntos)

Estas consultas devuelven números o textos, no objetos `Videojuego`, así que **no pueden usar `consultar`** (que siempre devuelve `List<Videojuego>`). Se escriben con el molde de siempre: conexión, `PreparedStatement`, `executeQuery` y lectura del `ResultSet`.

```java
int contar();
double mediaPrecio();
Map<String, Integer> contarPorPlataforma();
List<String> plataformasConAlMenos(int minimo);
List<String> titulosDeFabricante(String fabricante);
```

Las consultas se resuelven **en SQL** (`WHERE`, `JOIN`, `GROUP BY`, `HAVING`, `ORDER BY`, `LIMIT`, funciones de agregado). No se admite cargar todas las filas y filtrar, agrupar, ordenar o recortar en Java.

- `contar()`: número de videojuegos; 0 si no hay ninguno.
- `mediaPrecio()`: precio medio de todos los videojuegos; `0.0` si no hay ninguno.
- `contarPorPlataforma()`: número de videojuegos de cada código de plataforma, en un mapa **ordenado por código**. Con los datos iniciales: `{PC=3, PS5=2, SWITCH=2}`. Las plataformas sin videojuegos no aparecen.
- `plataformasConAlMenos(minimo)`: códigos de plataforma que tienen **al menos** `minimo` videojuegos, ordenados por código. Con 3 devuelve `[PC]`; con 2, `[PC, PS5, SWITCH]`. Las plataformas sin videojuegos no aparecen nunca.
- `titulosDeFabricante(fabricante)`: títulos de los videojuegos cuya plataforma es de ese fabricante (nombre exacto), **ordenados alfabéticamente**. Con `"Sony"` devuelve `[Assassin's Creed Shadows, Astro Bot]`.

### Bloque 5 — `VideojuegoService` (1,5 puntos)

El Service recibe los dos DAO en el constructor (ya está hecho en el esqueleto) y **piensa**: valida y decide antes de llamar al DAO.

```java
static boolean codigoPlataformaValido(String codigo);
void registrar(Videojuego videojuego);
Videojuego obtener(int id);
void rebajar(int id, int porcentaje);
```

1. `codigoPlataformaValido(codigo)`: `true` si el código tiene **entre 2 y 6** caracteres, todos **letras mayúsculas o dígitos** (usa una expresión regular con `String.matches`). `null` no es válido. Válidos: `PS5`, `SWITCH`, `PC`. No válidos: `ps5`, `P`, `ABCDEFG`, `PS-5`.
2. `registrar(videojuego)` lanza `AplicationException`, **sin guardar nada**, si:
   - el videojuego es `null`;
   - el título es `null` o está en blanco;
   - el precio es negativo (0 sí vale);
   - la nota no está entre 0 y 100 (los dos extremos valen);
   - el código de plataforma no es válido;
   - la plataforma no existe;
   - ya existe un videojuego con ese id.

   Si todo es correcto, lo inserta.
3. `obtener(id)`: devuelve el videojuego; si no existe, `AplicationException`.
4. `rebajar(id, porcentaje)`: el porcentaje tiene que estar entre 1 y 90 (los dos extremos valen); si no, `AplicationException`. Si el videojuego no existe, `AplicationException`. El nuevo precio es `precio * (100 - porcentaje) / 100.0` y se guarda con el `actualizar` del DAO, sin cambiar el resto de campos. Ejemplo: rebajar un 20 % Mario Kart deja su precio en 39.992.

## 5. Reglas generales

- **Manda la firma del esqueleto.** Si el texto de este enunciado y la firma de un método del esqueleto no dicen exactamente lo mismo, se respeta la firma: los tests la usan tal cual.
- **No modificar** `AplicationException` ni los modelos: están resueltos y **no reciben puntuación**.
- `AbstractDao` **debe** heredar de `ConexionBD`.
- No cambiar las firmas públicas, los constructores, las visibilidades ni los paquetes de los esqueletos; no editar las pruebas. Si los tests no compilan, la nota es 0.
- `PlataformaDao` y `VideojuegoDao` **deben** heredar de `AbstractDao` con su tipo (`AbstractDao<Plataforma>`, `AbstractDao<Videojuego>`) e implementar `mapRsToClass`. Los métodos que devuelven objetos `T` usan `consultar`/`actualizar` del padre; las consultas que no devuelven `T` siguen el molde clásico.
- La conexión se abre dentro de cada método y se cierra siempre (try-with-resources).
- Usa `PreparedStatement` con parámetros `?`. **No concatenes valores en el SQL**: hay títulos y nombres con apóstrofos.
- Los `SELECT` que se pasan a `consultar` tienen que traer **todas las columnas** que lee `mapRsToClass`.

## 6. Pruebas automáticas

Desde la raíz del proyecto:

```bash
mvn -o -Dtest=ConexionBDTest test   # solo los tests de la conexión
mvn -o test                    # ejecuta los tests
mvn -o clean verify -Pnota     # ejecuta los tests y calcula la nota
```

`-o` trabaja sin conexión, con las dependencias ya descargadas. Si es la primera vez que compilas el proyecto en ese ordenador, quítalo.

Los tests se organizan por bloques independientes. La nota aparece al final de la salida de Maven y se guarda en `target/nota.txt`.

## 7. Rúbrica y fórmula de nota

### Funcionalidad: hasta 9 puntos

```text
Nota funcional = Σ [peso del bloque × (tests superados del bloque / tests del bloque)]
```

| Bloque | Clase de test | Peso |
|---|---|---:|
| abstractdao | `AbstractDaoTest` | 2,0 |
| plataforma | `PlataformaDaoTest` | 1,5 |
| videojuego | `VideojuegoDaoTest` | 2,5 |
| consultas | `ConsultasVideojuegoTest` | 1,5 |
| service | `VideojuegoServiceTest` | 1,5 |

El **porcentaje global de tests superados** sirve únicamente para decidir el acceso al punto de documentación:

```text
Porcentaje de tests = 100 × (n.º total de tests funcionales superados)
                            / (n.º total de tests funcionales evaluados)
```

La evaluación consta de **46 tests funcionales**: con **40 de 46** ya se puede optar al punto de JavaDoc.

### JavaDoc: hasta 1 punto, condicionado

**Solo se revisará y puntuará la documentación si se supera estrictamente el 85 % de los tests funcionales (más del 85 %, no igual).** Si el porcentaje es del 85 % o inferior, la documentación aporta **0 puntos**, aunque esté escrita.

La documentación evaluable es **exclusivamente la clase `AbstractDao`**:

| Criterio | Puntos | Palabras/elementos requeridos |
|---|---:|---|
| Clase `AbstractDao` | 0,2 | `común`, `genérica` o `genérico`, y `DAO` |
| `consultar` | 0,4 | `PreparedStatement`, `lista`, `@return` |
| `mapRsToClass` | 0,4 | `fila`, `objeto`, `@return` |

No se distinguen mayúsculas y da igual escribir las palabras con o sin acento. La documentación debe ser un comentario `/** ... */` inmediatamente anterior a cada declaración, suficientemente descriptivo y sin marcadores `TODO` o `pendiente`.

**Nota final = nota funcional (máximo 9) + documentación admitida (máximo 1).**

### Ejemplo de JavaDoc válido (en otra clase)

```java
/**
 * Acceso a la tabla cliente de la base de datos de la tienda.
 */
public class ClienteDao extends AbstractDao<Cliente> {

    /**
     * Busca un cliente por su DNI exacto, con la letra incluida.
     *
     * @param dni DNI del cliente
     * @return el cliente si existe; Optional.empty() si no hay ninguno con ese DNI
     */
    public Optional<Cliente> buscarPorDni(String dni) {
        // ...
    }
}
```

## 8. Entrega y recomendaciones

- Entrega el proyecto Maven con las clases evaluables completadas, siguiendo el procedimiento indicado por el profesor.
- No incluyas `target/` ni modifiques `pom.xml`.
- Empieza por `AbstractDao`: sin `consultar` no funciona ningún DAO.
- Comprueba primero las lecturas (`listar`, `buscarPor...`) y después las escrituras.
- Si una actividad se complica, continúa con otra: **cada bloque puntúa por separado**.
- Antes de entregar, ejecuta `mvn -o clean verify -Pnota` y comprueba que **compila**.

**Se valorará la corrección del resultado, la reutilización de la clase padre y el respeto a los contratos establecidos.**
