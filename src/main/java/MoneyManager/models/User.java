package MoneyManager.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Entity
public class User {

    @Id
    @GeneratedValue
    private int id;

    @NotNull
    @Size(min=3, max=20, message = "Username must be 3-20 characters")
    private String userName;

    @NotNull
    @Size(min=3, max=20, message = "Password must be 5-15 characters")
    private String password;

    @Transient
    private String verifyPassword;

    public User() {}

    public int getId() {
        return id;
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

    public String getVerifyPassword() {
        return verifyPassword;
    }

    public void setVerifyPassword(String verifyPassword) {
        this.verifyPassword = verifyPassword;

    }

//    private void checkPassword(){
//        if(this.password != null && this.verifyPassword != null && !this.password.equals(this.verifyPassword)){
//            this.verifyPassword = null;
//        }
//    }
}
