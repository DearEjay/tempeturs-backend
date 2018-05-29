package petfinder.site.common.pet;

import java.io.IOException;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;

import petfinder.auth.Auth;
import petfinder.elasticclient.ElasticClient;
import petfinder.response.PFResponseBody;
import petfinder.response.ResponseUtils.PFStatusCode;
import petfinder.site.common.user.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
public class PetService {
    
    @Autowired
    private ObjectMapper mapper = new ObjectMapper();
    
    public PFResponseBody<List<PetDto>> getPetsForUser(String authToken, String userID) {
        
        try {
            
            DecodedJWT decodedUser = Auth.validateToken(authToken);
            
            if (decodedUser == null) {
                return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_AUTHORIZED);
            }
            
            SearchResponse response = ElasticClient.getInstance()
                    .search("petfinder2", "pet", "userID", userID);
            
            SearchHits hits = response.getHits();
            
            SearchHit[] hitArray = hits.getHits();
            
            List<PetDto> pets = new ArrayList<PetDto>();
            
            for (SearchHit hit : hitArray) {
                
                PetDto pet = mapper.readValue(hit.getSourceAsString(), PetDto.class);
                
                pets.add(pet);
            }
            
            return new PFResponseBody<List<PetDto>>(pets,PFStatusCode.SUCCESS_OK);
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new PFResponseBody<>(null,PFStatusCode.ERROR_SERVER);
        }
        
    }
    

    public PFResponseBody<PetDto> getPet(String authToken, String userID, String id) {
        
        try {
            
            DecodedJWT decodedUser = Auth.validateToken(authToken);
            
            if (decodedUser == null) {
                return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_AUTHORIZED);
            }
            
            GetResponse resp =  ElasticClient.getInstance().get("petfinder2","pet",id);
            
            if (!resp.isExists()) {
                return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_FOUND);
            }
                       
            PetDto pet = mapper.readValue(resp.getSourceAsString(), PetDto.class);
            
            System.out.println(pet.getUserID());
            
            if (!pet.getUserID().equals(userID)) {
                return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_AUTHORIZED);
            }
            
            return new PFResponseBody<>(pet,PFStatusCode.SUCCESS_OK);
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new PFResponseBody<>(null,PFStatusCode.ERROR_SERVER);
        }

    }
    
    public PFResponseBody<String> createPet(String authToken, String userID, PetDto pet) {
        
        try { 
            
            DecodedJWT decodedUser = Auth.validateToken(authToken);
            
            if (decodedUser == null) {
                return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_AUTHORIZED);
            }
            
            SearchResponse response = ElasticClient.getInstance()
                    .search("petfinder2", "user", "id", userID);
            
            SearchHits hits = response.getHits();
            
            if (hits.totalHits == 0) {
                return new PFResponseBody<String>(null,PFStatusCode.ERROR_NOT_FOUND);
            }
           
            
            String petID = UUID.randomUUID().toString();
            
            pet.setId(petID);
            
            pet.setUserID(userID);
            
            String petJSON = mapper.writeValueAsString(pet);

            
            IndexResponse petResponse = ElasticClient.getInstance()
                    .create("petfinder2", "pet", petID, petJSON);

            if (petResponse.getResult() != DocWriteResponse.Result.CREATED) {
                return new PFResponseBody<String>(null,PFStatusCode.ERROR_SERVER);
            }
        
            return new PFResponseBody<String>(petID,PFStatusCode.SUCCESS_CREATED);
                        
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new PFResponseBody<>(null,PFStatusCode.ERROR_SERVER);
        }

    }
    
    public PFResponseBody<PetDto> updatePet(String authToken, PetDto pet, String id, String userID) {
        
        try { 
            
            DecodedJWT decodedUser = Auth.validateToken(authToken);
            
            if (decodedUser == null) {
                return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_AUTHORIZED);
            }

            if (!id.equals(pet.getId())) {
                System.out.println("cannot change ID");
                return new PFResponseBody<>(null,PFStatusCode.NOT_MODIFIED);
            }
            
            if (!userID.equals(pet.getUserID())) {
                System.out.println("cannot change userID");
                return new PFResponseBody<>(null,PFStatusCode.NOT_MODIFIED);
            }
            
            GetResponse resp =  ElasticClient.getInstance().get("petfinder2","pet",id);
            
            if (!resp.isExists()) {
                return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_FOUND);
            }
            
            PetDto petFromDB = mapper.readValue(resp.getSourceAsString(), PetDto.class);
            
            pet.setUserID(userID);
            
            if (!petFromDB.getUserID().equals(userID)) {
                return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_AUTHORIZED);
            }
            
            String userJson = mapper.writeValueAsString(pet);

            UpdateResponse petResponse = ElasticClient.getInstance()
                    .update("petfinder2", "pet", id, userJson);

            if (petResponse.getResult() != DocWriteResponse.Result.UPDATED) {
                System.out.println("failed to update");
                return new PFResponseBody<PetDto>(null,PFStatusCode.NOT_MODIFIED);
            }

            return new PFResponseBody<PetDto>(pet,PFStatusCode.SUCCESS_MODIFIED);
                        
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new PFResponseBody<>(null,PFStatusCode.ERROR_SERVER);
        }
    }
    
    public PFResponseBody<?> deletePet(String authToken, String id) {
        
        try { 
            
            DecodedJWT decodedUser = Auth.validateToken(authToken);
            
            if (decodedUser == null) {
                return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_AUTHORIZED);
            }
            
            DeleteResponse resp = ElasticClient.getInstance().delete("petfinder2", "pet", id);
            
            if (resp.getResult() == DocWriteResponse.Result.NOT_FOUND) {
                return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_FOUND);
            }
            
            ReplicationResponse.ShardInfo shardInfo = resp.getShardInfo();
            if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
                return new PFResponseBody<UserDto>(null,PFStatusCode.ERROR_SERVER);
            }
       
            return new PFResponseBody<UserDto>(null,PFStatusCode.SUCCESS_DELETED);
                        
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new PFResponseBody<>(null,PFStatusCode.ERROR_SERVER);
        }
    }

}