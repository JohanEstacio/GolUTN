package ec.edu.utn.golutn.admin.bean;

import com.fasterxml.jackson.databind.ObjectMapper;

import ec.edu.utn.golutn.admin.model.Auditoria;
import ec.edu.utn.golutn.admin.service.EstadisticasApiClient;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Historial de auditoria del Servicio de Estadisticas (RF24).
 */
@Named("auditoriaBean")
@ViewScoped
public class AuditoriaBean implements Serializable {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Inject
    private EstadisticasApiClient apiClient;

    private List<Auditoria> auditorias;
    private Auditoria auditoriaSeleccionada;
    private LocalDateTime ultimaActualizacion;

    @PostConstruct
    public void init() {
        recargar();
    }

    public void recargar() {
        cargarAuditorias();
        this.ultimaActualizacion = LocalDateTime.now();
    }

    public void cargarAuditorias() {
        this.auditorias = apiClient.listarAuditorias();
    }

    public void prepararDetalle(Auditoria auditoria) {
        this.auditoriaSeleccionada = auditoria;
    }

    public String getDatosAnterioresFormateados() {
        return auditoriaSeleccionada == null ? null : formatearJson(auditoriaSeleccionada.getDatosAnteriores());
    }

    public String getDatosNuevosFormateados() {
        return auditoriaSeleccionada == null ? null : formatearJson(auditoriaSeleccionada.getDatosNuevos());
    }

    private static String formatearJson(String crudo) {
        if (crudo == null || crudo.isBlank()) {
            return "(sin datos)";
        }
        try {
            Object arbol = MAPPER.readValue(crudo, Object.class);
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(arbol);
        } catch (Exception e) {
            return crudo;
        }
    }

    public List<Auditoria> getAuditorias() { return auditorias; }

    public Auditoria getAuditoriaSeleccionada() { return auditoriaSeleccionada; }

    public LocalDateTime getUltimaActualizacion() { return ultimaActualizacion; }
}
