package petfinder.site.endpoint;

import java.net.UnknownHostException;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import petfinder.auth.PFPermissions;
import petfinder.response.PFResponseBody;

import petfinder.upload.FileService;

@RestController
@RequestMapping(value = "/api/file")
public class FileEndpoint {
    
    FileService fileService = new FileService();

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<PFResponseBody<?>> uploadFile(@RequestHeader("Authorization") String authToken, @RequestParam("file") MultipartFile file, @RequestParam("permissions") PFPermissions permissions) throws UnknownHostException {
                        
        PFResponseBody<?> response = fileService.uploadFile(authToken, file, permissions);
        return ResponseEntity.status(response.getCode()
                .getStatusCode())
                .headers(response.getHeaders())
                .body(response);
        
        
        
    }
    
    
    @RequestMapping(value = "/{file}", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getFile(@PathVariable(name = "file") String fileKey, @RequestParam(value = "token", required = false) String authToken) throws UnknownHostException {
        
        PFResponseBody<?> response = fileService.getFile(authToken, fileKey);
        return ResponseEntity.status(response.getCode()
                .getStatusCode())
                .headers(response.getHeaders())
                .body(response.getData());
        
    }
    
    @RequestMapping(value = "/{file}", method = RequestMethod.DELETE)
    public @ResponseBody ResponseEntity<?> deleteFile(@PathVariable(name = "file") String fileKey, @RequestHeader("Authorization") String authToken) throws UnknownHostException {
        
     
        PFResponseBody<?> response = fileService.deleteFile(authToken, fileKey);
        return ResponseEntity.status(response.getCode()
                .getStatusCode())
                .headers(response.getHeaders())
                .body(response.getData());
        
    }
    
}
