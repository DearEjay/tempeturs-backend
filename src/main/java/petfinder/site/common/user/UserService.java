package petfinder.site.common.user;

import java.io.IOException;

import java.util.concurrent.TimeUnit;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;

import petfinder.auth.Auth;
import petfinder.auth.PFPermissions;
import petfinder.elasticclient.ElasticClient;
import petfinder.response.PFResponseBody;
import petfinder.response.ResponseUtils.PFStatusCode;
import petfinder.site.common.user.NotificationDto.NotificationType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by jlutteringer on 8/23/17.
 */
@Service
public class UserService {
    
	@Autowired
    private ObjectMapper mapper = new ObjectMapper();
	
	public DecodedJWT authenticateUser(String authToken) {
	    
	    return Auth.validateToken(authToken);    
       
	}
	
	public UserDto getUser(String id) {
        
        try {
           
            
            GetResponse resp =  ElasticClient.getInstance().get("petfinder2","user",id);
            if (!resp.isExists()) {
                System.out.println("no user found");
                return null;
            }
                       
            UserDto user = mapper.readValue(resp.getSourceAsString(), UserDto.class);
            
            return user;
            
        } catch (IOException e) {
           
            return null;
        }

    }

	public PFResponseBody<?> getUser(String authToken, String id) {
            
            PFPermissions permissionsLevel = PFPermissions.PUBLIC;
               
            String userID = id;
            
            UserDto user = getUser(id);
            
            if (user == null) {
                             
                return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_FOUND);      
            }
            
            DecodedJWT decodedUser = Auth.validateToken(authToken);
                            
            permissionsLevel = user.getPermissions();
            
            //If user is logged in, get permissions level + userID
            if (decodedUser != null) {
                userID = decodedUser.getClaim("user").asString();

                if (userID.equals(id)) {
                    permissionsLevel = PFPermissions.PRIVATE;
                }
                //return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_AUTHORIZED);
            }
            
