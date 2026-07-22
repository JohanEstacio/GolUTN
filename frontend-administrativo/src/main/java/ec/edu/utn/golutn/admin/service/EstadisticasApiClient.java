package ec.edu.utn.golutn.admin.service;

import ec.edu.utn.golutn.admin.dto.LoginRequestDto;
import ec.edu.utn.golutn.admin.dto.ResultadoRequestDto;
import ec.edu.utn.golutn.admin.model.Partido;
import ec.edu.utn.golutn.admin.model.Seleccion;
import ec.edu.utn.golutn.admin.model.Usuario;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.context.FacesContext;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
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

    // ---------------------------------------------------------------
    // SELECCIONES / GRUPOS
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
    // AUTENTICACION (RF02)
    // ---------------------------------------------------------------

    public Usuario iniciarSesion(String username, String password) {
        try {
            Response resp = client.target(baseUrl).path("/Auth/login")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(new LoginRequestDto(username, password)));

            if (resp.getStatus() == 200) {
                return resp.readEntity(Usuario.class);
            }

            if (resp.getStatus() == 401 || resp.getStatus() == 403) {
                // El backend SI esta arriba y respondio que las credenciales son invalidas.
                return null;
            }

            // Cualquier otro codigo (404, 500, etc.) significa que el backend
            // todavia no tiene ese endpoint listo -> usamos el modo mock.
            LOG.warning("API de login respondio codigo " + resp.getStatus() + ", usando datos de ejemplo");
        } catch (Exception e) {
            LOG.log(Level.WARNING, "No se pudo conectar al Servicio de Estadisticas (login), usando modo mock", e);
        }

        if (usarMockSiFalla && "admin@golutn.edu.ec".equalsIgnoreCase(username) && "admin123".equals(password)) {
            return new Usuario(1L, "admin", "Administrador Demo", username, "ADMINISTRADOR", true);
        }
        return null;
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
        lista.add(new Seleccion(1L, "Mexico", "A", "mx"));
        lista.add(new Seleccion(2L, "Estados Unidos", "D", "us"));
        lista.add(new Seleccion(3L, "Canada", "B", "ca"));
        lista.add(new Seleccion(4L, "Argentina", "B", "ar"));
        lista.add(new Seleccion(5L, "Brasil", "C", "br"));
        lista.add(new Seleccion(6L, "Polonia", "A", "pl"));
        return lista;
    }

    private List<Usuario> datosEjemploUsuarios() {
        List<Usuario> lista = new ArrayList<>();
        lista.add(new Usuario(1L, "admin", "Administrador Demo", "admin@golutn.edu.ec", "ADMINISTRADOR", true));
        lista.add(new Usuario(2L, "ana.torres", "Ana Torres", "ana.torres@utn.edu.ec", "USUARIO", true));
        lista.add(new Usuario(3L, "luis.perez", "Luis Perez", "luis.perez@utn.edu.ec", "USUARIO", true));
        return lista;
    }
}
