# Práctica ORM: banco con JPA/Hibernate y SQLite (niveles 0 y 1)

**Módulo:** Acceso a Datos (2.º DAM)  
**Tecnologías:** Java 21, Maven, Jakarta Persistence con Hibernate 7.4.11, SQLite (sqlite-jdbc 3.46.1.0) y JUnit 5.  
**Objetivo:** montar la capa ORM de un banco (nivel 0) e implementar el repositorio de clientes con un CRUD escrito a mano y cinco consultas JPQL (nivel 1), siguiendo la estructura del taller mecánico con JPA visto en clase.

## 1. Contexto

Un banco quiere guardar sus sucursales, sus empleados, sus clientes, las cuentas de los clientes, los movimientos de cada cuenta y las transferencias entre cuentas. El proyecto crece por niveles: el esquema completo existe desde el principio, pero en estos dos niveles solo se trabaja la configuración de JPA y la tabla `clientes`. No es necesario construir interfaz gráfica ni aplicación de consola.

1. **Nivel 0, montaje:** `persistence.xml`, `OrmFactory` y `DemoOrm`. Las claves foráneas deben cumplirse en las conexiones de Hibernate.
2. **Nivel 1, clientes:** entidad, mapper, contrato documentado y `ClienteOrmRepository` con el CRUD y las consultas.

Paquetes (raíz `es.ejemplo.banco`):

```text
modelo/Cliente                      dominio (POJO, sin anotaciones JPA)
orm/config/OrmFactory               fábrica del EntityManagerFactory
orm/entity/ClienteEntity            entidad JPA
orm/mapper/ClienteMapper            dominio <-> entidad
orm/repository/                     IRepository, IClienteRepository, RepositoryException, ClienteOrmRepository
orm/DemoOrm                         demo de solo lectura
src/main/resources/                 META-INF/persistence.xml, schema.sql, datos.sql
datos/banco.sqlite3                 base de ejemplo (schema.sql + datos.sql)
```

## 2. Esquema de base de datos

El esquema está en `src/main/resources/schema.sql` y **no se modifica**. Las tablas que no se usan todavía se trabajarán en los niveles siguientes.

| Tabla | Columnas | Restricciones |
|---|---|---|
| `sucursales` | `id_sucursal`, `codigo`, `nombre`, `ciudad` | `codigo` único |
| `empleados` | `id_empleado`, `dni`, `nombre`, `puesto`, `salario`, `id_sucursal` | `dni` único; `puesto` en `CAJERO`, `GESTOR`, `DIRECTOR`; `salario >= 0`; FK a `sucursales` con `ON DELETE RESTRICT` |
| `clientes` | `id_cliente`, `dni`, `nombre`, `email`, `telefono`, `fecha_alta`, `id_gestor` | `dni` y `email` obligatorios y únicos; `telefono` e `id_gestor` pueden ser nulos; FK `id_gestor` a `empleados` con `ON DELETE SET NULL` |
| `cuentas` | `iban`, `id_cliente`, `id_sucursal`, `tipo`, `saldo`, `fecha_apertura`, `estado` | PK de texto (IBAN); `tipo` en `CORRIENTE`, `AHORRO`, `NOMINA`; `saldo >= 0`; `estado` en `ACTIVA`, `BLOQUEADA`, `CERRADA` |
| `movimientos` | `id_movimiento`, `iban`, `tipo`, `importe`, `fecha`, `concepto` | `importe > 0`; FK a `cuentas` |
| `transferencias` | `id_transferencia`, `iban_origen`, `iban_destino`, `importe`, `fecha`, `concepto` | `importe > 0`; origen distinto de destino; dos FK a `cuentas` |

- Las claves `INTEGER PRIMARY KEY AUTOINCREMENT` las genera la base de datos.
- Las fechas se guardan como texto ISO `YYYY-MM-DD`, así que se comparan bien como cadenas.
- `datos/banco.sqlite3` contiene `schema.sql` + `datos.sql`: 3 sucursales, 6 empleados, 8 clientes (uno sin gestor y otro sin cuentas), 10 cuentas, 25 movimientos y 6 transferencias, con saldos coherentes. Sirve para la demo y para la pestaña *Database* de IntelliJ. **Las pruebas no la usan.**

