# GolUTN — Frontend Administrativo (JSF)

Panel de control administrativo del proyecto **GolUTN Mundial 2026**. Es una
aplicación **Jakarta EE 10 / Jakarta Faces 4** (JSF) con **PrimeFaces 13**,
empaquetada como WAR y desplegada en **WildFly**. No tiene base de datos ni
lógica de negocio propia: todo el estado real vive en dos servicios REST
externos que este panel consume por HTTP.

## 1. Requisitos

| Herramienta | Versión | Notas |
|---|---|---|
| JDK | 17 | `maven.compiler.source`/`target` están fijados en 17 en el `pom.xml`. |
| Maven | 3.8+ | Para compilar y empaquetar el WAR. |
| WildFly | 27 o superior | Es la primera versión con soporte Jakarta EE 10 / Jakarta Faces 4, requerido por `jakarta.jakartaee-api:10.0.0`. |

Todas las dependencias de Jakarta EE, Jakarta Faces (Mojarra) y el cliente
REST (RESTEasy) están declaradas con `scope=provided` en el `pom.xml`: **no
se empaquetan dentro del WAR**, se usan los módulos que WildFly ya trae
integrados. Esto evita conflictos de classloader, pero también significa que
la app **no arrancará en un Tomcat plano** ni en un WildFly con esos
subsistemas deshabilitados.

Las únicas dependencias que sí viajan dentro del WAR son PrimeFaces
(`primefaces:13.0.6:jakarta`) y el módulo Jackson para `java.time`
(`jackson-datatype-jsr310`).

## 2. Compilar y desplegar el WAR

```bash
mvn clean package
```

Esto genera `target/golutn-admin.war` (el nombre final está fijado por
`<finalName>golutn-admin</finalName>` en el `pom.xml`, así que el contexto de
despliegue siempre es `/golutn-admin`, sin importar la versión del proyecto).

Para desplegarlo, copia el WAR a la carpeta de despliegues de WildFly:

```bash
cp target/golutn-admin.war $WILDFLY_HOME/standalone/deployments/
```

WildFly detecta el archivo nuevo y lo despliega automáticamente (modo
`standalone`, con *deployment scanner* activo, que es la configuración por
defecto). Podés verificar el resultado por los archivos marcador que WildFly
crea junto al WAR:

- `golutn-admin.war.deployed` → desplegó correctamente.
- `golutn-admin.war.failed` → falló; el detalle del error está en
  `standalone/log/server.log`.

Una vez desplegado, la app queda disponible en:

```
http://localhost:8080/golutn-admin/
```

Para volver a desplegar tras un cambio, alcanza con sobrescribir el WAR
(borra el `.deployed` viejo si quedó un `.failed` de un intento anterior).

## 3. Configuración: context-params de `web.xml`

Toda la configuración de esta app está en
`src/main/webapp/WEB-INF/web.xml`, como `<context-param>`. No hay archivo de
propiedades externo: **para cambiar cualquiera de estos valores hay que
editar `web.xml` y volver a compilar/empaquetar el WAR.**

| Parámetro | Valor actual | Para qué sirve |
|---|---|---|
| `jakarta.faces.PROJECT_STAGE` | `Development` | Fase de Jakarta Faces. En `Development` se muestran páginas de error más detalladas y no se cachean los recursos estáticos, útil mientras se desarrolla. **En un ambiente productivo real conviene cambiarlo a `Production`** para mejor rendimiento y para no exponer detalles de error al usuario final. |
| `jakarta.faces.DEFAULT_SUFFIX` | `.xhtml` | Sufijo de las vistas Facelets; debe coincidir con el `<url-pattern>*.xhtml</url-pattern>` del `FacesServlet`. |
| `jakarta.faces.STATE_SAVING_METHOD` | `server` | El estado de cada vista JSF se guarda en la sesión HTTP del servidor (no viaja serializado en un campo oculto del HTML). Simplifica el HTML pero consume memoria de sesión por cada usuario conectado, y **el estado se pierde si WildFly se reinicia** (ver sección de problemas frecuentes). |
| `ESTADISTICAS_API_URL` | `http://10.82.26.169:5138/api` | URL base del **Servicio de Estadísticas** (ver sección 4). |
| `UTNGOLCOIN_API_URL` | `http://10.116.216.195:8080/UTNGolCoin/api` | URL base del **Servicio UTNGolCoin** (ver sección 4). |
| `USAR_MOCK_SI_FALLA_API` | `false` | Si es `true`, cuando cualquiera de los dos servicios no responde, los clientes REST (`EstadisticasApiClient`, `UtnGolCoinApiClient`) devuelven datos de ejemplo hardcodeados en vez de listas vacías, para poder seguir demostrando/probando pantallas sin backend real levantado. En `false` (valor actual), si el backend no responde la pantalla simplemente se degrada (listas vacías, contadores en `—`, etc.), sin inventar datos. |

Adicionalmente (no son `context-param`, pero forman parte de la
configuración de despliegue):

- `<session-config><session-timeout>30</session-timeout>` — las sesiones
  expiran a los 30 minutos de inactividad.
- `AuthFilter` está mapeado a `/panel/*` y exige que en sesión exista un
  `Usuario` con rol `ADMINISTRADOR` y `activo=true`; si no, redirige a
  `login.xhtml`.
- Los `<error-page>` de `ViewExpiredException` y de HTTP 500 redirigen a
  `login.xhtml`.

