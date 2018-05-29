package petfinder.site.common.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class AvailabilityDto {
    
    @JsonFormat(pattern="yyyy-MM-dd")
    List<Date> unavailableDays;
    
    AvailabilityDto() {
        unavailableDays = new ArrayList<Date>();
    }

    /**
     * @return the unavailableDays
     */
    public List<Date> getUnavailableDays() {
        return unavailableDays;
    }

    /**
     * @param unavailableDays the unavailableDays to set
     */
    public void setUnavailableDays(List<Date> unavailableDays) {
        this.unavailableDays = unavailableDays;
    }
    
}
