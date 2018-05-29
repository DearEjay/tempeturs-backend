package petfinder.site.common.user;

public class PreferencesDto {
    //api/search/?rateLower=10&rateUpper=25&location=76706&radius=15&availBegin=2017-11-10&availEnd=2017-11-28&numPets=1
    int numPets;
    
    enum PetSize {
        SMALL,
        MEDIUM,
        LARGE;
    }
    
    PetSize petSize;
    
    
    
}