            //Sanitize user
            switch(permissionsLevel) {
            case GOD:
                //Get all contents
                return new PFResponseBody<>(user,PFStatusCode.SUCCESS_OK);
            case PRIVATE:
                //Get all contents
                return new PFResponseBody<>(user,PFStatusCode.SUCCESS_OK);
            case PROTECTED:
                //Sanitize private information
                ProtectedUserDto protectedUser = new ProtectedUserDto(user.getId(), user.getName(), user.getClassification(), user.getImage(),
                         user.getAvailability(), user.getPreferences(), user.getRate());

                       
                return new PFResponseBody<>(protectedUser,PFStatusCode.SUCCESS_OK);
            case PUBLIC:
                //Only show basic info
                PublicUserDto publicUser = new PublicUserDto(user.getId(), user.getName(), user.getClassification(), user.getImage(), user.getRate());
                return new PFResponseBody<>(publicUser,PFStatusCode.SUCCESS_OK);
            default:
                return new PFResponseBody<>(null,PFStatusCode.ERROR_SERVER);
            }  

	}
	
    public PFResponseBody<?> createUser(UserAndPasswordDto userAndPassword) {
	    
	    try { 
	        
	        UserDto user = userAndPassword.getUser();
	        String password = userAndPassword.getPassword();
	        
	        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
	        
	        userAndPassword.setPassword(hashed);
	        
	        SearchResponse response = ElasticClient.getInstance()
                    .search("petfinder2", "user_auth", "email", user.getEmail());
	        
	        SearchHits hits = response.getHits();
	        
	        if (hits.totalHits != 0) {
	            return new PFResponseBody<String>(null,PFStatusCode.ERROR_CONFLICT);
	        }
	        
	        String userID = UUID.randomUUID().toString();
	        
	        user.setId(userID);
	        
	        user.setAvailability(new AvailabilityDto());
	        
	        String userJson = mapper.writeValueAsString(user);
	        
            IndexResponse userResponse = ElasticClient.getInstance()
                    .create("petfinder2", "user", userID, userJson);

            if (userResponse.getResult() != DocWriteResponse.Result.CREATED) {
                return new PFResponseBody<String>(null,PFStatusCode.ERROR_SERVER);
            }
            
            LoginDetailsDto loginDetails = new LoginDetailsDto(user.getEmail(),hashed);
            
            loginDetails.setUserID(userID);
            
            String authJSON = mapper.writeValueAsString(loginDetails);

            IndexResponse authResponse = ElasticClient.getInstance()
                    .create("petfinder2", "user_auth", UUID.randomUUID().toString(), authJSON);

            if (authResponse.getResult() != DocWriteResponse.Result.CREATED) {
                return new PFResponseBody<String>(null,PFStatusCode.ERROR_SERVER);
            } 
            
            
            return authenticateUserAfterCreation(userID,24,TimeUnit.HOURS);
        
            //return new PFResponseBody<String>(userID,PFStatusCode.SUCCESS_CREATED);
                        
        } catch (IOException e) {
            
            return new PFResponseBody<>(null,PFStatusCode.ERROR_SERVER);
        }

    }
	
	public PFResponseBody<UserDto> updateUser(String authToken, UserDto user, String id) {
	    
	    try { 
	        
	        DecodedJWT decodedUser = Auth.validateToken(authToken);
            
            if (decodedUser == null) {
                return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_AUTHORIZED);
            }

	        if (!id.equals(user.getId())) {
	            return new PFResponseBody<>(null,PFStatusCode.NOT_MODIFIED);
	        }
            
	        UserDto userFromDB = getUser(id);
	        
	        if (userFromDB == null) {
	            return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_FOUND);
	        }
	        
	        if (!userFromDB.getEmail().equals(user.getEmail())) {
	            return new PFResponseBody<>(null,PFStatusCode.NOT_MODIFIED);
	        }
	        
            String userJson = mapper.writeValueAsString(user);

            UpdateResponse userResponse = ElasticClient.getInstance()
                    .update("petfinder2", "user", id, userJson);

            if (userResponse.getResult() != DocWriteResponse.Result.UPDATED) {
                return new PFResponseBody<UserDto>(null,PFStatusCode.NOT_MODIFIED);
            }

            return new PFResponseBody<UserDto>(user,PFStatusCode.SUCCESS_MODIFIED);
                        
        } catch (IOException e) {
            
            return new PFResponseBody<>(null,PFStatusCode.ERROR_SERVER);
        }
    }
	
