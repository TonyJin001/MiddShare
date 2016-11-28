package cs701b.middshare;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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


public class UserPage extends AppCompatActivity {
    private final String TAG = "User Page";
    private DatabaseReference mDatabase;
    private String mUserId;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private ProfilePictureView profilePictureView;
    private Bitmap currentBitmap = null;
    private LruCache<String,Bitmap> mMemoryCache;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory()/1024);
        final int cacheSize = maxMemory/8;
        mMemoryCache = new LruCache<String,Bitmap> (cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount()/1024;
            }
        };
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser == null) {
            // log in again
            loadLoginView();
        } else {
            mUserId = mFirebaseUser.getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            String photoUrl = "";
            String email = "";
            String name = "";
            for (UserInfo profile : mFirebaseUser.getProviderData()) {
                String uid = profile.getUid();
                profilePictureView = (ProfilePictureView) findViewById(R.id.user_page_profile_pic);
                profilePictureView.setProfileId(uid);
                photoUrl = "http://graph.facebook.com/" + uid + "/picture?width=800&height=600";
                email = profile.getEmail();
                name = profile.getDisplayName();
            }

            final String finalPhotoUrl = photoUrl;

            if (!mDatabase.child("users").child(mUserId).child("name").equals(mFirebaseUser.getDisplayName())) {
                mDatabase.child("users").child(mUserId).child("name").setValue(mFirebaseUser.getDisplayName());
                Log.d(TAG,"update name");
            }
            if (!mDatabase.child("users").child(mUserId).child("email").equals(mFirebaseUser.getEmail())){
                mDatabase.child("users").child(mUserId).child("email").setValue(mFirebaseUser.getEmail());
                Log.d(TAG,"update email");
            }
            if (!mDatabase.child("users").child(mUserId).child("photo").equals(photoUrl)){
                if (!photoUrl.equals(null)) {
                    mDatabase.child("users").child(mUserId).child("photo").setValue(photoUrl);
                    Log.d(TAG,"update photo");
                } else {
                    Log.d(TAG, "photo null");
                }
            }

            Button bLogOut = (Button) findViewById(R.id.user_page_logout_button);
            bLogOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logOut(v);
                }
            });

            final ListView upList = (ListView) findViewById(R.id.user_page_service_list);

            final FirebaseListAdapter<ServiceExchangeItemNoTime> adapter = new FirebaseListAdapter<ServiceExchangeItemNoTime>(
                    this,
                    ServiceExchangeItemNoTime.class,
                    R.layout.list_item_service_exchange,
                    mDatabase.child("user-service_exchange_items/" + mUserId.toString())
            ) {
                @Override
                protected void populateView(View v, ServiceExchangeItemNoTime model, int position) {
                    ImageView userPhoto = (ImageView) v.findViewById(R.id.user_photo);
                    TextView description = (TextView) v.findViewById(R.id.description);
                    TextView price = (TextView) v.findViewById(R.id.cost);
                    TextView userName = (TextView) v.findViewById(R.id.user_name)
                            ;
                    description.setText(model.getDescription());
                    userName.setText(model.getName());

                    Log.d(TAG,model.getDescription()+"@"+model.getPrice());
                    price.setText(model.getPrice());
                    Log.d(TAG,"Photo url:" + model.getPhotoUrl());
                    final Bitmap bitmap = getBitmapFromMemCache(model.getPhotoUrl());
                    if (bitmap != null) {
                        userPhoto.setImageBitmap(bitmap);
                    } else {
                        new GetProfilePhoto(userPhoto).execute(model.getPhotoUrl());
                    }
//                    userPhoto.setImageURI(Uri.parse(model.getPhotoUrl()));

//                    // Photo profiles swap quickly, problem maybe with async task and global variable currentBitmap....
//                    Log.d(TAG,"Current bitmap: " + currentBitmap);
//                    userPhoto.setImageBitmap(currentBitmap);
                }
            };
            upList.setAdapter(adapter);
        }
    }

    private void loadLoginView() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void logOut (View view) {
        mFirebaseAuth.signOut();
        FacebookSdk.sdkInitialize(getApplicationContext());
        LoginManager.getInstance().logOut();
        loadLoginView();
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

