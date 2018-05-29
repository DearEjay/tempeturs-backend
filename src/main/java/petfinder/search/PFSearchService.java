package petfinder.search;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.queryparser.ext.Extensions.Pair;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import petfinder.elasticclient.ElasticClient;
import petfinder.response.PFResponseBody;
import petfinder.response.ResponseUtils.PFStatusCode;
import petfinder.site.common.user.UserDto;

@Service
public class PFSearchService {
    
    @Autowired
    private ObjectMapper mapper = new ObjectMapper();

    public PFResponseBody<List<UserDto>> searchSitters(String authToken) {
        
        try {
            
            SearchResponse resp =  ElasticClient.getInstance().search("petfinder2", "user", "classification", "SITTER");
            
            SearchHits hits = resp.getHits();
            
            SearchHit[] hitArray = hits.getHits();
            
            List<UserDto> users = new ArrayList<UserDto>();
            
            for (SearchHit hit : hitArray) {
                
                UserDto user = mapper.readValue(hit.getSourceAsString(), UserDto.class);
                
                users.add(user);
            }
              
            return new PFResponseBody<List<UserDto>>(users,PFStatusCode.SUCCESS_OK);
   
            
        } catch (IOException e) {
            e.printStackTrace();
            return new PFResponseBody<>(null,PFStatusCode.ERROR_SERVER);
        }

    }
    
    public PFResponseBody<List<UserDto>> search(String authToken, Map<String,String> params) {
        
        Map<String,String> fields = new HashMap<String,String>();
        Map<String, Pair<String,String>> rangeFields = new HashMap<String, Pair<String,String>>();
        
        Map<String, Pair<String,String>> availabilityRange = new HashMap<String, Pair<String,String>>();
        
        int sortRate = 0;
        
        for (Map.Entry<String, String> entry : params.entrySet()){
            
            //api/search/?rateLower=10&rateUpper=25&location=76706&radius=15&availBegin=2017-11-10&availEnd=2017-11-28&numPets=1
            
            switch(entry.getKey()) {
            case "rateLower":
                if (rangeFields.containsKey("rate")) {
                    Pair<String,String> tempPair = rangeFields.get("rate");
                    rangeFields.put("rate", new Pair<String,String>(entry.getValue(),tempPair.cud));
                } else {
                    rangeFields.put("rate", new Pair<String,String>(entry.getValue(),"1000"));
                }
                
                break;
            case "rateUpper":
                if (rangeFields.containsKey("rate")) {
                    Pair<String,String> tempPair = rangeFields.get("rate");
                    rangeFields.put("rate", new Pair<String,String>(tempPair.cur,entry.getValue()));
                } else {
                    rangeFields.put("rate", new Pair<String,String>("0",entry.getValue()));
                }
                break;
            case "location":
                
                break;
            case "radius":
                break;
            case "availBegin":
                System.out.println("hit availBegin");

                if (availabilityRange.containsKey("unavailableDays")) {
                    Pair<String,String> tempPair = availabilityRange.get("unavailableDays");
                    availabilityRange.put("unavailableDays", new Pair<String,String>(entry.getValue(),tempPair.cud));
                } else {
                    availabilityRange.put("unavailableDays", new Pair<String,String>(entry.getValue(),"2099-01-01"));
                }
                break;
            case "availEnd":
                System.out.println("hit availEnd");
                if (availabilityRange.containsKey("unavailableDays")) {
                    Pair<String,String> tempPair = availabilityRange.get("unavailableDays");
                    availabilityRange.put("unavailableDays", new Pair<String,String>(tempPair.cur,entry.getValue()));
                } else {
                    availabilityRange.put("unavailableDays", new Pair<String,String>("2000-01-01",entry.getValue()));
                }
                break;
            case "sort":
                switch(entry.getValue()) {
                case "rateMost":
                    sortRate = 2;
                    break;
                case "rateLeast":
                    sortRate = 1;
                    break;
                }
                break;
            case "numPets":
                break;
            case "name":
                fields.put("name", entry.getValue());
                break;
            
            }
        }
        
        try {
            
            
            SearchResponse resp =  ElasticClient.getInstance().searchMultifieldsWithRange("petfinder2", "user", fields,rangeFields);
            
            SearchHits hits = resp.getHits();
            
            SearchHit[] hitArray = hits.getHits();
            
            List<UserDto> users = new ArrayList<UserDto>();
            
            for (SearchHit hit : hitArray) {
                
                UserDto user = mapper.readValue(hit.getSourceAsString(), UserDto.class);
                if (availabilityRange.containsKey("unavailableDays")) {
                    List<Date> unavailableDays = user.getAvailability().getUnavailableDays();
                    Pair<String,String> tempPair = availabilityRange.get("unavailableDays");
                                        
                    
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); 
                    Date startDate, endDate;
                    try {
                        startDate = df.parse(tempPair.cur);
                        endDate = df.parse(tempPair.cud);
                        
                    } catch (ParseException e) {
                        return new PFResponseBody<>(null,PFStatusCode.ERROR_SERVER);
                    }
                    
                    System.out.println(tempPair.cur);
                    System.out.println(tempPair.cud);

                    for (Date date : unavailableDays) {
                        System.out.println(startDate + " " + date + " " + endDate);
                        if (!(date.before(startDate) || date.after(endDate))) {
                            System.out.println("Unavail date found");
                            System.out.println("User =" + user.getName());
                            continue;
                        }
                    }
                }
                users.add(user);
            }
            
            
            switch(sortRate) {
            case 0:
                break;
            case 1:
                users.sort(new Comparator<UserDto>(){
                    
                    @Override
                    public int compare(UserDto o1, UserDto o2) {
                        return o1.getRate() - o2.getRate();
                        
                    }
              });
                break;
            case 2:
                users.sort(new Comparator<UserDto>(){
                    
                    @Override
                    public int compare(UserDto o1, UserDto o2) {
                        return o2.getRate() - o1.getRate();
                        
                    }
              });
                break;
            }
          
                                      
            return new PFResponseBody<List<UserDto>>(users,PFStatusCode.SUCCESS_OK);
   
            
        } catch (IOException e) {
            e.printStackTrace();
            return new PFResponseBody<>(null,PFStatusCode.ERROR_SERVER);
        }

    }    
    
}
