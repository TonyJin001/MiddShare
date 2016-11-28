package cs701b.middshare;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by asus1 on 2016/10/20.
 */

public class ServiceExchangeItem {
    private String description;
    private String price;
    private String photoUrl;
    private String name;
    private String details;
    private Map<String,String> time;
    private ArrayList<Comment> comments;

    public ServiceExchangeItem() {

    }

    public ServiceExchangeItem(String description, String price, String photoUrl, String name, String details, Map<String,String> time) {
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

    public Map<String,Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("description", description);
        result.put("price",price);
        result.put("photoUrl",photoUrl);
        result.put("name",name);
        result.put("details",details);
        result.put("time",time);
        result.put("comments", comments);
        return result;
    }

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

    public Map<String, String> getTime() {
        return time;
    }

    public void setTime(Map<String, String> time) {
        this.time = time;
    }



    }