> **Pista:** `schema.sql` empieza con `PRAGMA foreign_keys = ON`, pero en SQLite esa orden solo afecta a la conexión que la ejecuta. Hibernate abre sus propias conexiones desde un pool y ninguna la hereda. Hay que conseguir que **cada** conexión que abra Hibernate tenga las claves foráneas activas. El controlador `sqlite-jdbc` acepta la opción `foreign_keys` como propiedad de conexión o al final de la URL, y Hibernate reenvía al controlador, quitándoles el prefijo, las propiedades que empiezan por `hibernate.connection.`. Una consulta nativa `PRAGMA foreign_keys` debe devolver `1`.

## 3. Nivel 0: montaje (2 puntos)

| Pieza | Contrato |
|---|---|
| `META-INF/persistence.xml` | Versión 3.1, unidad **`bancoPU`** de tipo `RESOURCE_LOCAL`, proveedor `org.hibernate.jpa.HibernatePersistenceProvider`, una `<class>` por entidad (en este nivel, `ClienteEntity`). Propiedades: `jakarta.persistence.jdbc.driver=org.sqlite.JDBC`, `hibernate.dialect=org.hibernate.community.dialect.SQLiteDialect`, `hibernate.hbm2ddl.auto=none` y `hibernate.show_sql=false`. **La URL no va en el XML.** |
| `EntityManagerFactory OrmFactory.crear(String jdbcUrl)` | Clase `final` con constructor privado. Devuelve una fábrica **nueva** de la unidad `bancoPU` conectada a la URL recibida (por ejemplo `jdbc:sqlite:datos/banco.sqlite3`). No es un singleton: quien la crea la cierra. Si la ruta no existe, el error puede saltar al crear la fábrica o en la primera consulta (SQLite crea un fichero vacío y falla con `no such table`); en ningún caso afecta a otras fábricas abiertas. |
| Claves foráneas | En cualquier `EntityManager` de la fábrica, `PRAGMA foreign_keys` vale `1`, también si hay varios abiertos a la vez. Una fila con una FK inexistente se rechaza y los `ON DELETE` del esquema se cumplen. Quien llama a `crear` solo pasa `jdbc:sqlite:<ruta>`. |
| `DemoOrm.main` | Abre la fábrica sobre `datos/banco.sqlite3` (ruta relativa a la raíz del proyecto), muestra el número de tablas con una consulta nativa sobre `sqlite_master` (sin contar las internas `sqlite_%`) y el valor de `PRAGMA foreign_keys`. En el nivel 1 lista además los clientes con el repositorio. Solo lee: no modifica la base. |

Salida esperada de la demo, ejecutada desde IntelliJ con la raíz del proyecto como directorio de trabajo: `Tablas: 6`, `PRAGMA foreign_keys = 1` y los 8 clientes de ejemplo. Las tres líneas rojas `SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder"` del principio (también salen al ejecutar las pruebas) no son un error: `sqlite-jdbc` trae `slf4j-api` sin ninguna implementación de registro y avisa de que no escribirá mensajes. Los mensajes `INFO` de Hibernate tampoco son errores; la demo y `BaseOrmTest` ya los silencian.

## 4. Nivel 1: clientes (7 puntos + 1 de JavaDoc)

El dominio `Cliente` (`idCliente`, `dni`, `nombre`, `email`, `telefono`, `fechaAlta`, `idGestor`), las interfaces `IRepository<T, ID>` e `IClienteRepository` y `RepositoryException` forman parte del contrato: **no cambies sus firmas**.

**`ClienteEntity`** (`orm/entity`): `@Entity` y `@Table` con el nombre real de la tabla; `idCliente` `Integer` con `@GeneratedValue(strategy = GenerationType.IDENTITY)`; `@Column` con el nombre de cada columna y `nullable`/`unique` según el esquema; constructor vacío `protected` (lo necesita JPA), constructor completo y getters/setters. En este nivel `id_gestor` se mapea como un `Integer` normal que puede ser nulo: la relación con `empleados` llega en el nivel 2.

**`ClienteMapper`** (`orm/mapper`): clase `final` con constructor privado y dos métodos `static`, `toEntity(Cliente)` y `toDomain(ClienteEntity)`. Con `null` devuelven `null`.

**`ClienteOrmRepository implements IClienteRepository`** (`orm/repository`), con el constructor `public ClienteOrmRepository(EntityManagerFactory factory)`.

### 4.1 CRUD (4 puntos)

