package ec.edu.utn.golutn.admin.service;

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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Encapsula toda la comunicacion con el Servicio de Estadisticas (API REST).
 *
 * IMPORTANTE PARA EL EQUIPO:
 * Mientras el backend de Estadisticas no este desplegado, este cliente
 * atrapa el error de conexion y devuelve datos de ejemplo (mock), para que
 * las pantallas del panel administrativo se puedan seguir construyendo y
 * probando sin depender de que el otro servicio ya este arriba.
 *
 * Cuando el backend este listo:
 *   1. Ajusta ESTADISTICAS_API_URL en web.xml con la URL real.
 *   2. Pon USAR_MOCK_SI_FALLA_API en false si quieres que los errores
 *      se muestren en vez de ocultarse con datos de ejemplo.
 */
@ApplicationScoped
public class EstadisticasApiClient {

    private static final Logger LOG = Logger.getLogger(EstadisticasApiClient.class.getName());

    private Client client;
    private String baseUrl;
    private boolean usarMockSiFalla;

    @PostConstruct
    public void init() {
        this.client = ClientBuilder.newClient();
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc != null) {
            this.baseUrl = fc.getExternalContext().getInitParameter("ESTADISTICAS_API_URL");
            this.usarMockSiFalla = Boolean.parseBoolean(
                    fc.getExternalContext().getInitParameter("USAR_MOCK_SI_FALLA_API"));
        } else {
            this.baseUrl = "http://localhost:8080/estadisticas-api/api";
            this.usarMockSiFalla = true;
        }
    }

    // ---------------------------------------------------------------
    // PARTIDOS (RF10, RF11)
    // ---------------------------------------------------------------

    public List<Partido> listarPartidos() {
        try {
            Response resp = client.target(baseUrl).path("/partidos")
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            if (resp.getStatus() == 200) {
                return resp.readEntity(new GenericType<List<Partido>>() {});
            }
            LOG.warning("API de partidos respondio codigo " + resp.getStatus());
        } catch (Exception e) {
            LOG.log(Level.WARNING, "No se pudo conectar al Servicio de Estadisticas, usando datos de ejemplo", e);
        }
        return usarMockSiFalla ? datosEjemploPartidos() : new ArrayList<>();
    }

    public boolean registrarResultado(Long partidoId, int golesLocal, int golesVisitante) {
        try {
            Response resp = client.target(baseUrl).path("/partidos/" + partidoId + "/resultado")
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.json(new int[]{golesLocal, golesVisitante}));
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
            Response resp = client.target(baseUrl).path("/selecciones")
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
            Response resp = client.target(baseUrl).path("/usuarios")
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
            Response resp = client.target(baseUrl).path("/usuarios/" + usuarioId + "/rol")
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

    public Usuario iniciarSesion(String correo, String password) {
        try {
            Response resp = client.target(baseUrl).path("/auth/login")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(new String[]{correo, password}));

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

        if (usarMockSiFalla && "admin@golutn.edu.ec".equalsIgnoreCase(correo) && "admin123".equals(password)) {
            return new Usuario(1L, "Administrador Demo", correo, "ADMINISTRADOR", true);
        }
        return null;
    }

    // ---------------------------------------------------------------
    // DATOS DE EJEMPLO (MOCK) - solo para desarrollo mientras no hay backend
    // ---------------------------------------------------------------

    private List<Partido> datosEjemploPartidos() {
        List<Partido> lista = new ArrayList<>();
        lista.add(new Partido(1L, "Mexico", "Polonia", "Estadio Azteca (Ciudad de Mexico)",
                "Fase de grupos", LocalDateTime.of(2026, 6, 11, 20, 0),
                Partido.Estado.PROGRAMADO, null, null));
        lista.add(new Partido(2L, "Estados Unidos", "Gales", "AT&T Stadium (Dallas)",
                "Fase de grupos", LocalDateTime.of(2026, 6, 12, 16, 0),
                Partido.Estado.PROGRAMADO, null, null));
        lista.add(new Partido(3L, "Argentina", "Canada", "BMO Field (Toronto)",
                "Fase de grupos", LocalDateTime.of(2026, 6, 13, 18, 0),
                Partido.Estado.FINALIZADO, 2, 0));
        lista.add(new Partido(4L, "Brasil", "Corea del Sur", "SoFi Stadium (Los Angeles)",
                "Fase de grupos", LocalDateTime.of(2026, 6, 14, 19, 0),
                Partido.Estado.EN_JUEGO, 1, 1));
        return lista;
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
        lista.add(new Usuario(1L, "Administrador Demo", "admin@golutn.edu.ec", "ADMINISTRADOR", true));
        lista.add(new Usuario(2L, "Ana Torres", "ana.torres@utn.edu.ec", "USUARIO", true));
        lista.add(new Usuario(3L, "Luis Perez", "luis.perez@utn.edu.ec", "USUARIO", true));
        lista.add(new Usuario(4L, "Invitado Demo", "invitado@golutn.edu.ec", "INVITADO", true));
        return lista;
    }
}
