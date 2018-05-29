package petfinder.auth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.security.crypto.bcrypt.BCrypt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import petfinder.elasticclient.ElasticClient;
import petfinder.response.PFResponseBody;
import petfinder.response.ResponseUtils.PFStatusCode;
import petfinder.site.common.user.LoginDetailsDto;

public abstract class Auth {
    
    public static PFResponseBody<?> authenticateUserAfterCreation(String userID , long expiresIn, TimeUnit scale) { 
        //Generate JSON web token
        try {
            Algorithm algorithm;
        
        algorithm = Algorithm.HMAC256("mysuperdupersecretkey");

        expiresIn = scale.toMillis(expiresIn);
        
        String token = JWT.create()
            .withClaim("user", userID)
            .withIssuer("petfinder2")
            .withExpiresAt(new Date(System.currentTimeMillis()+expiresIn))
            .sign(algorithm);

    
        Map<String,String> userDetails = new HashMap<String,String>();
                        
        userDetails.put("id", userID);
        userDetails.put("token", token);
        
        return new PFResponseBody<Map<String,String>>(userDetails,PFStatusCode.SUCCESS_CREATED);
        
        } catch (IllegalArgumentException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return new PFResponseBody<String>(null,PFStatusCode.ERROR_SERVER);
        }
    
    }
    
    /**
     * @param loginDetails the username and password to validate
     * @param expiresIn SECONDS until expiration
     * @return
     */
    public static PFResponseBody<?> authenticateUser(LoginDetailsDto loginDetails, long expiresIn, TimeUnit scale) { 
     
        try {
            
            SearchResponse response = ElasticClient.getInstance()
                                                   .search("petfinder2", "user_auth", "email", loginDetails.getEmail());
            SearchHits hits = response.getHits();
            
            //Check if any results match
            if (hits.totalHits == 0) {
                
                return new PFResponseBody<String>(null,PFStatusCode.ERROR_NOT_AUTHORIZED);
            } 
        
            //If more than one result matches thats bad
            if (hits.totalHits != 1) {
                return new PFResponseBody<String>(null,PFStatusCode.ERROR_SERVER);
            }
        
            //If we get here, that means we matched the username and there is only one match
        
            SearchHit hit = hits.getAt(0);
        
            Map<String,Object> source = hit.getSource();
                    
            if (BCrypt.checkpw(loginDetails.getPassword(), (String) source.get("password"))) {
              
            
                //Generate JSON web token
                Algorithm algorithm;
                algorithm = Algorithm.HMAC256("mysuperdupersecretkey");

                expiresIn = scale.toMillis(expiresIn);
                
                String token = JWT.create()
                    .withClaim("user", (String)source.get("userID"))
                    .withIssuer("petfinder2")
                    .withExpiresAt(new Date(System.currentTimeMillis()+expiresIn))
                    .sign(algorithm);
        
            
                Map<String,String> userDetails = new HashMap<String,String>();
                                
                userDetails.put("id", (String)source.get("userID"));
                userDetails.put("token", token);
                
                return new PFResponseBody<Map<String,String>>(userDetails,PFStatusCode.SUCCESS_OK);
            }
        
            return new PFResponseBody<String>(null,PFStatusCode.ERROR_NOT_AUTHORIZED);
        
        } catch (IllegalArgumentException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return new PFResponseBody<String>(null,PFStatusCode.ERROR_SERVER);
        } catch (IOException e) {
        

        e.printStackTrace();
            return new PFResponseBody<String>(null,PFStatusCode.ERROR_SERVER);
        }
        
    }
    
    public static DecodedJWT validateToken(String token) {
        
        if (token == null) {
            return null;
        }
        
        //Check the proper format
        if (token.startsWith("Bearer ")) {
            
            token = token.substring(7);
            
            try {
                
                Algorithm algorithm = Algorithm.HMAC256("mysuperdupersecretkey");
                JWTVerifier verifier = JWT.require(algorithm)
                                          .withIssuer("petfinder2")
                                          .build();
                
                DecodedJWT jwt = verifier.verify(token);
                
                return jwt;
                
            } catch (UnsupportedEncodingException exception){
                //UTF-8 encoding not supported
                return null;
            } catch (JWTVerificationException exception){
                //Invalid signature/claims
                return null;
            }
        }

        return null;
        
    }
    
    
}
