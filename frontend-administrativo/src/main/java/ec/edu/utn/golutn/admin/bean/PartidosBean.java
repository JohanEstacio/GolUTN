package ec.edu.utn.golutn.admin.bean;

import ec.edu.utn.golutn.admin.model.Partido;
import ec.edu.utn.golutn.admin.service.EstadisticasApiClient;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

/**
 * Gestion de partidos y registro de resultados oficiales.
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

    @PostConstruct
    public void init() {
        cargarPartidos();
    }

    public void cargarPartidos() {
        this.partidos = apiClient.listarPartidos();
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
                    "Se actualizo la tabla de posiciones y se liquidaron las predicciones."));
            cargarPartidos();
        } else {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "No se pudo registrar",
                    "Intenta nuevamente o revisa la conexion con el Servicio de Estadisticas."));
        }
    }

    public List<Partido> getPartidos() { return partidos; }

    public Partido getPartidoSeleccionado() { return partidoSeleccionado; }
    public void setPartidoSeleccionado(Partido p) { this.partidoSeleccionado = p; }

    public Integer getGolesLocalIngresados() { return golesLocalIngresados; }
    public void setGolesLocalIngresados(Integer v) { this.golesLocalIngresados = v; }

    public Integer getGolesVisitanteIngresados() { return golesVisitanteIngresados; }
    public void setGolesVisitanteIngresados(Integer v) { this.golesVisitanteIngresados = v; }
}