public PFResponseBody<UserDto> updateUserInternal(UserDto user, String id) {
        
        try { 
            
            
            UserDto userFromDB = getUser(id);
            
            if (userFromDB == null) {
                return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_FOUND);
            }
            
            if (!userFromDB.getEmail().equals(user.getEmail())) {
                return new PFResponseBody<>(null,PFStatusCode.NOT_MODIFIED);
            }
            
            String userJson = mapper.writeValueAsString(user);

            UpdateResponse userResponse = ElasticClient.getInstance()
                    .update("petfinder2", "user", id, userJson);

            if (userResponse.getResult() != DocWriteResponse.Result.UPDATED) {
                return new PFResponseBody<UserDto>(null,PFStatusCode.NOT_MODIFIED);
            }

            return new PFResponseBody<UserDto>(user,PFStatusCode.SUCCESS_MODIFIED);
                        
        } catch (IOException e) {
            
            return new PFResponseBody<>(null,PFStatusCode.ERROR_SERVER);
        }
    }
	
	public PFResponseBody<?> deleteUser(String authToken, String id) {
        
        try { 
            
            DecodedJWT decodedUser = Auth.validateToken(authToken);
            
            if (decodedUser == null) {
                return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_AUTHORIZED);
            }
            
            DeleteResponse resp = ElasticClient.getInstance().delete("petfinder2", "user", id);
            
            if (resp.getResult() == DocWriteResponse.Result.NOT_FOUND) {
                return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_FOUND);
            }
            
            ReplicationResponse.ShardInfo shardInfo = resp.getShardInfo();
            if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
                return new PFResponseBody<UserDto>(null,PFStatusCode.ERROR_SERVER);
            }
       
            return new PFResponseBody<UserDto>(null,PFStatusCode.SUCCESS_DELETED);
                        
        } catch (IOException e) {
            
            return new PFResponseBody<>(null,PFStatusCode.ERROR_SERVER);
        }
    }
	
	public PFResponseBody<?> authenticateUser(LoginDetailsDto loginDetails, long expiresIn, TimeUnit scale) {
	    
	    return Auth.authenticateUser(loginDetails,expiresIn,scale);
	    
	}
	
	public PFResponseBody<?> authenticateUserAfterCreation(String userID, long expiresIn, TimeUnit scale) {
        
        return Auth.authenticateUserAfterCreation(userID, expiresIn, scale);
        
    }
	
	public PFResponseBody<List<RatingDto>> getRatingsForUser(String authToken, String userID) {
        
	    try {
            
            DecodedJWT decodedUser = Auth.validateToken(authToken);
            
            if (decodedUser == null) {
                return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_AUTHORIZED);
            }
            
            SearchResponse response = ElasticClient.getInstance()
                    .search("petfinder2", "rating", "toUserID", userID);
            
            SearchHits hits = response.getHits();
            
            SearchHit[] hitArray = hits.getHits();
            
            List<RatingDto> ratings = new ArrayList<RatingDto>();
            
            for (SearchHit hit : hitArray) {
                
                RatingDto rating = mapper.readValue(hit.getSourceAsString(), RatingDto.class);
                
                ratings.add(rating);
            }
            
            return new PFResponseBody<List<RatingDto>>(ratings,PFStatusCode.SUCCESS_OK);
            
        } catch (IOException e) {
            
            return new PFResponseBody<>(null,PFStatusCode.ERROR_SERVER);
        }
            
    }
	
	public PFResponseBody<RatingDto> getRatingForUser(String authToken, String userID, String ratingID) {
	    
	    try {
            
            DecodedJWT decodedUser = Auth.validateToken(authToken);
            
            if (decodedUser == null) {
                return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_AUTHORIZED);
            }
            
            GetResponse resp =  ElasticClient.getInstance().get("petfinder2","rating",ratingID);
            
            if (!resp.isExists()) {
                return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_FOUND);
            }
                       
            RatingDto rating = mapper.readValue(resp.getSourceAsString(), RatingDto.class);
            
            return new PFResponseBody<RatingDto>(rating,PFStatusCode.SUCCESS_OK);
            
        } catch (IOException e) {
           
            return new PFResponseBody<>(null,PFStatusCode.ERROR_SERVER);
        }
    
    }
	
    public PFResponseBody<?> addRatingForUser(String authToken, String userID, RatingDto rating) {
            
        try { 
            
            DecodedJWT decodedUser = Auth.validateToken(authToken);
            
            if (decodedUser == null) {
                return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_AUTHORIZED);
            }
            
            GetResponse resp =  ElasticClient.getInstance().get("petfinder2","user",userID);
            
            if (!resp.isExists()) {
                return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_FOUND);
            }
            
            resp =  ElasticClient.getInstance().get("petfinder2","user",rating.getFromUserID());
            
            if (!resp.isExists()) {
                return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_FOUND);
            }
            
            rating.setToUserID(userID);
            
            SearchResponse response = ElasticClient.getInstance()
                    .search("petfinder2", "rating", "toUserID", rating.getToUserID());
            
            SearchHits hits = response.getHits();
            
            if (hits.totalHits != 0) {
                
                for (SearchHit hit : hits.getHits()) {
                    RatingDto ratingFromDB = mapper.readValue(hit.getSourceAsString(), RatingDto.class);
                    
                    if (ratingFromDB.getFromUserID().equals(userID) && !ratingFromDB.getToUserID().equals(userID)) {
                        return new PFResponseBody<String>(null,PFStatusCode.ERROR_CONFLICT);
                    }
                }
            }
            
            String ratingID = UUID.randomUUID().toString();
            
            rating.setId(ratingID);
            
            String ratingJSON = mapper.writeValueAsString(rating);
            
            IndexResponse userResponse = ElasticClient.getInstance()
                    .create("petfinder2", "rating", ratingID, ratingJSON);

            if (userResponse.getResult() != DocWriteResponse.Result.CREATED) {
                return new PFResponseBody<String>(null,PFStatusCode.ERROR_SERVER);
            }           
            
            if (!addNotification(userID, NotificationType.RATING, ratingID)) {
                return new PFResponseBody<String>(null,PFStatusCode.ERROR_SERVER);
            }
        
            return new PFResponseBody<String>(ratingID,PFStatusCode.SUCCESS_CREATED);
                        
        } catch (IOException e) {
            
            return new PFResponseBody<>(null,PFStatusCode.ERROR_SERVER);
        }
            
    }
    

    public PFResponseBody<?> removeRatingForUser(String authToken, String userID, String ratingID) {
    
        try { 
            
            DecodedJWT decodedUser = Auth.validateToken(authToken);
            
            if (decodedUser == null) {
                return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_AUTHORIZED);
            }
            
            DeleteResponse resp = ElasticClient.getInstance().delete("petfinder2", "rating", ratingID);
            
            if (resp.getResult() == DocWriteResponse.Result.NOT_FOUND) {
                return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_FOUND);
            }
            
            ReplicationResponse.ShardInfo shardInfo = resp.getShardInfo();
            if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
                return new PFResponseBody<UserDto>(null,PFStatusCode.ERROR_SERVER);
            }
       
            return new PFResponseBody<UserDto>(null,PFStatusCode.SUCCESS_DELETED);
                        
        } catch (IOException e) {
            
            return new PFResponseBody<>(null,PFStatusCode.ERROR_SERVER);
        }
    
    }
    
    public boolean addNotification(String userID, NotificationType type, String refersToID) { 
        
        try { 
        
        NotificationDto notification = new NotificationDto();
        
        notification.setForUserID(userID);
        
        String notificationID = UUID.randomUUID().toString();
        
        notification.setId(notificationID);
        
        notification.setType(type);
        
        notification.setRefersToID(refersToID);
        
        String notificationJSON;
       
        notificationJSON = mapper.writeValueAsString(notification);
       
        
        IndexResponse notificationResponse = ElasticClient.getInstance()
                .create("petfinder2", "notification", notificationID, notificationJSON);

        if (notificationResponse.getResult() != DocWriteResponse.Result.CREATED) {
            return false;
        }   
        
        return true;
        
        } catch (IOException e) {
            
            return false;
        }   
        
    }
    
    public NotificationDto getNotification(String id) {
        
        try {
           
            
            GetResponse resp =  ElasticClient.getInstance().get("petfinder2","notification",id);
            if (!resp.isExists()) {
                System.out.println("no notification found");
                return null;
            }
                       
            NotificationDto notification = mapper.readValue(resp.getSourceAsString(), NotificationDto.class);
            
            return notification;
            
        } catch (IOException e) {
           
            return null;
        }
    }
    
    public PFResponseBody<?> getNotificationForUser(String authToken, String userID, String id) {
        
        DecodedJWT decodedUser = Auth.validateToken(authToken);
        
        NotificationDto notification = getNotification(id);
        
        if (notification == null) {
            return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_FOUND);
        }
        
        //If user is logged in, get permissions level + userID
        if (decodedUser == null) {
            return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_AUTHORIZED);
        }
        
        String decodedUserID = decodedUser.getClaim("user").asString();

        UserDto user = getUser(decodedUserID);
        
        if (user == null) {
            return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_AUTHORIZED);
        }
        
        if (!decodedUserID.equals(notification.getForUserID()) && !user.getPermissions().equals(PFPermissions.GOD)) {
            return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_AUTHORIZED);
        }
        
        return new PFResponseBody<>(notification,PFStatusCode.SUCCESS_OK);
        
        
    }
    
    
    public PFResponseBody<List<NotificationDto>> getNotificationsForUser(String authToken, NotificationType type, String userID) {
        
        try {
            
            DecodedJWT decodedUser = Auth.validateToken(authToken);
            
            if (decodedUser == null) {
                return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_AUTHORIZED);
            }
            
            UserDto user = getUser(userID);
            
            if (user == null) {
                return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_AUTHORIZED);
            }
            
            SearchResponse response = ElasticClient.getInstance()
                    .search("petfinder2", "notification", "forUserID", userID);
            
            SearchHits hits = response.getHits();
            
            SearchHit[] hitArray = hits.getHits();
            
            List<NotificationDto> notifications = new ArrayList<NotificationDto>();
            
            for (SearchHit hit : hitArray) {
                
                NotificationDto notification = mapper.readValue(hit.getSourceAsString(), NotificationDto.class);
                
                if (!userID.equals(notification.getForUserID()) && !user.getPermissions().equals(PFPermissions.GOD)) {
                    return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_AUTHORIZED);
                }
                
                if (type.equals(NotificationType.ALL) || notification.getType().equals(type)) {
                    notifications.add(notification);
                }
            }
            
            return new PFResponseBody<List<NotificationDto>>(notifications,PFStatusCode.SUCCESS_OK);
            
        } catch (IOException e) {
            
            return new PFResponseBody<>(null,PFStatusCode.ERROR_SERVER);
        }
            
    }
    
