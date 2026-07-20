package ec.edu.utn.golutn.admin.bean;

import ec.edu.utn.golutn.admin.model.ReporteResumen;
import ec.edu.utn.golutn.admin.service.UtnGolCoinApiClient;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;

/**
 * Reportes basicos y auditoria (RF27).
 */
@Named("reportesBean")
@ViewScoped
public class ReportesBean implements Serializable {

    @Inject
    private UtnGolCoinApiClient utnGolCoinApiClient;

    private ReporteResumen resumen;

    @PostConstruct
    public void init() {
        this.resumen = utnGolCoinApiClient.obtenerResumenReportes();
    }

    public ReporteResumen getResumen() { return resumen; }
}