| Método | Contrato |
|---|---|
| `List<Cliente> findAll()` | Devuelve todos los clientes ordenados por `idCliente` ascendente. Lista vacía si no hay ninguno. |
| `Optional<Cliente> findById(Integer id)` | Devuelve el cliente o `Optional.empty()`, también si `id` es nulo. |
| `void create(Cliente cliente)` | Inserta el cliente y **copia en el objeto recibido el id generado**. Un DNI o email repetido, o un `idGestor` que no existe en `empleados`, lanza `RepositoryException` conservando la causa. |
| `boolean update(Cliente cliente)` | Modifica **todas** las columnas editables (`dni`, `nombre`, `email`, `telefono`, `fechaAlta`, `idGestor`) del cliente con ese id. Devuelve `false` si no existe o si el cliente no tiene id, y en ese caso **no inserta nada**. Si se viola una restricción lanza `RepositoryException` y la fila queda como estaba. |
| `boolean delete(Integer id)` | Borra el cliente. Devuelve `false` si no existe o si `id` es nulo. |

### 4.2 Consultas (3 puntos)

**Consulta 1: búsqueda por email exacto.** Firma: `Optional<Cliente> buscarPorEmail(String email)`. El email tiene que coincidir entero y exactamente: un fragmento no vale, `_` y `%` no son comodines y se distinguen mayúsculas. Sin coincidencia, `Optional.empty()`.

**Consulta 2: búsqueda por DNI.** Firma: `Optional<Cliente> buscarPorDni(String dni)`. Coincidencia exacta. Sin coincidencia, `Optional.empty()`.

**Consulta 3: clientes cuyo nombre contiene un texto.** Firma: `List<Cliente> listarPorNombre(String texto)`. Sin distinguir mayúsculas y minúsculas, ordenados por nombre y, si hay empate, por id. Con texto nulo, vacío o en blanco devuelve una lista vacía. En esta consulta no hace falta escapar `%` ni `_`: pueden funcionar como comodines.

> **Ojo con las tildes:** `lower`/`upper` de SQLite solo convierten las letras sin tilde (A-Z), y su `like` solo ignora las mayúsculas en esas letras. Las letras con tilde y la ñ se comparan tal como están guardadas, y el resto del texto sigue sin distinguir mayúsculas: `Álvarez` y `ÁLVAREZ` tienen que encontrar a `Miguel Álvarez Prieto`, y `romero` y `ROMERO` a `Luis Romero`.

**Consulta 4: altas desde una fecha (inclusive).** Firma: `List<Cliente> listarAltasDesde(String fecha)`. Incluye la fecha indicada y ordena por fecha de alta y, si hay empate, por id. Con fecha nula devuelve una lista vacía.

**Consulta 5: número de clientes.** Firma: `long contar()`.

### 4.3 Requisitos de implementación

- CRUD **manual, método a método**, como en la guía del taller. La generalización con `AbstractOrmRepository` llega en un nivel posterior.
- Un `EntityManager` por operación, abierto con `factory.createEntityManager()` sobre la fábrica que recibe el constructor y cerrado con `try-with-resources`. No se guarda en un atributo ni se comparte.
- Escrituras: `begin` y `commit`; en el `catch`, `rollback` solo si la transacción sigue activa. Nunca `commit` en un `finally`.
- `create`: `persist` + `flush` + copiar el id generado al dominio.
- `update` y `delete`: primero `find`; si no existe, `false`. `update` cambia la entidad gestionada con sus setters.
- Lecturas sin transacción. La conversión a dominio se hace **dentro** del `EntityManager` abierto.
- JPQL con el nombre de la entidad y de sus atributos (`ClienteEntity`, `fechaAlta`), nunca con el de la tabla o las columnas. Parámetros con nombre (`:email`) y `setParameter`: no se concatenan valores en las consultas.
- Los errores de escritura se envuelven en `RepositoryException(mensaje, causa)`. Después de un error el repositorio sigue funcionando con normalidad.
- **Documenta la API en el lugar correcto** con JavaDoc: descripción precisa, `@param` y `@return` cuando correspondan y palabras clave relacionadas con la finalidad de cada método. Se evalúan los 10 métodos de `IRepository` e `IClienteRepository`.

## 5. Reglas

