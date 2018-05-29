package petfinder.site.endpoint;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import petfinder.response.PFResponseBody;
import petfinder.site.common.user.NotificationDto;
import petfinder.site.common.user.UserService;
import petfinder.site.common.user.NotificationDto.NotificationType;


@RestController
@RequestMapping(value = "/api/user/{user_id}/notifications")
public class NotificationsEndpoint {
    
    private UserService userService = new UserService();

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<PFResponseBody<?>> getNotificationForUser(@RequestHeader("Authorization") String authToken, @PathVariable(name = "user_id") String userID, @PathVariable(name = "notification_type", required = false) NotificationType type) throws IOException {
        
        if (type == null) {
            type = NotificationType.ALL;
        }
        
        PFResponseBody<List<NotificationDto>> response = userService.getNotificationsForUser(authToken, type, userID);
        
        
        return ResponseEntity.status(response.getCode().getStatusCode()).body(response);
    }
    
    @RequestMapping(value = "/", method = RequestMethod.DELETE)
    public ResponseEntity<PFResponseBody<?>> deleteAllNotificationsForUser(@RequestHeader("Authorization") String authToken, @PathVariable(name = "user_id") String userID, @PathVariable(name = "notification_type", required = false) NotificationType type) throws IOException {
        
        if (type == null) {
            type = NotificationType.ALL;
        }
        
        PFResponseBody<?> response = userService.deleteAllNotificationsForUser(authToken, type, userID);
        
        
        return ResponseEntity.status(response.getCode().getStatusCode()).body(response);
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<PFResponseBody<?>> getNotificationForUser(@RequestHeader("Authorization") String authToken, @PathVariable(name = "user_id") String userID, @PathVariable(name = "id") String id) throws IOException {
        
        PFResponseBody<?> response = userService.getNotificationForUser(authToken, userID, id);
                
        return ResponseEntity.status(response.getCode().getStatusCode()).body(response);
        
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<PFResponseBody<?>> removeNotificationForUser(@RequestHeader("Authorization") String authToken, @PathVariable(name = "user_id") String userID, @PathVariable(name = "id") String id) throws UnknownHostException {
        
        PFResponseBody<?> response = userService.removeNotificationForUser(authToken, userID, id);
        
        return ResponseEntity.status(response.getCode().getStatusCode()).body(response);
    }
}
