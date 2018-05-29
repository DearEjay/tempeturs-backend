package petfinder.upload;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.auth0.jwt.interfaces.DecodedJWT;

import petfinder.auth.Auth;
import petfinder.auth.PFPermissions;
import petfinder.response.PFResponseBody;
import petfinder.response.ResponseUtils.PFStatusCode;
import petfinder.site.common.user.UserDto;
import petfinder.site.common.user.UserService;

public class FileService {
    
    private static String bucketName     = "petfinder-group3";
    
    private UserService userService = new UserService();

    public PFResponseBody<?> uploadFile(String authToken, MultipartFile file, PFPermissions permissions) {

        DecodedJWT decodedUser = Auth.validateToken(authToken);
        
        if (decodedUser == null) {
            System.out.println("not decoded");
            return new PFResponseBody<>(null,PFStatusCode.ERROR_NOT_AUTHORIZED);
        }
        
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).withCredentials(new EnvironmentVariableCredentialsProvider()).build();
        try {
            String fileKey = UUID.randomUUID().toString();
            System.out.println("Uploading a new object to S3 from a file\n");
            //File fileToUpload = file;
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.addUserMetadata("user", decodedUser.getClaim("user").asString());
            
            objectMetadata.addUserMetadata("permissions", permissions.toString());
            objectMetadata.setContentType(file.getContentType());
            s3Client.putObject(new PutObjectRequest(
                                     bucketName, fileKey, file.getInputStream(), objectMetadata));
            HttpHeaders headers = new HttpHeaders();
            String url = "https://group-3-tempeturs-backend.herokuapp.com/api/file/";
            return new PFResponseBody<>(url + fileKey,PFStatusCode.SUCCESS_CREATED,headers);
         } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which " +
                    "means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
            
            HttpHeaders headers = new HttpHeaders();
            return new PFResponseBody<>(ase.getMessage(),PFStatusCode.getByCode(ase.getStatusCode()),headers);
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which " +
                    "means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
            HttpHeaders headers = new HttpHeaders();
            return new PFResponseBody<>(ace.getMessage(),PFStatusCode.ERROR_SERVER,headers);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            HttpHeaders headers = new HttpHeaders();
            return new PFResponseBody<>(e.getMessage(),PFStatusCode.ERROR_SERVER,headers);
        }
    
    }
    
    public PFResponseBody<?> getFile(String authToken, String fileKey) {
        
        UserDto user = null;
        
        PFPermissions permissionsLevel = PFPermissions.PUBLIC;
        
        String userID = null;
        
        if (authToken != null) {
            DecodedJWT decodedUser = Auth.validateToken("Bearer " + authToken);
            
            if (decodedUser != null) {
                user = userService.getUser(decodedUser.getClaim("user").asString());
                
                if (user == null) {
                    
                    
                    try {
                        
                        byte[] encoded = Files.readAllBytes(Paths.get("./src/main/resources/templates/401.html"));
                        
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.TEXT_HTML);
                        return new PFResponseBody<>(new String(encoded,StandardCharsets.UTF_8),PFStatusCode.ERROR_SERVER,headers);
                    } catch (IOException e) {
                        return new PFResponseBody<>(null,PFStatusCode.ERROR_SERVER);
                    }
                    
                    
                }
                                
                permissionsLevel = user.getPermissions();
                
                userID = user.getId();
            }
        }
  
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).withCredentials(new EnvironmentVariableCredentialsProvider()).build();
        try {
            //System.out.println("Downloading an object");
            S3Object s3object = s3Client.getObject(new GetObjectRequest(
                    bucketName, fileKey));
            //System.out.println("Content-Type: "  + 
                    //s3object.getObjectMetadata().getContentType());          
            
            ObjectMetadata data = s3object.getObjectMetadata();
            
            
            if (!permissionsLevel.hasPermissionsFor(PFPermissions.valueOf(data.getUserMetaDataOf("permissions")))) {
                System.out.println(data.getUserMetaDataOf("user"));
                System.out.println(userID);
                if (!data.getUserMetaDataOf("user").equals(userID)) {
                    
                    
                    
                    byte[] encoded = Files.readAllBytes(Paths.get("./src/main/resources/templates/401.html"));
                    
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.TEXT_HTML);
                    return new PFResponseBody<>(new String(encoded,StandardCharsets.UTF_8),PFStatusCode.ERROR_NOT_AUTHORIZED,headers);
                }      
            }
            
             
            HttpHeaders headers = new HttpHeaders();
            InputStream in = s3object.getObjectContent();
            byte[] media = IOUtils.toByteArray(in);
            headers.setCacheControl(CacheControl.noCache().getHeaderValue());
            headers.setContentType(MediaType.parseMediaType(s3object.getObjectMetadata().getContentType())); 
            
           return new PFResponseBody<>(media,PFStatusCode.SUCCESS_OK,headers);
            
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which" +
                    " means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
            
            HttpHeaders headers = new HttpHeaders();
            return new PFResponseBody<>(ase.getMessage(),PFStatusCode.getByCode(ase.getStatusCode()),headers);
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means"+
                    " the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
            HttpHeaders headers = new HttpHeaders();
            return new PFResponseBody<>(ace.getMessage(),PFStatusCode.ERROR_SERVER,headers);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("Error Message: " + e.getMessage());
            HttpHeaders headers = new HttpHeaders();
            return new PFResponseBody<>(e.getMessage(),PFStatusCode.ERROR_SERVER,headers);
        }
        
    }
    
    public PFResponseBody<?> deleteFile(String authToken, String fileKey) {
        
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).withCredentials(new EnvironmentVariableCredentialsProvider()).build();

        try {
            
            s3Client.deleteObject(new DeleteObjectRequest(bucketName, fileKey));
            
            return new PFResponseBody<>(null,PFStatusCode.SUCCESS_DELETED);
            
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
            
            return new PFResponseBody<>(ase.getMessage(),PFStatusCode.ERROR_SERVER);
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException.");
            System.out.println("Error Message: " + ace.getMessage());
            
            return new PFResponseBody<>(ace.getMessage(),PFStatusCode.ERROR_SERVER);
        }  
        
    }
    
}
