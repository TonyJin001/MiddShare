package cs701b.middshare;

import android.util.Log;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shanniafu on 10/31/16.
 */

public class Comment {
    private String name;
    private String comment;
    private String photoUrl;
    private long time;

    public Comment() {

    }
    public Comment(String name, String comment, String photoUrl) {
        this.name = name;
        this.comment = comment;

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
        result.put("name", name);
        result.put("comment", comment);
        result.put("photoUrl",photoUrl);
        return result;
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

    public void setName (String name) {
        this.name = name;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setTime() { this.time = Calendar.getInstance().getTimeInMillis(); }

    public long getTime() { return this.time; }
}
