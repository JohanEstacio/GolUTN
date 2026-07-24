package ec.edu.utn.golutn.admin.bean;

import ec.edu.utn.golutn.admin.dto.SeleccionRequestDto;
import ec.edu.utn.golutn.admin.model.Grupo;
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
 * Gestion del catalogo de selecciones participantes (RF10).
 */
@Named("seleccionesBean")
@ViewScoped
public class SeleccionesBean implements Serializable {

    @Inject
    private EstadisticasApiClient apiClient;

    private List<Seleccion> selecciones;
    private List<Grupo> grupos;

    private SeleccionRequestDto seleccionEnEdicion;
    private Integer seleccionIdEnEdicion;
    private boolean guardadoExitoso;

    @PostConstruct
    public void init() {
        cargarSelecciones();
        cargarGrupos();
    }

    public void cargarSelecciones() {
        this.selecciones = apiClient.listarSelecciones();
    }

    public void cargarGrupos() {
        this.grupos = apiClient.listarGrupos();
    }

    /** Abre el formulario en blanco para agregar una seleccion nueva al catalogo. */
    public void prepararNueva() {
        this.seleccionIdEnEdicion = null;
        this.guardadoExitoso = false;
        SeleccionRequestDto dto = new SeleccionRequestDto();
        dto.setEsAnfitrion(false);
        dto.setEliminada(false);
        this.seleccionEnEdicion = dto;
    }

    /** Pre-llena el formulario a partir de los datos de la seleccion elegida en la tabla. */
    public void prepararEdicion(Seleccion seleccion) {
        this.seleccionIdEnEdicion = seleccion.getId();
        this.guardadoExitoso = false;

        SeleccionRequestDto dto = new SeleccionRequestDto();
        dto.setId(seleccion.getId());
        dto.setCodigoFifa(seleccion.getCodigoFifa());
        dto.setNombre(seleccion.getNombre());
        dto.setConfederacion(seleccion.getConfederacion());
        dto.setEsAnfitrion(seleccion.isEsAnfitrion());
        dto.setClasificacion(seleccion.getClasificacion());
        dto.setEliminada(seleccion.isEliminada());
        dto.setGrupoCodigo(seleccion.getGrupoCodigo());
        this.seleccionEnEdicion = dto;
    }

    /** Valida y crea o actualiza la seleccion segun corresponda. */
    public void guardarSeleccion() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (seleccionEnEdicion == null) {
            return;
        }

        if (seleccionEnEdicion.getNombre() == null || seleccionEnEdicion.getNombre().isBlank()) {
            guardadoExitoso = false;
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Nombre requerido",
                    "Debes indicar el nombre de la selección."));
            return;
        }
        if (seleccionEnEdicion.getCodigoFifa() == null || seleccionEnEdicion.getCodigoFifa().isBlank()) {
            guardadoExitoso = false;
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Código FIFA requerido",
                    "Debes indicar el código FIFA de la selección."));
            return;
        }

        boolean esEdicion = seleccionIdEnEdicion != null;
        ResultadoOperacion resultado = esEdicion
                ? apiClient.actualizarSeleccion(seleccionIdEnEdicion, seleccionEnEdicion)
                : apiClient.crearSeleccion(seleccionEnEdicion);

        guardadoExitoso = resultado.isExito();
        if (resultado.isExito()) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    esEdicion ? "Selección actualizada" : "Selección creada",
                    esEdicion ? "Los datos de la selección se guardaron correctamente."
                              : "La selección se agregó al catálogo correctamente."));
            cargarSelecciones();
        } else {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
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

    public List<Seleccion> getSelecciones() { return selecciones; }

    public List<Grupo> getGrupos() { return grupos; }

    public SeleccionRequestDto getSeleccionEnEdicion() { return seleccionEnEdicion; }
    public void setSeleccionEnEdicion(SeleccionRequestDto seleccionEnEdicion) { this.seleccionEnEdicion = seleccionEnEdicion; }

    public boolean isEditando() { return seleccionIdEnEdicion != null; }

    public boolean isGuardadoExitoso() { return guardadoExitoso; }
}
