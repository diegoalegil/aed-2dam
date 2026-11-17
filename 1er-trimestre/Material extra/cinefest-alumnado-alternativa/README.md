# CineFest: guía completa para sacar un 10 en el examen de ORM

**Cómo usar esta guía**
- Léela en orden: primero el plan y las 6 trampas, luego cada parte en el orden en que harás el examen.
- El enunciado original del profe está en `ENUNCIADO_PROFE.md`. El código de esta carpeta está comentado línea a línea y tiene Javadoc en cada método público: úsalo como modelo.
- `src/main/java/es/ejemplo/cinefest/app/Main.java` prueba todos los métodos con una copia de la base (botón ▶ de IntelliJ).

**Índice**
1. Plan del examen (para el 5 y para el 10) · Las 6 trampas que más cuestan
2. Arranque: `persistence.xml` y `OrmFactory`
3. Entidades desde `schema.sql` (también con relaciones)
4. Mappers
5. El DAO: constructor y CRUD
6. Operaciones con reglas (transacciones)
7. Consultas: la regla de los 4 pasos, los moldes, el traductor SQL → JPQL, las 28 de CineFest y 9 extra
8. `consultas.sql`: el traductor al revés
9. Javadoc
10. Errores frecuentes → arreglo
11. Comprobar sin tests: el `Main`
12. Checklist final

## 1. Plan del examen

**Para qué:** saber qué te asegura el 5, qué te sube al 10 y en qué orden lo haces.

### Para el 5

1. **Arranque:** `persistence.xml` con la línea de las FK, y las entidades si vienen vacías (apartados 2 y 3).
2. **CRUD de todos los DAO (3 puntos):** es el mismo molde copiado N veces.
3. **La operación sencilla con reglas (1 punto),** como `cancelarEntrada`: el `actualizar` con un `if` por regla.
4. **Javadoc en todos los métodos públicos (1 punto):** guárdate los últimos 10 minutos para esto. Son puntos casi regalados.
5. **Margen:** las consultas fáciles (`where` + `order by`, un `join`) suben un poco más.

Con eso estás en un 5-6, y todo lo de esta lista ya lo has practicado.

### Para el 10, además

6. **Todas las consultas,** también las de `having`, `left join` con ceros, `not exists`, top 3 y la media. Antes de elegir el `join`, mira la tabla de resultados del README (apartado 7.1: la regla de los 4 pasos, cara 8).
7. **La operación grande,** como `venderEntrada`: todas las reglas en una sola transacción. Primero el camino que devuelve 1 y después cada `if` con `rollback` y 0.
8. **El SQL en `consultas.sql`,** si lo piden: el traductor al revés, con el nombre de la tabla y de la columna.

### Los primeros 10 minutos

Atajos de Ubuntu. En el Mac, Cmd en vez de Ctrl.

| Min. | Haz | Mira y apunta |
| --- | --- | --- |
| 0-2 | File > Open → la carpeta del `pom.xml` (si pregunta, Trust Project). File > Project Structure → SDK 21. Build > Rebuild Project | Que compila sin rojo. Si hay `src/test`, no lo toques |
| 2-4 | Lee el contrato del README | `insertar` → el id · `actualizar` y `eliminar` → 1 · si no procede → 0 · sin resultados → `Optional.empty()` o lista vacía · «sin propagar» → nunca `throw`: en el `catch`, `return 0` (o `List.of()`, `Optional.empty()`) · qué devuelve cada operación con reglas (1 o el id) |
| 4-5 | Ctrl+Shift+F → `TODO` | Qué viene vacío: DAO, entidades, mappers, `persistence.xml`, `OrmFactory` |
| 5-7 | Abre `persistence.xml` y `OrmFactory` | El nombre de la unidad (igual en los dos) · el método de `OrmFactory` (`crear`) · una `<class>` por entidad · la línea de las FK: si falta, añádela ya |
| 7-10 | Abre un mapper, una entidad y un DAO | Rellena la ficha de nombres |

- No toques firmas, paquetes, constructores escritos ni `src/test`. Si no compila, la nota es 0.
- Los nombres se copian del proyecto, nunca de memoria.

### Ficha de nombres

Cópiala en el papel antes de escribir código, con una fila por entidad. Encima, el nombre de la unidad (en CineFest, `cinefestPU`).

| Entidad (tabla) | PK y su tipo | getId | Mapper: métodos | DAO: id de la firma |
| --- | --- | --- | --- | --- |
| `SedeEntity` (`sedes`) | `idSede`, `Integer`, AUTOINCREMENT | `getIdSede()` | `SedeMapper.desdeEntidad` / `aEntidad` | `SedeDao`, `int id` |
| `VehiculoEntity` (`vehiculos`, taller) | `matricula`, `String`, clave natural | `getMatricula()` | `VehiculoMapper.toDomain` / `toEntity` | `VehiculoDao`, `String id` |

- Manda la firma del DAO, no el modelo. `int id` → `if (id <= 0)` (`int == null` no compila). `Integer id` → `if (id == null || id <= 0)`. `String id` → `if (id == null || id.isBlank())`.
- Clave natural (PK de texto): la entidad va sin `@GeneratedValue` y su `insertar` devuelve 1.

### Plan de tiempo (unos 105 minutos)

| Min. | Bloque | Si no sale a tiempo |
| --- | --- | --- |
| 0-10 | Los primeros 10 minutos | — |
| 10-22 | Arranque: la línea de las FK y las entidades vacías, una a una | Plan B de la relación (apartado 3) |
| 22-45 | CRUD de todos los DAO: el primero entero y probado con el `Main`; los demás, copiando | Deja el `eliminar` del último DAO para el final |
| 45-50 | Operación sencilla (tipo `cancelarEntrada`) | — |
| 50-65 | Consultas fáciles: `where` + `order by`, un `join` | 5 min por consulta. Si no sale, su `return List.of();` y la siguiente |
| 65-80 | Consultas difíciles: `left join` con ceros, `having`, `not exists`, top 3, media | Igual. En el minuto 80, pasa a la operación grande |
| 80-90 | Operación grande (tipo `venderEntrada`) | Que salga al menos el camino del 1 |
| 90-95 | `consultas.sql`, si lo piden | — |
| 95-105 | Javadoc de todos los públicos. Al final, Build > Rebuild Project sin rojo y entregar | Desde el 95 no empiezas nada nuevo |

- Si las entidades vienen hechas, los 12 minutos del arranque pasan a las consultas.
- Si el examen dura menos, recorta los bloques del 10, nunca el CRUD ni el Javadoc.
- Antes de saltar un método, que compile con su `return` original (`0`, `List.of()`, `Optional.empty()`). Apunta en el papel lo que dejas.

## Las 6 trampas que más cuestan

1. **Cambiar una firma, un constructor o el nombre de un método del mapper** → los tests no compilan → 0 en todo. Las firmas no se tocan nunca.
2. **El constructor del DAO viene vacío** (`public XDao(String jdbcUrl) { }`) → si no lo rellenas, todos los métodos fallan. Va el `emf` + `OrmFactory.crear(jdbcUrl)`.
3. **Las entidades vienen vacías y te toca escribirlas con su relación** (casi un 50 % de probabilidad). Cualquiera de estos fallos hace que no arranque nada:
   - un tipo prohibido (`Long`, `Boolean`, `LocalDate`);
   - falta `insertable = false, updatable = false`;
   - un `mappedBy` mal escrito;
   - falta una `<class>` en `persistence.xml`.
4. **Nombres del mapper escritos de memoria** (`toDomain` en vez de `desdeEntidad`) → no compila. Se copian del fichero.
5. **La línea de las FK no viene** → `eliminar` de un padre con hijos devuelve 1 y falla el test. Añade `<property name="hibernate.connection.foreign_keys" value="true"/>`.
6. **Un `return 0` sin `rollback`** → todas las escrituras de después devuelven 0. Antes de cada `return 0` con la transacción abierta: `tx.rollback();`.

## 2. Arranque: persistence.xml y OrmFactory

**Para qué:** `persistence.xml` le dice a Hibernate qué driver, qué dialecto y qué entidades usa. `OrmFactory` lo arranca con la URL de la base. Si falla algo aquí, no funciona ningún DAO.

### persistence.xml

Va en `src/main/resources/META-INF/persistence.xml`. Si no existe: clic derecho en `resources` → New → Directory → `META-INF`. Después, clic derecho en `META-INF` → New → File → `persistence.xml`.

```xml
<persistence xmlns="https://jakarta.ee/xml/ns/persistence" version="3.1">
    <persistence-unit name="xPU" transaction-type="RESOURCE_LOCAL">
        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
        <class>es.ejemplo.x.orm.entity.XEntity</class>
        <properties>
            <property name="jakarta.persistence.jdbc.driver" value="org.sqlite.JDBC"/>
            <property name="hibernate.dialect"
                      value="org.hibernate.community.dialect.SQLiteDialect"/>
            <property name="hibernate.connection.foreign_keys" value="true"/>
            <property name="hibernate.hbm2ddl.auto" value="validate"/>
        </properties>
    </persistence-unit>
</persistence>
```

En CineFest, `xPU` es `cinefestPU` y hay siete `<class>`, una por entidad: `es.ejemplo.cinefest.orm.entity.SedeEntity`, `es.ejemplo.cinefest.orm.entity.SalaEntity`…

**Reglas**
- Si viene vacío, copia el molde entero. Si viene hecho, no lo reescribas: añade solo la línea de las FK y las `<class>` que falten.
- `xPU` es el nombre que usa `OrmFactory`, letra por letra.
- Una `<class>` por entidad, con el paquete completo, debajo de `<provider>`.
- La línea de las FK, siempre. SQLite trae las FK apagadas y, sin ella, `eliminar` borra un padre con hijos y devuelve 1.
- `validate` comprueba al arrancar que tablas, columnas y tipos coinciden con la base. Si no arranca por una entidad que ya venía hecha, cambia `validate` por `none`.
- La URL de la base no va aquí: la pasa `OrmFactory`.
- Comentarios, solo `<!-- … -->`. Nunca `//`.

### OrmFactory

Va en `orm/config/OrmFactory.java`. Escríbelo solo si viene vacío; es el de CineFest:

```java
package es.ejemplo.x.orm.config;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.Map;

public final class OrmFactory {
    private OrmFactory() {}

    public static EntityManagerFactory crear(String jdbcUrl) {
        return Persistence.createEntityManagerFactory("xPU",
                Map.of("jakarta.persistence.jdbc.url", jdbcUrl));
    }
}
```

- Cada DAO lo llama una sola vez, en su constructor: `this.emf = OrmFactory.crear(jdbcUrl);`.
- Si viene hecho, no lo toques. Copia a la ficha el nombre del método (`crear`) y el de la unidad.

### Si pasa X → haz Y

