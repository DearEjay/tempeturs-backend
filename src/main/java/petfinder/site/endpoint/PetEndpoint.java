package petfinder.site.endpoint;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import petfinder.response.PFResponseBody;
import petfinder.site.common.pet.PetDto;
import petfinder.site.common.pet.PetService;

@RestController
@RequestMapping(value = "/api/user/{user_id}/pets")
public class PetEndpoint {
    
    @Autowired
    private PetService petService;
    
    
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<PFResponseBody<?>> getPetsForUser(@RequestHeader("Authorization") String authToken, @PathVariable(name = "user_id") String userID) throws IOException {
        
        PFResponseBody<List<PetDto>> response = petService.getPetsForUser(authToken, userID);
        
        
        return ResponseEntity.status(response.getCode().getStatusCode()).body(response);
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<PFResponseBody<?>> getPetByID(@RequestHeader("Authorization") String authToken, @PathVariable(name = "user_id") String userID, @PathVariable(name = "id") String id) throws IOException {
        
        PFResponseBody<PetDto> response = petService.getPet(authToken, userID, id);
        
        
        return ResponseEntity.status(response.getCode().getStatusCode()).body(response);
    }
    
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<PFResponseBody<?>> createPetForUser(@RequestHeader("Authorization") String authToken, 
                                                              @PathVariable(name = "user_id") String userID, 
                                                              @RequestBody PetDto pet) throws IOException {
        
        PFResponseBody<String> response = petService.createPet(authToken, userID, pet);
        
   
        return ResponseEntity.status(response.getCode().getStatusCode()).body(response);
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public @ResponseBody ResponseEntity<PFResponseBody<?>> updateUserByID(@RequestHeader("Authorization") String authToken,
                                                                          @PathVariable(name = "id") String id,
                                                                          @PathVariable(name = "user_id") String userID,
                                                                          @RequestBody PetDto pet) throws UnknownHostException {
       
        PFResponseBody<PetDto> response = petService.updatePet(authToken, pet,id, userID);
        return ResponseEntity.status(response.getCode().getStatusCode()).body(response);
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<PFResponseBody<?>> deletePetByID(@RequestHeader("Authorization") String authToken, @PathVariable(name = "id") String id) throws UnknownHostException {
        
        PFResponseBody<?> response = petService.deletePet(authToken, id);
        return ResponseEntity.status(response.getCode().getStatusCode()).body(response);
    }
    
}
