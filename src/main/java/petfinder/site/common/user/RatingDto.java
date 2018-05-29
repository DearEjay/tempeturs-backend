package petfinder.site.common.user;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class RatingDto {

    private int stars;
    private String comments;
    private String toUserID;
    private String fromUserID;
    private String id;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date dateAdded;
   
    public RatingDto() {
        
    }
     
    /**
     * @return the stars
     */
    public int getStars() {
        return stars;
    }

    /**
     * @param stars the stars to set
     */
    public void setStars(int stars) {
        if (stars > 5) {
            this.stars = 5;
        }
        
        if (stars < 0) {
            this.stars = 0;
        }
        
        this.stars = stars;
    }

    /**
     * @return the comments
     */
    public String getComments() {
        return comments;
    }

    /**
     * @param comments the comments to set
     */
    public void setComments(String comments) {
        this.comments = comments;
    }



    /**
     * @return the toUserID
     */
    public String getToUserID() {
        return toUserID;
    }



    /**
     * @param toUserID the toUserID to set
     */
    public void setToUserID(String toUserID) {
        this.toUserID = toUserID;
    }



    /**
     * @return the fromUserID
     */
    public String getFromUserID() {
        return fromUserID;
    }



    /**
     * @param fromUserID the fromUserID to set
     */
    public void setFromUserID(String fromUserID) {
        this.fromUserID = fromUserID;
    }



    /**
     * @return the id
     */
    public String getId() {
        return id;
    }



    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the dateAdded
     */
    public Date getDateAdded() {
        return dateAdded;
    }

    /**
     * @param dateAdded the dateAdded to set
     */
    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

   
    
    
}
