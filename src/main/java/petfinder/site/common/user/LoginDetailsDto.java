package petfinder.site.common.user;


/**
 * Created by Morgan Wesemann on 10/7/17.
 */
public class LoginDetailsDto {
    

    private String email;
    
    //Should be hashed!
    private String password;
    
    private String userID;
    

    public LoginDetailsDto() {
        
    }

    public LoginDetailsDto(String email, String password) {
      
        this.email = email;
        this.password = password;
        this.userID = null;
    }
    
    public LoginDetailsDto(String email, String password, String userID) {
        
        this.email = email;
        this.password = password;
        this.userID = userID;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
    
}