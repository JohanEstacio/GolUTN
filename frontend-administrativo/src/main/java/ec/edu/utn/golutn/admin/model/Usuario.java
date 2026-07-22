package ec.edu.utn.golutn.admin.model;

import java.io.Serializable;

/**
 * Representa un usuario del sistema (viene del Servicio de Estadisticas,
 * donde el documento sugiere centralizar la autenticacion).
 * Coincide con UsuarioDto: id, username, nombre, email, rolNombre, activo.
 */
public class Usuario implements Serializable {

    private Long id;
    private String username;
    private String nombre;
    private String email;
    private String rolNombre;       // ADMINISTRADOR, USUARIO, INVITADO
    private boolean activo;

    public Usuario() {
    }

    public Usuario(Long id, String username, String nombre, String email, String rolNombre, boolean activo) {
        this.id = id;
        this.username = username;
        this.nombre = nombre;
        this.email = email;
        this.rolNombre = rolNombre;
        this.activo = activo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRolNombre() { return rolNombre; }
    public void setRolNombre(String rolNombre) { this.rolNombre = rolNombre; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
