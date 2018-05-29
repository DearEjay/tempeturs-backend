package petfinder.site.endpoint;

import org.springframework.beans.factory.annotation.Autowired;

import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import petfinder.site.common.user.UserDto;
import petfinder.site.common.user.UserService;
import petfinder.site.common.user.LoginDetailsDto;
import petfinder.site.common.user.UserAndPasswordDto;
import petfinder.auth.*;
import petfinder.response.PFResponseBody;

@RestController
@RequestMapping(value = "/api/user")
public class UserEndpoint {
    
	@Autowired
	private UserService userService;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<PFResponseBody<?>> getUserByID(@RequestHeader(value = "Authorization", required = false) String authToken, @PathVariable(name = "id") String id) throws IOException {
	    
	    PFResponseBody<?> response = userService.getUser(authToken, id);
        
	    System.out.println(response.getData());
	    
	    return ResponseEntity.status(response.getCode().getStatusCode()).body(response);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<PFResponseBody<?>> deleteUserByID(@RequestHeader("Authorization") String authToken, @PathVariable(name = "id") String id) throws UnknownHostException {
        
	    PFResponseBody<?> response = userService.deleteUser(authToken, id);
        return ResponseEntity.status(response.getCode().getStatusCode()).body(response);
    }
	
	@RequestMapping(value = "/", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<PFResponseBody<?>> createUser(@RequestBody UserAndPasswordDto userAndPassword) throws UnknownHostException {
        
	    PFResponseBody<?> response = userService.createUser(userAndPassword);
        return ResponseEntity.status(response.getCode().getStatusCode()).body(response);
	    
    }
	
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public @ResponseBody ResponseEntity<PFResponseBody<?>> updateUserByID(@RequestHeader("Authorization") String authToken,
                                                                          @PathVariable(name = "id") String id,
                                                                          @RequestBody UserDto user) throws UnknownHostException {
       
	    PFResponseBody<UserDto> response = userService.updateUser(authToken, user,id);
        return ResponseEntity.status(response.getCode().getStatusCode()).body(response);
    }
	
	@RequestMapping(value = "/token", method = RequestMethod.GET)
    public @ResponseBody String sampleToken(@RequestHeader("Authorization") String authToken) throws UnknownHostException {
        
	    DecodedJWT decodedToken = Auth.validateToken(authToken);
        
	    if (decodedToken != null) {
	        return decodedToken.getToken();
	    }
	    
        return "bad authorization";
    }
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<PFResponseBody<?>> login(@RequestBody LoginDetailsDto loginDetails) throws UnknownHostException {
        
	    //Attempt to authenticate a user 
	    PFResponseBody<?> p = userService.authenticateUser(loginDetails,24,TimeUnit.HOURS);
	    return ResponseEntity.status(p.getCode().getStatusCode()).body(p);

    }
	
	
}