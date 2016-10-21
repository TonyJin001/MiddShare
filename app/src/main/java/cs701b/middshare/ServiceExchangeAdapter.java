package cs701b.middshare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by asus1 on 2016/10/20.
 */


public class ServiceExchangeAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<ServiceExchangeItem> mDataSource;

    public ServiceExchangeAdapter(Context context, ArrayList<ServiceExchangeItem> items) {
        mContext = context;
        mDataSource = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get view for row item
        View rowView = mInflater.inflate(R.layout.list_item_service_exchange, parent, false);
        ImageView userPhoto = (ImageView) rowView.findViewById(R.id.user_photo);
        TextView description = (TextView) rowView.findViewById(R.id.description);
        TextView price = (TextView) rowView.findViewById(R.id.cost);

        ServiceExchangeItem seItem = (ServiceExchangeItem) getItem(position);
//        description.setText(seItem.getDescription());
//        price.setText(seItem.getPrice());
        return rowView;
    }
}
