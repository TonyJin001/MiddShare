package cs701b.middshare;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by asus1 on 2016/10/20.
 */

public class ServiceExchangeItem {
    private String description;
    private String price;

    public ServiceExchangeItem() {

    }

    public ServiceExchangeItem(String description, String price) {
        this.description = description;
        this.price = price;
    }

    public Map<String,Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("description", description);
        result.put("price",price);
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
}
