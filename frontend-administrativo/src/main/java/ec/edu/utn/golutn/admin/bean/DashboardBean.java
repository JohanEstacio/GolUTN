package ec.edu.utn.golutn.admin.bean;

import ec.edu.utn.golutn.admin.service.EstadisticasApiClient;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Tarjetas de resumen del inicio del panel administrativo.
 */
@Named("dashboardBean")
@ViewScoped
public class DashboardBean implements Serializable {

    private static final Logger LOG = Logger.getLogger(DashboardBean.class.getName());

    @Inject
    private EstadisticasApiClient apiClient;

    private Integer totalPartidos;
    private Integer totalSelecciones;
    private Integer totalGrupos;

    @PostConstruct
    public void init() {
        totalPartidos = contarSinFallar(apiClient::contarPartidos, "partidos");
        totalSelecciones = contarSinFallar(apiClient::contarSelecciones, "selecciones");
        totalGrupos = contarSinFallar(apiClient::contarGrupos, "grupos");
    }

    private Integer contarSinFallar(java.util.function.IntSupplier conteo, String etiqueta) {
        try {
            return conteo.getAsInt();
        } catch (Exception e) {
            LOG.log(Level.WARNING, "No se pudo obtener el conteo de " + etiqueta + " para el dashboard", e);
            return null;
        }
    }

    public Integer getTotalPartidos() { return totalPartidos; }

    public Integer getTotalSelecciones() { return totalSelecciones; }

    public Integer getTotalGrupos() { return totalGrupos; }
}
