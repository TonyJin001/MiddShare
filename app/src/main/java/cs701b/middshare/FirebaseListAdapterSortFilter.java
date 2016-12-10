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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by asus1 on 2016/12/8.
 */

public abstract class FirebaseListAdapterSortFilter<T> extends FirebaseListAdapter<T> {
    private String TAG = "FirebaseListSortFilter";
    private String sortingMethod = "OF";
    private HashMap<Integer,Integer> positionMap = new HashMap<>();

    public FirebaseListAdapterSortFilter(Activity activity, Class<T> modelClass, int modelLayout, Query ref) {
        super(activity, modelClass, modelLayout, ref);
    }
    public FirebaseListAdapterSortFilter(Activity activity, Class<T> modelClass, int modelLayout, DatabaseReference ref) {
        super(activity, modelClass, modelLayout, ref);
    }

    @Override
    public T getItem(int position) {
        if (sortingMethod.equals("NF")){
            position = getCount() - (position+1);
        }
        return super.getItem(position);
    }

    public T getItem(int position, boolean bar) {
        Log.d(TAG,"Original position: " + position);
        Log.d(TAG, positionMap.toString());
        if (sortingMethod.equals("NF")){
            position = getCount() - (position+1);
        } else if (sortingMethod.equals("AZ")||sortingMethod.equals("ZA")||sortingMethod.equals("BF")||sortingMethod.equals("SF")) {
            position = positionMap.get(position);
        }
        return super.getItem(position);
    }


    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = mActivity.getLayoutInflater().inflate(mLayout, viewGroup, false);
        }

        if (sortingMethod.equals("AZ")||sortingMethod.equals("ZA")) {
            LinkedHashMap<String,ServiceExchangeItemNoTime> nameToItem = new LinkedHashMap();
            int nItems = getCount();

            for (int i=0; i<nItems; i++){
                ServiceExchangeItemNoTime tempModel = UnsafeCastUtil.cast(getItem(i));
                nameToItem.put(tempModel.getName()+tempModel.getTime(),tempModel);
            }

            List<Map.Entry<String, ServiceExchangeItemNoTime>> entries =
                    new ArrayList<Map.Entry<String, ServiceExchangeItemNoTime>>(nameToItem.entrySet());

            if (sortingMethod.equals("AZ")){
                Collections.sort(entries, new Comparator<Map.Entry<String, ServiceExchangeItemNoTime>>() {
                    public int compare(Map.Entry<String, ServiceExchangeItemNoTime> a, Map.Entry<String, ServiceExchangeItemNoTime> b){
                        return a.getKey().compareTo(b.getKey());
                    }
                });
            } else {
                Collections.sort(entries, new Comparator<Map.Entry<String, ServiceExchangeItemNoTime>>() {
                    public int compare(Map.Entry<String, ServiceExchangeItemNoTime> a, Map.Entry<String, ServiceExchangeItemNoTime> b){
                        return a.getKey().compareTo(b.getKey())*-1;
                    }
                });
            }

            ArrayList<ServiceExchangeItemNoTime> sortedItems = new ArrayList<>();
            Map<String, ServiceExchangeItemNoTime> sortedMap = new LinkedHashMap<String, ServiceExchangeItemNoTime>();
            for (Map.Entry<String, ServiceExchangeItemNoTime> entry : entries) {
                sortedMap.put(entry.getKey(), entry.getValue());
                sortedItems.add(entry.getValue());
            }

            for (ServiceExchangeItemNoTime a : sortedItems) {
                Log.d(TAG,a.getName() + " " + a.getDescription());
            }

            for (int i=0; i<nItems; i++) {
                ServiceExchangeItemNoTime tempModel = UnsafeCastUtil.cast(getItem(i));
                int newPosition = sortedItems.indexOf(tempModel);
                positionMap.put(newPosition,i);
            }

            try{
                Log.d(TAG,sortedItems.size()+" size of sortedItems");
                Log.d(TAG,position + " position");
                populateView(view, (T) sortedItems.get(position), position);
            } catch (IndexOutOfBoundsException e){
                Log.e(TAG,"IndexOutOfBound");
            }
        } else if (sortingMethod.equals("NF") || sortingMethod.equals("OF")){
            populateView(view, getItem(position), position);
        } else {
            LinkedHashMap<String,ServiceExchangeItemNoTime> buySellToItem = new LinkedHashMap();
            int nItems = getCount();

            for (int i=0; i<nItems; i++){
                ServiceExchangeItemNoTime tempModel = UnsafeCastUtil.cast(getItem(i));
                Log.d(TAG,tempModel.isBuy()+"");
                buySellToItem.put(tempModel.isBuy()+""+tempModel.getTime(),tempModel);
            }

            List<Map.Entry<String, ServiceExchangeItemNoTime>> entries =
                    new ArrayList<Map.Entry<String, ServiceExchangeItemNoTime>>(buySellToItem.entrySet());

            if (sortingMethod.equals("SF")){
                Collections.sort(entries, new Comparator<Map.Entry<String, ServiceExchangeItemNoTime>>() {
                    public int compare(Map.Entry<String, ServiceExchangeItemNoTime> a, Map.Entry<String, ServiceExchangeItemNoTime> b){
                        return a.getKey().compareTo(b.getKey());
                    }
                });
            } else {
                Collections.sort(entries, new Comparator<Map.Entry<String, ServiceExchangeItemNoTime>>() {
                    public int compare(Map.Entry<String, ServiceExchangeItemNoTime> a, Map.Entry<String, ServiceExchangeItemNoTime> b){
                        return a.getKey().compareTo(b.getKey())*-1;
                    }
                });
            }

            ArrayList<ServiceExchangeItemNoTime> sortedItems = new ArrayList<>();
            Map<String, ServiceExchangeItemNoTime> sortedMap = new LinkedHashMap<String, ServiceExchangeItemNoTime>();
            for (Map.Entry<String, ServiceExchangeItemNoTime> entry : entries) {
                sortedMap.put(entry.getKey(), entry.getValue());
                sortedItems.add(entry.getValue());
            }
            for (int i=0; i<nItems; i++) {
                ServiceExchangeItemNoTime tempModel = UnsafeCastUtil.cast(getItem(i));
                int newPosition = sortedItems.indexOf(tempModel);
                positionMap.put(newPosition,i);
            }

            try{
                Log.d(TAG,sortedItems.size()+" size of sortedItems");
                Log.d(TAG,position + " position");
                populateView(view, (T) sortedItems.get(position), position);
            } catch (IndexOutOfBoundsException e){
                Log.e(TAG,"IndexOutOfBound");
            }
        }

        return view;
    }

    @Override
    public DatabaseReference getRef(int position) {
        if (sortingMethod.equals("NF")) {
            return super.getRef(getCount() - (position+1));
        } else if (sortingMethod.equals("OF")) {
            return super.getRef(position);
        }
        if (!positionMap.isEmpty()) {
            Log.d(TAG,positionMap.toString());
            return super.getRef(positionMap.get(position));
        } else {
            Log.d(TAG,"PositionMap is empty");
            return super.getRef(position);
        }

    }

    public String getSortingMethod() {
        return sortingMethod;
    }

    public void setSortingMethod(String sortingMethod) {
        this.sortingMethod = sortingMethod;
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