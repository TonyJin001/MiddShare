package cs701b.middshare;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ServiceExchangeDetails extends AppCompatActivity {

    private ImageView userPhoto;
    private TextView description;
    private TextView price;
    private TextView name;
    private TextView details;
    private TextView buySellDetails;
    private String itemKey;
    private ListView comments;
    private EditText writeComment;
    private Button submitComment;
    private LinearLayout outsideLL;
    private RelativeLayout itemInfoRL;
    private LinearLayout bottomLL;
    private final String TAG = "Service Exchange Detail";
    private LruCache<String,Bitmap> mMemoryCache;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUserId;
    private DatabaseReference mDatabase;



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



            final ListView commentList = (ListView) findViewById(R.id.comments);
            submitComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                         String key = mDatabase.child("item-comments").push().getKey();
                         Comment comment = new Comment(mFirebaseUser.getDisplayName(), writeComment.getText().toString(), mFirebaseUser.getPhotoUrl().toString());
                         Map<String, Object> comValues = comment.toMap();

                         Map<String, Object> childUpdates = new HashMap<>();
                         childUpdates.put("/item-comments/" + itemKey + "/" + key, comValues);

                         mDatabase.updateChildren(childUpdates);
                         writeComment.setText("");

                     }
                 });
            final FirebaseListAdapter <Comment> adapter = new FirebaseListAdapter<Comment>(

                    this,
                    Comment.class,
                    R.layout.list_comment_detail,
                    //this needs to be fixed (itemKey)
                    mDatabase.child("item-comments/"+itemKey)
            )
            {
                @Override
                protected void populateView(View v, Comment com, int position) {
                    ImageView userPhoto = (ImageView) v.findViewById(R.id.comment_user_photo);
                    TextView comment = (TextView) v.findViewById(R.id.comment_user_comment);
                    TextView name = (TextView) v.findViewById(R.id.comment_user_name);
                    comment.setText(com.getComment());
                    name.setText(com.getName());
                    Log.d(TAG, com.getComment());
                    Log.d(TAG, "Photo url:" + com.getPhotoUrl());
                    userPhoto.setImageURI(Uri.parse(com.getPhotoUrl()));
                    final Bitmap bitmap = getBitmapFromMemCache(com.getPhotoUrl());
                    if (bitmap != null) {
                        userPhoto.setImageBitmap(bitmap);
                    } else {
                        new GetProfilePhoto(userPhoto).execute(com.getPhotoUrl());
                    }
//                    // Photo profiles swap quickly, problem maybe with async task and global variable currentBitmap....
//                    Log.d(TAG,"Current bitmap: " + currentBitmap);
//                    userPhoto.setImageBitmap(currentBitmap);
                }
            };
            commentList.setAdapter(adapter);


        }
    }

    private void initView() {

        outsideLL = (LinearLayout) findViewById(R.id.outside_ll);
        itemInfoRL = (RelativeLayout) outsideLL.findViewById(R.id.item_info_rl);
        userPhoto = (ImageView) itemInfoRL.findViewById(R.id.user_photo_details);
        description = (TextView) itemInfoRL.findViewById(R.id.description_details);
        price = (TextView) itemInfoRL.findViewById(R.id.cost_details);
        name = (TextView) itemInfoRL.findViewById(R.id.name_details);
        details = (TextView) itemInfoRL.findViewById(R.id.details_details);
        buySellDetails = (TextView) itemInfoRL.findViewById(R.id.buy_sell);


        comments = (ListView) outsideLL.findViewById(R.id.comments);
        bottomLL = (LinearLayout) findViewById(R.id.bottom_ll);
        writeComment = (EditText) findViewById(R.id.write_comments);
        submitComment = (Button) findViewById(R.id.submit_comments);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        description.setText(extras.getString("EXTRA_DESCRIPTION"));
        price.setText(extras.getString("EXTRA_PRICE"));
        name.setText(extras.getString("EXTRA_NAME"));
        details.setText(extras.getString("EXTRA_DETAILS"));
        buySellDetails.setText(extras.getString("EXTRA_BUY_SELL"));
        if (buySellDetails.getText().toString().equals("Buying for")){
            buySellDetails.setTextColor(Color.parseColor("#E91E63"));
        } else {
            buySellDetails.setTextColor(Color.parseColor("#4CAF50"));
        }
        //this needs to be fixed (itemKey)

        itemKey = extras.getString("EXTRA_ITEM_KEY");
        new GetProfilePhoto(userPhoto).execute(extras.getString("EXTRA_PHOTOURL"));


    }
    private void logOut (View view) {
        mFirebaseAuth.signOut();
        FacebookSdk.sdkInitialize(getApplicationContext());
        LoginManager.getInstance().logOut();
        loadLoginView();
    }

    private void loadLoginView() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key,bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }
}
