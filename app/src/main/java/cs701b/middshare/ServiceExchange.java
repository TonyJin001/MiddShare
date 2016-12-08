package cs701b.middshare;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.LruCache;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.R.attr.bitmap;
import static java.lang.System.err;

public class ServiceExchange extends AppCompatActivity {

    private ProfilePictureView profilePictureView;
    private TextView userNameSelf;
    private DatabaseReference mDatabase;
    private String mUserId;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private final String TAG = "Service_Exchange";
    private Bitmap currentBitmap = null;
    private LruCache<String, Bitmap> mMemoryCache;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_exchange);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // hasn't verified isRegistered... yet


        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ServiceExchange.this, CreateNew.class);
                startActivity(intent);
            }
        });

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
                profilePictureView = (ProfilePictureView) findViewById(R.id.profile_pic);
                profilePictureView.setProfileId(uid);
                photoUrl = "http://graph.facebook.com/" + uid + "/picture?width=800&height=600";
                Log.d(TAG,photoUrl);
                email = profile.getEmail();
                name = profile.getDisplayName();
            }

            View.OnClickListener goToUserPage = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ServiceExchange.this, UserPage.class);
                    startActivity(intent);
                }
            };

            profilePictureView.setOnClickListener(goToUserPage);

            userNameSelf = (TextView) findViewById(R.id.se_user_name);
            userNameSelf.setText(name);
            userNameSelf.setOnClickListener(goToUserPage);

            final String finalPhotoUrl = photoUrl;

            if (!mDatabase.child("users").child(mUserId).child("name").equals(mFirebaseUser.getDisplayName())) {
                mDatabase.child("users").child(mUserId).child("name").setValue(mFirebaseUser.getDisplayName());
                Log.d(TAG, "update name");
            }
            if (!mDatabase.child("users").child(mUserId).child("email").equals(mFirebaseUser.getEmail())) {
                mDatabase.child("users").child(mUserId).child("email").setValue(mFirebaseUser.getEmail());
                Log.d(TAG, "update email");
            }
            if (!mDatabase.child("users").child(mUserId).child("photo").equals(photoUrl)) {
                if (!photoUrl.equals(null)) {
                    mDatabase.child("users").child(mUserId).child("photo").setValue(photoUrl);
                    Log.d(TAG, "update photo");
                } else {
                    Log.d(TAG, "photo null");
                }
            }

