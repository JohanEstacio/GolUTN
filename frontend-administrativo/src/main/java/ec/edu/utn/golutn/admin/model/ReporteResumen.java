package ec.edu.utn.golutn.admin.model;

import java.io.Serializable;

/** Datos agregados para el modulo de reportes (RF27). */
public class ReporteResumen implements Serializable {

    private String partidoMasPredicciones;
    private long totalPredicciones;
    private double utnGolCoinEnCirculacion;
    private long usuariosRegistrados;
    private long partidosFinalizados;

    public ReporteResumen() {
    }

    public ReporteResumen(String partidoMasPredicciones, long totalPredicciones,
                           double utnGolCoinEnCirculacion, long usuariosRegistrados,
                           long partidosFinalizados) {
        this.partidoMasPredicciones = partidoMasPredicciones;
        this.totalPredicciones = totalPredicciones;
        this.utnGolCoinEnCirculacion = utnGolCoinEnCirculacion;
        this.usuariosRegistrados = usuariosRegistrados;
        this.partidosFinalizados = partidosFinalizados;
    }

    public String getPartidoMasPredicciones() { return partidoMasPredicciones; }
    public void setPartidoMasPredicciones(String v) { this.partidoMasPredicciones = v; }

    public long getTotalPredicciones() { return totalPredicciones; }
    public void setTotalPredicciones(long v) { this.totalPredicciones = v; }

    public double getUtnGolCoinEnCirculacion() { return utnGolCoinEnCirculacion; }
    public void setUtnGolCoinEnCirculacion(double v) { this.utnGolCoinEnCirculacion = v; }

    public long getUsuariosRegistrados() { return usuariosRegistrados; }
    public void setUsuariosRegistrados(long v) { this.usuariosRegistrados = v; }

    public long getPartidosFinalizados() { return partidosFinalizados; }
    public void setPartidosFinalizados(long v) { this.partidosFinalizados = v; }
}