public PFResponseBody<?> deleteAllNotificationsForUser(String authToken, NotificationType type, String userID) {
        
        try {
            
            DecodedJWT decodedUser = Auth.validateToken(authToken);
            
            if (decodedUser == null) {
                return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_AUTHORIZED);
            }
            
            UserDto user = getUser(userID);
            
            if (user == null) {
                return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_AUTHORIZED);
            }
            
            SearchResponse response = ElasticClient.getInstance()
                    .search("petfinder2", "notification", "forUserID", userID);
            
            SearchHits hits = response.getHits();
            
            SearchHit[] hitArray = hits.getHits();
                        
            for (SearchHit hit : hitArray) {
                
                NotificationDto notification = mapper.readValue(hit.getSourceAsString(), NotificationDto.class);
                
                DeleteResponse resp = ElasticClient.getInstance().delete("petfinder2", "notification", notification.getId());
                
                if (resp.getResult() == DocWriteResponse.Result.NOT_FOUND) {
                    return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_FOUND);
                }
                
                ReplicationResponse.ShardInfo shardInfo = resp.getShardInfo();
                if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
                    return new PFResponseBody<NotificationDto>(null,PFStatusCode.ERROR_SERVER);
                }
                
                
            }
            
            return new PFResponseBody<List<NotificationDto>>(null,PFStatusCode.SUCCESS_OK);
            
        } catch (IOException e) {
            
            return new PFResponseBody<>(null,PFStatusCode.ERROR_SERVER);
        }
            
    }

    public PFResponseBody<?> removeNotificationForUser(String authToken, String userID, String id) {
    
        try { 
            
            DecodedJWT decodedUser = Auth.validateToken(authToken);
            
            if (decodedUser == null) {
                return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_AUTHORIZED);
            }
            
            DeleteResponse resp = ElasticClient.getInstance().delete("petfinder2", "notification", id);
            
            if (resp.getResult() == DocWriteResponse.Result.NOT_FOUND) {
                return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_FOUND);
            }
            
            ReplicationResponse.ShardInfo shardInfo = resp.getShardInfo();
            if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
                return new PFResponseBody<UserDto>(null,PFStatusCode.ERROR_SERVER);
            }
       
            return new PFResponseBody<NotificationDto>(null,PFStatusCode.SUCCESS_DELETED);
                        
        } catch (IOException e) {
            
            return new PFResponseBody<>(null,PFStatusCode.ERROR_SERVER);
        }
    
    }
    
    
    
}