| Si pasa | Haz |
| --- | --- |
| `No Persistence provider for EntityManager named xPU` | Pon el mismo nombre de unidad en `persistence.xml` y en `OrmFactory`. Si ya es igual, el fichero no está en `src/main/resources/META-INF` |
| `Unable to locate persistence units` | El XML está roto. Ve a la línea que dice el mensaje (`line number` o `[row,col]`): una etiqueta sin cerrar o un comentario mal escrito |
| `Schema validation: …` (`wrong column type`, `missing column`, `missing table`) | Una entidad no coincide con `schema.sql`: tabla de errores del apartado 3 |
| `Could not resolve root entity 'XEntity'` · `Unknown entity type` | Añade la `<class>` de esa entidad, con su paquete. Si entre comillas sale la tabla (`'sedes'`), en el JPQL va la clase: `SedeEntity` |
| `… targets the type '…PadreEntity' which does not belong to the same persistence unit` | Añade la `<class>` de la otra entidad de la relación |
| `eliminar` de un padre con hijos devuelve 1 | Añade la línea de las FK |
| Falla todo a la vez, también `listar` | El fallo está en el arranque (este apartado o el 3), no en los DAO |

## 3. Entidades desde schema.sql

**Para qué:** si `orm/entity` viene vacío o con `TODO`, cada entidad se copia de su `CREATE TABLE`. Sin entidades no arranca ningún DAO.

### Orden (el del profe)

1. Empieza por una tabla sin FK (sin `REFERENCES`): su entidad → su `<class>` → arranca con el `Main` → la siguiente.
2. Después, la tabla que tiene la FK (lado N) y, al final, el lado 1 en su padre.
3. Las `<class>` de entidades que aún están vacías no molestan: Hibernate las ignora hasta que tienen `@Entity`.

### Tipos y palabras de schema.sql

| En `schema.sql` | En Java | Nunca (no arranca con `validate`) |
| --- | --- | --- |
| `INTEGER` (ids, FK, números, `0/1`) | `Integer` | `Long`, `Boolean` |
| `TEXT` (también fechas y horas) | `String` | `LocalDate` |
| `REAL` | `Double` | `BigDecimal` |
| `NUMERIC` | `BigDecimal` | `Integer` |

- `AUTOINCREMENT` → `@GeneratedValue(strategy = GenerationType.IDENTITY)`.
- `NOT NULL` → no va en la entidad: va en el `if` de `insertar` y `actualizar` del DAO.
- `UNIQUE` → la base rechaza el repetido → `catch` → 0.
- `REFERENCES` → la línea de las FK y, si piden relaciones, el molde de abajo.
- `CHECK` y `DEFAULT` → nada.

### Molde de la entidad (lado N = la tabla que tiene la FK)

```java
package es.ejemplo.x.orm.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "hijos")                                  // nombre exacto de la tabla
public class HijoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // solo si AUTOINCREMENT
    @Column(name = "id_hijo")
    private Integer idHijo;
    @Column(name = "nombre")                            // un @Column por columna
    private String nombre;
    @Column(name = "id_padre")
    private Integer idPadre;                            // FK: la que se escribe

    @ManyToOne(fetch = FetchType.LAZY)                  // solo si piden relaciones
    @JoinColumn(name = "id_padre", insertable = false, updatable = false)
    private PadreEntity padre;                          // solo para leer

    public HijoEntity() {}                              // vacío obligatorio

    // + getters y setters de todos los campos; de padre, solo el getter
}
```

Lado 1, dentro de `PadreEntity` (sin `@JoinColumn`):

```java
    @OneToMany(mappedBy = "padre", fetch = FetchType.LAZY)   // "padre" = atributo del lado N
    private List<HijoEntity> hijos = new ArrayList<>();      // + solo el getter
```

- Tabla sin FK (como `sedes`): el mismo molde sin `idPadre` ni `padre`. El padre es eso más las dos líneas del lado 1.
- Imports del lado 1: `java.util.ArrayList` y `java.util.List` (Alt+Enter sobre el rojo).
- En CineFest, `SalaEntity` sería el lado N (`id_sede` → `sedes`), con `private SedeEntity sede;`. `SedeEntity` sería el lado 1, con `@OneToMany(mappedBy = "sede", …)` y `private List<SalaEntity> salas`.

Clave natural (PK de texto sin `AUTOINCREMENT`, como una matrícula): el `@Id` va sin `@GeneratedValue`.

```java
    @Id
    @Column(name = "matricula")
    private String matricula;
```

**Reglas**
- `@Table` y cada `@Column`: el nombre exacto de `schema.sql` (cuidado con la s del plural). El atributo, en camelCase y como lo usa el mapper: `fecha_compra` → `fechaCompra` (`getFechaCompra()`).
- Constructor vacío obligatorio. Si la entidad ya trae `protected XEntity() {}`, déjalo así.
- Getters y setters: Alt+Insert (Mac: Cmd+N) → Getter and Setter → marca todos los campos menos `padre` y `hijos`. A esos dos, solo Getter.
- Para cambiar el vínculo, cambia la columna escalar: `e.setIdPadre(nuevoId)`. El objeto `padre` no guarda nada.
- `getPadre()` y `getHijos()`, solo dentro del `try (var em = …)`. Fuera, `LazyInitializationException`.
- Las dos entidades de la relación, cada una con su `<class>`.
- **Plan B** (si la relación no arranca en 12 minutos): borra `padre` y `hijos` y deja solo `Integer idPadre`, como en CineFest. Las consultas no cambian: ya unen con `join PadreEntity p on p.idPadre = h.idPadre`.

### Si pasa X → haz Y

| Si pasa | Haz |
| --- | --- |
| `Schema validation: wrong column type … but expecting [bigint …]` (o `boolean`, `date`) | Tipo prohibido: cámbialo con la tabla de tipos |
| `Schema validation: missing column [x]` o `missing table [x]` | Copia el nombre de `schema.sql` en el `@Column` o en el `@Table` |
| `no such column` o `no such table` (con `none`) | Lo mismo, pero sale al consultar, no al arrancar |
| `Column 'id_padre' is duplicated in mapping` | Añade `insertable = false, updatable = false` al `@JoinColumn` |
| `… is 'mappedBy' a property named 'x' which does not exist` | En `mappedBy`, el nombre del atributo del lado N (`"padre"`), no la columna |
| `… targets the type '…PadreEntity' which is not an '@Entity' type` | El padre aún está vacío: escríbelo primero |
| `No default constructor for entity` | Añade `public XEntity() {}` |
| `LazyInitializationException … no session` | Usa `getPadre()` o `getHijos()` dentro del `try` |

## 4. Mappers

**Para qué:** pasan de la entidad (lo de la base) al modelo (lo de fuera) y al revés. Casi siempre vienen hechos: copia sus dos nombres a la ficha.

Molde de CineFest, por si viene vacío:

```java
package es.ejemplo.x.orm.mapper;

import es.ejemplo.x.modelo.X;
import es.ejemplo.x.orm.entity.XEntity;

public final class XMapper {
    private XMapper() {}
    public static X desdeEntidad(XEntity source) {      // base → fuera: devuelve el modelo
        if (source == null) return null;
        X result = new X();
        result.setIdX(source.getIdX());
        result.setNombre(source.getNombre());           // un set por campo, FK incluida
        return result;
    }
    public static XEntity aEntidad(X source) {          // fuera → base: devuelve la entidad
        if (source == null) return null;
        XEntity result = new XEntity();
        result.setIdX(source.getIdX());
        result.setNombre(source.getNombre());
        return result;
    }
}
```

Si el proyecto los llama `toDomain` y `toEntity`, es el mismo molde: `toDomain` = `desdeEntidad` (devuelve el modelo) y `toEntity` = `aEntidad` (devuelve la entidad).

**Reglas**
- Lectura (`listar`, `buscarPorId` y las consultas que devuelven `List<X>`) → `desdeEntidad`.
- Escritura (`insertar`) → `aEntidad`.
- `actualizar` y `eliminar` → `find`, sin mapper.
- Un `set` por campo, también la FK (`setIdPadre`). Nunca `getPadre()` ni la lista de hijos.
- Si el mapper viene hecho, no cambies sus nombres ni su paquete.

### Si pasa X → haz Y

| Si pasa (al compilar) | Haz |
| --- | --- |
| `incompatible types: XEntity cannot be converted to X` | Mapper al revés en una lectura: pon `desdeEntidad` |
| `incompatible types: X cannot be converted to XEntity` | Mapper al revés en `insertar`: pon `aEntidad` |
| `invalid method reference` · `cannot find symbol … method toDomain()` | Nombre inventado: copia el del mapper |

## 5. El DAO: constructor y CRUD

**Para qué:** es el molde que copias en todos los DAO (el CRUD vale 3 puntos). Solo cambian los nombres: `X`, `XEntity`, `XMapper`, `idX` y un `get`/`set` por columna. En CineFest lo tienes entero en `SedeDao.java`.

### Arriba del DAO

```java
private final EntityManagerFactory emf;
private static final Logger LOG = Logger.getLogger(XDao.class.getName());

public XDao(String jdbcUrl) {                     // si viene vacío, rellénalo sin tocar la firma
    this.emf = OrmFactory.crear(jdbcUrl);
}
```

### Leer: listar, buscarPorId y buscar por un campo UNIQUE (sin transacción)

```java
public List<X> listar() {
    try (var em = emf.createEntityManager()) {
        return em.createQuery("select x from XEntity x order by x.idX", XEntity.class)
                .getResultStream()
                .map(XMapper::desdeEntidad)
                .toList();
    } catch (RuntimeException ex) {
        LOG.warning("listar: " + ex);
        return List.of();
    }
}

public Optional<X> buscarPorId(int id) {
    if (id <= 0) return Optional.empty();
    try (var em = emf.createEntityManager()) {
        return Optional.ofNullable(em.find(XEntity.class, id))
                .map(XMapper::desdeEntidad);
    } catch (RuntimeException ex) {
        LOG.warning("buscarPorId: " + ex);
        return Optional.empty();
    }
}

public Optional<X> buscarPorEmail(String email) {      // email = el campo UNIQUE del README
    if (email == null || email.isBlank()) return Optional.empty();
    try (var em = emf.createEntityManager()) {
        return em.createQuery("select x from XEntity x where x.email = :email", XEntity.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst()
                .map(XMapper::desdeEntidad);
    } catch (RuntimeException ex) {
        LOG.warning("buscarPorEmail: " + ex);
        return Optional.empty();
    }
}
```

### Escribir: insertar, el esqueleto de las tres escrituras (con transacción)

```java
public int insertar(X dato) {
    if (dato == null || dato.getNombre() == null || dato.getNombre().isBlank()) return 0;
    try (var em = emf.createEntityManager()) {
        var tx = em.getTransaction();
        try {
            tx.begin();
            XEntity e = XMapper.aEntidad(dato);
            e.setIdX(null);
            em.persist(e);
            tx.commit();
            return e.getIdX();                       // el id nuevo, no 1
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            LOG.warning("insertar: " + ex);
            return 0;                                // repetido (UNIQUE) o FK mala
        }
    }
}
```

