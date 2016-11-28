package cs701b.middshare;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.net.URL;

/**
 * Created by asus1 on 2016/10/30.
 */

class GetProfilePhoto extends AsyncTask<String, Void, Bitmap> {

    private Exception exception;
    private ImageView bmImage;
    private final String TAG = "Get Profile Photo";

    public GetProfilePhoto(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urlStr) {
        try {
            Bitmap bitmap = null;
            try {
                URL url = new URL(urlStr[0]);
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (NullPointerException e) {
                Log.e(TAG,"nullpointer when setting user image");
            } catch (IOException e) {
                Log.e(TAG, "IO exception when setting user image");
            }
            return bitmap;
        } catch (Exception e) {
            this.exception = e;
            return null;
        }
    }

    protected void onPostExecute(Bitmap bitmap) {
        bmImage.setImageBitmap(bitmap);
    }
}
