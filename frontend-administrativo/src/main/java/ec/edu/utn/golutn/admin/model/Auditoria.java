package ec.edu.utn.golutn.admin.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/** Un registro del historial de auditoria del Servicio de Estadisticas (RF24). */
public class Auditoria implements Serializable {

    private Long id;
    private String tablaAfectada;
    private Integer registroId;
    private String tipoAccionAuditoria;
    private String datosAnteriores;
    private String datosNuevos;
    private LocalDateTime fecha;
    private Integer usuarioId;

    public Auditoria() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTablaAfectada() { return tablaAfectada; }
    public void setTablaAfectada(String tablaAfectada) { this.tablaAfectada = tablaAfectada; }

    public Integer getRegistroId() { return registroId; }
    public void setRegistroId(Integer registroId) { this.registroId = registroId; }

    public String getTipoAccionAuditoria() { return tipoAccionAuditoria; }
    public void setTipoAccionAuditoria(String tipoAccionAuditoria) { this.tipoAccionAuditoria = tipoAccionAuditoria; }

    public String getDatosAnteriores() { return datosAnteriores; }
    public void setDatosAnteriores(String datosAnteriores) { this.datosAnteriores = datosAnteriores; }

    public String getDatosNuevos() { return datosNuevos; }
    public void setDatosNuevos(String datosNuevos) { this.datosNuevos = datosNuevos; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
}
