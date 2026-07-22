package ec.edu.utn.golutn.admin.bean;

import ec.edu.utn.golutn.admin.model.Usuario;
import ec.edu.utn.golutn.admin.service.EstadisticasApiClient;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

/**
 * Gestion de usuarios y asignacion de roles (RF23, RF25).
 */
@Named("usuariosBean")
@ViewScoped
public class UsuariosBean implements Serializable {

    @Inject
    private EstadisticasApiClient apiClient;

    private List<Usuario> usuarios;
    private final String[] rolesDisponibles = { "ADMINISTRADOR", "USUARIO"};

    @PostConstruct
    public void init() {
        cargarUsuarios();
    }

    public void cargarUsuarios() {
        this.usuarios = apiClient.listarUsuarios();
    }

    public void cambiarRol(Usuario usuario) {
        boolean ok = apiClient.actualizarRolUsuario(usuario.getId(), usuario.getRol());
        FacesContext fc = FacesContext.getCurrentInstance();
        if (ok) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Rol actualizado", "El rol de " + usuario.getNombre() + " fue actualizado."));
        } else {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "No se pudo actualizar", "Revisa la conexion con el Servicio de Estadisticas."));
        }
    }

    public List<Usuario> getUsuarios() { return usuarios; }
    public String[] getRolesDisponibles() { return rolesDisponibles; }
}
