package og.prj.adminservice.customer;

public class CustomerSignUp {

    private String userName;
    private String password;

    public CustomerSignUp() {
    }

    public CustomerSignUp(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
