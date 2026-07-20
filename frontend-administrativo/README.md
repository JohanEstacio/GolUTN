# GolUTN — Frontend Administrativo (JSF)

Panel de control para el proyecto **GolUTN Mundial 2026**. Consume el
**Servicio de Estadísticas** (partidos, selecciones, usuarios) y el
**Servicio UTNGolCoin** (para el módulo de reportes) mediante API REST.

## ¿Qué incluye?

- **Login** (`login.xhtml`) — acceso exclusivo para administradores.
- **Dashboard** (`panel/dashboard.xhtml`) — resumen general al iniciar sesión.
- **Gestión de partidos** (`panel/partidos.xhtml`) — lista el calendario y
  permite registrar el resultado oficial de cada partido (RF10, RF11).
- **Gestión de usuarios** (`panel/usuarios.xhtml`) — administra cuentas y
  roles: administrador, usuario, invitado (RF23, RF25).
- **Reportes y auditoría** (`panel/reportes.xhtml`) — indicadores básicos:
  predicciones totales, UTNGolCoin en circulación, etc. (RF27).

## Mientras el backend no está listo

Cada llamada a la API pasa por `EstadisticasApiClient` o
`UtnGolCoinApiClient` (carpeta `service/`). Si el backend no responde
(porque tus compañeros aún no lo terminan), **el sistema no se cae**:
automáticamente muestra datos de ejemplo (mock) para que puedas seguir
armando y probando las pantallas.

Login de prueba mientras no hay backend real:
```
correo:    admin@golutn.edu.ec
password:  admin123
```

Cuando el Servicio de Estadísticas esté desplegado, solo edita en
`src/main/webapp/WEB-INF/web.xml`:
```xml
<param-name>ESTADISTICAS_API_URL</param-name>
<param-value>http://localhost:8080/estadisticas-api/api</param-value>
```
y ponla en la URL real (host/puerto/contexto donde quede tu compañero
del backend). Lo mismo para `UTNGOLCOIN_API_URL`.

## Cómo correrlo

Requiere WildFly (por ser Jakarta EE / JSF) y Maven.

```bash
mvn clean package
# copia target/golutn-admin.war a la carpeta standalone/deployments de WildFly
```

Luego entra a: `http://localhost:8080/golutn-admin/`

## Pendientes / próximos pasos sugeridos

1. **Autenticación real**: ahora mismo `AuthFilter` deja pasar todo — falta
   conectar la sesión real una vez que el login contra la API esté estable
   (guardar el usuario en sesión HTTP y redirigir si no hay sesión).
2. Cuando el Servicio de Estadísticas exponga los endpoints reales
   (`/partidos`, `/usuarios`, `/selecciones`, `/auth/login`), verifica que
   los nombres de campos JSON coincidan con las clases en `model/`
   (`Partido`, `Usuario`, `Seleccion`) o ajusta el mapeo con Jackson
   (`@JsonProperty`) si el backend usa otros nombres.
3. Falta el endpoint de reportes agregados (`/reportes/resumen`) en el
   Servicio UTNGolCoin — coordina con quien hace ese backend qué campos va
   a devolver exactamente.
4. Diseño: ya está pensado como "panel sobrio" (RNF02) — distinto del
   frontend público de tu compañero, que debe ser llamativo con temática
   mundialista.
