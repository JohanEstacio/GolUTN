# GolUTN — Frontend Administrativo (JSF)

Proyecto Integrador UTN. Yo soy responsable **únicamente** de este componente.
No tocar los otros repos del equipo.

## Stack
Jakarta EE 10, JSF (Mojarra) + PrimeFaces, Maven, WildFly sobre Fedora.
Se despliega copiando `target/golutn-admin.war` a `$WILDFLY_HOME/standalone/deployments/`.

## Arquitectura del sistema (4 componentes, 4 personas)
- **Servicio Estadísticas** — .NET + PostgreSQL. *Ya existe*, repo `davidpp19/Project_UTNGolMundial`. Es el backend del que depende este panel.
- **Servicio UTNGolCoin** — Jakarta EE + MySQL. En desarrollo por otro integrante.
- **Frontend público** — C# MVC, repo `estebanhuera/UTNGolMundial`.
- **Frontend administrativo** — este.

## Contrato real con el backend de Estadísticas
- `GET  /api/Estadisticas/calendario` → CalendarioPartidoDto (campos: partidoId,
  fechaPartido, localNombre, visitanteNombre, sedeNombre, faseNombre, grupoCodigo,
  estado, golesLocal/golesVisitante nullable)
- `PUT  /api/Partidos/{id}/resultado` → body `{golesLocal, golesVisitante, usuarioId}`
- `GET  /api/Usuarios`, `PUT /api/Usuarios/{id}`
- `GET  /api/Selecciones`, `GET /api/Estadisticas/selecciones`
- `GET  /api/Auditorias`
- **Login: NO EXISTE todavía** en el backend. Sigue en mock.

## Pendientes conocidos (en orden)
1. Quitar credenciales demo visibles en `login.xhtml` y el login hardcodeado
   `admin@golutn.edu.ec/admin123` de `EstadisticasApiClient`.
2. `pom.xml`: Mojarra a scope `provided` (choca con el módulo de WildFly),
   Jersey → cliente RESTEasy, agregar `jackson-datatype-jsr310`.
3. DTOs reales para las peticiones — hoy se envían arrays (`new String[]{...}`),
   ningún backend los deserializa.
4. Migrar del mock al contrato real de arriba; agregar timeouts al cliente REST.
5. RF10 incompleto: falta CRUD de partidos y pantalla de selecciones.
6. RF24 incompleto: falta pantalla de auditoría (`GET /api/Auditorias`).
7. RNF01: `estilos.css` no tiene ni un `@media`; falta `<meta viewport>`.
8. Faltan tildes en toda la UI.

## Notas
- El backend .NET escribe el estado como "Finalizado" (capitalizado) pero el resto
  del sistema usa "FINALIZADO". El enum `Partido.Estado` tiene un `@JsonCreator`
  que normaliza, así que da igual cómo llegue.
- No borrar el rol INVITADO del sistema (RF25), pero no debe haber *cuentas*
  con ese rol: el invitado es el visitante anónimo sin sesión.
