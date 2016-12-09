package cs701b.middshare;

import android.app.Activity;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by asus1 on 2016/12/8.
 */

public abstract class FirebaseListAdapterSortFilter<T> extends FirebaseListAdapter<T> {
    public FirebaseListAdapterSortFilter(Activity activity, Class<T> modelClass, int modelLayout, Query ref) {
        super(activity, modelClass, modelLayout, ref);
    }
    public FirebaseListAdapterSortFilter(Activity activity, Class<T> modelClass, int modelLayout, DatabaseReference ref) {
        super(activity, modelClass, modelLayout, ref);
    }

    @Override
    public T getItem(int position) {
        return super.getItem(getCount() - (position + 1));
    }
}