`actualizar` y `eliminar` copian ese esqueleto. Cambian el `if` del principio, lo que va entre `tx.begin()` y el `catch`, y el texto del `LOG`:

```java
// actualizar(X dato): el if de insertar, añadiendo || dato.getIdX() == null
            tx.begin();
            XEntity e = em.find(XEntity.class, dato.getIdX());
            if (e == null) { tx.rollback(); return 0; }
            e.setNombre(dato.getNombre());           // un set por CADA columna menos la PK
            e.setIdPadre(dato.getIdPadre());         // también la FK
            tx.commit();
            return 1;

// eliminar(int id): if (id <= 0) return 0;
            tx.begin();
            XEntity e = em.find(XEntity.class, id);
            if (e == null) { tx.rollback(); return 0; }
            em.remove(e);                            // padre con hijos + FK → catch → 0
            tx.commit();
            return 1;
```

**Reglas**
- **Mapper:** `listar` y los `buscarPor…` → `desdeEntidad`. `insertar` → `aEntidad`. `actualizar` y `eliminar` → `find`, sin mapper. Los nombres se copian del proyecto.
- **El `if` del principio:** `int id` → `id <= 0` · `Integer id` → `id == null || id <= 0` · texto → `== null || isBlank()`, uno por cada `TEXT NOT NULL` de `schema.sql` · número con `CHECK` (aforo > 0) → `|| dato.getAforo() == null || dato.getAforo() <= 0`. En `insertar` el id no se mira: llega `null`.
- **`tx.rollback();` antes de cada `return 0` que va después de `tx.begin()`.** Si falta, la base se queda bloqueada (`SQLITE_BUSY`) y las escrituras siguientes devuelven 0.
- **`insertar`:** `setIdX(null)` antes del `persist` y `return e.getIdX()`. Clave natural (PK de texto, sin AUTOINCREMENT): sin `setIdX(null)`, en el `if` también que la PK no venga vacía, y `return 1`.
- **`actualizar`:** cuenta las columnas del `CREATE TABLE` y pon un `set` por cada una menos la PK, la FK incluida. Sin `merge`: el `commit` guarda los `set`.
- **Nunca** `Optional.of`, `getFirst()` ni `getSingleResult()` para buscar: si no hay fila, revientan. **`listar`:** `order by` lo que diga el README; si no dice nada, por el id.
- **Cada `catch` acaba en su `return`** (`0`, `List.of()`, `Optional.empty()`). Si falta: «missing return statement». «Sin propagar» en el README → `return 0`; un `throw` solo si el README pide lanzar.
- **Al copiar un método a otro DAO,** cambia `XEntity.class`, `XMapper`, los `set` y el texto del `LOG`. Prueba el primer DAO con el `Main` (apartado 11) antes de copiarlo a los demás.

## 6. Operaciones con reglas (transacciones)

**Para qué:** un método que comprueba varias reglas y escribe, todo en una sola transacción. Devuelve `1` si se cumple todo y `0` si falla algo. En CineFest, C29 y C30 valen 2 puntos: cada una vale como unas 6 consultas.

### Operación que cambia: `cancelarEntrada` (C30)

Es el `actualizar` copiado, con un `if` por regla debajo del `if (e == null)`. Es el de tu `EntradaDao`, con los `if` en una línea:

```java
public int cancelarEntrada(int idEntrada) {
    if (idEntrada <= 0) return 0;
    try (var em = emf.createEntityManager()) {
        var tx = em.getTransaction();
        try {
            tx.begin();
            EntradaEntity e = em.find(EntradaEntity.class, idEntrada);
            if (e == null) { tx.rollback(); return 0; }                          // no existe
            if ("CANCELADA".equals(e.getEstado())) { tx.rollback(); return 0; }  // ya cancelada
            e.setEstado("CANCELADA");                                            // nunca remove
            tx.commit();
            return 1;
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            LOG.warning("cancelarEntrada: " + ex);
            return 0;
        }
    }
}
```

Comprobado en una copia: `cancelarEntrada(1)` → `1` (queda `CANCELADA`, siguen las 22 entradas y la sesión 1 pasa a 2 plazas libres); otra vez → `0`; la `5`, que ya venía cancelada → `0`; la `999` → `0`.

### Operación que crea: `venderEntrada` (C29)

1. Escribe primero el camino que devuelve 1: los dos `find`, el `new`, un `set` por columna, `persist`, `commit` y `return 1`.
2. Después mete las reglas del README, en su orden, entre los `find` y el `new`: cada una es un `if (…) { tx.rollback(); return 0; }`.

Es el molde de la cara 4 con A = sesión, B = espectador y C = entrada. Va en `SesionDao` (imports: las cuatro entidades y `Logger`):

```java
public int venderEntrada(int idSesion, int idEspectador, String fechaCompra) {
    if (idSesion <= 0 || idEspectador <= 0
            || fechaCompra == null || fechaCompra.isBlank()) return 0;
    try (var em = emf.createEntityManager()) {
        var tx = em.getTransaction();
        try {
            tx.begin();
            SesionEntity s = em.find(SesionEntity.class, idSesion);
            EspectadorEntity es = em.find(EspectadorEntity.class, idEspectador);
            if (s == null || es == null) { tx.rollback(); return 0; }              // 1 existen
            if (!"PROGRAMADA".equals(s.getEstado())) { tx.rollback(); return 0; }  // 2 programada
            if (es.getActivo() != 1) { tx.rollback(); return 0; }                  // 3 activo
            Long ya = em.createQuery("select count(e) from EntradaEntity e "
                            + "where e.idSesion = :s and e.idEspectador = :es", Long.class)
                    .setParameter("s", idSesion).setParameter("es", idEspectador)
                    .getSingleResult();
            if (ya > 0) { tx.rollback(); return 0; }                               // 4 no repetida
            SalaEntity sa = em.find(SalaEntity.class, s.getIdSala());
            Long vendidas = em.createQuery("select count(e) from EntradaEntity e "
                            + "where e.idSesion = :s and e.estado <> 'CANCELADA'", Long.class)
                    .setParameter("s", idSesion)
                    .getSingleResult();
            if (vendidas >= sa.getAforo()) { tx.rollback(); return 0; }           // 5 plazas
            EntradaEntity e = new EntradaEntity();
            e.setIdSesion(idSesion);
            e.setIdEspectador(idEspectador);
            e.setFechaCompra(fechaCompra);
            e.setEstado("PAGADA");
            em.persist(e);
            tx.commit();
            return 1;                                                              // 1, no el id
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            LOG.warning("venderEntrada: " + ex);
            return 0;
        }
    }
}
```

Comprobado en una copia: `(1, 8, "2026-11-10")` → `1` (entrada 23, `PAGADA`, con su fecha); justo después, `(1, 6, …)` → `0` por aforo; `(2, 8, …)` dos veces → `1` y `0`; `(1, 5, …)`, que tiene su entrada cancelada → `0`; `(8, 8, …)`, sesión cancelada, y `(999, 8, …)` → `0`; `(2, 7, …)`, espectador inactivo, y `(2, 999, …)` → `0`; `(3, 8, …)`, sesión llena → `0`. Después de cada `0`, otra escritura da `1`: los `rollback` están bien.

### Regla del README → código

| El README dice | Escribes (cada `if` con `{ tx.rollback(); return 0; }`, antes del `new`) |
| --- | --- |
| «existe» | `em.find(...)` y `if (a == null)` |
| «está PROGRAMADA» | `if (!"PROGRAMADA".equals(s.getEstado()))` |
| «ya está CANCELADA» | `if ("CANCELADA".equals(e.getEstado()))` |
| «activo» (INTEGER 0/1) | `if (es.getActivo() != 1)` |
| «no tiene ya…» (UNIQUE, aunque esté cancelada) | `Long ya` = `count` de sus entradas con los dos ids, y `if (ya > 0)` |
| «hay plazas» (aforo − válidas) | `Long vendidas` = `count` de las entradas no canceladas de la sesión, y `if (vendidas >= sa.getAforo())` |
| «le quedan bonos» (un número) | `if (b.getBonos() <= 0)` y, antes del `commit`, `b.setBonos(b.getBonos() - 1);` (sin `merge`: lo guarda el `commit`) |

**Reglas**
- Comprueba y escribe en la misma transacción: un `begin` y un `commit`. El README lo avisa: un `SELECT` fuera de ella puede producir sobreventa.
- `tx.rollback();` antes de **cada** `return 0` que va después del `begin`. Puede que los tests no lo vean; el `Main` sí: la escritura siguiente da `0` y tarda unos 3 s.
- `count` → `Long`, con `Long.class`. `vendidas >= sa.getAforo()` compara bien un `Long` con un `Integer` (4 ≥ 5 da `false`; 5 ≥ 5, `true`).
- Estados con el texto delante: `"PROGRAMADA".equals(s.getEstado())`, nunca `==`. Cancelar es `setEstado(...)`, nunca `remove`: el README dice «No borrar la entrada».
- Devuelve lo que diga el README: C29 y C30 → `1`. Si pide el id, `return e.getIdX();` después del `commit`.
- Nunca llames a otro DAO dentro: cada DAO abre su transacción y esa escritura da `0`.
- Padre e hijos a la vez (cara 4), en la misma transacción: `em.persist(padre);` → `hijo.setIdPadre(padre.getIdPadre());` (el id ya está tras el `persist`) → `em.persist(hijo);` → `commit`. Si falla el hijo, el `rollback` quita también el padre.

## 7. Consultas

### 7.1 Antes de escribir: la regla de los 4 pasos (cara 8)

Mira la **tabla de resultados** del README, no solo el título:

1. ¿Sale algún **0** en una columna que cuenta o suma hijos (vendidas, entradas, total)? → `left join`, con el filtro en el `on`. Un 0 que sale de una resta (como «libres» en C15) no cuenta.
2. ¿El **«Total real»** son todos los de la tabla padre (los 8 espectadores, las 12 sesiones)? → `left join`, con el filtro en el `on`.
3. ¿Salen **solo** los que tienen 0 («sin», «nunca»)? → `not exists`.
4. En cualquier otro caso → `join` normal.

| `join` | `left join … on … and filtro` | `not exists (select …)` |
|---|---|---|
| Lo normal, por defecto | «incluyendo», «incluso sin», «incluidos», «aunque sea 0», o un 0 en la tabla | «sin X», «nunca», «que no tienen» |
| «X con su Y», «de un espectador», «top 3», «agotadas», «al menos N» | Salen todos, también los de 0 | Salen solo los de 0 |
| Salen solo los que tienen | Filtro de estado en el `on`, nunca en el `where` | «sin ventas válidas» → la condición va dentro del paréntesis |
| Filtro de estado en el `where` | `count(h.idHijo)` y `coalesce(sum(…), 0)` | |
| CineFest: C06 C07 C08 C10 C18 C20 C21 C23 C24 | CineFest: C12 C13 C15 C19 C22 C25 C26 C27 | CineFest: C09 C16 C17 C28 |

