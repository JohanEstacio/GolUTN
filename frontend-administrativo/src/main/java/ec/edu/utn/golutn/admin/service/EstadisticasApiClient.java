package ec.edu.utn.golutn.admin.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ec.edu.utn.golutn.admin.dto.LoginRequestDto;
import ec.edu.utn.golutn.admin.dto.PartidoRequestDto;
import ec.edu.utn.golutn.admin.dto.ResultadoRequestDto;
import ec.edu.utn.golutn.admin.dto.SeleccionRequestDto;
import ec.edu.utn.golutn.admin.model.Auditoria;
import ec.edu.utn.golutn.admin.model.Fase;
import ec.edu.utn.golutn.admin.model.Grupo;
import ec.edu.utn.golutn.admin.model.Partido;
import ec.edu.utn.golutn.admin.model.Sede;
import ec.edu.utn.golutn.admin.model.Seleccion;
import ec.edu.utn.golutn.admin.model.Usuario;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.context.FacesContext;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Encapsula toda la comunicacion con el Servicio de Estadisticas (API REST).
 *
 * IMPORTANTE PARA EL EQUIPO:
 * Si el backend no responde, este cliente atrapa el error de conexion y
 * devuelve datos de ejemplo (mock) cuando USAR_MOCK_SI_FALLA_API es true,
 * para que las pantallas del panel administrativo se puedan seguir
 * construyendo y probando sin depender de que el backend este arriba.
 */
@ApplicationScoped
public class EstadisticasApiClient {

    private static final Logger LOG = Logger.getLogger(EstadisticasApiClient.class.getName());
    private static final ObjectMapper ERROR_MAPPER = new ObjectMapper();

    private Client client;
    private String baseUrl;
    private boolean usarMockSiFalla;

