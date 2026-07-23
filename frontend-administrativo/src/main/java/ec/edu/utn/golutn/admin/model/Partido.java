package ec.edu.utn.golutn.admin.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.io.Serializable;
import java.time.LocalDateTime;

/** Uno de los 104 partidos del torneo (RF10, RF11). */
public class Partido implements Serializable {

    public enum Estado {
        PROGRAMADO, EN_JUEGO, FINALIZADO, SUSPENDIDO, CANCELADO;

        @JsonCreator
        public static Estado desde(String valor) {
            if (valor == null || valor.isBlank()) return null;
            String normalizado = valor.trim().toUpperCase().replace(' ', '_');
            for (Estado e : values()) {
                if (e.name().equals(normalizado)) return e;
            }
            return null;
        }
    }

    private Long partidoId;
    private Integer numeroPartidoFifa;
    private String localNombre;
    private String visitanteNombre;
    private Long localId;
    private Long visitanteId;
    private String sedeNombre;
    private String sedeCiudad;
    private String sedePais;
    private Long sedeId;
    private String faseNombre;
    private String faseCodigo;
    private String grupoCodigo;
    private String grupoNombre;
    private LocalDateTime fechaPartido;
    private Estado estado;
    private Integer golesLocal;
    private Integer golesVisitante;

    public Partido() {
    }

    public String getEstadoCss() {
        return estado == null ? "desconocido" : estado.name().toLowerCase();
    }

    public Long getPartidoId() { return partidoId; }
    public void setPartidoId(Long partidoId) { this.partidoId = partidoId; }

    public Integer getNumeroPartidoFifa() { return numeroPartidoFifa; }
    public void setNumeroPartidoFifa(Integer numeroPartidoFifa) { this.numeroPartidoFifa = numeroPartidoFifa; }

    public String getLocalNombre() { return localNombre; }
    public void setLocalNombre(String localNombre) { this.localNombre = localNombre; }

    public String getVisitanteNombre() { return visitanteNombre; }
    public void setVisitanteNombre(String visitanteNombre) { this.visitanteNombre = visitanteNombre; }

    public Long getLocalId() { return localId; }
    public void setLocalId(Long localId) { this.localId = localId; }

    public Long getVisitanteId() { return visitanteId; }
    public void setVisitanteId(Long visitanteId) { this.visitanteId = visitanteId; }

    public String getSedeNombre() { return sedeNombre; }
    public void setSedeNombre(String sedeNombre) { this.sedeNombre = sedeNombre; }

    public String getSedeCiudad() { return sedeCiudad; }
    public void setSedeCiudad(String sedeCiudad) { this.sedeCiudad = sedeCiudad; }

    public String getSedePais() { return sedePais; }
    public void setSedePais(String sedePais) { this.sedePais = sedePais; }

    public Long getSedeId() { return sedeId; }
    public void setSedeId(Long sedeId) { this.sedeId = sedeId; }

    public String getFaseNombre() { return faseNombre; }
    public void setFaseNombre(String faseNombre) { this.faseNombre = faseNombre; }

    public String getFaseCodigo() { return faseCodigo; }
    public void setFaseCodigo(String faseCodigo) { this.faseCodigo = faseCodigo; }

    public String getGrupoCodigo() { return grupoCodigo; }
    public void setGrupoCodigo(String grupoCodigo) { this.grupoCodigo = grupoCodigo; }

    public String getGrupoNombre() { return grupoNombre; }
    public void setGrupoNombre(String grupoNombre) { this.grupoNombre = grupoNombre; }

    public LocalDateTime getFechaPartido() { return fechaPartido; }
    public void setFechaPartido(LocalDateTime fechaPartido) { this.fechaPartido = fechaPartido; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }

    public Integer getGolesLocal() { return golesLocal; }
    public void setGolesLocal(Integer golesLocal) { this.golesLocal = golesLocal; }

    public Integer getGolesVisitante() { return golesVisitante; }
    public void setGolesVisitante(Integer golesVisitante) { this.golesVisitante = golesVisitante; }
}