**La trampa:** C13 («Ranking de espectadores…») y C15 («Plazas vendidas y libres…») no dicen «incluyendo» en el título, pero llevan `left join`: C13 tiene «Total real: 8» (todos los espectadores) y C15 tiene «Total real: 12» (todas las sesiones). El 0 que se ve en C15 está en «libres» y no es la pista: la pista es el «Total real». Con `join` normal salen menos filas y el test falla.

### 7.2 Los 3 moldes alrededor y qué tipo sale

Todos van dentro de `try (var em = emf.createEntityManager()) { … } catch (RuntimeException ex) { LOG.warning("metodo: " + ex); return List.of(); }`, como `listar`.

```java
// 1) Lista de entidades → List<X>
return em.createQuery("select x from XEntity x where x.campo = :v order by x.campo", XEntity.class)
        .setParameter("v", valor)
        .getResultStream().map(XMapper::desdeEntidad).toList();

// 2) Varias columnas → List<Object[]>: una cosa en el select por cada columna de «Salida», en su orden
return em.createQuery("select p.nombre, count(h.idHijo) from …", Object[].class)
        .getResultList();

// 3) Un número → Long
Long n = em.createQuery("select count(x) from XEntity x", Long.class).getSingleResult();
```

| En el `select` | Tipo que sale en Java |
|---|---|
| `count`, `count(distinct …)`, `sum` de un Integer, `aforo - count` | `Long` |
| `avg`, `round`, `sum` de un Double | `Double` |
| Una columna normal | su tipo del `.java` (`Integer`, `String`, `Double`) |
| `sum` sin filas | `null` → por eso `coalesce(sum(…), 0)` |

**Reglas**
- Filtro sobre `count`, `sum` o `avg` → `having`. Sobre una columna → `where`.
- Comparar un `count` con un decimal → `count(…) * 1.0 > :media` (si no, `QueryArgumentException`).
- Cada `:nombre` lleva su `.setParameter("nombre", valor)`.
- No es Java: `p.genero`, no `p.getGenero()`; `=` y no `==`.
- La línea «Tablas» del README dice qué entidades van en el `from`; la línea «Orden», el `order by`.
- `count(h.idHijo)`, no `count(*)`, cuando hay `left join`.

### 7.3 El traductor SQL → JPQL

JPQL es el mismo SQL con nombres de Java. Solo cambian 3 cosas: **tabla → Entity con su letra**, **columna → atributo con la letra delante**, **`?` → `:nombre` + `.setParameter`**.

| SQL (`consultas.sql`) | JPQL (lo que va en `createQuery`) |
|---|---|
| `SELECT * FROM peliculas` | `select p from PeliculaEntity p` → `PeliculaEntity.class` + mapper |
| `SELECT titulo, anio FROM peliculas` | `select p.titulo, p.anio from PeliculaEntity p` → `Object[].class` |
| `WHERE genero = ?` | `where p.genero = :genero` → `.setParameter("genero", genero)` |
| `JOIN salas sa ON sa.id_sala = s.id_sala` | `join SalaEntity sa on sa.idSala = s.idSala` |
| `LEFT JOIN entradas e ON e.id_sesion = s.id_sesion AND e.estado <> 'CANCELADA'` | `left join EntradaEntity e on e.idSesion = s.idSesion and e.estado <> 'CANCELADA'` |
| `COUNT(*)` | `count(*)`; con `left join`: `count(e.idEntrada)` |
| `SUM`, `AVG`, `MAX`, `MIN`, `ROUND(AVG(x), 2)`, `COALESCE`, `CASE WHEN`, `IN`, `BETWEEN`, `IS NULL`, `EXISTS`, `DISTINCT`, `LIMIT 3` | igual |
| `LIKE '%' \|\| ? \|\| '%'` | `like concat('%', :t, '%')` |
| `strftime('%Y-%m', fecha)` o `substr(fecha, 1, 7)` | `substring(e.fechaCompra, 1, 7)` |
| `CAST(x AS REAL)` | `x * 1.0` |
| `COUNT(*) AS total … ORDER BY total DESC` | el alias vale en `select` y `order by`; en `having` no: repite `count(…)` |
| `LIMIT ?` (con parámetro) | `.setMaxResults(limite)` |

**No existe en JPQL:** `strftime`, `date('now')`, `group_concat`, `SELECT p.*`.

### 7.4 Las 28 consultas de CineFest resueltas

Cada una: la frase del README que te dice el patrón, el SQL de `consultas.sql`, el JPQL del DAO y lo que sale de verdad con los datos iniciales.

#### C01. Películas ordenadas alfabéticamente
**Frase → patrón:** «ordenadas alfabéticamente» → una tabla y `order by` del texto.  
**Salida:** `titulo, genero, anio`. **Orden:** titulo ASC.
```sql
SELECT titulo, genero, anio
FROM peliculas
ORDER BY titulo;
```
```java
// solo consultas.sql
"select p.titulo, p.genero, p.anio from PeliculaEntity p order by p.titulo"
```
**Sale:** Aurora | Animación | 2026 · Bruma | Drama | 2022 · Ecos | Documental | 2025 · … (9 filas)

#### C02. Espectadores activos por antigüedad
**Frase → patrón:** «activos» + «por antigüedad» → `where activo = 1` + `order by` la fecha de alta.  
**Salida:** `nombre, fecha_alta`. **Orden:** fecha_alta ASC, nombre ASC.
```sql
SELECT nombre, fecha_alta
FROM espectadores
WHERE activo = 1
ORDER BY fecha_alta, nombre;
```
```java
// solo consultas.sql
"select es.nombre, es.fechaAlta from EspectadorEntity es where es.activo = 1 order by es.fechaAlta, es.nombre"
```
**Sale:** Ana | 2026-10-01 · Bruno | 2026-10-02 · Carla | 2026-10-03 · … (7 filas)

#### C03. Películas de un género
**Frase → patrón:** «de un género» → `where p.genero = :genero`.  
**Salida:** `titulo, pais, anio`. **Orden:** titulo ASC.
```sql
SELECT titulo, pais, anio
FROM peliculas
WHERE genero = 'Drama'
ORDER BY titulo;
```
```java
// método PeliculaDao.buscarPorGenero(String genero)
"select p from PeliculaEntity p where p.genero = :genero order by p.titulo"
```
**Sale:** Bruma | Chile | 2022 · Horizonte | España | 2023 · Mareas | España | 2024 (3 filas)

#### C04. Sesiones entre dos fechas
**Frase → patrón:** «entre dos fechas» → `between :desde and :hasta` (incluye los dos extremos; la fecha es String).  
**Salida:** `fecha, hora, id_sesion, precio`. **Orden:** fecha, hora, id_sesion ASC.
```sql
SELECT fecha, hora, id_sesion, precio
FROM sesiones
WHERE fecha BETWEEN '2026-11-13' AND '2026-11-14'
ORDER BY fecha, hora, id_sesion;
```
```java
// en el DAO, SesionDao.sesionesPorFecha(String fecha) recibe una sola fecha: where s.fecha = :fecha
"select s.fecha, s.hora, s.idSesion, s.precio from SesionEntity s where s.fecha between :desde and :hasta order by s.fecha, s.hora, s.idSesion"
```
**Sale:** 2026-11-13 | 17:00 | 3 | 5 · 2026-11-13 | 19:00 | 4 | 6 · 2026-11-14 | 16:00 | 6 | 4 · … (4 filas)

#### C05. Géneros distintos
**Frase → patrón:** «distintos», «sin repetir» → `select distinct`.  
**Salida:** `genero`. **Orden:** genero ASC.
```sql
SELECT DISTINCT genero
FROM peliculas
ORDER BY genero;
```
```java
// solo consultas.sql
"select distinct p.genero from PeliculaEntity p order by p.genero"
```
**Sale:** Animación · Ciencia ficción · Comedia · … (5 filas)

#### C06. Salas con su sede
**Frase → patrón:** «X con su Y» → `join` normal: salen solo los que tienen.  
**Salida:** `sede, sala, aforo`. **Orden:** sede, sala ASC.
```sql
SELECT se.nombre AS sede, sa.nombre AS sala, sa.aforo
FROM salas sa
JOIN sedes se ON se.id_sede = sa.id_sede
ORDER BY sede, sala;
```
```java
// método SalaDao.salasConSede()
"select se.nombre, sa.nombre, sa.aforo from SalaEntity sa join SedeEntity se on se.idSede = sa.idSede order by se.nombre, sa.nombre"
```
**Sale:** Auditorio Atlántico | Sala Azul | 5 · Auditorio Atlántico | Sala Roja | 8 · Centro Cultural Sur | Sala Dunas | 4 · … (4 filas)

#### C07. Sesiones con película, sala y sede
**Frase → patrón:** «sesiones con película, sala y sede» → un `join` por tabla; cada `on`: PK de la nueva = FK de la que ya tienes.  
**Salida:** `id_sesion, pelicula, sede, sala, fecha, hora`. **Orden:** id_sesion ASC.
```sql
SELECT s.id_sesion, p.titulo AS pelicula, se.nombre AS sede, sa.nombre AS sala, s.fecha, s.hora
FROM sesiones s
JOIN peliculas p ON p.id_pelicula = s.id_pelicula
JOIN salas sa ON sa.id_sala = s.id_sala
JOIN sedes se ON se.id_sede = sa.id_sede
ORDER BY s.id_sesion;
```
```java
// método SesionDao.sesionesConDetalles()
"select s.idSesion, p.titulo, se.nombre, sa.nombre, s.fecha, s.hora from SesionEntity s join PeliculaEntity p on p.idPelicula = s.idPelicula join SalaEntity sa on sa.idSala = s.idSala join SedeEntity se on se.idSede = sa.idSede order by s.idSesion"
```
**Sale:** 1 | Mareas | Auditorio Atlántico | Sala Azul | 2026-11-12 | 18:00 · 2 | Órbita | Auditorio Atlántico | Sala Roja | 2026-11-12 | 20:00 · 3 | Horizonte | Centro Cultural Sur | Sala Dunas | 2026-11-13 | 17:00 · … (12 filas)

#### C08. Entradas con espectador y película
**Frase → patrón:** «entradas con espectador y película» → `join` encadenados (la película se llega por la sesión).  
**Salida:** `id_entrada, espectador, pelicula, estado`. **Orden:** id_entrada ASC.
```sql
SELECT e.id_entrada, es.nombre AS espectador, p.titulo AS pelicula, e.estado
FROM entradas e
JOIN espectadores es ON es.id_espectador = e.id_espectador
JOIN sesiones s ON s.id_sesion = e.id_sesion
JOIN peliculas p ON p.id_pelicula = s.id_pelicula
ORDER BY e.id_entrada;
```
```java
// método EntradaDao.entradasConDetalles()
"select e.idEntrada, es.nombre, p.titulo, e.estado from EntradaEntity e join EspectadorEntity es on es.idEspectador = e.idEspectador join SesionEntity s on s.idSesion = e.idSesion join PeliculaEntity p on p.idPelicula = s.idPelicula order by e.idEntrada"
```
**Sale:** 1 | Ana | Mareas | PAGADA · 2 | Bruno | Mareas | PAGADA · 3 | Carla | Mareas | PAGADA · … (22 filas)

