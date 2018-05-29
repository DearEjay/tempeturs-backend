package petfinder.site.common.user;

public class UserAndPasswordDto {

    UserDto user;
    String password;
    
    public UserAndPasswordDto() {
        this.user = null;
        this.password = null;     
    }
    
    /**
     * @param user
     * @param password
     */
    public UserAndPasswordDto(UserDto user, String password) {
        super();
        this.user = user;
        this.password = password;
    }
    
    /**
     * @return the user
     */
    public UserDto getUser() {
        return user;
    }
    
    /**
     * @param user the user to set
     */
    public void setUser(UserDto user) {
        this.user = user;
    }
    
    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    
    
}
