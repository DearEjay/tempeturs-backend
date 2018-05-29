package petfinder.auth;

public enum PFPermissions {
    PUBLIC(0),
    PROTECTED(1),
    PRIVATE(2),
    GOD(9000);
    
    int code;
    
    private PFPermissions(int code) {
        this.code = code;
    }
    
    public int getStatusCode() {
        return code;
    }
    
    public static PFPermissions getByCode(int statusCode) {
        //Iterate through every ErrorType to find a match
          for (PFPermissions value : PFPermissions.values()) {
              if (value.getStatusCode() == statusCode) {
                  return value;
              }
          }  
          
          //Null if no matches
          return null;
     }
    
    public boolean hasPermissionsFor(PFPermissions other) {
        return this.code >= other.code;
    }
    
}