#### C09. Espectadores que nunca compraron entradas
**Frase → patrón:** «nunca compraron» → `not exists`.  
**Salida:** `nombre, email`. **Orden:** nombre ASC.
```sql
SELECT es.nombre, es.email
FROM espectadores es
WHERE NOT EXISTS (SELECT 1 FROM entradas e WHERE e.id_espectador = es.id_espectador)
ORDER BY es.nombre;
```
```java
// método EspectadorDao.espectadoresSinEntradas()
"select es from EspectadorEntity es where not exists (select e from EntradaEntity e where e.idEspectador = es.idEspectador) order by es.nombre"
```
**Sale:** Gabriel | gabriel@example.org · Hugo | hugo@example.org (2 filas)

#### C10. Valoraciones con datos de película y espectador
**Frase → patrón:** «valoraciones con datos de película y espectador» → dos `join`.  
**Salida:** `pelicula, espectador, puntuacion`. **Orden:** pelicula, espectador ASC.
```sql
SELECT p.titulo AS pelicula, es.nombre AS espectador, v.puntuacion
FROM valoraciones v
JOIN peliculas p ON p.id_pelicula = v.id_pelicula
JOIN espectadores es ON es.id_espectador = v.id_espectador
ORDER BY pelicula, espectador;
```
```java
// método ValoracionDao.valoracionesConDetalles()
"select p.titulo, es.nombre, v.puntuacion from ValoracionEntity v join PeliculaEntity p on p.idPelicula = v.idPelicula join EspectadorEntity es on es.idEspectador = v.idEspectador order by p.titulo, es.nombre"
```
**Sale:** Aurora | Carla | 5 · Aurora | Elena | 4 · Bruma | Elena | 4 · … (12 filas)

#### C11. Número de películas por género
**Frase → patrón:** «número de … por …» → `count` + `group by`.  
**Salida:** `genero, total_peliculas`. **Orden:** genero ASC.
```sql
SELECT genero, COUNT(*) AS total_peliculas
FROM peliculas
GROUP BY genero
ORDER BY genero;
```
```java
// solo consultas.sql
"select p.genero, count(p.idPelicula) from PeliculaEntity p group by p.genero order by p.genero"
```
**Sale:** Animación | 1 · Ciencia ficción | 1 · Comedia | 1 · … (5 filas)

#### C12. Número de salas y aforo total por sede, incluso sin salas
**Frase → patrón:** «incluso sin salas» → `left join` + `count(sa.idSala)` + `coalesce(sum(…), 0)`.  
**Salida:** `sede, total_salas, aforo_total`. **Orden:** sede ASC.
```sql
SELECT se.nombre AS sede, COUNT(sa.id_sala) AS total_salas, COALESCE(SUM(sa.aforo), 0) AS aforo_total
FROM sedes se
LEFT JOIN salas sa ON sa.id_sede = se.id_sede
GROUP BY se.id_sede, se.nombre
ORDER BY sede;
```
```java
// método SedeDao.aforoTotalPorSede()
"select se.nombre, count(sa.idSala), coalesce(sum(sa.aforo), 0) from SedeEntity se left join SalaEntity sa on sa.idSede = se.idSede group by se.idSede, se.nombre order by se.nombre"
```
**Sale:** Auditorio Atlántico | 2 | 13 · Centro Cultural Sur | 1 | 4 · Cine del Puerto | 1 | 6 (3 filas)  
**Ojo:** `count(sa.idSala)`, no `count(*)`: con `count(*)` la sede sin salas saldría con 1.

#### C13. Ranking de espectadores por entradas no canceladas
**Frase → patrón:** «ranking … no canceladas» y «Total real: 8» = todos los espectadores → `left join` con el filtro en el `on`.  
**Salida:** `espectador, total_entradas`. **Orden:** total_entradas DESC, espectador ASC.
```sql
SELECT es.nombre AS espectador, COUNT(e.id_entrada) AS total_entradas
FROM espectadores es
LEFT JOIN entradas e ON e.id_espectador = es.id_espectador AND e.estado IN ('PAGADA','USADA')
GROUP BY es.id_espectador, es.nombre
ORDER BY total_entradas DESC, espectador;
```
```java
// método EspectadorDao.rankingEspectadores()
"select es.nombre, count(e.idEntrada) from EspectadorEntity es left join EntradaEntity e on e.idEspectador = es.idEspectador and e.estado in ('PAGADA','USADA') group by es.idEspectador, es.nombre order by count(e.idEntrada) desc, es.nombre"
```
**Sale:** Ana | 4 · Bruno | 4 · Carla | 4 · … (8 filas)  
**Ojo:** con `join` normal salen 6 filas en vez de 8.

#### C14. Películas con al menos dos valoraciones y su media
**Frase → patrón:** «al menos dos» sobre un `count` → `having count(…) >= :minimo`.  
**Salida:** `pelicula, numero_valoraciones, media`. **Orden:** media DESC, pelicula ASC.
```sql
SELECT p.titulo AS pelicula, COUNT(v.id_valoracion) AS numero_valoraciones, ROUND(AVG(v.puntuacion), 2) AS media
FROM peliculas p
JOIN valoraciones v ON v.id_pelicula = p.id_pelicula
GROUP BY p.id_pelicula, p.titulo
HAVING COUNT(v.id_valoracion) >= 2
ORDER BY media DESC, pelicula;
```
```java
// método PeliculaDao.mejorValoradas(int minimo)
"select p.titulo, count(v.idValoracion), round(avg(v.puntuacion), 2) from PeliculaEntity p left join ValoracionEntity v on v.idPelicula = p.idPelicula group by p.idPelicula, p.titulo having count(v.idValoracion) >= :minimo order by avg(v.puntuacion) desc, p.titulo"
```
**Sale:** Mareas | 3 | 4.67 · Aurora | 2 | 4.5 · Horizonte | 2 | 4.5 · … (4 filas)  
**Ojo:** sin `round(…, 2)` sale 4.666… y el README pone 4.67.

#### C15. Plazas vendidas y libres por sesión
**Frase → patrón:** «vendidas y libres» y «Total real: 12» = todas las sesiones → `left join` con el filtro en el `on`; libres = aforo − vendidas.  
**Salida:** `id_sesion, aforo, vendidas, libres`. **Orden:** id_sesion ASC.
```sql
SELECT s.id_sesion, sa.aforo, COUNT(e.id_entrada) AS vendidas, sa.aforo - COUNT(e.id_entrada) AS libres
FROM sesiones s
JOIN salas sa ON sa.id_sala = s.id_sala
LEFT JOIN entradas e ON e.id_sesion = s.id_sesion AND e.estado IN ('PAGADA','USADA')
GROUP BY s.id_sesion, sa.aforo
ORDER BY s.id_sesion;
```
```java
// método SesionDao.disponibilidadSesiones()
"select s.idSesion, sa.aforo, count(e.idEntrada), sa.aforo - count(e.idEntrada) from SesionEntity s join SalaEntity sa on sa.idSala = s.idSala left join EntradaEntity e on e.idSesion = s.idSesion and e.estado in ('PAGADA','USADA') group by s.idSesion, sa.aforo order by s.idSesion"
```
**Sale:** 1 | 5 | 4 | 1 · 2 | 8 | 3 | 5 · 3 | 4 | 4 | 0 · … (12 filas)  
**Ojo:** con `join` normal desaparecen las sesiones 8, 9 y 12, que tienen 0 vendidas, y salen 9 filas en vez de 12.

#### C16. Películas sin sesiones
**Frase → patrón:** «sin sesiones» → `not exists`.  
**Salida:** `titulo`. **Orden:** titulo ASC.
```sql
SELECT p.titulo
FROM peliculas p
WHERE NOT EXISTS (SELECT 1 FROM sesiones s WHERE s.id_pelicula = p.id_pelicula)
ORDER BY p.titulo;
```
```java
// método PeliculaDao.peliculasSinSesiones()
"select p from PeliculaEntity p where not exists (select s from SesionEntity s where s.idPelicula = p.idPelicula) order by p.titulo"
```
**Sale:** Islas Invisibles (1 filas)

#### C17. Sesiones programadas sin ventas válidas
**Frase → patrón:** «programadas sin ventas válidas» → `where` del estado + `not exists` con la condición dentro del paréntesis.  
**Salida:** `id_sesion, pelicula, fecha`. **Orden:** id_sesion ASC.
```sql
SELECT s.id_sesion, p.titulo AS pelicula, s.fecha
FROM sesiones s
JOIN peliculas p ON p.id_pelicula = s.id_pelicula
WHERE s.estado = 'PROGRAMADA' AND NOT EXISTS (SELECT 1 FROM entradas e WHERE e.id_sesion = s.id_sesion AND e.estado IN ('PAGADA','USADA'))
ORDER BY s.id_sesion;
```
```java
// método SesionDao.sesionesSinVentas()
"select s.idSesion, p.titulo, s.fecha from SesionEntity s join PeliculaEntity p on p.idPelicula = s.idPelicula where s.estado = 'PROGRAMADA' and not exists (select e from EntradaEntity e where e.idSesion = s.idSesion and e.estado in ('PAGADA','USADA')) order by s.idSesion"
```
**Sale:** 9 | Bruma | 2026-11-16 · 12 | Ecos | 2026-11-17 (2 filas)  
**Ojo:** si el filtro de estado va fuera del paréntesis, cambia el resultado.

#### C18. Sesiones agotadas, sin contar cancelaciones
**Frase → patrón:** «agotadas, sin contar cancelaciones» → `join` + `where` válidas + `having count(…) >= aforo`.  
**Salida:** `id_sesion, pelicula, aforo`. **Orden:** id_sesion ASC.
```sql
SELECT s.id_sesion, p.titulo AS pelicula, sa.aforo
FROM sesiones s
JOIN peliculas p ON p.id_pelicula = s.id_pelicula
JOIN salas sa ON sa.id_sala = s.id_sala
JOIN entradas e ON e.id_sesion = s.id_sesion
WHERE e.estado IN ('PAGADA','USADA')
GROUP BY s.id_sesion, p.titulo, sa.aforo
HAVING COUNT(e.id_entrada) >= sa.aforo
ORDER BY s.id_sesion;
```
```java
// método SesionDao.sesionesAgotadas()
"select s.idSesion, p.titulo, sa.aforo from SesionEntity s join PeliculaEntity p on p.idPelicula = s.idPelicula join SalaEntity sa on sa.idSala = s.idSala join EntradaEntity e on e.idSesion = s.idSesion where e.estado in ('PAGADA','USADA') group by s.idSesion, p.titulo, sa.aforo having count(e.idEntrada) >= sa.aforo order by s.idSesion"
```
**Sale:** 3 | Horizonte | 4 (1 filas)

