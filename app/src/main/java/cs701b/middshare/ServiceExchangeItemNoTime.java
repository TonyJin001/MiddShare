package cs701b.middshare;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

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


    public ServiceExchangeItemNoTime() {

    }

    public ServiceExchangeItemNoTime(String description, String price, String photoUrl, String name, String details, long time) {
        this.description = description;
        this.price = price;
        this.name = name;
        this.details = details;
        this.time = time;
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
}
