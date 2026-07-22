package ec.edu.utn.golutn.admin.service;

/**
 * Describe los errores esperados al autenticar contra el backend.
 */
public class AutenticacionException extends Exception {

    public enum Tipo {
        CREDENCIALES_INVALIDAS,
        ACCESO_DENEGADO,
        CONEXION,
        RESPUESTA_INESPERADA
    }

    private final Tipo tipo;

    public AutenticacionException(Tipo tipo, String message) {
        super(message);
        this.tipo = tipo;
    }

    public AutenticacionException(Tipo tipo, String message, Throwable cause) {
        super(message, cause);
        this.tipo = tipo;
    }

    public Tipo getTipo() {
        return tipo;
    }
}
