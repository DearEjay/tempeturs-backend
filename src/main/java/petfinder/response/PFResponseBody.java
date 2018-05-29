package petfinder.response;

import org.springframework.http.HttpHeaders;

import petfinder.response.ResponseUtils.PFStatusCode;

public class PFResponseBody<T> {
    private T data;
    private PFStatusCode code; 
    private HttpHeaders headers;
    
    public PFResponseBody() {
        
    }
    
    /**
     * @param data
     */
    public PFResponseBody(T data) {
        this.data = data;
    }
    
    /**
     * @param data
     * @param code
     */
    public PFResponseBody(T data, PFStatusCode code) {
        this.data = data;
        this.code = code;
    }
    
    /**
     * @param data
     * @param code
     */
    public PFResponseBody(T data, PFStatusCode code, HttpHeaders headers) {
        this.data = data;
        this.code = code;
        this.headers = headers;
    }
    
    /**
     * @return
     */
    public T getData() {
        return data;
    }
    
    /**
     * @param data
     */
    public void setData(T data) {
        this.data = data;
    }
    
    /**
     * @return the code
     */
    public PFStatusCode getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(PFStatusCode code) {
        this.code = code;
    }

    /**
     * @return the headers
     */
    public HttpHeaders getHeaders() {
        return headers;
    }

    /**
     * @param headers the headers to set
     */
    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    
}
