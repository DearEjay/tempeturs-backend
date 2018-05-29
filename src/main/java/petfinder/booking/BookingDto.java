package petfinder.booking;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import petfinder.site.common.user.AddressDto;

public class BookingDto {
    
    enum BookingStatus {
        REQUESTED,
        ACCEPTED,
        CHECKED_IN,
        COMPLETED,
        CANCELED
    }
    
    private String id;
    private BookingStatus status;
    private String sitterID;
    private String ownerID;
    private List<String> pet_ids;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date startDate;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date endDate;
    private boolean pickup;
    private AddressDto address;
    private String comments;
    private PaymentInfoDto paymentInfo;

    public BookingDto() {
        
    }
    
    /**
     * @param id
     * @param status
     * @param sitterID
     * @param ownerID
     * @param pet_ids
     * @param startDate
     * @param endDate
     * @param pickup
     * @param address
     * @param comments
     * @param paymentInfo
     */
    public BookingDto(String id, BookingStatus status, String sitterID, String ownerID, List<String> pet_ids,
            Date startDate, Date endDate, boolean pickup, AddressDto address, String comments,
            PaymentInfoDto paymentInfo) {
        super();
        this.id = id;
        this.status = status;
        this.sitterID = sitterID;
        this.ownerID = ownerID;
        this.pet_ids = pet_ids;
        this.startDate = startDate;
        this.endDate = endDate;
        this.pickup = pickup;
        this.address = address;
        this.comments = comments;
        this.paymentInfo = paymentInfo;
    }
    
    /**
     * @return the transaction_id
     */
    public String getId() {
        return id;
    }

    /**
     * @param transaction_id the transaction_id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the status
     */
    public BookingStatus getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    /**
     * @return the pet_ids
     */
    public List<String> getPet_ids() {
        return pet_ids;
    }

    /**
     * @param pet_ids the pet_ids to set
     */
    public void setPet_ids(List<String> pet_ids) {
        this.pet_ids = pet_ids;
    }

    /**
     * @return the startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * @return the pickup
     */
    public boolean isPickup() {
        return pickup;
    }

    /**
     * @param pickup the pickup to set
     */
    public void setPickup(boolean pickup) {
        this.pickup = pickup;
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
     * @return the paymentInfo
     */
    public PaymentInfoDto getPaymentInfo() {
        return paymentInfo;
    }

    /**
     * @param paymentInfo the paymentInfo to set
     */
    public void setPaymentInfo(PaymentInfoDto paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    /**
     * @return the sitterID
     */
    public String getSitterID() {
        return sitterID;
    }

    /**
     * @param sitterID the sitterID to set
     */
    public void setSitterID(String sitterID) {
        this.sitterID = sitterID;
    }

    /**
     * @return the ownerID
     */
    public String getOwnerID() {
        return ownerID;
    }

    /**
     * @param ownerID the ownerID to set
     */
    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }
    
}
