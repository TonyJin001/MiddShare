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

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
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


            Button bLogOut = (Button) findViewById(R.id.logout_button);
            bLogOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logOut(v);
                }
            });

            final ListView commentList = (ListView) findViewById(R.id.comments);

            FirebaseListAdapter<Comment> adapter = new FirebaseListAdapter<Comment>(
                    this,
                    Comment.class,
                    R.layout.list_comment_detail,
                    //mDatabase.child("service_exchange_items")
            ) //tony's copy pasted code
            {
                @Override
                protected void populateView(View v, Comment com, int position) {
                    ImageView userPhoto = (ImageView) v.findViewById(R.id.comment_user_photo);
                    TextView comment = (TextView) v.findViewById(R.id.comment_user_comment);
                    TextView name = (TextView) v.findViewById(R.id.comment_user_name);
                    comment.setText(com.getComment());
                    name.setText(com.getName());
                    Log.d(TAG,com.getComment());
                    Log.d(TAG,"Photo url:" + com.getPhotoUrl());
                    userPhoto.setImageURI(Uri.parse(com.getPhotoUrl()));
                    new GetProfilePhoto(userPhoto).execute(com.getPhotoUrl());
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
