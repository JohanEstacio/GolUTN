package ec.edu.utn.golutn.admin.model;

import java.io.Serializable;

/**
 * Representa un usuario del sistema (viene del Servicio de Estadisticas,
 * donde el documento sugiere centralizar la autenticacion).
 */
public class Usuario implements Serializable {

    private Long id;
    private String nombre;
    private String correo;
    private String rol;       // ADMINISTRADOR, USUARIO, INVITADO
    private boolean activo;

    public Usuario() {
    }

    public Usuario(Long id, String nombre, String correo, String rol, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.rol = rol;
        this.activo = activo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
