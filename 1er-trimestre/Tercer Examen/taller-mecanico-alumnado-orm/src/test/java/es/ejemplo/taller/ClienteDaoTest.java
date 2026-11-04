package es.ejemplo.taller;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import es.ejemplo.taller.modelo.*;
import java.sql.*;
import java.math.BigDecimal;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
public class ClienteDaoTest extends BaseTest {
    @Test @DisplayName("Insertar Ana devuelve un ID positivo") void caso01() throws Exception { assertTrue(cliente()>0); }
    @Test @DisplayName("Buscar el ID insertado devuelve nombre Ana") void caso02() throws Exception { int id=cliente(); assertEquals("Ana",clientes.buscarPorId(id).orElseThrow().getNombre()); }
    @Test @DisplayName("Buscar 999 devuelve Optional.empty") void caso03() throws Exception { assertTrue(clientes.buscarPorId(999).isEmpty()); }
    @Test @DisplayName("Listar tras una inserción devuelve una lista de tamaño 1") void caso04() throws Exception { cliente(); assertEquals(1,clientes.listar().size()); }
    @Test @DisplayName("Actualizar Ana por Eva devuelve 1") void caso05() throws Exception { int id=cliente(); assertEquals(1,clientes.actualizar(new Cliente(id,"Eva","eva@example.com","666"))); }
    @Test @DisplayName("Consultar después de actualizar devuelve nombre Eva") void caso06() throws Exception { int id=cliente(); clientes.actualizar(new Cliente(id,"Eva","eva@example.com","666")); assertEquals("Eva",clientes.buscarPorId(id).orElseThrow().getNombre()); }
    @Test @DisplayName("Eliminar el ID existente devuelve 1") void caso07() throws Exception { int id=cliente(); assertEquals(1,clientes.eliminar(id)); }
    @Test @DisplayName("Eliminar 999 devuelve 0") void caso08() throws Exception { assertEquals(0,clientes.eliminar(999)); }
    @Test @DisplayName("Insertar correo duplicado devuelve 0") void caso09() throws Exception { cliente(); assertEquals(0,clientes.insertar(new Cliente(null,"Luis","ana@example.com","666"))); }
    @Test @DisplayName("Insertar null devuelve 0") void caso10() throws Exception { assertEquals(0,clientes.insertar(null)); }
}