#### C19. Películas con más ventas válidas que la media de las nueve películas (incluidas las de cero ventas)
**Frase → patrón:** «más ventas que la media … incluidas las de cero» → media en Java con dos `count` + `left join` + `having count(…) * 1.0 > :media`.  
**Salida:** `pelicula, entradas_validas`. **Orden:** entradas_validas DESC, pelicula ASC.
```sql
SELECT p.titulo AS pelicula, COUNT(e.id_entrada) AS entradas_validas
FROM peliculas p
LEFT JOIN sesiones s ON s.id_pelicula = p.id_pelicula
LEFT JOIN entradas e ON e.id_sesion = s.id_sesion AND e.estado IN ('PAGADA','USADA')
GROUP BY p.id_pelicula, p.titulo
HAVING COUNT(e.id_entrada) > (SELECT COUNT(*) * 1.0 / (SELECT COUNT(*) FROM peliculas) FROM entradas WHERE estado IN ('PAGADA','USADA'))
ORDER BY entradas_validas DESC, pelicula;
```
```java
// método PeliculaDao.masVendidasQueMedia()
"select p.titulo, count(e.idEntrada) from PeliculaEntity p left join SesionEntity s on s.idPelicula = p.idPelicula left join EntradaEntity e on e.idSesion = s.idSesion and e.estado in ('PAGADA','USADA') group by p.idPelicula, p.titulo having count(e.idEntrada) * 1.0 > :media order by count(e.idEntrada) desc, p.titulo"
// antes, en Java: Long validas = count de entradas válidas; Long pelis = count de películas;
// double media = validas * 1.0 / pelis;   → .setParameter("media", media)
```
**Sale:** Mareas | 7 · Horizonte | 5 · Órbita | 3 (3 filas)  
**Ojo:** sin `* 1.0` salta `QueryArgumentException … not a whole number`.

#### C20. Espectadores con entradas válidas en al menos dos sedes
**Frase → patrón:** «en al menos dos sedes» → `count(distinct …)` + `having … >= :minimo`.  
**Salida:** `espectador, sedes_distintas`. **Orden:** sedes_distintas DESC, espectador ASC.
```sql
SELECT es.nombre AS espectador, COUNT(DISTINCT sa.id_sede) AS sedes_distintas
FROM espectadores es
JOIN entradas e ON e.id_espectador = es.id_espectador
JOIN sesiones s ON s.id_sesion = e.id_sesion
JOIN salas sa ON sa.id_sala = s.id_sala
WHERE e.estado IN ('PAGADA','USADA')
GROUP BY es.id_espectador, es.nombre
HAVING COUNT(DISTINCT sa.id_sede) >= 2
ORDER BY sedes_distintas DESC, espectador;
```
```java
// método EspectadorDao.espectadoresVariasSedes(int minimo)
"select es.nombre, count(distinct sa.idSede) from EspectadorEntity es join EntradaEntity e on e.idEspectador = es.idEspectador join SesionEntity s on s.idSesion = e.idSesion join SalaEntity sa on sa.idSala = s.idSala where e.estado in ('PAGADA','USADA') group by es.idEspectador, es.nombre having count(distinct sa.idSede) >= :minimo order by count(distinct sa.idSede) desc, es.nombre"
```
**Sale:** Bruno | 3 · Ana | 2 · Carla | 2 · … (6 filas)

#### C21. Top 3 de películas por recaudación de entradas válidas
**Frase → patrón:** «top 3» → `order by … desc` + `setMaxResults(3)`.  
**Salida:** `pelicula, entradas_validas, recaudacion`. **Orden:** recaudacion DESC, pelicula ASC.
```sql
SELECT p.titulo AS pelicula, COUNT(e.id_entrada) AS entradas_validas, SUM(s.precio) AS recaudacion
FROM peliculas p
JOIN sesiones s ON s.id_pelicula = p.id_pelicula
JOIN entradas e ON e.id_sesion = s.id_sesion
WHERE e.estado IN ('PAGADA','USADA')
GROUP BY p.id_pelicula, p.titulo
ORDER BY recaudacion DESC, pelicula
LIMIT 3;
```
```java
// método SesionDao.rankingRecaudacion()
"select p.titulo, count(e.idEntrada), sum(s.precio) from PeliculaEntity p join SesionEntity s on s.idPelicula = p.idPelicula join EntradaEntity e on e.idSesion = s.idSesion where e.estado in ('PAGADA','USADA') group by p.idPelicula, p.titulo order by sum(s.precio) desc, p.titulo"
// + .setMaxResults(3)
```
**Sale:** Mareas | 7 | 45 · Horizonte | 5 | 27 · Órbita | 3 | 24 (3 filas)  
**Ojo:** el 3 va fijo: `setMaxResults(3)`.

#### C22. Recaudación por sede, incluyendo sedes sin ventas
**Frase → patrón:** «incluyendo sedes sin ventas» → `left join` + `coalesce(sum(case when e.idEntrada is not null then s.precio end), 0)`.  
**Salida:** `sede, recaudacion`. **Orden:** recaudacion DESC, sede ASC.
```sql
SELECT se.nombre AS sede, COALESCE(SUM(CASE WHEN e.id_entrada IS NOT NULL THEN s.precio END), 0) AS recaudacion
FROM sedes se
LEFT JOIN salas sa ON sa.id_sede = se.id_sede
LEFT JOIN sesiones s ON s.id_sala = sa.id_sala
LEFT JOIN entradas e ON e.id_sesion = s.id_sesion AND e.estado IN ('PAGADA','USADA')
GROUP BY se.id_sede, se.nombre
ORDER BY recaudacion DESC, sede;
```
```java
// método SesionDao.ingresosPorSede()
"select se.nombre, coalesce(sum(case when e.idEntrada is not null then s.precio end), 0) from SedeEntity se left join SalaEntity sa on sa.idSede = se.idSede left join SesionEntity s on s.idSala = sa.idSala left join EntradaEntity e on e.idSesion = s.idSesion and e.estado in ('PAGADA','USADA') group by se.idSede, se.nombre order by coalesce(sum(case when e.idEntrada is not null then s.precio end), 0) desc, se.nombre"
```
**Sale:** Auditorio Atlántico | 69 · Centro Cultural Sur | 46 · Cine del Puerto | 6 (3 filas)  
**Ojo:** `sum(s.precio)` a secas suma también las sesiones sin entradas.

#### C23. Ventas diarias, excluyendo entradas canceladas
**Frase → patrón:** «ventas diarias, excluyendo canceladas» → `join` + `where` válidas + `group by` la fecha.  
**Salida:** `fecha_compra, entradas_validas, total`. **Orden:** fecha_compra ASC.
```sql
SELECT e.fecha_compra, COUNT(e.id_entrada) AS entradas_validas, SUM(s.precio) AS total
FROM entradas e
JOIN sesiones s ON s.id_sesion = e.id_sesion
WHERE e.estado IN ('PAGADA','USADA')
GROUP BY e.fecha_compra
ORDER BY e.fecha_compra;
```
```java
// método EntradaDao.ventasPorDia()
"select e.fechaCompra, count(e.idEntrada), sum(s.precio) from EntradaEntity e join SesionEntity s on s.idSesion = e.idSesion where e.estado in ('PAGADA','USADA') group by e.fechaCompra order by e.fechaCompra"
```
**Sale:** 2026-11-01 | 2 | 12 · 2026-11-02 | 3 | 22 · 2026-11-03 | 2 | 14 · … (9 filas)

#### C24. Historial de entradas de un espectador
**Frase → patrón:** «historial de un espectador» → `join` + `where e.idEspectador = :id`.  
**Salida:** `pelicula, fecha_sesion, estado_entrada, precio`. **Orden:** fecha_sesion ASC, pelicula ASC.
```sql
SELECT p.titulo AS pelicula, s.fecha AS fecha_sesion, e.estado AS estado_entrada, s.precio
FROM entradas e
JOIN sesiones s ON s.id_sesion = e.id_sesion
JOIN peliculas p ON p.id_pelicula = s.id_pelicula
WHERE e.id_espectador = 1
ORDER BY fecha_sesion, pelicula;
```
```java
// método EntradaDao.entradasPorEspectador(int idEspectador)
"select p.titulo, s.fecha, e.estado, s.precio from EntradaEntity e join SesionEntity s on s.idSesion = e.idSesion join PeliculaEntity p on p.idPelicula = s.idPelicula where e.idEspectador = :id order by s.fecha, p.titulo"
```
**Sale:** Mareas | 2026-11-12 | PAGADA | 6 · Órbita | 2026-11-12 | PAGADA | 8 · Horizonte | 2026-11-13 | PAGADA | 5 · … (4 filas)

#### C25. Informe por fecha de sesión, incluidos días sin ventas
**Frase → patrón:** «incluidos días sin ventas» → `left join` con el filtro en el `on` + `count(distinct case …)`.  
**Salida:** `fecha, sesiones_programadas, entradas_validas, recaudacion`. **Orden:** fecha ASC.
```sql
SELECT s.fecha, COUNT(DISTINCT CASE WHEN s.estado = 'PROGRAMADA' THEN s.id_sesion END) AS sesiones_programadas, COUNT(e.id_entrada) AS entradas_validas, COALESCE(SUM(CASE WHEN e.id_entrada IS NOT NULL THEN s.precio END), 0) AS recaudacion
FROM sesiones s
LEFT JOIN entradas e ON e.id_sesion = s.id_sesion AND e.estado IN ('PAGADA','USADA')
GROUP BY s.fecha
ORDER BY s.fecha;
```
```java
// método SesionDao.informeDiario()
"select s.fecha, count(distinct case when s.estado = 'PROGRAMADA' then s.idSesion end), count(e.idEntrada), coalesce(sum(case when e.idEntrada is not null then s.precio end), 0) from SesionEntity s left join EntradaEntity e on e.idSesion = s.idSesion and e.estado in ('PAGADA','USADA') group by s.fecha order by s.fecha"
```
**Sale:** 2026-11-12 | 2 | 7 | 48 · 2026-11-13 | 2 | 5 | 26 · 2026-11-14 | 2 | 5 | 29 · … (6 filas)  
**Ojo:** sin `distinct`, una sesión con 3 entradas cuenta 3 veces.

#### C26. Media y cantidad de valoraciones por película, incluyendo películas sin valorar
**Frase → patrón:** «incluyendo películas sin valorar» → `left join` + `count(v.idValoracion)` + `round(avg(…), 2)`.  
**Salida:** `pelicula, numero_valoraciones, media`. **Orden:** pelicula ASC.
```sql
SELECT p.titulo AS pelicula, COUNT(v.id_valoracion) AS numero_valoraciones, ROUND(AVG(v.puntuacion), 2) AS media
FROM peliculas p
LEFT JOIN valoraciones v ON v.id_pelicula = p.id_pelicula
GROUP BY p.id_pelicula, p.titulo
ORDER BY pelicula;
```
```java
// método PeliculaDao.peliculasConMediaValoraciones()
"select p.titulo, count(v.idValoracion), round(avg(v.puntuacion), 2) from PeliculaEntity p left join ValoracionEntity v on v.idPelicula = p.idPelicula group by p.idPelicula, p.titulo order by p.titulo"
```
**Sale:** Aurora | 2 | 4.5 · Bruma | 1 | 4 · Ecos | 1 | 5 · … (9 filas)  
**Ojo:** la película sin valoraciones sale con media `null`, no 0.

