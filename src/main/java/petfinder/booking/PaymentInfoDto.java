package petfinder.booking;

import petfinder.site.common.user.AddressDto;

public class PaymentInfoDto {
    String cc_num;
    AddressDto billing_address;
    int security_code;
    String name;
    
    public PaymentInfoDto() {
        
    }
    
    /**
     * @return the cc_num
     */
    public String getCc_num() {
        return cc_num;
    }
    /**
     * @param cc_num the cc_num to set
     */
    public void setCc_num(String cc_num) {
        this.cc_num = cc_num;
    }
    /**
     * @return the billing_address
     */
    public AddressDto getBilling_address() {
        return billing_address;
    }
    /**
     * @param billing_address the billing_address to set
     */
    public void setBilling_address(AddressDto billing_address) {
        this.billing_address = billing_address;
    }
    /**
     * @return the security_code
     */
    public int getSecurity_code() {
        return security_code;
    }
    /**
     * @param security_code the security_code to set
     */
    public void setSecurity_code(int security_code) {
        this.security_code = security_code;
    }
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
}
