package petfinder.site.common.user;

public class NotificationDto {
    
    public enum NotificationType {
        BOOKING,
        RATING,
        ALL
    }

    private NotificationType type;
    private String refersToID;
    private String id;
    private String forUserID;
    
    NotificationDto() {
        
    }

    /**
     * @return the type
     */
    public NotificationType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(NotificationType type) {
        this.type = type;
    }

    /**
     * @return the refersToID
     */
    public String getRefersToID() {
        return refersToID;
    }

    /**
     * @param refersToID the refersToID to set
     */
    public void setRefersToID(String refersToID) {
        this.refersToID = refersToID;
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
     * @return the forUserID
     */
    public String getForUserID() {
        return forUserID;
    }

    /**
     * @param forUserID the forUserID to set
     */
    public void setForUserID(String forUserID) {
        this.forUserID = forUserID;
    }
    
    
}