//Estos son los errores que estamos analizando: /home/diego/.jdks/openjdk-27/bin/java -javaagent:/home/diego/herramientas/idea-IU-262.10968.63/plugins/java/lib/rt/debugger-agent.jar=file:///tmp/capture9578921855842058352.props -ea -Didea.test.cyclic.buffer.size=1048576 -javaagent:/home/diego/herramientas/idea-IU-262.10968.63/lib/idea_rt.jar=46617 -Dkotlinx.coroutines.debug.enable.creation.stack.trace=false -Ddebugger.agent.enable.coroutines=true -Dkotlinx.coroutines.debug.enable.flows.stack.trace=true -Dkotlinx.coroutines.debug.enable.mutable.state.flows.stack.trace=true -Ddebugger.async.stack.trace.for.all.threads=true -Ddebugger.agent.enable.log.capture=true -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8 -classpath /home/diego/.m2/repository/org/junit/platform/junit-platform-launcher/1.11.4/junit-platform-launcher-1.11.4.jar:/home/diego/herramientas/idea-IU-262.10968.63/lib/idea_rt.jar:/home/diego/herramientas/idea-IU-262.10968.63/plugins/junit/lib/junit5-rt.jar:/home/diego/herramientas/idea-IU-262.10968.63/plugins/junit/lib/junit-rt.jar:/home/diego/Escritorio/DAM/AED/1er-trimestre/Tercer Examen/taller-mecanico-alumnado-orm/target/test-classes:/home/diego/Escritorio/DAM/AED/1er-trimestre/Tercer Examen/taller-mecanico-alumnado-orm/target/classes:/home/diego/.m2/repository/org/hibernate/orm/hibernate-core/7.4.11.Final/hibernate-core-7.4.11.Final.jar:/home/diego/.m2/repository/jakarta/persistence/jakarta.persistence-api/3.2.0/jakarta.persistence-api-3.2.0.jar:/home/diego/.m2/repository/jakarta/transaction/jakarta.transaction-api/2.0.1/jakarta.transaction-api-2.0.1.jar:/home/diego/.m2/repository/org/jboss/logging/jboss-logging/3.6.1.Final/jboss-logging-3.6.1.Final.jar:/home/diego/.m2/repository/org/hibernate/models/hibernate-models/1.1.2/hibernate-models-1.1.2.jar:/home/diego/.m2/repository/net/bytebuddy/byte-buddy/1.18.8/byte-buddy-1.18.8.jar:/home/diego/.m2/repository/jakarta/xml/bind/jakarta.xml.bind-api/4.0.4/jakarta.xml.bind-api-4.0.4.jar:/home/diego/.m2/repository/jakarta/activation/jakarta.activation-api/2.1.4/jakarta.activation-api-2.1.4.jar:/home/diego/.m2/repository/org/glassfish/jaxb/jaxb-runtime/4.0.7/jaxb-runtime-4.0.7.jar:/home/diego/.m2/repository/org/glassfish/jaxb/jaxb-core/4.0.7/jaxb-core-4.0.7.jar:/home/diego/.m2/repository/org/eclipse/angus/angus-activation/2.0.3/angus-activation-2.0.3.jar:/home/diego/.m2/repository/org/glassfish/jaxb/txw2/4.0.7/txw2-4.0.7.jar:/home/diego/.m2/repository/com/sun/istack/istack-commons-runtime/4.1.2/istack-commons-runtime-4.1.2.jar:/home/diego/.m2/repository/jakarta/inject/jakarta.inject-api/2.0.1/jakarta.inject-api-2.0.1.jar:/home/diego/.m2/repository/org/antlr/antlr4-runtime/4.13.2/antlr4-runtime-4.13.2.jar:/home/diego/.m2/repository/org/hibernate/orm/hibernate-community-dialects/7.4.11.Final/hibernate-community-dialects-7.4.11.Final.jar:/home/diego/.m2/repository/org/xerial/sqlite-jdbc/3.46.1.0/sqlite-jdbc-3.46.1.0.jar:/home/diego/.m2/repository/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar:/home/diego/.m2/repository/org/junit/jupiter/junit-jupiter/5.11.4/junit-jupiter-5.11.4.jar:/home/diego/.m2/repository/org/junit/jupiter/junit-jupiter-api/5.11.4/junit-jupiter-api-5.11.4.jar:/home/diego/.m2/repository/org/opentest4j/opentest4j/1.3.0/opentest4j-1.3.0.jar:/home/diego/.m2/repository/org/junit/platform/junit-platform-commons/1.11.4/junit-platform-commons-1.11.4.jar:/home/diego/.m2/repository/org/apiguardian/apiguardian-api/1.1.2/apiguardian-api-1.1.2.jar:/home/diego/.m2/repository/org/junit/jupiter/junit-jupiter-params/5.11.4/junit-jupiter-params-5.11.4.jar:/home/diego/.m2/repository/org/junit/jupiter/junit-jupiter-engine/5.11.4/junit-jupiter-engine-5.11.4.jar:/home/diego/.m2/repository/org/junit/platform/junit-platform-engine/1.11.4/junit-platform-engine-1.11.4.jar com.intellij.rt.junit.JUnitStarter -ideVersion5 -junit5 es.ejemplo.taller.ClienteDaoTest
//nov 04, 2026 3:11:34 P. M. org.hibernate.jpa.internal.util.LogHelper logPersistenceUnitInformation
//INFO: HHH008540: Processing PersistenceUnitInfo [name: tallerPU]
//nov 04, 2026 3:11:35 P. M. org.hibernate.Version logVersion
//INFO: HHH000001: Hibernate ORM core version 7.4.11.Final
//nov 04, 2026 3:11:35 P. M. org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProvider configure
//WARN: HHH10001002: Using built-in connection pool (not intended for production use)
//SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
//SLF4J: Defaulting to no-operation (NOP) logger implementation
//SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
//WARNING: A restricted method in java.lang.System has been called
//WARNING: java.lang.System::load has been called by org.sqlite.SQLiteJDBCLoader in an unnamed module (file:/home/diego/.m2/repository/org/xerial/sqlite-jdbc/3.46.1.0/sqlite-jdbc-3.46.1.0.jar)
//WARNING: Use --enable-native-access=ALL-UNNAMED to avoid a warning for callers in this module
//WARNING: Restricted methods will be blocked in a future release unless native access is enabled
//
//nov 04, 2026 3:11:35 P. M. org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentInitiator logConnectionInfo
//INFO: HHH10001005: Database info:
//	Database JDBC URL [jdbc:sqlite:/tmp/taller-test-15521999455266707542.db]
//	Database driver: SQLite JDBC
//	Database dialect: SQLiteDialect
//	Database version: 3.46.1
//	Default catalog/schema: undefined/undefined
//	Autocommit mode: false
//	Isolation level: SERIALIZABLE
//	JDBC fetch size: none
//	Pool: DriverManagerConnectionProvider
//	Minimum pool size: 1
//	Maximum pool size: 20
//nov 04, 2026 3:11:37 P. M. org.hibernate.engine.transaction.jta.platform.internal.JtaPlatformInitiator initiateService
//INFO: HHH000489: No JTA platform available (set 'hibernate.transaction.jta.platform' to enable JTA platform integration)
//Hibernate: insert into clientes (email,nombre,telefono) values (?,?,?)
//Hibernate: select last_insert_rowid()
//nov 04, 2026 3:11:37 P. M. org.hibernate.jpa.internal.util.LogHelper logPersistenceUnitInformation
//INFO: HHH008540: Processing PersistenceUnitInfo [name: tallerPU]
//nov 04, 2026 3:11:37 P. M. org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProvider configure
//WARN: HHH10001002: Using built-in connection pool (not intended for production use)
//nov 04, 2026 3:11:37 P. M. org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentInitiator logConnectionInfo
//INFO: HHH10001005: Database info:
//	Database JDBC URL [jdbc:sqlite:/tmp/taller-test-12631214738303317966.db]
//	Database driver: SQLite JDBC
//	Database dialect: SQLiteDialect
//	Database version: 3.46.1
//	Default catalog/schema: undefined/undefined
//	Autocommit mode: false
//	Isolation level: SERIALIZABLE
//	JDBC fetch size: none
//	Pool: DriverManagerConnectionProvider
//	Minimum pool size: 1
//	Maximum pool size: 20
//nov 04, 2026 3:11:37 P. M. org.hibernate.engine.transaction.jta.platform.internal.JtaPlatformInitiator initiateService
//INFO: HHH000489: No JTA platform available (set 'hibernate.transaction.jta.platform' to enable JTA platform integration)
//Hibernate: insert into clientes (email,nombre,telefono) values (?,?,?)
//Hibernate: select last_insert_rowid()
//Hibernate: select ce1_0.id_cliente,ce1_0.email,ce1_0.nombre,ce1_0.telefono from clientes ce1_0 where ce1_0.id_cliente=?
//nov 04, 2026 3:11:37 P. M. org.hibernate.jpa.internal.util.LogHelper logPersistenceUnitInformation
//INFO: HHH008540: Processing PersistenceUnitInfo [name: tallerPU]
//nov 04, 2026 3:11:37 P. M. org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProvider configure
//WARN: HHH10001002: Using built-in connection pool (not intended for production use)
//nov 04, 2026 3:11:37 P. M. org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentInitiator logConnectionInfo
//INFO: HHH10001005: Database info:
//	Database JDBC URL [jdbc:sqlite:/tmp/taller-test-13408370508615058070.db]
//	Database driver: SQLite JDBC
//	Database dialect: SQLiteDialect
//	Database version: 3.46.1
//	Default catalog/schema: undefined/undefined
//	Autocommit mode: false
//	Isolation level: SERIALIZABLE
//	JDBC fetch size: none
//	Pool: DriverManagerConnectionProvider
//	Minimum pool size: 1
//	Maximum pool size: 20
//nov 04, 2026 3:11:37 P. M. org.hibernate.engine.transaction.jta.platform.internal.JtaPlatformInitiator initiateService
//INFO: HHH000489: No JTA platform available (set 'hibernate.transaction.jta.platform' to enable JTA platform integration)
//Hibernate: select ce1_0.id_cliente,ce1_0.email,ce1_0.nombre,ce1_0.telefono from clientes ce1_0 where ce1_0.id_cliente=?
//nov 04, 2026 3:11:37 P. M. org.hibernate.jpa.internal.util.LogHelper logPersistenceUnitInformation
//INFO: HHH008540: Processing PersistenceUnitInfo [name: tallerPU]
//nov 04, 2026 3:11:37 P. M. org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProvider configure
//WARN: HHH10001002: Using built-in connection pool (not intended for production use)
//nov 04, 2026 3:11:37 P. M. org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentInitiator logConnectionInfo
//INFO: HHH10001005: Database info:
//	Database JDBC URL [jdbc:sqlite:/tmp/taller-test-3838570405805347965.db]
//	Database driver: SQLite JDBC
//	Database dialect: SQLiteDialect
//	Database version: 3.46.1
//	Default catalog/schema: undefined/undefined
//	Autocommit mode: false
//	Isolation level: SERIALIZABLE
//	JDBC fetch size: none
//	Pool: DriverManagerConnectionProvider
//	Minimum pool size: 1
//	Maximum pool size: 20
//nov 04, 2026 3:11:37 P. M. org.hibernate.engine.transaction.jta.platform.internal.JtaPlatformInitiator initiateService
//INFO: HHH000489: No JTA platform available (set 'hibernate.transaction.jta.platform' to enable JTA platform integration)
//Hibernate: insert into clientes (email,nombre,telefono) values (?,?,?)
//Hibernate: select last_insert_rowid()
//Hibernate: select ce1_0.id_cliente,ce1_0.email,ce1_0.nombre,ce1_0.telefono from clientes ce1_0 order by ce1_0.nombre
//nov 04, 2026 3:11:37 P. M. org.hibernate.jpa.internal.util.LogHelper logPersistenceUnitInformation
//INFO: HHH008540: Processing PersistenceUnitInfo [name: tallerPU]
//nov 04, 2026 3:11:37 P. M. org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProvider configure
//WARN: HHH10001002: Using built-in connection pool (not intended for production use)
//nov 04, 2026 3:11:37 P. M. org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentInitiator logConnectionInfo
//INFO: HHH10001005: Database info:
//	Database JDBC URL [jdbc:sqlite:/tmp/taller-test-8916297091758549029.db]
//	Database driver: SQLite JDBC
//	Database dialect: SQLiteDialect
//	Database version: 3.46.1
//	Default catalog/schema: undefined/undefined
//	Autocommit mode: false
//	Isolation level: SERIALIZABLE
//	JDBC fetch size: none
//	Pool: DriverManagerConnectionProvider
//	Minimum pool size: 1
//	Maximum pool size: 20
//nov 04, 2026 3:11:38 P. M. org.hibernate.engine.transaction.jta.platform.internal.JtaPlatformInitiator initiateService
//INFO: HHH000489: No JTA platform available (set 'hibernate.transaction.jta.platform' to enable JTA platform integration)
//Hibernate: insert into clientes (email,nombre,telefono) values (?,?,?)
//Hibernate: select last_insert_rowid()
//Hibernate: select ce1_0.id_cliente,ce1_0.email,ce1_0.nombre,ce1_0.telefono from clientes ce1_0 where ce1_0.id_cliente=?
//Hibernate: update clientes set email=?,nombre=?,telefono=? where id_cliente=?
//nov 04, 2026 3:11:38 P. M. org.hibernate.jpa.internal.util.LogHelper logPersistenceUnitInformation
//INFO: HHH008540: Processing PersistenceUnitInfo [name: tallerPU]
//nov 04, 2026 3:11:38 P. M. org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProvider configure
//WARN: HHH10001002: Using built-in connection pool (not intended for production use)
//nov 04, 2026 3:11:38 P. M. org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentInitiator logConnectionInfo
//INFO: HHH10001005: Database info:
//	Database JDBC URL [jdbc:sqlite:/tmp/taller-test-9215953615587381973.db]
//	Database driver: SQLite JDBC
//	Database dialect: SQLiteDialect
//	Database version: 3.46.1
//	Default catalog/schema: undefined/undefined
//	Autocommit mode: false
//	Isolation level: SERIALIZABLE
//	JDBC fetch size: none
//	Pool: DriverManagerConnectionProvider
//	Minimum pool size: 1
//	Maximum pool size: 20
//nov 04, 2026 3:11:38 P. M. org.hibernate.engine.transaction.jta.platform.internal.JtaPlatformInitiator initiateService
//INFO: HHH000489: No JTA platform available (set 'hibernate.transaction.jta.platform' to enable JTA platform integration)
//Hibernate: insert into clientes (email,nombre,telefono) values (?,?,?)
//Hibernate: select last_insert_rowid()
//Hibernate: select ce1_0.id_cliente,ce1_0.email,ce1_0.nombre,ce1_0.telefono from clientes ce1_0 where ce1_0.id_cliente=?
//Hibernate: update clientes set email=?,nombre=?,telefono=? where id_cliente=?
//Hibernate: select ce1_0.id_cliente,ce1_0.email,ce1_0.nombre,ce1_0.telefono from clientes ce1_0 where ce1_0.id_cliente=?
//nov 04, 2026 3:11:38 P. M. org.hibernate.jpa.internal.util.LogHelper logPersistenceUnitInformation
//INFO: HHH008540: Processing PersistenceUnitInfo [name: tallerPU]
//nov 04, 2026 3:11:38 P. M. org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProvider configure
//WARN: HHH10001002: Using built-in connection pool (not intended for production use)
//nov 04, 2026 3:11:38 P. M. org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentInitiator logConnectionInfo
//INFO: HHH10001005: Database info:
//	Database JDBC URL [jdbc:sqlite:/tmp/taller-test-15721630367566340868.db]
//	Database driver: SQLite JDBC
//	Database dialect: SQLiteDialect
//	Database version: 3.46.1
//	Default catalog/schema: undefined/undefined
//	Autocommit mode: false
//	Isolation level: SERIALIZABLE
//	JDBC fetch size: none
//	Pool: DriverManagerConnectionProvider
//	Minimum pool size: 1
//	Maximum pool size: 20
//nov 04, 2026 3:11:38 P. M. org.hibernate.engine.transaction.jta.platform.internal.JtaPlatformInitiator initiateService
//INFO: HHH000489: No JTA platform available (set 'hibernate.transaction.jta.platform' to enable JTA platform integration)
//Hibernate: insert into clientes (email,nombre,telefono) values (?,?,?)
//Hibernate: select last_insert_rowid()
//Hibernate: select ce1_0.id_cliente,ce1_0.email,ce1_0.nombre,ce1_0.telefono from clientes ce1_0 where ce1_0.id_cliente=?
//Hibernate: delete from clientes where id_cliente=?
//nov 04, 2026 3:11:38 P. M. org.hibernate.jpa.internal.util.LogHelper logPersistenceUnitInformation
//INFO: HHH008540: Processing PersistenceUnitInfo [name: tallerPU]
//nov 04, 2026 3:11:38 P. M. org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProvider configure
//WARN: HHH10001002: Using built-in connection pool (not intended for production use)
//nov 04, 2026 3:11:38 P. M. org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentInitiator logConnectionInfo
//INFO: HHH10001005: Database info:
//	Database JDBC URL [jdbc:sqlite:/tmp/taller-test-16333776070320319551.db]
//	Database driver: SQLite JDBC
//	Database dialect: SQLiteDialect
//	Database version: 3.46.1
//	Default catalog/schema: undefined/undefined
//	Autocommit mode: false
//	Isolation level: SERIALIZABLE
//	JDBC fetch size: none
//	Pool: DriverManagerConnectionProvider
//	Minimum pool size: 1
//	Maximum pool size: 20
//nov 04, 2026 3:11:38 P. M. org.hibernate.engine.transaction.jta.platform.internal.JtaPlatformInitiator initiateService
//INFO: HHH000489: No JTA platform available (set 'hibernate.transaction.jta.platform' to enable JTA platform integration)
//Hibernate: select ce1_0.id_cliente,ce1_0.email,ce1_0.nombre,ce1_0.telefono from clientes ce1_0 where ce1_0.id_cliente=?
//nov 04, 2026 3:11:38 P. M. org.hibernate.jpa.internal.util.LogHelper logPersistenceUnitInformation
//INFO: HHH008540: Processing PersistenceUnitInfo [name: tallerPU]
//nov 04, 2026 3:11:38 P. M. org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProvider configure
//WARN: HHH10001002: Using built-in connection pool (not intended for production use)
//nov 04, 2026 3:11:38 P. M. org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentInitiator logConnectionInfo
//INFO: HHH10001005: Database info:
//	Database JDBC URL [jdbc:sqlite:/tmp/taller-test-11119813780529652609.db]
//	Database driver: SQLite JDBC
//	Database dialect: SQLiteDialect
//	Database version: 3.46.1
//	Default catalog/schema: undefined/undefined
//	Autocommit mode: false
//	Isolation level: SERIALIZABLE
//	JDBC fetch size: none
//	Pool: DriverManagerConnectionProvider
//	Minimum pool size: 1
//	Maximum pool size: 20
//nov 04, 2026 3:11:38 P. M. org.hibernate.engine.transaction.jta.platform.internal.JtaPlatformInitiator initiateService
//INFO: HHH000489: No JTA platform available (set 'hibernate.transaction.jta.platform' to enable JTA platform integration)
//Hibernate: insert into clientes (email,nombre,telefono) values (?,?,?)
//Hibernate: select last_insert_rowid()
//Hibernate: insert into clientes (email,nombre,telefono) values (?,?,?)
//nov 04, 2026 3:11:38 P. M. org.hibernate.engine.jdbc.spi.SqlExceptionHelper logExceptions
//WARN: HHH000247: ErrorCode: 19, SQLState: null
//nov 04, 2026 3:11:38 P. M. org.hibernate.engine.jdbc.spi.SqlExceptionHelper logExceptions
//WARN: [SQLITE_CONSTRAINT_UNIQUE] A UNIQUE constraint failed (UNIQUE constraint failed: clientes.email)
//
//org.hibernate.exception.GenericJDBCException: could not execute statement [[SQLITE_CONSTRAINT_UNIQUE] A UNIQUE constraint failed (UNIQUE constraint failed: clientes.email)] [insert into clientes (email,nombre,telefono) values (?,?,?)]
//
//	at org.hibernate.exception.internal.StandardSQLExceptionConverter.convert(StandardSQLExceptionConverter.java:39)
//	at org.hibernate.engine.jdbc.spi.SqlExceptionHelper.convert(SqlExceptionHelper.java:115)
//	at org.hibernate.engine.jdbc.internal.ResultSetReturnImpl.executeUpdate(ResultSetReturnImpl.java:189)
//	at org.hibernate.id.insert.AbstractSelectingDelegate.performMutation(AbstractSelectingDelegate.java:78)
//	at org.hibernate.engine.jdbc.mutation.internal.MutationExecutorSingleNonBatched.performNonBatchedOperations(MutationExecutorSingleNonBatched.java:45)
//	at org.hibernate.engine.jdbc.mutation.internal.AbstractMutationExecutor.execute(AbstractMutationExecutor.java:67)
//	at org.hibernate.engine.jdbc.mutation.internal.AbstractMutationExecutor.execute(AbstractMutationExecutor.java:56)
//	at org.hibernate.persister.entity.mutation.InsertCoordinatorStandard.doStaticInserts(InsertCoordinatorStandard.java:183)
//	at org.hibernate.persister.entity.mutation.InsertCoordinatorStandard.coordinateInsert(InsertCoordinatorStandard.java:123)
//	at org.hibernate.persister.entity.mutation.InsertCoordinatorStandard.insert(InsertCoordinatorStandard.java:87)
//	at org.hibernate.action.internal.EntityIdentityInsertAction.execute(EntityIdentityInsertAction.java:87)
//	at org.hibernate.engine.spi.ActionQueue.execute(ActionQueue.java:667)
//	at org.hibernate.engine.spi.ActionQueue.addResolvedEntityInsertAction(ActionQueue.java:291)
//	at org.hibernate.engine.spi.ActionQueue.addInsertAction(ActionQueue.java:270)
//	at org.hibernate.engine.spi.ActionQueue.addAction(ActionQueue.java:322)
//	at org.hibernate.event.internal.AbstractSaveEventListener.addInsertAction(AbstractSaveEventListener.java:384)
//	at org.hibernate.event.internal.AbstractSaveEventListener.performSaveOrReplicate(AbstractSaveEventListener.java:298)
//	at org.hibernate.event.internal.AbstractSaveEventListener.performSave(AbstractSaveEventListener.java:228)
//	at org.hibernate.event.internal.AbstractSaveEventListener.saveWithGeneratedId(AbstractSaveEventListener.java:144)
//	at org.hibernate.event.internal.DefaultPersistEventListener.entityIsTransient(DefaultPersistEventListener.java:144)
//	at org.hibernate.event.internal.DefaultPersistEventListener.persist(DefaultPersistEventListener.java:91)
//	at org.hibernate.event.internal.DefaultPersistEventListener.onPersist(DefaultPersistEventListener.java:75)
//	at org.hibernate.event.internal.DefaultPersistEventListener.onPersist(DefaultPersistEventListener.java:51)
//	at org.hibernate.event.service.internal.EventListenerGroupImpl.fireEventOnEachListener(EventListenerGroupImpl.java:138)
//	at org.hibernate.internal.SessionImpl.firePersist(SessionImpl.java:709)
//	at org.hibernate.internal.SessionImpl.persist(SessionImpl.java:692)
//	at es.ejemplo.taller.dao.ClienteDao.insertar(ClienteDao.java:46)
//	at es.ejemplo.taller.ClienteDaoTest.caso09(ClienteDaoTest.java:18)
//	at java.base/java.lang.reflect.Method.invoke(Method.java:583)
//	at java.base/java.util.ArrayList.forEach(ArrayList.java:1612)
//	at java.base/java.util.ArrayList.forEach(ArrayList.java:1612)
//Caused by: org.sqlite.SQLiteException: [SQLITE_CONSTRAINT_UNIQUE] A UNIQUE constraint failed (UNIQUE constraint failed: clientes.email)
//	at org.sqlite.core.DB.newSQLException(DB.java:1179)
//	at org.sqlite.core.DB.newSQLException(DB.java:1190)
//	at org.sqlite.core.DB.execute(DB.java:985)
//	at org.sqlite.core.DB.executeUpdate(DB.java:1054)
//	at org.sqlite.jdbc3.JDBC3PreparedStatement.lambda$executeLargeUpdate$2(JDBC3PreparedStatement.java:129)
//	at org.sqlite.jdbc3.JDBC3Statement.withConnectionTimeout(JDBC3Statement.java:454)
//	at org.sqlite.jdbc3.JDBC3PreparedStatement.executeLargeUpdate(JDBC3PreparedStatement.java:124)
//	at org.sqlite.jdbc3.JDBC3PreparedStatement.executeUpdate(JDBC3PreparedStatement.java:105)
//	at org.hibernate.engine.jdbc.internal.ResultSetReturnImpl.executeUpdate(ResultSetReturnImpl.java:185)
//	... 28 more
//
//nov 04, 2026 3:11:38 P. M. org.hibernate.jpa.internal.util.LogHelper logPersistenceUnitInformation
//INFO: HHH008540: Processing PersistenceUnitInfo [name: tallerPU]
//nov 04, 2026 3:11:38 P. M. org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProvider configure
//WARN: HHH10001002: Using built-in connection pool (not intended for production use)
//nov 04, 2026 3:11:38 P. M. org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentInitiator logConnectionInfo
//INFO: HHH10001005: Database info:
//	Database JDBC URL [jdbc:sqlite:/tmp/taller-test-6526019002768456011.db]
//	Database driver: SQLite JDBC
//	Database dialect: SQLiteDialect
//	Database version: 3.46.1
//	Default catalog/schema: undefined/undefined
//	Autocommit mode: false
//	Isolation level: SERIALIZABLE
//	JDBC fetch size: none
//	Pool: DriverManagerConnectionProvider
//	Minimum pool size: 1
//	Maximum pool size: 20
//nov 04, 2026 3:11:38 P. M. org.hibernate.engine.transaction.jta.platform.internal.JtaPlatformInitiator initiateService
//INFO: HHH000489: No JTA platform available (set 'hibernate.transaction.jta.platform' to enable JTA platform integration)
//
//java.lang.NullPointerException: Cannot invoke "es.ejemplo.taller.modelo.Cliente.getIdCliente()" because "cliente" is null
//
//	at es.ejemplo.taller.orm.mapper.ClienteMapper.toEntity(ClienteMapper.java:9)
//	at es.ejemplo.taller.dao.ClienteDao.insertar(ClienteDao.java:45)
//	at es.ejemplo.taller.ClienteDaoTest.caso10(ClienteDaoTest.java:19)
//	at java.base/java.lang.reflect.Method.invoke(Method.java:583)
//	at java.base/java.util.ArrayList.forEach(ArrayList.java:1612)
//	at java.base/java.util.ArrayList.forEach(ArrayList.java:1612)
//
//
//Process finished with exit code 255