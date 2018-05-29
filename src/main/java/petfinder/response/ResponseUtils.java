package petfinder.response;

public abstract class ResponseUtils {
    public static enum PFStatusCode {
        ERROR_BAD_REQUEST(400),
        ERROR_NOT_AUTHORIZED(401),
        ERROR_SERVER(500),
        ERROR_NOT_FOUND(404),
        ERROR_CONFLICT(409),
        NOT_MODIFIED(304),
        SUCCESS_OK(200),
        SUCCESS_CREATED(201),
        SUCCESS_MODIFIED(200),
        SUCCESS_DELETED(200)
        ;
        
        int code;
        
        private PFStatusCode(int code) {
            this.code = code;
        }
        
        public int getStatusCode() {
            return code;
        }
        
        public static PFStatusCode getByCode(int statusCode) {
            //Iterate through every ErrorType to find a match
              for (PFStatusCode value : PFStatusCode.values()) {
                  if (value.getStatusCode() == statusCode) {
                      return value;
                  }
              }  
              
              //Null if no matches
              return null;
          }
    }

}
