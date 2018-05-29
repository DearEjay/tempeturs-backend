package petfinder.site.endpoint;

import java.io.IOException;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import petfinder.elasticclient.ElasticClient;
import petfinder.response.PFResponseBody;

@RestController
@RequestMapping(value = "/api")
public class UtilsEndpoint {

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<PFResponseBody<?>> delete() throws IOException {
       
        /*if (authToken.equals("withgreatpowercomesgreatdeletions")) {
            System.out.println("not correct suth");
            return null;
        }*/
        
        System.out.println("nuke");
        
        SearchResponse response = ElasticClient.getInstance()
                .searchWildcard("petfinder2", "user", "id", "*");
        
        
        
        
        SearchHits hits = response.getHits();
        
        SearchHit[] hitArray = hits.getHits();
                
        for (SearchHit hit : hitArray) {
            

            ElasticClient.getInstance()
            .delete("petfinder2", "user", hit.getId());
        }
        
        response = ElasticClient.getInstance()
                .searchWildcard("petfinder2", "user_auth", "id", "*");
        
        hits = response.getHits();
        
        hitArray = hits.getHits();
                
        for (SearchHit hit : hitArray) {

            ElasticClient.getInstance()
            .delete("petfinder2", "user_auth", hit.getId());
        }
        
        response = ElasticClient.getInstance()
                .searchWildcard("petfinder2", "pet", "id", "*");
        
        hits = response.getHits();
        
        hitArray = hits.getHits();
                
        for (SearchHit hit : hitArray) {

            ElasticClient.getInstance()
            .delete("petfinder2", "pet", hit.getId());
        }
        
        response = ElasticClient.getInstance()
                .searchWildcard("petfinder2", "rating", "id", "*");
        
        hits = response.getHits();
        
        hitArray = hits.getHits();
                
        for (SearchHit hit : hitArray) {

            ElasticClient.getInstance()
            .delete("petfinder2", "rating", hit.getId());
        }
        
        response = ElasticClient.getInstance()
                .searchWildcard("petfinder2", "notifications", "id", "*");
        
        hits = response.getHits();
        
        hitArray = hits.getHits();
                
        for (SearchHit hit : hitArray) {

            DeleteResponse d = ElasticClient.getInstance()
            .delete("petfinder2", "notifications", hit.getId());
            
            if (d.getShardInfo().getFailed() > 0) {
                System.out.println("something failed");
            }
        }
        
        return null;
    }
    
}
