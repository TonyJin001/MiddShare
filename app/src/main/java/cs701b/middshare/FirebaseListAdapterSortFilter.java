package cs701b.middshare;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.LinkedHashMap;

/**
 * Created by asus1 on 2016/12/8.
 */

public abstract class FirebaseListAdapterSortFilter<T> extends FirebaseListAdapter<T> {
    private String TAG = "FirebaseListSortFilter";
    public FirebaseListAdapterSortFilter(Activity activity, Class<T> modelClass, int modelLayout, Query ref) {
        super(activity, modelClass, modelLayout, ref);
    }
    public FirebaseListAdapterSortFilter(Activity activity, Class<T> modelClass, int modelLayout, DatabaseReference ref) {
        super(activity, modelClass, modelLayout, ref);
    }

//    @Override
//    public T getItem(int position) {
//        Log.d(TAG, "position: " + position);
//        Log.d(TAG,"count: " + getCount());
//        return super.getItem(getCount() - (position + 1));
//    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = mActivity.getLayoutInflater().inflate(mLayout, viewGroup, false);
        }
        
        LinkedHashMap<String,ServiceExchangeItemNoTime> nameToItem = new LinkedHashMap();
        int nItems = getCount();
        for (int i=0; i<nItems; i++){
            ServiceExchangeItemNoTime tempModel = UnsafeCastUtil.cast(getItem(i));
            nameToItem.put(tempModel.getName(),tempModel);
        }
        System.out.println(nameToItem);
        // Call out to subclass to marshall this model into the provided view
        populateView(view, (T)nameToItem.get("Yanfeng Jin"), position);
        return view;
    }
}
class UnsafeCastUtil {

    private UnsafeCastUtil(){ /* not instatiable */}

    /**
     * Warning! Using this method is a sin against the gods of programming!
     */
    @SuppressWarnings("unchecked")
    public static <T> T cast(Object o){
        return (T)o;
    }

}