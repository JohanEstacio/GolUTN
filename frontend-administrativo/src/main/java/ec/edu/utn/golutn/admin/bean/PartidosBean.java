package ec.edu.utn.golutn.admin.bean;

import ec.edu.utn.golutn.admin.dto.PartidoRequestDto;
import ec.edu.utn.golutn.admin.model.Fase;
import ec.edu.utn.golutn.admin.model.Grupo;
import ec.edu.utn.golutn.admin.model.Partido;
import ec.edu.utn.golutn.admin.model.Sede;
import ec.edu.utn.golutn.admin.model.Seleccion;
import ec.edu.utn.golutn.admin.service.EstadisticasApiClient;
import ec.edu.utn.golutn.admin.service.ResultadoOperacion;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

/**
 * Gestion de partidos: alta, edicion y registro de resultados oficiales.
 * Cubre RF10, RF11, RF12 y los casos de prueba CP09/CP10.
 */
@Named("partidosBean")
@ViewScoped
public class PartidosBean implements Serializable {

    @Inject
    private EstadisticasApiClient apiClient;

    @Inject
    private LoginBean loginBean;

    private List<Partido> partidos;
    private Partido partidoSeleccionado;
    private Integer golesLocalIngresados;
    private Integer golesVisitanteIngresados;

    // Catalogos para el formulario de creacion/edicion (RF10)
    private List<Seleccion> selecciones;
    private List<Fase> fases;
    private List<Grupo> grupos;
    private List<Sede> sedes;

    // Alta y edicion de partidos (RF10)
    private PartidoRequestDto partidoEnEdicion;
    private Long partidoIdEnEdicion;
    private boolean guardadoExitoso;

    @PostConstruct
    public void init() {
        cargarPartidos();
        cargarCatalogos();
    }

    public void cargarPartidos() {
        this.partidos = apiClient.listarPartidos();
    }

    public void cargarCatalogos() {
        this.selecciones = apiClient.listarSelecciones();
        this.fases = apiClient.listarFases();
        this.grupos = apiClient.listarGrupos();
        this.sedes = apiClient.listarSedes();
    }

    public void prepararRegistroResultado(Partido partido) {
        this.partidoSeleccionado = partido;
        this.golesLocalIngresados = partido.getGolesLocal();
        this.golesVisitanteIngresados = partido.getGolesVisitante();
    }