#### C27. Títulos, sesiones y ventas válidas por género
**Frase → patrón:** «por género» con tres recuentos → `left join` + `count(distinct …)` para no contar doble.  
**Salida:** `genero, titulos, sesiones, entradas_validas`. **Orden:** genero ASC.
```sql
SELECT p.genero, COUNT(DISTINCT p.id_pelicula) AS titulos, COUNT(DISTINCT s.id_sesion) AS sesiones, COUNT(e.id_entrada) AS entradas_validas
FROM peliculas p
LEFT JOIN sesiones s ON s.id_pelicula = p.id_pelicula
LEFT JOIN entradas e ON e.id_sesion = s.id_sesion AND e.estado IN ('PAGADA','USADA')
GROUP BY p.genero
ORDER BY p.genero;
```
```java
// método PeliculaDao.estadisticasGenero()
"select p.genero, count(distinct p.idPelicula), count(distinct s.idSesion), count(e.idEntrada) from PeliculaEntity p left join SesionEntity s on s.idPelicula = p.idPelicula left join EntradaEntity e on e.idSesion = s.idSesion and e.estado in ('PAGADA','USADA') group by p.genero order by p.genero"
```
**Sale:** Animación | 1 | 1 | 2 · Ciencia ficción | 1 | 2 | 3 · Comedia | 1 | 1 | 1 · … (5 filas)  
**Ojo:** sin `distinct`, los `join` multiplican las películas y las sesiones.

#### C28. Salas sin sesiones programadas
**Frase → patrón:** «sin sesiones programadas» → `not exists` con la condición dentro del paréntesis.  
**Salida:** `sede, sala`. **Orden:** sede, sala ASC.
```sql
SELECT se.nombre AS sede, sa.nombre AS sala
FROM salas sa
JOIN sedes se ON se.id_sede = sa.id_sede
WHERE NOT EXISTS (SELECT 1 FROM sesiones s WHERE s.id_sala = sa.id_sala AND s.estado = 'PROGRAMADA')
ORDER BY sede, sala;
```
```java
// método SalaDao.salasSinSesiones()
"select sa from SalaEntity sa join SedeEntity se on se.idSede = sa.idSede where not exists (select s from SesionEntity s where s.idSala = sa.idSala and s.estado = 'PROGRAMADA') order by se.nombre, sa.nombre"
```
**Sale:** ∅ (0 filas)

### 7.5 Consultas extra que pueden caer (comprobadas sobre CineFest)

#### E1. «contiene un texto»
**Patrón:** `lower(…) like lower(concat('%', :t, '%'))`.
```sql
SELECT titulo
FROM peliculas
WHERE LOWER(titulo) LIKE '%' || LOWER('ar') || '%'
ORDER BY titulo;
```
```java
"select p.titulo from PeliculaEntity p where lower(p.titulo) like lower(concat('%', :t, '%')) order by p.titulo"
```
**Sale en CineFest:** Mareas (1 filas)

#### E2. «del mes de…», «en noviembre»
**Patrón:** `substring(fecha, 1, 7) = :mes` con '2026-11'.
```sql
SELECT id_entrada, fecha_compra
FROM entradas
WHERE SUBSTR(fecha_compra, 1, 7) = '2026-11'
ORDER BY id_entrada;
```
```java
"select e.idEntrada, e.fechaCompra from EntradaEntity e where substring(e.fechaCompra, 1, 7) = :mes order by e.idEntrada"
```
**Sale en CineFest:** 1 | 2026-11-01 · 2 | 2026-11-01 · 3 | 2026-11-02 · … (22 filas)

#### E3. «la más cara», «el máximo» (con empates)
**Patrón:** subconsulta con `max`.
```sql
SELECT id_sesion, precio
FROM sesiones
WHERE precio = (SELECT MAX(precio) FROM sesiones)
ORDER BY id_sesion;
```
```java
"select s.idSesion, s.precio from SesionEntity s where s.precio = (select max(s2.precio) from SesionEntity s2) order by s.idSesion"
```
**Sale en CineFest:** 2 | 8 · 8 | 8 (2 filas)

#### E4. «más que la media» (de una columna)
**Patrón:** subconsulta con `avg`.
```sql
SELECT titulo, duracion_min
FROM peliculas
WHERE duracion_min > (SELECT AVG(duracion_min) FROM peliculas)
ORDER BY duracion_min DESC, titulo;
```
```java
"select p.titulo, p.duracionMin from PeliculaEntity p where p.duracionMin > (select avg(p2.duracionMin) from PeliculaEntity p2) order by p.duracionMin desc, p.titulo"
```
**Sale en CineFest:** Órbita | 115 · Bruma | 110 · Nómadas | 105 · … (4 filas)

#### E5. «los N primeros» con N como parámetro
**Patrón:** `order by … desc` + `.setMaxResults(n)`.
```sql
SELECT es.nombre, COUNT(e.id_entrada) AS entradas
FROM espectadores es
JOIN entradas e ON e.id_espectador = es.id_espectador
WHERE e.estado IN ('PAGADA','USADA')
GROUP BY es.id_espectador, es.nombre
ORDER BY entradas DESC, es.nombre
LIMIT 3;
```
```java
"select es.nombre, count(e.idEntrada) from EspectadorEntity es join EntradaEntity e on e.idEspectador = es.idEspectador where e.estado in ('PAGADA','USADA') group by es.idEspectador, es.nombre order by count(e.idEntrada) desc, es.nombre"
// + .setMaxResults(n)
```
**Sale en CineFest:** Ana | 4 · Bruno | 4 · Carla | 4 (3 filas)

#### E6. «en cuántas … distintas»
**Patrón:** `count(distinct …)`.
```sql
SELECT p.titulo, COUNT(DISTINCT sa.id_sede) AS sedes
FROM peliculas p
JOIN sesiones s ON s.id_pelicula = p.id_pelicula
JOIN salas sa ON sa.id_sala = s.id_sala
GROUP BY p.id_pelicula, p.titulo
ORDER BY p.titulo;
```
```java
"select p.titulo, count(distinct sa.idSede) from PeliculaEntity p join SesionEntity s on s.idPelicula = p.idPelicula join SalaEntity sa on sa.idSala = s.idSala group by p.idPelicula, p.titulo order by p.titulo"
```
**Sale en CineFest:** Aurora | 1 · Bruma | 1 · Ecos | 1 · … (8 filas)

#### E7. «válidas y canceladas» en la misma fila
**Patrón:** `sum(case when … then 1 else 0 end)`.
```sql
SELECT s.id_sesion,
       SUM(CASE WHEN e.estado IN ('PAGADA','USADA') THEN 1 ELSE 0 END) AS validas,
       SUM(CASE WHEN e.estado = 'CANCELADA' THEN 1 ELSE 0 END) AS canceladas
FROM sesiones s
JOIN entradas e ON e.id_sesion = s.id_sesion
GROUP BY s.id_sesion
ORDER BY s.id_sesion;
```
```java
"select s.idSesion, sum(case when e.estado in ('PAGADA','USADA') then 1 else 0 end), sum(case when e.estado = 'CANCELADA' then 1 else 0 end) from SesionEntity s join EntradaEntity e on e.idSesion = s.idSesion group by s.idSesion order by s.idSesion"
```
**Sale en CineFest:** 1 | 4 | 1 · 2 | 3 | 1 · 3 | 4 | 0 · … (9 filas)

#### E8. «sin comentario», «sin dato»
**Patrón:** `is null` (nunca `= null`).
```sql
SELECT id_valoracion, puntuacion
FROM valoraciones
WHERE comentario IS NULL
ORDER BY id_valoracion;
```
```java
"select v.idValoracion, v.puntuacion from ValoracionEntity v where v.comentario is null order by v.idValoracion"
```
**Sale en CineFest:** 2 | 4 · 3 | 5 · 4 | 4 · … (11 filas)

#### E9. «que han … alguna vez»
**Patrón:** `exists` (lo contrario de C09).
```sql
SELECT es.nombre
FROM espectadores es
WHERE EXISTS (SELECT 1 FROM valoraciones v WHERE v.id_espectador = es.id_espectador)
ORDER BY es.nombre;
```
```java
"select es.nombre from EspectadorEntity es where exists (select v from ValoracionEntity v where v.idEspectador = es.idEspectador) order by es.nombre"
```
**Sale en CineFest:** Ana · Bruno · Carla · … (6 filas)

## 8. consultas.sql: el traductor al revés

En `consultas.sql` se escribe SQL, no JPQL. Es el traductor de 7.3 al revés:

1. `XEntity x` → el nombre de la **tabla** (`PeliculaEntity p` → `peliculas p`).
2. `x.atributoJava` → el nombre de la **columna** (`p.idPelicula` → `p.id_pelicula`).
3. `:parametro` → el valor que da el README (`:genero` → `'Drama'`).
4. Cada columna del `SELECT` con `AS` y el nombre de la «Salida» del README.
5. Una consulta por reto, debajo de su línea `-- Cxx:`.

Para C29 y C30 no hay una consulta, sino los **pasos de la transacción** en SQL:

```sql
-- C29
BEGIN IMMEDIATE;
SELECT COUNT(*) AS sesion_programada
FROM sesiones
WHERE id_sesion = 1 AND estado = 'PROGRAMADA';
SELECT COUNT(*) AS espectador_activo
FROM espectadores
WHERE id_espectador = 8 AND activo = 1;
SELECT COUNT(*) AS entrada_existente
FROM entradas
WHERE id_sesion = 1 AND id_espectador = 8;
SELECT sa.aforo - COUNT(e.id_entrada) AS libres
FROM sesiones s
JOIN salas sa ON sa.id_sala = s.id_sala
LEFT JOIN entradas e ON e.id_sesion = s.id_sesion AND e.estado IN ('PAGADA','USADA')
WHERE s.id_sesion = 1
GROUP BY s.id_sesion, sa.aforo;
-- si falla alguna comprobación: ROLLBACK
INSERT INTO entradas (id_sesion, id_espectador, fecha_compra, estado)
VALUES (1, 8, '2026-11-10', 'PAGADA');
COMMIT;

-- C30
BEGIN IMMEDIATE;
SELECT COUNT(*) AS entrada_sin_cancelar
FROM entradas
WHERE id_entrada = 1 AND estado <> 'CANCELADA';
-- si no existe o ya está CANCELADA: ROLLBACK
UPDATE entradas
SET estado = 'CANCELADA'
WHERE id_entrada = 1;
COMMIT;
```

## 9. Javadoc (1 punto casi regalado)

**Para qué:** el README pide Javadoc en cada método público de los DAO con «finalidad, parámetros, retorno y situaciones de error». Es el punto más barato del examen.

