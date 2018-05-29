package petfinder.booking;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;

import petfinder.auth.Auth;
import petfinder.booking.BookingDto.BookingStatus;
import petfinder.elasticclient.ElasticClient;
import petfinder.response.PFResponseBody;
import petfinder.response.ResponseUtils.PFStatusCode;
import petfinder.site.common.user.UserDto;
import petfinder.site.common.user.UserService;
import petfinder.site.common.user.NotificationDto.NotificationType;

@Service
public class BookingService {
    
    @Autowired
    private ObjectMapper mapper = new ObjectMapper();
    
    private UserService userService = new UserService();
    
    public PFResponseBody<List<BookingDto>> getBookingsForUser(String authToken, String userID) {
        
        DecodedJWT decodedUser = Auth.validateToken(authToken);
        
        if (decodedUser == null) {
            return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_AUTHORIZED);
        }
        
        try {
            SearchResponse response = ElasticClient.getInstance()
                    .search("petfinder2", "booking", "ownerID", userID);
            
            SearchHits ownerHits = response.getHits();
            
            
                
           response = ElasticClient.getInstance()
                        .search("petfinder2", "booking", "sitterID", userID);
                
           SearchHits sitterHits = response.getHits();  
           
            SearchHit[] hitArray = ownerHits.getHits();

            SearchHit[] sitterHitArray = sitterHits.getHits();
            
            List<BookingDto> bookings = new ArrayList<BookingDto>();
            
            for (SearchHit hit : hitArray) {
                
                BookingDto booking = mapper.readValue(hit.getSourceAsString(), BookingDto.class);
                
                bookings.add(booking);
            }
            
            for (SearchHit hit : sitterHitArray) {
                
                BookingDto booking = mapper.readValue(hit.getSourceAsString(), BookingDto.class);
                
                bookings.add(booking);
            }
            
            return new PFResponseBody<List<BookingDto>>(bookings,PFStatusCode.SUCCESS_OK);
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new PFResponseBody<>(null,PFStatusCode.ERROR_SERVER);
        }
        
    }
    

    public PFResponseBody<BookingDto> getBooking(String authToken, String userID, String id) {
        
        DecodedJWT decodedUser = Auth.validateToken(authToken);
        
        if (decodedUser == null) {
            return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_AUTHORIZED);
        }
        
        try {
            
            GetResponse resp =  ElasticClient.getInstance().get("petfinder2","booking",id);
            
            if (!resp.isExists()) {
                return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_FOUND);
            }
                       
            BookingDto booking = mapper.readValue(resp.getSourceAsString(), BookingDto.class);
            
        
            
            if (!booking.getOwnerID().equals(userID) && !booking.getSitterID().equals(userID)) {
                return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_AUTHORIZED);
            }
            
            return new PFResponseBody<>(booking,PFStatusCode.SUCCESS_OK);
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new PFResponseBody<>(null,PFStatusCode.ERROR_SERVER);
        }

    }
    
    public PFResponseBody<String> createBooking(String authToken, String userID, BookingDto booking) {
        
        DecodedJWT decodedUser = Auth.validateToken(authToken);
        
        if (decodedUser == null) {
            return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_AUTHORIZED);
        }
        
        try { 
            
            if (!booking.getOwnerID().equals(userID)) {
                System.out.println("Trying to create a booking for not you");
                return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_AUTHORIZED);
            }
            
            if (booking.getOwnerID().equals(booking.getSitterID())) {
                System.out.println("Trying to create a booking where you are both the owner and sitter");
                return new PFResponseBody<>(null,PFStatusCode.ERROR_BAD_REQUEST);
            }
            
            if (!(booking.getStatus() == null)) {
                System.out.println("Leave booking status blank");
                return new PFResponseBody<>(null,PFStatusCode.ERROR_BAD_REQUEST);
            }
            
            SearchResponse ownerResponse = ElasticClient.getInstance()
                    .search("petfinder2", "user", "id", booking.getOwnerID());
            
            SearchHits ownerHits = ownerResponse.getHits();
            
            SearchResponse sitterResponse = ElasticClient.getInstance()
                    .search("petfinder2", "user", "id", booking.getSitterID());
            
            SearchHits  sitterHits = sitterResponse.getHits();
            
            if (ownerHits.totalHits == 0 || sitterHits.totalHits == 0) {
                System.out.println("Cannot find either user in DB");
                return new PFResponseBody<String>(null,PFStatusCode.ERROR_NOT_FOUND);
            }
           
            String bookingID = UUID.randomUUID().toString();
            
            booking.setId(bookingID);
            
            booking.setStatus(BookingStatus.REQUESTED);
            
            UserDto sitter = userService.getUser(booking.getSitterID());
            
            if (sitter == null) {
                return new PFResponseBody<String>(null,PFStatusCode.ERROR_NOT_FOUND);
            }
            
            List<Date> unavailableDays = sitter.getAvailability().getUnavailableDays();
            
            for (Date date : unavailableDays) {
                if (!(date.before(booking.getStartDate()) || date.after(booking.getEndDate()))) {
                    return new PFResponseBody<String>(null,PFStatusCode.ERROR_CONFLICT);
                }
            }
                        
            
             
            String bookingJSON = mapper.writeValueAsString(booking);
            
            IndexResponse bookingResponse = ElasticClient.getInstance()
                    .create("petfinder2", "booking", bookingID, bookingJSON);

            if (bookingResponse.getResult() != DocWriteResponse.Result.CREATED) {
                System.out.println("Cannot create record in DB");

                return new PFResponseBody<String>(null,PFStatusCode.ERROR_SERVER);
            }
            
            if (!userService.addNotification(booking.getSitterID(), NotificationType.BOOKING, bookingID)) {
                return new PFResponseBody<String>(null,PFStatusCode.ERROR_SERVER);
            }
        
            return new PFResponseBody<String>(bookingID,PFStatusCode.SUCCESS_CREATED);
                        
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new PFResponseBody<>(null,PFStatusCode.ERROR_SERVER);
        }

    }
    
    public PFResponseBody<BookingDto> updateBooking(String authToken, BookingDto booking, String id, String userID) {
        
        DecodedJWT decodedUser = Auth.validateToken(authToken);
        
        if (decodedUser == null) {
            return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_AUTHORIZED);
        }
        
        try { 

            if (!id.equals(booking.getId())) {
                System.out.println("cannot change ID");
                return new PFResponseBody<>(null,PFStatusCode.NOT_MODIFIED);
            }
            
            
            
            GetResponse resp =  ElasticClient.getInstance().get("petfinder2","booking",id);
            
            if (!resp.isExists()) {
                return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_FOUND);
            }
            
            BookingDto bookingFromDB = mapper.readValue(resp.getSourceAsString(), BookingDto.class);
            
            if (!bookingFromDB.getOwnerID().equals(userID) && !bookingFromDB.getSitterID().equals(userID)) {
                return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_AUTHORIZED);
            }
            
            String bookingJSON = mapper.writeValueAsString(booking);

            UpdateResponse petResponse = ElasticClient.getInstance()
                    .update("petfinder2", "booking", id, bookingJSON);

            if (petResponse.getResult() != DocWriteResponse.Result.UPDATED) {
                System.out.println("failed to update");
                return new PFResponseBody<BookingDto>(null,PFStatusCode.NOT_MODIFIED);
            }
            
            if (booking.getStatus().equals(BookingStatus.ACCEPTED)) {
                
                UserDto sitter = userService.getUser(booking.getSitterID());
                
                if (sitter == null) {
                    return new PFResponseBody<BookingDto>(null,PFStatusCode.ERROR_NOT_FOUND);
                }
                
                List<Date> unavailableDays = sitter.getAvailability().getUnavailableDays();
                
                GregorianCalendar gcal = new GregorianCalendar();
                gcal.setTime(booking.getStartDate());
                
                while (!gcal.getTime().after(booking.getEndDate())) {
                    Date d = gcal.getTime();
                    unavailableDays.add(d);
                    gcal.add(Calendar.DAY_OF_MONTH, 1);
                }
               
                sitter.getAvailability().setUnavailableDays(unavailableDays);
                
                userService.updateUserInternal(sitter, sitter.getId());
                
                if (!userService.addNotification(booking.getOwnerID(), NotificationType.BOOKING, booking.getId())) {
                    return new PFResponseBody<BookingDto>(null,PFStatusCode.ERROR_SERVER);
                }
            }

            return new PFResponseBody<BookingDto>(booking,PFStatusCode.SUCCESS_MODIFIED);
                        
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new PFResponseBody<>(null,PFStatusCode.ERROR_SERVER);
        }
    }
    
    public PFResponseBody<?> deleteBooking(String authToken, String id) {
        
        DecodedJWT decodedUser = Auth.validateToken(authToken);
        
        if (decodedUser == null) {
            return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_AUTHORIZED);
        }
        
        try { 
            
            DeleteResponse resp = ElasticClient.getInstance().delete("petfinder2", "booking", id);
            
            if (resp.getResult() == DocWriteResponse.Result.NOT_FOUND) {
                return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_FOUND);
            }
            
            ReplicationResponse.ShardInfo shardInfo = resp.getShardInfo();
            if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
                return new PFResponseBody<BookingDto>(null,PFStatusCode.ERROR_SERVER);
            }
       
            return new PFResponseBody<BookingDto>(null,PFStatusCode.SUCCESS_DELETED);
                        
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new PFResponseBody<>(null,PFStatusCode.ERROR_SERVER);
        }
    }
    

}
