package cs701b.middshare;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by asus1 on 2016/10/20.
 */

public class ServiceExchangeItemNoTime {
    private String description;
    private String price;
    private String photoUrl;
    private String name;
    private String details;
    private long time;
    private boolean buy;
    private long timeLimit;


    public ServiceExchangeItemNoTime() {

    }

    public ServiceExchangeItemNoTime(String description, String price, String photoUrl, String name, String details, long time, boolean buy, long timeLimit) {
        this.description = description;
        this.price = price;
        this.name = name;
        this.details = details;
        this.time = time;
        this.buy = buy;
        this.timeLimit = timeLimit;
        if (photoUrl.equals(null)) {
            this.photoUrl = "";
            Log.d("SEItem","url is null");
        } else {
            this.photoUrl = photoUrl;
            Log.d("SEItem","url isn't null");
        }
    }

//    public Map<String,Object> toMap() {
//        HashMap<String, Object> result = new HashMap<>();
//        result.put("description", description);
//        result.put("price",price);
//        result.put("photoUrl",photoUrl);
//        result.put("name",name);
//        result.put("details",details);
//        result.put("time",time);
//        return result;
//    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isBuy() {
        return buy;
    }

    public void setBuy(boolean buy) {
        this.buy = buy;
    }

    public long getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(long timeLimit) {
        this.timeLimit = timeLimit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getName(),this.getTime(),this.getDescription(),this.getPrice(),this.getDetails());
    }

    @Override
    public boolean equals (Object object) {
        boolean result = false;
        if (object == null || object.getClass() != getClass()) {
            result = false;
        } else {
            ServiceExchangeItemNoTime other = (ServiceExchangeItemNoTime) object;
            if (this.name.equals(other.getName())
                    && this.getTime() == other.getTime()
                    && this.getDescription().equals(other.getDescription())
                    && this.getPrice().equals(other.getPrice())
                    && this.getDetails().equals(other.getDetails())) {
                result = true;
            }
        }
        return result;
    }


}
