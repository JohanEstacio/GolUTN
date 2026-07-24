package ec.edu.utn.golutn.admin.dto;

import java.io.Serializable;

/** Cuerpo de POST /api/Auth/login. */
public class LoginRequestDto implements Serializable {

    private String username;
    private String password;

    public LoginRequestDto() {
    }

    public LoginRequestDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