    /** RF11 / RF12: al guardar, el Servicio de Estadisticas notifica a UTNGolCoin para liquidar. */
    public void guardarResultado() {
        if (partidoSeleccionado == null || golesLocalIngresados == null || golesVisitanteIngresados == null) {
            return;
        }
        Long usuarioId = loginBean.getUsuarioActual() != null ? loginBean.getUsuarioActual().getId() : null;
        boolean ok = apiClient.registrarResultado(
                partidoSeleccionado.getPartidoId(), golesLocalIngresados, golesVisitanteIngresados, usuarioId);

        FacesContext fc = FacesContext.getCurrentInstance();
        if (ok) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Resultado registrado",
                    "Se actualizó la tabla de posiciones y se liquidaron las predicciones."));
            cargarPartidos();
        } else {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "No se pudo registrar",
                    "Intenta nuevamente o revisa la conexión con el Servicio de Estadísticas."));
        }
    }

    /** RF10: abre el formulario en blanco para agregar un partido nuevo al calendario. */
    public void prepararNuevo() {
        this.partidoIdEnEdicion = null;
        this.guardadoExitoso = false;
        PartidoRequestDto dto = new PartidoRequestDto();
        dto.setEstado(Partido.Estado.PROGRAMADO);
        this.partidoEnEdicion = dto;
    }

    /** RF10: pre-llena el formulario a partir de los datos y los IDs que ya trae el CalendarioPartidoDto. */
    public void prepararEdicion(Partido partido) {
        this.partidoIdEnEdicion = partido.getPartidoId();
        this.guardadoExitoso = false;

        PartidoRequestDto dto = new PartidoRequestDto();
        dto.setId(partido.getPartidoId());
        dto.setNumeroPartidoFifa(partido.getNumeroPartidoFifa());
        dto.setFechaPartido(partido.getFechaPartido());
        dto.setEstado(partido.getEstado());
        // Se conservan los goles ya registrados para no borrar resultados existentes.
        dto.setGolesLocal(partido.getGolesLocal());
        dto.setGolesVisitante(partido.getGolesVisitante());
        dto.setFaseCodigo(partido.getFaseCodigo());
        dto.setGrupoCodigo(partido.getGrupoCodigo());
        dto.setSedeId(partido.getSedeId());
        dto.setLocalId(partido.getLocalId());
        dto.setVisitanteId(partido.getVisitanteId());
        this.partidoEnEdicion = dto;

        if (partido.getEstado() == Partido.Estado.FINALIZADO) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "Partido finalizado",
                    "Este partido ya finalizó. Modificar sus datos puede afectar resultados y predicciones ya liquidadas."));
        }
    }

    /** RF10 / RNF10: valida y crea o actualiza el partido segun corresponda. */
    public void guardarPartido() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (partidoEnEdicion == null) {
            return;
        }

        if (partidoEnEdicion.getLocalId() == null || partidoEnEdicion.getVisitanteId() == null) {
            guardadoExitoso = false;
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Selección incompleta",
                    "Debes seleccionar la selección local y la selección visitante."));
            return;
        }
        if (partidoEnEdicion.getLocalId().equals(partidoEnEdicion.getVisitanteId())) {
            guardadoExitoso = false;
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Selección inválida",
                    "La selección local y la visitante no pueden ser la misma."));
            return;
        }
        if (partidoEnEdicion.getFechaPartido() == null) {
            guardadoExitoso = false;
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Fecha requerida",
                    "Debes indicar la fecha y hora del partido."));
            return;
        }

        boolean esEdicion = partidoIdEnEdicion != null;
        ResultadoOperacion resultado = esEdicion
                ? apiClient.actualizarPartido(partidoIdEnEdicion, partidoEnEdicion)
                : apiClient.crearPartido(partidoEnEdicion);

        guardadoExitoso = resultado.isExito();
        if (resultado.isExito()) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    esEdicion ? "Partido actualizado" : "Partido creado",
                    esEdicion ? "Los datos del partido se guardaron correctamente."
                              : "El partido se agregó al calendario correctamente."));
            cargarPartidos();
        } else {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "No se pudo guardar", mensajeError(resultado)));
        }
    }

    private String mensajeError(ResultadoOperacion resultado) {
        if (resultado.getCodigoHttp() == 0) {
            return "No se pudo conectar con el Servicio de Estadísticas. Intenta nuevamente.";
        }
        String mensaje = resultado.getMensaje();
        if (mensaje == null || mensaje.isBlank()) {
            return "El servicio respondió con el código " + resultado.getCodigoHttp() + ".";
        }
        return mensaje;
    }

    public List<Partido> getPartidos() { return partidos; }

    public Partido getPartidoSeleccionado() { return partidoSeleccionado; }
    public void setPartidoSeleccionado(Partido p) { this.partidoSeleccionado = p; }

    public Integer getGolesLocalIngresados() { return golesLocalIngresados; }
    public void setGolesLocalIngresados(Integer v) { this.golesLocalIngresados = v; }

    public Integer getGolesVisitanteIngresados() { return golesVisitanteIngresados; }
    public void setGolesVisitanteIngresados(Integer v) { this.golesVisitanteIngresados = v; }

    public List<Seleccion> getSelecciones() { return selecciones; }

    public List<Fase> getFases() { return fases; }

    public List<Grupo> getGrupos() { return grupos; }

    public List<Sede> getSedes() { return sedes; }

    /** FINALIZADO no se incluye: ese estado lo asigna el backend al liquidar un resultado (RF11). */
    public Partido.Estado[] getEstadosDisponibles() {
        return new Partido.Estado[] {
                Partido.Estado.PROGRAMADO, Partido.Estado.EN_JUEGO,
                Partido.Estado.SUSPENDIDO, Partido.Estado.CANCELADO
        };
    }

    public PartidoRequestDto getPartidoEnEdicion() { return partidoEnEdicion; }
    public void setPartidoEnEdicion(PartidoRequestDto partidoEnEdicion) { this.partidoEnEdicion = partidoEnEdicion; }

    public boolean isEditando() { return partidoIdEnEdicion != null; }

    public boolean isGuardadoExitoso() { return guardadoExitoso; }
}
