package cz.cvut.fel.tk21.model.security;

public class AuthenticationRequest {

    private String username;
    private String password;
    private boolean signOut;

    public AuthenticationRequest() {
    }

    public AuthenticationRequest(String username, String password, boolean signOut) {
        this.username = username;
        this.password = password;
        this.signOut = signOut;
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

    public boolean isSignOut() {
        return signOut;
    }

    public void setSignOut(boolean signOut) {
        this.signOut = signOut;
    }
}
