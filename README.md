# AED — Acceso a Datos

Prácticas y proyectos del módulo de Acceso a Datos.

## Datos

- Alumno: Diego Gil
- Curso: 2.º DAM
- Curso académico: 2026/2027

## Contenidos

### 1.er trimestre

#### Primer examen

- [Práctica de Maven](1er-trimestre/Primer%20Examen/proyecto-maven/README.md): compilación, dependencias, perfiles, empaquetado y cambio de versión de Java.
- [Ficheros](1er-trimestre/Primer%20Examen/ficheros): rutas con `Path` y `File`, recursos del proyecto y lectura de CSV con Apache Commons CSV.
- [Catálogo de productos con repositorios](1er-trimestre/Primer%20Examen/catalogo-productos-repositorios/README.md): CRUD de productos en CSV, JSON y XML con una interfaz genérica y una clase abstracta de repositorio, y configuración con `.properties`. En curso, con [tareas para practicar](1er-trimestre/Primer%20Examen/catalogo-productos-repositorios/README.md#para-practicar).
- [Preparación del examen](1er-trimestre/Primer%20Examen/Preparacion%20del%20Examen): mis apuntes de lo que entra.

#### Segundo examen

Bases de datos con SQLite, SQL y ORM. De momento empezado dentro del catálogo (`data/app.db`).

### 2.º trimestre

Próximamente.

### 3.er trimestre

Próximamente.

## Herramientas

- Java 21
- Maven 3.9
- IntelliJ IDEA

## Cómo abrir un proyecto

Cada carpeta con un `pom.xml` es un proyecto Maven independiente.

1. En IntelliJ IDEA, **File → Open** y elegir la carpeta del proyecto.
2. Esperar a que Maven descargue las dependencias.
3. Ejecutar la clase que tiene el método `main`.

Desde la terminal, dentro de la carpeta del proyecto:

```bash
mvn compile
```
