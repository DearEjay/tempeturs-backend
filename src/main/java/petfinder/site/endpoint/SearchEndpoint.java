package petfinder.site.endpoint;


import java.io.IOException;
import java.util.Map;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import petfinder.response.PFResponseBody;
import petfinder.search.PFSearchService;

@RestController
@RequestMapping(value = "/api/search")
public class SearchEndpoint {
    
    //@Autowired
    private PFSearchService searchService = new PFSearchService();

    @RequestMapping(value = "/sitter", method = RequestMethod.GET)
    public ResponseEntity<PFResponseBody<?>> getUserByID(@RequestHeader(value = "Authorization", required = false) String authToken) throws IOException {
        
        PFResponseBody<?> response = searchService.searchSitters(authToken);
        
        //System.out.println(response.getData());
        //return null;
        return ResponseEntity.status(response.getCode().getStatusCode()).body(response);
    }
    
    
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<PFResponseBody<?>> getUserByID(@RequestHeader(value = "Authorization", required = false) String authToken, @RequestParam Map<String,String> requestParams) throws IOException {
        
        PFResponseBody<?> response = searchService.search(authToken, requestParams);
        
        //System.out.println(response.getData());
        return ResponseEntity.status(response.getCode().getStatusCode()).body(response);
    }
   
    
    
}