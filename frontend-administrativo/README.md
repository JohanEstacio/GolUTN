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


## Cómo correrlo

Requiere WildFly (por ser Jakarta EE / JSF) y Maven.

```bash
mvn clean package
# copia target/golutn-admin.war a la carpeta standalone/deployments de WildFly
```

Luego entramos a: `http://localhost:8080/golutn-admin/`


