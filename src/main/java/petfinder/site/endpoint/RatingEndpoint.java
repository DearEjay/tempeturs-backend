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

import petfinder.response.PFResponseBody;
import petfinder.site.common.user.RatingDto;
import petfinder.site.common.user.UserService;

@RestController
@RequestMapping(value = "/api/user/{user_id}/ratings")
public class RatingEndpoint {

    private UserService userService = new UserService();

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<PFResponseBody<?>> getRatingForUser(@RequestHeader("Authorization") String authToken, @PathVariable(name = "user_id") String userID, @PathVariable(name = "id") String ratingID) throws IOException {
        
        PFResponseBody<RatingDto> response = userService.getRatingForUser(authToken, userID, ratingID);
                
        return ResponseEntity.status(response.getCode().getStatusCode()).body(response);
        
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<PFResponseBody<?>> removeRatingForUser(@RequestHeader("Authorization") String authToken, @PathVariable(name = "user_id") String userID, @PathVariable(name = "id") String ratingID) throws UnknownHostException {
        
        PFResponseBody<?> response = userService.removeRatingForUser(authToken, userID, ratingID);
        
        return ResponseEntity.status(response.getCode().getStatusCode()).body(response);
    }
    
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<PFResponseBody<?>> addRatingForUser(@RequestHeader("Authorization") String authToken, @PathVariable(name = "user_id") String userID, @RequestBody RatingDto rating) throws UnknownHostException {
        
        PFResponseBody<?> response = userService.addRatingForUser(authToken, userID, rating);
        return ResponseEntity.status(response.getCode().getStatusCode()).body(response);
        
    }
    
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<PFResponseBody<?>> getRatingsForUser(@RequestHeader("Authorization") String authToken, @PathVariable(name = "user_id") String userID) throws IOException {
        
        PFResponseBody<List<RatingDto>> response = userService.getRatingsForUser(authToken, userID);
        
        
        return ResponseEntity.status(response.getCode().getStatusCode()).body(response);
    }
}
