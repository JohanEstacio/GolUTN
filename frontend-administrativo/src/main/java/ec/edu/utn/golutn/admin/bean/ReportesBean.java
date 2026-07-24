package ec.edu.utn.golutn.admin.bean;

import ec.edu.utn.golutn.admin.model.ReporteResumen;
import ec.edu.utn.golutn.admin.service.UtnGolCoinApiClient;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Reportes basicos y auditoria (RF27).
 */
@Named("reportesBean")
@ViewScoped
public class ReportesBean implements Serializable {

    @Inject
    private UtnGolCoinApiClient utnGolCoinApiClient;

    private ReporteResumen resumen;
    private LocalDateTime ultimaActualizacion;

    @PostConstruct
    public void init() {
        recargar();
    }

    public void recargar() {
        this.resumen = utnGolCoinApiClient.obtenerResumenReportes();
        this.ultimaActualizacion = LocalDateTime.now();
    }

    public ReporteResumen getResumen() { return resumen; }

    public LocalDateTime getUltimaActualizacion() { return ultimaActualizacion; }
}
