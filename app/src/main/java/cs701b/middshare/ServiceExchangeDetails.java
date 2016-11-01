package cs701b.middshare;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.FirebaseDatabase;

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
    private LruCache<String,Bitmap> mMemoryCache;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;


//    public ServiceExchangeDetails() {
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_exchange_details);

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory()/1024);
        final int cacheSize = maxMemory/8;

        mMemoryCache = new LruCache<String,Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount()/1024;
            }
        };

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        initView();
        if (mFirebaseUser == null) {
            // log in again
            loadLoginView();
        } else {
            mUserId = mFirebaseUser.getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            String photoUrl = "";
            String email = "";
            String name = "";

            final String finalPhotoUrl = photoUrl;


            Button bLogOut = (Button) findViewById(R.id.logout_button);
            bLogOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logOut(v);
                }
            });

            final ListView commentList = (ListView) findViewById(R.id.comments);

            FirebaseListAdapter<ServiceExchangeItem> adapter = new FirebaseListAdapter<ServiceExchangeItem>(
                    this,
                    ServiceExchangeItem.class,
                    R.layout.list_item_service_exchange,
                    mDatabase.child("service_exchange_items")
            ) {
                @Override
                protected void populateView(View v, ServiceExchangeItem model, int position) {
                    ImageView userPhoto = (ImageView) v.findViewById(R.id.user_photo);
                    TextView description = (TextView) v.findViewById(R.id.description);
                    TextView price = (TextView) v.findViewById(R.id.cost);
                    description.setText(model.getDescription());
                    Log.d(TAG,model.getDescription()+"@"+model.getPrice());
                    price.setText(model.getPrice());
                    Log.d(TAG,"Photo url:" + model.getPhotoUrl());
                    userPhoto.setImageURI(Uri.parse(model.getPhotoUrl()));
                    new GetProfilePhoto(userPhoto).execute(model.getPhotoUrl());
//                    // Photo profiles swap quickly, problem maybe with async task and global variable currentBitmap....
//                    Log.d(TAG,"Current bitmap: " + currentBitmap);
//                    userPhoto.setImageBitmap(currentBitmap);
                }
            };
            seList.setAdapter(adapter);
            seList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ServiceExchangeItem itemDetails = (ServiceExchangeItem) parent.getAdapter().getItem(position);
                    String itemDescription = itemDetails.getDescription();
                    String itemPrice =  itemDetails.getPrice();
                    String itemPhotoUrl = itemDetails.getPhotoUrl();
                    Log.d(TAG,itemDescription +"\t" + itemPrice + "\t" + itemPhotoUrl);
                    Intent intent = new Intent(ServiceExchange.this,ServiceExchangeDetails.class);
                    Bundle extras = new Bundle();
                    extras.putString("EXTRA_DESCRIPTION",itemDetails.getDescription());
                    extras.putString("EXTRA_PRICE",itemDetails.getPrice());
                    extras.putString("EXTRA_PHOTOURL",itemDetails.getPhotoUrl());
                    intent.putExtras(extras);
                    startActivity(intent);
                }
            });

        }
    }

    private void initView() {

        outsideLL = (LinearLayout) findViewById(R.id.outside_ll);
        itemInfoLL = (LinearLayout) outsideLL.findViewById(R.id.item_info_ll);
        userPhoto = (ImageView) itemInfoLL.findViewById(R.id.user_photo_details);
        description = (TextView) itemInfoLL.findViewById(R.id.description_details);
        price = (TextView) itemInfoLL.findViewById(R.id.cost_details);
        comments = (ListView) outsideLL.findViewById(R.id.comments);
        bottomLL = (LinearLayout) findViewById(R.id.bottom_ll);
        writeComment = (EditText) findViewById(R.id.write_comments);
        submitComment = (Button) findViewById(R.id.submit_comments);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        description.setText(extras.getString("EXTRA_DESCRIPTION"));
        price.setText(extras.getString("EXTRA_PRICE"));
        new GetProfilePhoto(userPhoto).execute(extras.getString("EXTRA_PHOTOURL"));

    }
}
