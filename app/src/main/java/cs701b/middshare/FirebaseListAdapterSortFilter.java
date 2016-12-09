package cs701b.middshare;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        // maybe this getCount can be modified??????? to get the count of total items instead of the visible ones...???
        for (int i=0; i<nItems; i++){
            ServiceExchangeItemNoTime tempModel = UnsafeCastUtil.cast(getItem(i));
            nameToItem.put(tempModel.getName(),tempModel);
        }

        List<Map.Entry<String, ServiceExchangeItemNoTime>> entries =
                new ArrayList<Map.Entry<String, ServiceExchangeItemNoTime>>(nameToItem.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<String, ServiceExchangeItemNoTime>>() {
            public int compare(Map.Entry<String, ServiceExchangeItemNoTime> a, Map.Entry<String, ServiceExchangeItemNoTime> b){
                return a.getKey().compareTo(b.getKey());
            }
        });
        ArrayList<ServiceExchangeItemNoTime> sortedItems = new ArrayList<>();
        Map<String, ServiceExchangeItemNoTime> sortedMap = new LinkedHashMap<String, ServiceExchangeItemNoTime>();
        for (Map.Entry<String, ServiceExchangeItemNoTime> entry : entries) {
            sortedMap.put(entry.getKey(), entry.getValue());
            sortedItems.add(entry.getValue());
        }

//        String keyName = sortedItems.get(position).getName();
        // Call out to subclass to marshall this model into the provided view
        try{
            Log.d(TAG,sortedItems.size()+" size of sortedItems");
            Log.d(TAG,position + " position");
            populateView(view, (T) sortedItems.get(position), position);
            // sortedItems list size is always 3....
        } catch (IndexOutOfBoundsException e){
            Log.e(TAG,"IndexOutOfBound");
        }

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