**Cuándo y cómo:** en los últimos 10 minutos. Ponte en la línea de encima del método, escribe `/**` y pulsa Enter: IntelliJ crea el bloque con un `@param` por parámetro y el `@return`. Solo rellenas los huecos.

```java
/**
 * Busca el x con ese identificador en la base de datos.
 *
 * @param id identificador del x (mayor que 0)
 * @return el x, u Optional.empty() si no existe, el id no es valido o hay un error
 */
public Optional<X> buscarPorId(int id) {
```

| Método | Primera línea (finalidad) | `@return` (retorno y errores) |
| --- | --- | --- |
| La clase (encima de `public class`) | `Acceso a la tabla x con JPA: CRUD y consultas.` | — |
| Constructor | `Crea el DAO y la fabrica de JPA para la base indicada.` | — (solo `@param jdbcUrl URL JDBC de la base SQLite`) |
| `listar` | `Devuelve la lista de x ordenada por su id.` | `la lista, o una lista vacia si no hay filas o hay un error` |
| `insertar` | `Inserta un x nuevo dentro de una transaccion.` | `el id generado, o 0 si el dato no es valido, esta repetido o hay un error` |
| `actualizar` | `Cambia los datos del x que tiene el id del dato.` | `1 si se cambia, o 0 si no existe, no es valido o hay un error` |
| `eliminar` | `Borra el x que tiene ese identificador.` | `1 si se borra, o 0 si no existe, tiene filas que dependen de el o hay un error` |
| Consulta `List<Object[]>` | `Cuenta las entradas validas de cada espectador. Cada fila: [nombre, entradas].` | `las filas en el orden del README, o una lista vacia si no hay o hay un error` |
| Operación con reglas | `Vende una entrada en una sola transaccion si se cumplen las reglas del README.` | `1 si se vende, o 0 si falla una regla o hay un error (con rollback)` |

**Reglas**
- En todos los métodos públicos, también el constructor, y uno encima de la clase.
- Primera línea con verbo y de 25 letras o más. No empieces por el nombre del método.
- Un `@param` por parámetro: `@param`, un espacio, el nombre exacto y el texto.
- Nada entre el `*/` y el método: ni `@Override` ni código.
- Sin tildes y sin las palabras «todo», «pendiente», «rellenar» o «documentar»: un corrector del profe las fallaba. Borra también los `// TODO` del esqueleto.
- Si no llegas a terminar, una línea con `@param` y `@return` vale más que nada.

## 10. Errores frecuentes → arreglo

**Para qué:** encontrar en un minuto qué línea cambiar. Los de compilación salen en Build > Rebuild Project. Los demás, en la consola: como los DAO no lanzan, sale `ADVERTENCIA: metodo: <excepción>: <mensaje>` (o `WARNING:`) y el método devuelve `0` o vacío. Si sale una traza (`Exception in thread "main"`), lee el último `Caused by:`. Van en orden: compilar, arrancar, consultas, escrituras y, al final, lo que sale mal sin error.

| Si ves… | Haz… |
| --- | --- |
| `cannot find symbol` · `invalid method reference` | Nombre inventado: cópialo del proyecto (mapper, getters, clases) |
| `incompatible types: SedeEntity cannot be converted to Sede` (o al revés) | Mapper al revés: leer → `desdeEntidad`; `insertar` → `aEntidad` |
| `missing return statement` | Falta el `return 0;`, `List.of();` u `Optional.empty();` del `catch` |
| `bad operand types for binary operator '=='` | Un `int` no es `null`: `if (id <= 0)` |
| `variable emf might not have been initialized` · en la consola, `NullPointerException … because "this.emf" is null` | El constructor está vacío: `this.emf = OrmFactory.crear(jdbcUrl);` |
| `unreported exception IOException` (en el `Main`) | `public static void main(String[] args) throws Exception` |
| `No Persistence provider for EntityManager named …` · `Unable to locate persistence units` | Unidad con otro nombre que en `OrmFactory`, o `persistence.xml` roto (un comentario que no es `<!-- -->`) |
| `Column 'id_x' is duplicated in mapping` | `@JoinColumn(name = "id_x", insertable = false, updatable = false)` |
| `Schema validation: missing column` · `wrong column type` (con `none`: `no such column`) | `@Column(name = …)` mal escrito o tipo prohibido (`Long`, `Boolean`, `LocalDate`) |
| `LazyInitializationException … no session` | `getPadre()` o `getHijos()` fuera del `try`: úsalos dentro |
| `Could not resolve root entity 'sedes'` | La clase, no la tabla: `from SedeEntity s`, y su `<class>` en `persistence.xml` |
| `Could not resolve attribute 'id_sede'` | El atributo Java, no la columna: `s.idSede` |
| `Could not interpret path expression 'total'` | En el `having` no vale el alias: repite el `count(...)` |
| `No argument for named parameter ':id'` · `No parameter named ':idSede'` | Un `.setParameter("id", …)` por cada `:id`, con el mismo nombre |
| `QueryArgumentException … not a whole number` | Un `count` contra un decimal: `count(...) * 1.0 > :media` |
| `query produces 'java.lang.Long' but type 'java.lang.Integer'` · `ClassCastException: class java.lang.Long cannot be cast to class java.lang.Integer` | `count` es `Long`: `Long.class`; en un `Object[]`, `((Number) f[1]).intValue()` |
| `NoSuchElementException` · `NoResultException` | Buscar sin filas: `getResultStream().findFirst()` |
| `buscarPorId: java.lang.NullPointerException` | `Optional.ofNullable(...)`, nunca `Optional.of` |
| `Detached entity passed to persist` | `e.setIdX(null);` antes del `persist` |
| `must be manually assigned before calling 'persist()'` | Clave natural: quita el `setIdX(null)` |
| `RollbackException: Error while committing` · `SQLITE_BUSY` · `database is locked`, o una escritura da `0` justo después de otro `0` | Falta `tx.rollback();` antes de un `return 0` |
| `UNIQUE constraint failed` · `FOREIGN KEY constraint failed` y el método da `0` | Está bien: es el `0` del README (repetido o con hijos) |
| `eliminar` de un padre con hijos devuelve `1` | Falta la línea de las FK en `persistence.xml` |
| Salen menos filas que el «Total real» en un `left join` | El filtro va en el `on`, no en el `where` |

## 11. Comprobar sin tests: el Main

**Para qué:** si el profe no da tests, compruebas cada método con un `Main` y comparas lo que imprime con las tablas del README. Siempre sobre una copia de la base: el README prohíbe tocar los datos de partida.

1. Clic derecho en el paquete `es.ejemplo.cinefest` → New → Java Class → escribe `app.Main` (crea el paquete `app` y la clase).
2. Dentro, `psvm` + Tab, añade `throws Exception`, escribe el molde y pulsa Alt+Enter sobre cada nombre rojo.
3. Ejecuta con el ▶ verde junto a `main` → Run 'Main.main()' (Ctrl+Shift+F10).

```java
public static void main(String[] args) throws Exception {
    Path copia = Path.of("target/prueba.sqlite3");
    Files.copy(Path.of("datos/cinefest.sqlite3"), copia, StandardCopyOption.REPLACE_EXISTING);
    String url = "jdbc:sqlite:" + copia;
    SedeDao sedes = new SedeDao(url);
    System.out.println(sedes.listar().size() + " " + sedes.buscarPorId(999).isEmpty());
    for (Object[] f : sedes.aforoTotalPorSede()) System.out.println(Arrays.toString(f));
    SesionDao sesiones = new SesionDao(url);
    EntradaDao entradas = new EntradaDao(url);
    System.out.println(sesiones.venderEntrada(1, 8, "2026-11-10"));
    System.out.println(sesiones.venderEntrada(1, 6, "2026-11-10"));
    System.out.println(entradas.cancelarEntrada(1));
}
```

Comprobado en CineFest: imprime `3 true`, `[Auditorio Atlántico, 2, 13]`, `[Centro Cultural Sur, 1, 4]`, `[Cine del Puerto, 1, 6]`, `1`, `0` y `1`, y termina con `exit code 0`. La base de `datos/` queda igual. Si a `venderEntrada` le quitas el `rollback` del aforo, la última línea da `0`: así se ve un `rollback` que falta.

| Método | Imprime | Debe salir |
| --- | --- | --- |
| `listar()` | `.size()` | el número de filas del README («Total real») |
| `buscarPorId(1)` / `buscarPorId(999)` | `.map(Sede::getNombre)` / `.isEmpty()` | `Optional[Auditorio Atlántico]` / `true` |
| `insertar` (el dato, montado con `set…`) / `actualizar` / `eliminar(999)` | el número | un id nuevo / `1` / `0` |
| Consulta `List<Object[]>` | `for (Object[] f : …) System.out.println(Arrays.toString(f));` | las filas del README, con las columnas en su orden |
| Operación con reglas | el número y, después, otra escritura | `1` o `0`, y la escritura siguiente da `1` |

- `6.0` en Java es el `6.00` del README, y `null` es su `NULL`. No es un fallo.
- Las líneas de Hibernate al arrancar (`INFO`, `WARN`) salen en rojo y son normales. Cada ejecución copia otra vez la base: siempre empiezas con los datos del README.
- `NoSuchFileException: datos/cinefest.sqlite3` → abre en IntelliJ la carpeta del `pom.xml` y copia el nombre exacto del fichero de `datos/`. `NoSuchFileException: target/prueba.sqlite3` → no existe `target/`: clic derecho en el proyecto → New → Directory → `target`.

## 12. Checklist final (los últimos 10 minutos)

Desde aquí no empiezas nada nuevo: Javadoc (apartado 9) y esta lista.

1. Build > Rebuild Project sin ningún error. Lo que no compile vuelve a su `return` original (`return 0;`, `List.of()`, `Optional.empty()`): si no compila, es un 0.
2. No has cambiado ninguna firma, paquete ni constructor que viniera escrito, ni nada de `src/test`.
3. Cada DAO tiene `this.emf = OrmFactory.crear(jdbcUrl);` en su constructor, y `persistence.xml` la línea de las FK y una `<class>` por entidad.
4. Cada `catch` acaba en su `return`, y cada `return 0` que va después de `tx.begin()` lleva antes `tx.rollback();` (Ctrl+F `return 0` en cada DAO).
5. Ctrl+Shift+F `TODO` en `src/main`: ninguno en un método que ya está hecho, y ningún `throw new UnsupportedOperationException`.
6. El `Main` termina con `exit code 0`, las consultas salen con las columnas y el orden del README, y después de un `0` la siguiente escritura da `1`.
7. Javadoc en la clase, el constructor y todos los métodos públicos de cada DAO. Imports en gris: Ctrl+Alt+O en cada DAO.
8. Si hay tests: Maven (panel de la derecha) → Lifecycle → `clean` y después `test` → `BUILD SUCCESS`.
9. Ctrl+S y entrega como diga el profe (si es un zip, sin `target/`).
