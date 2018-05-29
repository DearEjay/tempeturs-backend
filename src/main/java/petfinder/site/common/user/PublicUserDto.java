package petfinder.site.common.user;

import petfinder.site.common.user.UserDto.Classification;

public class PublicUserDto {
   
    private String id;
    private String name;
    private Classification classification;
    private String image;
    private int rate;
    
    /**
     * @param id
     * @param name
     * @param classification
     * @param image
     * @param rate
     * @param permissions
     * @param lastRatingAdded
     */
    public PublicUserDto(String id, String name, Classification classification, String image, int rate) {
        super();
        this.id = id;
        this.name = name;
        this.classification = classification;
        this.image = image;
        this.rate = rate;
    }
    
    public PublicUserDto() {
        
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