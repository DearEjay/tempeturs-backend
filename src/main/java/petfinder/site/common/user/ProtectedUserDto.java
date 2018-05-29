package petfinder.site.common.user;

import petfinder.site.common.user.UserDto.Classification;

public class ProtectedUserDto {
    
    private String id;
    private String name;
    private Classification classification;
    private String image;
    private AvailabilityDto availability;
    private PreferencesDto preferences;
    private int rate;
    
    public ProtectedUserDto() {
        
    }


    /**
     * @param id
     * @param name
     * @param classification
     * @param image
     * @param availability
     * @param preferences
     * @param rate
     */
    public ProtectedUserDto(String id, String name, Classification classification, String image,
            AvailabilityDto availability, PreferencesDto preferences, int rate) {
        super();
        this.id = id;
        this.name = name;
        this.classification = classification;
        this.image = image;
        this.availability = availability;
        this.preferences = preferences;
        this.rate = rate;
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
    
    public void setClassification(Classification classification) {
        this.classification = classification;
    }

    public Classification getClassification() {
        return classification;
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

    
}