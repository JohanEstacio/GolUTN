package ec.edu.utn.golutn.admin.service;

import java.io.Serializable;

/** Resultado de una operacion de escritura contra un backend: exito, codigo HTTP y el mensaje que informa el motivo. */
public class ResultadoOperacion implements Serializable {

    private final boolean exito;
    private final int codigoHttp;
    private final String mensaje;

    public ResultadoOperacion(boolean exito, int codigoHttp, String mensaje) {
        this.exito = exito;
        this.codigoHttp = codigoHttp;
        this.mensaje = mensaje;
    }

    public boolean isExito() { return exito; }
    public int getCodigoHttp() { return codigoHttp; }
    public String getMensaje() { return mensaje; }
}