- No modifiques las firmas, los paquetes, los nombres de clases ni las pruebas JUnit.
- No modifiques `schema.sql` ni actives la generación de tablas de Hibernate: `hibernate.hbm2ddl.auto` se queda en `none`.
- Si el proyecto **no compila**, no hay nota.

## 6. Pruebas automáticas

`BaseOrmTest` crea en cada prueba un fichero SQLite temporal, ejecuta `schema.sql` con JDBC, abre `OrmFactory.crear("jdbc:sqlite:" + ruta)` y, al terminar, cierra la fábrica y borra el fichero. Algunas comprobaciones leen la tabla física con JDBC directo, sin pasar por Hibernate.

| Clase | Tests | Comprueba |
|---|---:|---|
| `OrmFactoryTest` | 7 | Fábrica y `EntityManager`, unidad `bancoPU`, URL recibida, 6 tablas, `PRAGMA foreign_keys` y FK rechazada. |
| `ClienteOrmRepositoryTest` | 25 | CRUD (16): ids generados, orden, vacíos, `false`, duplicados, FK del gestor, tabla física. Consultas (9). |

La corrección añade **pruebas ocultas** en el mismo paquete, con el sufijo `OcultoTest`, que cuentan en el bloque que les corresponde. La descripción JavaDoc de cada test explica el comportamiento esperado. Todos los métodos `@Test` terminan en `Test`.

Para ejecutar solo las pruebas:

```bash
mvn clean test
```

## 7. Cómo consultar la nota

Ejecuta **desde la raíz del proyecto**, donde está `pom.xml`:

```bash
mvn clean verify -Pnota
```

El perfil `nota` ejecuta las pruebas sin detenerse por los fallos y, en la fase `verify`, llama a `calcular_nota.py`. El informe aparece por pantalla y se guarda en `target/notas_por_bloques.txt`. Requisitos: Maven, JDK 21 y `python3` en el terminal.

| Bloque | Pruebas | Puntos |
|---|---|---:|
| Nivel 0: montaje | `OrmFactory*Test` | 2,00 |
| Nivel 1: CRUD de clientes | Tests de CRUD de `ClienteOrmRepository*Test` | 4,00 |
| Nivel 1: consultas de clientes | Tests de consultas de `ClienteOrmRepository*Test` | 3,00 |
| Documentación JavaDoc | `IRepository` + `IClienteRepository` | 1,00 |
| **Total** | | **10,00** |

Cada bloque se puntúa en proporción a las pruebas superadas. **El JavaDoc solo cuenta si se superan todas las pruebas del CRUD de clientes.** Reparto por método: descripción 40 %, `@param` 25 %, `@return` 20 % y palabras clave 15 %. No se puntúa `@throws`.

## 8. Mejoras voluntarias (hasta 2 puntos extra)

Además de la nota automática se pueden conseguir **hasta 2,00 puntos extra** por mejoras de diseño e implementación, aunque solo se haya completado el CRUD. No basta con mencionarlas: tienen que estar aplicadas y poder comprobarse en el código entregado, sin cambiar las interfaces, las firmas ni las pruebas proporcionadas.

| Mejora evaluable | Máximo | Evidencias esperadas |
|---|---:|---|
| **Verificación y robustez** | **0,75** | Pruebas propias en clases nuevas (sin tocar las dadas), tratamiento coherente de nulos y casos límite, comprobación de restricciones. |
| **Abstracción y reutilización** | **0,75** | Responsabilidades separadas y duplicidades eliminadas sin cambiar los contratos. El CRUD de este nivel sigue siendo manual, método a método (§4.3). |
| **Calidad del acceso a datos** | **0,50** | Recursos bien cerrados, consultas que traen solo lo necesario y sin operaciones redundantes, con una justificación breve. |
| **Total de mejoras** | **2,00** | |

`mvn clean verify -Pnota` muestra solo la nota automática (0-10). La nota final es `min(10, nota automática + mejoras)`. Si optas a las mejoras, añade en la raíz del proyecto un fichero `MEJORAS.md` que diga qué has hecho, dónde está y cómo se comprueba. **No modifiques este README ni las pruebas facilitadas.**

## 9. Entrega

- Ejecuta `mvn clean verify -Pnota` antes de entregar y comprueba que el proyecto compila.
- Entrega el proyecto Maven completo, comprimido, **sin** las carpetas `target/` ni `.idea/`.
- Si optas a la puntuación extra, incluye `MEJORAS.md`.
