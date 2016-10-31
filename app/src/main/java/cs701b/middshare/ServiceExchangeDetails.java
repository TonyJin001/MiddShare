package cs701b.middshare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ServiceExchangeDetails extends AppCompatActivity {

    private ImageView userPhoto;
    private TextView description;
    private TextView price;
    private ListView comments;
    private EditText writeComment;
    private Button submitComment;
    private LinearLayout outsideLL;
    private LinearLayout itemInfoLL;
    private LinearLayout bottomLL;
    private final String TAG = "Service Exchange Detail";

//    public ServiceExchangeDetails() {
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_exchange_details);
        initView();
    }

    private void initView() {
        Log.d(TAG,"Before outsideLL");
        outsideLL = (LinearLayout) findViewById(R.id.outside_ll);
        Log.d(TAG,"after outsideLL");
        itemInfoLL = (LinearLayout) outsideLL.findViewById(R.id.item_info_ll);
        Log.d(TAG,"after itemInfoLL");
        userPhoto = (ImageView) itemInfoLL.findViewById(R.id.user_photo_details);
        Log.d(TAG,"after userphoto");
        description = (TextView) itemInfoLL.findViewById(R.id.description_details);
        Log.d(TAG,"after description");
        price = (TextView) itemInfoLL.findViewById(R.id.cost_details);
        Log.d(TAG,"after price");
        comments = (ListView) outsideLL.findViewById(R.id.comments);
        Log.d(TAG,"after comments");
        bottomLL = (LinearLayout) findViewById(R.id.bottom_ll);
        Log.d(TAG,"after buttomLL");
        writeComment = (EditText) findViewById(R.id.write_comments);
        Log.d(TAG,"after wrteComment");
        submitComment = (Button) findViewById(R.id.submit_comments);
        Log.d(TAG,"after submitcomment");

        Intent intent = getIntent();
        Log.d(TAG,"after intent");
        Bundle extras = intent.getExtras();
        Log.d(TAG,"after extra");
        description.setText(extras.getString("EXTRA_DESCRIPTION"));
        Log.d(TAG,"after setdescription");
        price.setText(extras.getString("EXTRA_PRICE"));
        Log.d(TAG,"after setprice");
        new GetProfilePhoto(userPhoto).execute(extras.getString("EXTRA_PHOTOURL"));
        Log.d(TAG,"after getprofilephoto");
    }
}
