package petfinder.site.common.user;

public class AddressDto {
    String street_line_1;
    String street_line_2;
    String city;
    String state;
    String zipcode;
    
    public AddressDto() {
        
    }
    
    /**
     * @param street_line_1
     * @param street_line_2
     * @param city
     * @param state
     * @param zip
     */
    public AddressDto(String street_line_1, String street_line_2, String city, String state, String zip) {
        super();
        this.street_line_1 = street_line_1;
        this.street_line_2 = street_line_2;
        this.city = city;
        this.state = state;
        this.zipcode = zip;
    }
    /**
     * @return the street_line_1
     */
    public String getStreet_line_1() {
        return street_line_1;
    }
    /**
     * @param street_line_1 the street_line_1 to set
     */
    public void setStreet_line_1(String street_line_1) {
        this.street_line_1 = street_line_1;
    }
    /**
     * @return the street_line_2
     */
    public String getStreet_line_2() {
        return street_line_2;
    }
    /**
     * @param street_line_2 the street_line_2 to set
     */
    public void setStreet_line_2(String street_line_2) {
        this.street_line_2 = street_line_2;
    }
    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }
    /**
     * @param city the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }
    /**
     * @return the state
     */
    public String getState() {
        return state;
    }
    /**
     * @param state the state to set
     */
    public void setState(String state) {
        this.state = state;
    }
    /**
     * @return the zipcode
     */
    public String getZipcode() {
        return zipcode;
    }
    /**
     * @param zip the zip to set
     */
    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }
}
