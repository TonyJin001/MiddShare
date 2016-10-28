package cs701b.middshare;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by asus1 on 2016/10/20.
 */

public class ServiceExchangeItem {
    private String description;
    private String price;
    private String photoUrl;

    public ServiceExchangeItem() {

    }

    public ServiceExchangeItem(String description, String price, String photoUrl) {
        this.description = description;
        this.price = price;
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
}
