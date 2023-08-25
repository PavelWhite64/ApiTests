package success;

public class SuccessLogin {
    private String token;

    public SuccessLogin() {
    }

    public SuccessLogin(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