    @PostConstruct
    public void init() {
        this.client = ClientBuilder.newBuilder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build()
                .register(JacksonObjectMapperResolver.class);
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc != null) {
            this.baseUrl = fc.getExternalContext().getInitParameter("ESTADISTICAS_API_URL");
            this.usarMockSiFalla = Boolean.parseBoolean(
                    fc.getExternalContext().getInitParameter("USAR_MOCK_SI_FALLA_API"));
        } else {
            this.baseUrl = "http://localhost:5138/api";
            this.usarMockSiFalla = false;
        }
    }

    // ---------------------------------------------------------------
    // PARTIDOS (RF10, RF11)
    // ---------------------------------------------------------------

    public List<Partido> listarPartidos() {
        try {
            Response resp = client.target(baseUrl).path("/Estadisticas/calendario")
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            if (resp.getStatus() == 200) {
                return resp.readEntity(new GenericType<List<Partido>>() {});
            }
            LOG.warning("API de calendario respondio codigo " + resp.getStatus());
        } catch (Exception e) {
            LOG.log(Level.WARNING, "No se pudo conectar al Servicio de Estadisticas, usando datos de ejemplo", e);
        }
        return usarMockSiFalla ? datosEjemploPartidos() : new ArrayList<>();
    }

    /** Lanza RuntimeException si la API no responde, para que el llamador pueda distinguir "0" de "no se pudo consultar". */
    public int contarPartidos() {
        try {
            Response resp = client.target(baseUrl).path("/Estadisticas/calendario")
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            if (resp.getStatus() == 200) {
                return resp.readEntity(new GenericType<List<Partido>>() {}).size();
            }
            LOG.warning("API de calendario respondio codigo " + resp.getStatus());
        } catch (Exception e) {
            LOG.log(Level.WARNING, "No se pudo conectar al Servicio de Estadisticas (conteo de partidos)", e);
        }
        throw new IllegalStateException("No se pudo obtener el conteo de partidos.");
    }

    public boolean registrarResultado(Long partidoId, int golesLocal, int golesVisitante, Long usuarioId) {
        try {
            Response resp = client.target(baseUrl).path("/Partidos/" + partidoId + "/resultado")
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.json(new ResultadoRequestDto(golesLocal, golesVisitante, usuarioId)));
            return resp.getStatus() == 200 || resp.getStatus() == 204;
        } catch (Exception e) {
            LOG.log(Level.WARNING, "No se pudo registrar el resultado en la API (modo mock)", e);
            // En modo desarrollo simulamos exito para poder seguir probando la pantalla
            return usarMockSiFalla;
        }
    }

    public ResultadoOperacion crearPartido(PartidoRequestDto datos) {
        try {
            Response resp = client.target(baseUrl).path("/Partidos")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(datos));
            int status = resp.getStatus();
            if (status == 200 || status == 201) {
                return new ResultadoOperacion(true, status, null);
            }
            String cuerpo = resp.readEntity(String.class);
            LOG.warning("API de creacion de partido respondio codigo " + status + " - cuerpo: " + cuerpo);
            return new ResultadoOperacion(false, status, extraerMensajeError(cuerpo));
        } catch (Exception e) {
            LOG.log(Level.WARNING, "No se pudo crear el partido en la API (modo mock)", e);
            return new ResultadoOperacion(usarMockSiFalla, 0, e.getMessage());
        }
    }

    public ResultadoOperacion actualizarPartido(Long partidoId, PartidoRequestDto datos) {
        try {
            Response resp = client.target(baseUrl).path("/Partidos/" + partidoId)
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.json(datos));
            int status = resp.getStatus();
            if (status >= 200 && status < 300) {
                return new ResultadoOperacion(true, status, null);
            }
            String cuerpo = resp.readEntity(String.class);
            LOG.warning("API de actualizacion de partido respondio codigo " + status
                    + " - cuerpo: " + cuerpo);
            return new ResultadoOperacion(false, status, extraerMensajeError(cuerpo));
        } catch (Exception e) {
            LOG.log(Level.WARNING, "No se pudo actualizar el partido en la API (modo mock)", e);
            return new ResultadoOperacion(usarMockSiFalla, 0, e.getMessage());
        }
    }

    /** El Servicio de Estadisticas informa el motivo de un 400 en un campo "mensaje" del cuerpo JSON. */
    private String extraerMensajeError(String cuerpo) {
        if (cuerpo == null || cuerpo.isBlank()) {
            return null;
        }
        try {
            JsonNode nodo = ERROR_MAPPER.readTree(cuerpo);
            JsonNode mensaje = nodo.get("mensaje");
            if (mensaje != null && mensaje.isTextual()) {
                return mensaje.asText();
            }
        } catch (Exception e) {
            LOG.log(Level.FINE, "El cuerpo de la respuesta de error no es JSON valido", e);
        }
        return cuerpo;
    }

    // ---------------------------------------------------------------
    // SELECCIONES / FASES / GRUPOS / SEDES
    // ---------------------------------------------------------------

    public List<Seleccion> listarSelecciones() {
        try {
            Response resp = client.target(baseUrl).path("/Selecciones")
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            if (resp.getStatus() == 200) {
                return resp.readEntity(new GenericType<List<Seleccion>>() {});
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, "No se pudo conectar al Servicio de Estadisticas (selecciones)", e);
        }
        return usarMockSiFalla ? datosEjemploSelecciones() : new ArrayList<>();
    }

    public ResultadoOperacion crearSeleccion(SeleccionRequestDto datos) {
        try {
            Response resp = client.target(baseUrl).path("/Selecciones")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(datos));
            int status = resp.getStatus();
            if (status == 200 || status == 201) {
                return new ResultadoOperacion(true, status, null);
            }
            String cuerpo = resp.readEntity(String.class);
            LOG.warning("API de creacion de seleccion respondio codigo " + status + " - cuerpo: " + cuerpo);
            return new ResultadoOperacion(false, status, extraerMensajeError(cuerpo));
        } catch (Exception e) {
            LOG.log(Level.WARNING, "No se pudo crear la seleccion en la API (modo mock)", e);
            return new ResultadoOperacion(usarMockSiFalla, 0, e.getMessage());
        }
    }

    /** El PUT del backend valida que el id de la URL coincida con el del cuerpo. */
    public ResultadoOperacion actualizarSeleccion(Integer id, SeleccionRequestDto datos) {
        datos.setId(id);
        try {
            Response resp = client.target(baseUrl).path("/Selecciones/" + id)
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.json(datos));
            int status = resp.getStatus();
            if (status >= 200 && status < 300) {
                return new ResultadoOperacion(true, status, null);
            }
            String cuerpo = resp.readEntity(String.class);
            LOG.warning("API de actualizacion de seleccion respondio codigo " + status + " - cuerpo: " + cuerpo);
            return new ResultadoOperacion(false, status, extraerMensajeError(cuerpo));
        } catch (Exception e) {
            LOG.log(Level.WARNING, "No se pudo actualizar la seleccion en la API (modo mock)", e);
            return new ResultadoOperacion(usarMockSiFalla, 0, e.getMessage());
        }
    }

    /** Lanza RuntimeException si la API no responde, para que el llamador pueda distinguir "0" de "no se pudo consultar". */
    public int contarSelecciones() {
        try {
            Response resp = client.target(baseUrl).path("/Selecciones")
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            if (resp.getStatus() == 200) {
                return resp.readEntity(new GenericType<List<Seleccion>>() {}).size();
            }
            LOG.warning("API de selecciones respondio codigo " + resp.getStatus());
        } catch (Exception e) {
            LOG.log(Level.WARNING, "No se pudo conectar al Servicio de Estadisticas (conteo de selecciones)", e);
        }
        throw new IllegalStateException("No se pudo obtener el conteo de selecciones.");
    }

    /** Lanza RuntimeException si la API no responde, para que el llamador pueda distinguir "0" de "no se pudo consultar". */
    public int contarGrupos() {
        try {
            Response resp = client.target(baseUrl).path("/Grupos")
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            if (resp.getStatus() == 200) {
                return resp.readEntity(new GenericType<List<Object>>() {}).size();
            }
            LOG.warning("API de grupos respondio codigo " + resp.getStatus());
        } catch (Exception e) {
            LOG.log(Level.WARNING, "No se pudo conectar al Servicio de Estadisticas (conteo de grupos)", e);
        }
        throw new IllegalStateException("No se pudo obtener el conteo de grupos.");
    }

    public List<Fase> listarFases() {
        try {
            Response resp = client.target(baseUrl).path("/Fases")
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            if (resp.getStatus() == 200) {
                return resp.readEntity(new GenericType<List<Fase>>() {});
            }
            LOG.warning("API de fases respondio codigo " + resp.getStatus());
        } catch (Exception e) {
            LOG.log(Level.WARNING, "No se pudo conectar al Servicio de Estadisticas (fases)", e);
        }
        return usarMockSiFalla ? datosEjemploFases() : new ArrayList<>();
    }

    public List<Grupo> listarGrupos() {
        try {
            Response resp = client.target(baseUrl).path("/Grupos")
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            if (resp.getStatus() == 200) {
                return resp.readEntity(new GenericType<List<Grupo>>() {});
            }
            LOG.warning("API de grupos respondio codigo " + resp.getStatus());
        } catch (Exception e) {
            LOG.log(Level.WARNING, "No se pudo conectar al Servicio de Estadisticas (grupos)", e);
        }
        return usarMockSiFalla ? datosEjemploGrupos() : new ArrayList<>();
    }

    public List<Sede> listarSedes() {
        try {
            Response resp = client.target(baseUrl).path("/Sedes")
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            if (resp.getStatus() == 200) {
                return resp.readEntity(new GenericType<List<Sede>>() {});
            }
            LOG.warning("API de sedes respondio codigo " + resp.getStatus());
        } catch (Exception e) {
            LOG.log(Level.WARNING, "No se pudo conectar al Servicio de Estadisticas (sedes)", e);
        }
        return usarMockSiFalla ? datosEjemploSedes() : new ArrayList<>();
    }

    // ---------------------------------------------------------------
    // USUARIOS (RF23, RF25)
    // ---------------------------------------------------------------

    public List<Usuario> listarUsuarios() {
        try {
            Response resp = client.target(baseUrl).path("/Usuarios")
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            if (resp.getStatus() == 200) {
                return resp.readEntity(new GenericType<List<Usuario>>() {});
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, "No se pudo conectar al Servicio de Estadisticas (usuarios)", e);
        }
        return usarMockSiFalla ? datosEjemploUsuarios() : new ArrayList<>();
    }

    public boolean actualizarRolUsuario(Long usuarioId, String nuevoRol) {
        try {
            Response resp = client.target(baseUrl).path("/Usuarios/" + usuarioId + "/rol")
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.json(nuevoRol));
            return resp.getStatus() == 200 || resp.getStatus() == 204;
        } catch (Exception e) {
            LOG.log(Level.WARNING, "No se pudo actualizar el rol en la API (modo mock)", e);
            return usarMockSiFalla;
        }
    }

    // ---------------------------------------------------------------
    // AUDITORIA (RF24)
    // ---------------------------------------------------------------

    public List<Auditoria> listarAuditorias() {
        try {
            Response resp = client.target(baseUrl).path("/Auditorias")
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            if (resp.getStatus() == 200) {
                return resp.readEntity(new GenericType<List<Auditoria>>() {});
            }
            LOG.warning("API de auditorias respondio codigo " + resp.getStatus());
        } catch (Exception e) {
            LOG.log(Level.WARNING, "No se pudo conectar al Servicio de Estadisticas (auditorias)", e);
        }
        return usarMockSiFalla ? datosEjemploAuditorias() : new ArrayList<>();
    }

    // ---------------------------------------------------------------
    // AUTENTICACION (RF02)
    // ---------------------------------------------------------------

    public Usuario iniciarSesion(String username, String password) throws AutenticacionException {
        try (Response resp = client.target(baseUrl).path("Auth/login")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(new LoginRequestDto(username, password)))) {

            switch (resp.getStatus()) {
                case 200: {
                    try {
                        Usuario usuario = resp.readEntity(Usuario.class);
                        if (usuario == null) {
                            throw new AutenticacionException(
                                    AutenticacionException.Tipo.RESPUESTA_INESPERADA,
                                    "La respuesta de autenticacion no contiene un usuario.");
                        }
                        return usuario;
                    } catch (ProcessingException e) {
                        throw new AutenticacionException(
                                AutenticacionException.Tipo.RESPUESTA_INESPERADA,
                                "La respuesta de autenticacion no tiene el formato esperado.", e);
                    }
                }
                case 401:
                    throw new AutenticacionException(
                            AutenticacionException.Tipo.CREDENCIALES_INVALIDAS,
                            "El username o la contrasena no son validos.");
                case 403:
                    throw new AutenticacionException(
                            AutenticacionException.Tipo.ACCESO_DENEGADO,
                            "El backend denego el acceso del usuario.");
                default:
                    LOG.warning("API de login respondio codigo " + resp.getStatus());
                    throw new AutenticacionException(
                            AutenticacionException.Tipo.RESPUESTA_INESPERADA,
                            "La API de autenticacion respondio con codigo " + resp.getStatus() + ".");
            }
        } catch (AutenticacionException e) {
            throw e;
        } catch (ProcessingException e) {
            LOG.log(Level.WARNING, "No se pudo conectar a la API de autenticacion", e);
            throw new AutenticacionException(
                    AutenticacionException.Tipo.CONEXION,
                    "No fue posible conectar con la API de autenticacion.", e);
        }
    }

    // ---------------------------------------------------------------
    // DATOS DE EJEMPLO (MOCK) - solo para desarrollo mientras no hay backend
    // ---------------------------------------------------------------

    private List<Partido> datosEjemploPartidos() {
        List<Partido> lista = new ArrayList<>();
        lista.add(crearPartidoEjemplo(1L, 1, "Mexico", "Polonia", "Estadio Azteca", "Ciudad de Mexico", "MEX",
                "Fase de grupos", "GRUPOS", "A",
                java.time.LocalDateTime.of(2026, 6, 11, 20, 0), Partido.Estado.PROGRAMADO, null, null));
        lista.add(crearPartidoEjemplo(2L, 2, "Estados Unidos", "Gales", "AT&T Stadium", "Dallas", "USA",
                "Fase de grupos", "GRUPOS", "D",
                java.time.LocalDateTime.of(2026, 6, 12, 16, 0), Partido.Estado.PROGRAMADO, null, null));
        lista.add(crearPartidoEjemplo(3L, 3, "Argentina", "Canada", "BMO Field", "Toronto", "CAN",
                "Fase de grupos", "GRUPOS", "B",
                java.time.LocalDateTime.of(2026, 6, 13, 18, 0), Partido.Estado.FINALIZADO, 2, 0));
        lista.add(crearPartidoEjemplo(4L, 4, "Brasil", "Corea del Sur", "SoFi Stadium", "Los Angeles", "USA",
                "Fase de grupos", "GRUPOS", "C",
                java.time.LocalDateTime.of(2026, 6, 14, 19, 0), Partido.Estado.EN_JUEGO, 1, 1));
        return lista;
    }

    private Partido crearPartidoEjemplo(Long partidoId, int numeroFifa, String local, String visitante,
                                         String sede, String ciudad, String pais,
                                         String faseNombre, String faseCodigo, String grupoCodigo,
                                         java.time.LocalDateTime fecha, Partido.Estado estado,
                                         Integer golesLocal, Integer golesVisitante) {
        Partido p = new Partido();
        p.setPartidoId(partidoId);
        p.setNumeroPartidoFifa(numeroFifa);
        p.setLocalNombre(local);
        p.setVisitanteNombre(visitante);
        p.setSedeNombre(sede);
        p.setSedeCiudad(ciudad);
        p.setSedePais(pais);
        p.setFaseNombre(faseNombre);
        p.setFaseCodigo(faseCodigo);
        p.setGrupoCodigo(grupoCodigo);
        p.setGrupoNombre("Grupo " + grupoCodigo);
        p.setFechaPartido(fecha);
        p.setEstado(estado);
        p.setGolesLocal(golesLocal);
        p.setGolesVisitante(golesVisitante);
        return p;
    }

    private List<Seleccion> datosEjemploSelecciones() {
        List<Seleccion> lista = new ArrayList<>();
        lista.add(new Seleccion(1, "MEX", "Mexico", "CONCACAF", true, "Clasificado", false, "A"));
        lista.add(new Seleccion(2, "USA", "Estados Unidos", "CONCACAF", true, "Clasificado", false, "D"));
        lista.add(new Seleccion(3, "CAN", "Canada", "CONCACAF", true, "Clasificado", false, "B"));
        lista.add(new Seleccion(4, "ARG", "Argentina", "CONMEBOL", false, "Clasificado", false, "B"));
        lista.add(new Seleccion(5, "BRA", "Brasil", "CONMEBOL", false, "Clasificado", false, "C"));
        lista.add(new Seleccion(6, "POL", "Polonia", "UEFA", false, "Clasificado", false, "A"));
        return lista;
    }

    private List<Fase> datosEjemploFases() {
        List<Fase> lista = new ArrayList<>();
        lista.add(new Fase("GRUPOS", "Fase de grupos", 1));
        lista.add(new Fase("OCTAVOS", "Octavos de final", 2));
        lista.add(new Fase("CUARTOS", "Cuartos de final", 3));
        lista.add(new Fase("SEMIS", "Semifinal", 4));
        lista.add(new Fase("FINAL", "Final", 5));
        return lista;
    }

    private List<Grupo> datosEjemploGrupos() {
        List<Grupo> lista = new ArrayList<>();
        lista.add(new Grupo("A", "Grupo A"));
        lista.add(new Grupo("B", "Grupo B"));
        lista.add(new Grupo("C", "Grupo C"));
        lista.add(new Grupo("D", "Grupo D"));
        return lista;
    }

    private List<Sede> datosEjemploSedes() {
        List<Sede> lista = new ArrayList<>();
        lista.add(new Sede(1L, "Estadio Azteca", "Ciudad de Mexico", "MEX"));
        lista.add(new Sede(2L, "AT&T Stadium", "Dallas", "USA"));
        lista.add(new Sede(3L, "BMO Field", "Toronto", "CAN"));
        return lista;
    }

    private List<Auditoria> datosEjemploAuditorias() {
        List<Auditoria> lista = new ArrayList<>();
        lista.add(crearAuditoriaEjemplo(1L, "Partidos", 3, "ACTUALIZAR",
                "{\"golesLocal\":null,\"golesVisitante\":null}", "{\"golesLocal\":2,\"golesVisitante\":0}",
                java.time.LocalDateTime.of(2026, 6, 13, 20, 5), 1));
        lista.add(crearAuditoriaEjemplo(2L, "Usuarios", 2, "ACTUALIZAR",
                "{\"rolNombre\":\"USUARIO\"}", "{\"rolNombre\":\"ADMINISTRADOR\"}",
                java.time.LocalDateTime.of(2026, 6, 12, 9, 30), 1));
        lista.add(crearAuditoriaEjemplo(3L, "Partidos", 5, "CREAR",
                null, "{\"numeroPartidoFifa\":5,\"faseCodigo\":\"GRUPOS\"}",
                java.time.LocalDateTime.of(2026, 6, 10, 14, 0), 1));
        return lista;
    }

    private Auditoria crearAuditoriaEjemplo(Long id, String tabla, int registroId, String accion,
                                             String datosAnteriores, String datosNuevos,
                                             java.time.LocalDateTime fecha, int usuarioId) {
        Auditoria a = new Auditoria();
        a.setId(id);
        a.setTablaAfectada(tabla);
        a.setRegistroId(registroId);
        a.setTipoAccionAuditoria(accion);
        a.setDatosAnteriores(datosAnteriores);
        a.setDatosNuevos(datosNuevos);
        a.setFecha(fecha);
        a.setUsuarioId(usuarioId);
        return a;
    }

    private List<Usuario> datosEjemploUsuarios() {
        List<Usuario> lista = new ArrayList<>();
        lista.add(new Usuario(1L, "admin", "Administrador Demo", "admin@golutn.edu.ec", "ADMINISTRADOR", true));
        lista.add(new Usuario(2L, "ana.torres", "Ana Torres", "ana.torres@utn.edu.ec", "USUARIO", true));
        lista.add(new Usuario(3L, "luis.perez", "Luis Perez", "luis.perez@utn.edu.ec", "USUARIO", true));
        return lista;
    }
}