## 4. Servicios externos que consume

Este panel no tiene persistencia propia. Todo dato mostrado viene de dos
APIs REST separadas, ambas consumidas vía `jakarta.ws.rs.client.Client`
(RESTEasy) con Jackson para el JSON:

- **Servicio de Estadísticas** (`ESTADISTICAS_API_URL`, cliente
  `EstadisticasApiClient`): partidos, calendario, resultados, selecciones,
  fases, grupos, sedes, usuarios y auditoría, además del login
  (`Auth/login`).
- **Servicio UTNGolCoin** (`UTNGOLCOIN_API_URL`, cliente
  `UtnGolCoinApiClient`): resumen de reportes (RF27), billeteras de
  UTNGolCoin y el bono anti-bancarrota (RF20).

Ambos clientes están anotados `@ApplicationScoped` y leen su URL base una
sola vez, en `@PostConstruct`, desde el `context-param` correspondiente. Si
alguno de los dos servicios está caído, cada pantalla se degrada de forma
independiente (no hay un único punto de falla): el resto del panel sigue
funcionando aunque uno de los dos backends no responda.

## 5. Arrancar WildFly accesible desde la red

Por defecto, WildFly en modo `standalone` sólo escucha en `127.0.0.1`
(loopback), así que solo se puede acceder desde la misma máquina. Para que
otros equipos de la red (o un dispositivo de otra persona del equipo) puedan
entrar al panel, hay que bindear todas las interfaces de red al arrancar:

```bash
# Linux / macOS
$WILDFLY_HOME/bin/standalone.sh -b 0.0.0.0

# Windows
%WILDFLY_HOME%\bin\standalone.bat -b 0.0.0.0
```

Con `-b 0.0.0.0`, WildFly acepta conexiones en el puerto 8080 desde
cualquier IP que le llegue a la máquina. Después, desde otro equipo de la
misma red se accede reemplazando `localhost` por la IP de la máquina que
corre WildFly:

```
http://<IP-de-la-máquina-wildfly>:8080/golutn-admin/
```

Notas:

- Esto solo abre la interfaz HTTP de la aplicación (puerto 8080). La consola
  de administración de WildFly (puerto 9990) sigue en loopback salvo que
  además se pase `-bmanagement 0.0.0.0` (no hace falta para simplemente usar
  el panel).
- Verificá que el firewall del sistema operativo permita conexiones
  entrantes al puerto 8080.
- Si la máquina de WildFly y las máquinas donde corren el Servicio de
  Estadísticas y el Servicio UTNGolCoin son distintas, la conectividad tiene
  que funcionar en ambos sentidos: el navegador del usuario hacia WildFly, y
  WildFly hacia esas dos IPs configuradas en `web.xml`.

## 6. Problemas frecuentes

**El WAR queda como `.failed` en `standalone/deployments`.**
Revisá `standalone/log/server.log`. Las causas más comunes son: WildFly
corriendo con un JDK menor a 17, o una versión de WildFly anterior a la 27
(sin soporte Jakarta EE 10 / Faces 4, las dependencias `provided` no
encuentran sus clases en tiempo de ejecución).

**Las pantallas cargan pero muestran solo datos de ejemplo (partidos
"Mexico vs Polonia", usuarios "admin"/"ana.torres", etc.) en vez de datos
reales.**
Significa que `USAR_MOCK_SI_FALLA_API` es `true` y el cliente REST no pudo
conectarse a alguno de los dos backends. Revisá que `ESTADISTICAS_API_URL` y
`UTNGOLCOIN_API_URL` en `web.xml` apunten a IPs y puertos accesibles desde el
servidor WildFly (no desde tu navegador — la conexión la hace el servidor).

**Las pantallas se ven vacías (tablas sin filas, contadores en `—`) sin
ningún mensaje de error.**
Es la degradación esperada cuando un backend no responde y
`USAR_MOCK_SI_FALLA_API` es `false` (el valor por defecto). Revisá
conectividad de red hacia el servicio correspondiente y los logs de WildFly
(`java.util.logging`, nivel `WARNING`) para ver el detalle de la excepción.

**Cambié un `context-param` en `web.xml` y no pasa nada.**
Los `context-param` se leen del `web.xml` que quedó empaquetado dentro del
WAR, no del archivo fuente del proyecto. Hay que volver a `mvn clean
package` y redesplegar el WAR.

**Al usuario le aparece "View Expired" o lo manda de nuevo al login sin
avisar.**
`STATE_SAVING_METHOD` es `server`, así que el estado de la vista vive en la
sesión HTTP. Si la sesión expiró (más de 30 minutos de inactividad, ver
`session-timeout`) o si WildFly se reinició, el estado ya no existe y JSF
lanza `ViewExpiredException`, que esta app redirige a `login.xhtml`.

**El login funciona pero igual redirige de vuelta a `login.xhtml`.**
`AuthFilter` exige que el usuario autenticado tenga `activo=true` **y**
`rolNombre` igual a `ADMINISTRADOR` (no distingue mayúsculas/minúsculas).
Cualquier otro rol o un usuario inactivo es rechazado, aunque las
credenciales sean correctas.

**No puedo entrar al panel desde otra computadora de la red.**
Ver la sección 5: por defecto WildFly solo escucha en `127.0.0.1`. Hay que
arrancarlo con `-b 0.0.0.0` y revisar el firewall del sistema operativo.
