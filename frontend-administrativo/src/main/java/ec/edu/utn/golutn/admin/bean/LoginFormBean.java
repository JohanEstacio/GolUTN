package ec.edu.utn.golutn.admin.bean;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Mantiene las credenciales solo durante la peticion del formulario.
 */
@Named("loginFormBean")
@RequestScoped
public class LoginFormBean {

    @Inject
    private LoginBean loginBean;

    private String username;
    private String password;

    public String iniciarSesion() {
        try {
            return loginBean.iniciarSesion(username, password);
        } finally {
            password = null;
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
