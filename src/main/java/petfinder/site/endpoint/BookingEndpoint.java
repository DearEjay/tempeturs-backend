package petfinder.site.endpoint;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import petfinder.booking.BookingDto;
import petfinder.booking.BookingService;
import petfinder.response.PFResponseBody;


@RestController
@RequestMapping(value = "/api/user/{user_id}/bookings")
public class BookingEndpoint {
    
    private BookingService bookingService = new BookingService();
    
    
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<PFResponseBody<?>> getPetsForUser(@RequestHeader("Authorization") String authToken, @PathVariable(name = "user_id") String userID) throws IOException {
        
        PFResponseBody<List<BookingDto>> response = bookingService.getBookingsForUser(authToken, userID);
        
        
        return ResponseEntity.status(response.getCode().getStatusCode()).body(response);
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<PFResponseBody<?>> getPetByID(@RequestHeader("Authorization") String authToken, @PathVariable(name = "user_id") String userID, @PathVariable(name = "id") String id) throws IOException {
        
        PFResponseBody<BookingDto> response = bookingService.getBooking(authToken, userID, id);
        
        
        return ResponseEntity.status(response.getCode().getStatusCode()).body(response);
    }
    
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<PFResponseBody<?>> createPetForUser(@RequestHeader("Authorization") String authToken, 
                                                              @PathVariable(name = "user_id") String userID, 
                                                              @RequestBody BookingDto booking) throws IOException {
        
        PFResponseBody<String> response = bookingService.createBooking(authToken, userID, booking);
        
   
        return ResponseEntity.status(response.getCode().getStatusCode()).body(response);
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public @ResponseBody ResponseEntity<PFResponseBody<?>> updateUserByID(@RequestHeader("Authorization") String authToken,
                                                                          @PathVariable(name = "id") String id,
                                                                          @PathVariable(name = "user_id") String userID,
                                                                          @RequestBody BookingDto booking) throws UnknownHostException {
       
        PFResponseBody<BookingDto> response = bookingService.updateBooking(authToken, booking, id, userID);
        return ResponseEntity.status(response.getCode().getStatusCode()).body(response);
    }
}