//            Button bLogOut = (Button) findViewById(R.id.logout_button);
//            bLogOut.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    logOut(v);
//                }
//            });

            final ListView seList = (ListView) findViewById(R.id.service_list);

            final FirebaseListAdapter<ServiceExchangeItemNoTime> adapter = new FirebaseListAdapter<ServiceExchangeItemNoTime>(
                    this,
                    ServiceExchangeItemNoTime.class,
                    R.layout.list_item_service_exchange,
                    mDatabase.child("service_exchange_items")
            ) {
                @Override
                protected void populateView(View v, ServiceExchangeItemNoTime model, int position) {
                    DatabaseReference ref = this.getRef(position);
                    if (model.getTimeLimit() < Calendar.getInstance().getTimeInMillis() && model.getTimeLimit()!=-1){
                        Log.v(TAG,"Time limit passed");
                        ref.removeValue();
                    }
                    ImageView userPhoto = (ImageView) v.findViewById(R.id.user_photo);
                    TextView description = (TextView) v.findViewById(R.id.description);
                    TextView price = (TextView) v.findViewById(R.id.cost);
                    TextView userName = (TextView) v.findViewById(R.id.user_name);
                    TextView buySell = (TextView) v.findViewById(R.id.buy_sell);
                    description.setText(model.getDescription());
                    userName.setText(model.getName());
                    if (model.isBuy()){
                        buySell.setText("Buying for");
                        buySell.setTextColor(Color.parseColor("#E91E63"));
                    } else {
                        buySell.setText("Selling for");
                        buySell.setTextColor(Color.parseColor("#4CAF50"));
                    }

                    Log.d(TAG, model.getDescription() + "@" + model.getPrice());
                    price.setText(model.getPrice());

                    Log.d(TAG,model.getPhotoUrl());
                    final Bitmap bitmap = getBitmapFromMemCache(model.getPhotoUrl());
                    if (bitmap != null) {
                        Log.d(TAG,"isnot null");
                        userPhoto.setImageBitmap(bitmap);
                    } else {
                        Log.d(TAG,"isnull");
//                        new GetProfilePhoto(userPhoto).execute(model.getPhotoUrl());
                        Picasso.with(ServiceExchange.this).load(model.getPhotoUrl()).into(userPhoto);
                    }
//                    userPhoto.setImageURI(Uri.parse(model.getPhotoUrl()));

//                    // Photo profiles swap quickly, problem maybe with async task and global variable currentBitmap....
//                    Log.d(TAG,"Current bitmap: " + currentBitmap);
//                    userPhoto.setImageBitmap(currentBitmap);
                }
            };
            seList.setAdapter(adapter);

            seList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ServiceExchangeItemNoTime itemDetails = (ServiceExchangeItemNoTime) parent.getAdapter().getItem(position);
                    String itemDescription = itemDetails.getDescription();
                    String itemPrice = itemDetails.getPrice();
                    String itemPhotoUrl = itemDetails.getPhotoUrl();
                    String itemName = itemDetails.getName();
                    String itemDetailedInfo = itemDetails.getDetails();
                    String itemKey = adapter.getRef(position).getKey();
                    String itemBuySell = "";
                    if (itemDetails.isBuy()){
                        itemBuySell = "Buying for";
                    } else {
                        itemBuySell = "Selling for";
                    }
                    Log.d(TAG,itemDescription +"\t" + itemPrice + "\t" + itemPhotoUrl);
                    Intent intent = new Intent(ServiceExchange.this,ServiceExchangeDetails.class);
                    Bundle extras = new Bundle();
                    extras.putString("EXTRA_DESCRIPTION", itemDescription);
                    extras.putString("EXTRA_PRICE", itemPrice);
                    extras.putString("EXTRA_PHOTOURL", itemPhotoUrl);
                    extras.putString("EXTRA_NAME", itemName);
                    extras.putString("EXTRA_DETAILS",itemDetailedInfo);
                    extras.putString("EXTRA_ITEM_KEY",itemKey);
                    extras.putString("EXTRA_BUY_SELL",itemBuySell);

                    Log.d(TAG, itemKey);

                    intent.putExtras(extras);
                    startActivity(intent);
                }
            });
//            seList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//                @Override
//                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                    ServiceExchangeItemNoTime itemDetails = (ServiceExchangeItemNoTime) parent.getAdapter().getItem(position);
//                    DatabaseReference globalItemRef = adapter.getRef(position);
//                    String itemRefKey = adapter.getRef(position).getKey();
//                    DatabaseReference itemRef = (DatabaseReference) mDatabase.child("user-service_exchange_items/" + mUserId.toString() + "/" + itemRefKey);
//                    Log.d(TAG, "position is " + itemRef.toString());
//                    itemRef.removeValue();
//                    globalItemRef.removeValue();
//                    return true;
//                }
//            });




            startService(new Intent(this, NotificationListener.class));

        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.global_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_log_out:
                logOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateProfile(String name, String email, Uri uri) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(uri)
                .build();

        mFirebaseUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "User profile updated");
                }
            }
        });

        mFirebaseUser.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "User email updated");
                }
            }
        });
    }


    private void logOut() {
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
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("ServiceExchange Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }


    class GetProfilePhoto extends AsyncTask<String, Void, Bitmap> {

        private Exception exception;
        private ImageView bmImage;
        private final String TAG = "Get Profile Photo";

        public GetProfilePhoto(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urlStr) {
            try {
                Bitmap bitmap = null;
                try {
                    URL url = new URL(urlStr[0]);
                    Log.d(TAG,urlStr[0]);
                    bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    addBitmapToMemoryCache(urlStr[0], bitmap);
                } catch (NullPointerException e) {
                    Log.e(TAG, "nullpointer when setting user image");
                } catch (IOException e) {
                    Log.e(TAG, "IO exception when setting user image");
                }
                return bitmap;
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }

        protected void onPostExecute(Bitmap bitmap) {
            bmImage.setImageBitmap(bitmap);
        }
    }


}
