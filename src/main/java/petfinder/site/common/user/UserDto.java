package petfinder.site.common.user;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import petfinder.auth.PFPermissions;
import petfinder.booking.PaymentInfoDto;
import petfinder.site.common.pet.PetDto;

/**
 * Created by jlutteringer on 8/23/17.
 */
public class UserDto {
    
    enum Classification {
        OWNER(0),
        SITTER(1)
        ;
        
        int type;
        
        Classification(int type) {
            this.type = type;
        }
    }
    
	private String id;
    private String name;
	private String email;
	private Classification classification;
	private String phone;
	private AddressDto address;
	private String image;
	private AvailabilityDto availability;
	private PreferencesDto preferences;
	private PaymentInfoDto payment_info;
	private int rate;
	private PFPermissions permissions;
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date lastRatingAdded;
	
	public UserDto() {
	    
	}

	/**
	 * @param id
	 * @param name
	 * @param email
	 * @param classification
	 */
	public UserDto(String id, String name, String email, Classification classification) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.classification = classification;
	}
	
	/**
     * @param id
     * @param name
     * @param email
     * @param classification
     * @param phone
     * @param address_street
     * @param address_city
     * @param address_state
     * @param address_zip
     */
    public UserDto(String id, String name, String email, Classification classification, String phone,
            AddressDto address, List<PetDto> pets) {
        super();
        this.id = id;
        this.name = name;
        this.email = email;
        this.classification = classification;
        this.phone = phone;
        this.address = address;

    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public void setClassification(Classification classification) {
        this.classification = classification;
    }

    public Classification getClassification() {
        return classification;
    }

    /**
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone the phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return the address
     */
    public AddressDto getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(AddressDto address) {
        this.address = address;
    }

    /**
     * @return the image
     */
    public String getImage() {
        return image;
    }

    /**
     * @param image the image to set
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * @return the availability
     */
    public AvailabilityDto getAvailability() {
        return availability;
    }

    /**
     * @param availability the availability to set
     */
    public void setAvailability(AvailabilityDto availability) {
        this.availability = availability;
    }

    /**
     * @return the preferences
     */
    public PreferencesDto getPreferences() {
        return preferences;
    }

    /**
     * @param preferences the preferences to set
     */
    public void setPreferences(PreferencesDto preferences) {
        this.preferences = preferences;
    }

    /**
     * @return the payment_info
     */
    public PaymentInfoDto getPayment_info() {
        return payment_info;
    }

    /**
     * @param payment_info the payment_info to set
     */
    public void setPayment_info(PaymentInfoDto payment_info) {
        this.payment_info = payment_info;
    }

    /**
     * @return the rate
     */
    public int getRate() {
        return rate;
    }

    /**
     * @param rate the rate to set
     */
    public void setRate(int rate) {
        this.rate = rate;
    }

    /**
     * @return the permissions
     */
    public PFPermissions getPermissions() {
        return permissions;
    }

    /**
     * @param permissions the permissions to set
     */
    public void setPermissions(PFPermissions permissions) {
        this.permissions = permissions;
    }

    /**
     * @return the lastRatingAdded
     */
    public Date getLastRatingAdded() {
        return lastRatingAdded;
    }

    /**
     * @param lastRatingAdded the lastRatingAdded to set
     */
    public void setLastRatingAdded(Date lastRatingAdded) {
        this.lastRatingAdded = lastRatingAdded;
    }

    
}