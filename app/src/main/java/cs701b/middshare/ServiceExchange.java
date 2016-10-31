package cs701b.middshare;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.err;

public class ServiceExchange extends AppCompatActivity {

    private ProfilePictureView profilePictureView;
    private DatabaseReference mDatabase;
    private String mUserId;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private final String TAG = "Service_Exchange";
    private Bitmap currentBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_exchange);

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

            Button bLogOut = (Button) findViewById(R.id.logout_button);
            bLogOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logOut(v);
                }
            });

            final ListView seList = (ListView) findViewById(R.id.service_list);

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

            final EditText editDescription = (EditText) findViewById(R.id.edit_description);
            final EditText editPrice = (EditText) findViewById(R.id.edit_price);
            final Button seSubmit = (Button) findViewById(R.id.se_submit);
            seSubmit.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {

                    String key = mDatabase.child("items").push().getKey();
                    ServiceExchangeItem newItem = new ServiceExchangeItem(editDescription.getText().toString(),editPrice.getText().toString(),
                            mFirebaseUser.getPhotoUrl().toString());
                    Map<String,Object> newItemValues = newItem.toMap();

                    Map<String,Object> childUpdates = new HashMap<>();
                    childUpdates.put("/service_exchange_items/" + key, newItemValues);
                    childUpdates.put("/user-service_exchange_items/" + mUserId + "/" + key, newItemValues);

                    mDatabase.updateChildren(childUpdates);
                    editDescription.setText("");
                    editPrice.setText("");
                }
            });
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
                    Log.d(TAG,"User profile updated");
                }
            }
        });

        mFirebaseUser.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG,"User email updated");
                }
            }
        });
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




}
