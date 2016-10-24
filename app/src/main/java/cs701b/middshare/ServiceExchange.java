package cs701b.middshare;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static java.lang.System.err;

public class ServiceExchange extends AppCompatActivity {

    private ProfilePictureView profilePictureView;
    private DatabaseReference mDatabase;
    private String mUserId;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

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

            for (UserInfo profile : mFirebaseUser.getProviderData()) {
                String uid = profile.getUid();
                profilePictureView = (ProfilePictureView) findViewById(R.id.profile_pic);
                profilePictureView.setProfileId(uid);
            }

            Button bLogOut = (Button) findViewById(R.id.logout_button);
            bLogOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logOut(v);
                }
            });

            mDatabase = FirebaseDatabase.getInstance().getReference();
            final ListView seList = (ListView) findViewById(R.id.service_list);

            FirebaseListAdapter<ServiceExchangeItem> adapter = new FirebaseListAdapter<ServiceExchangeItem>(
                    this,
                    ServiceExchangeItem.class,
                    R.layout.list_item_service_exchange,
                    mDatabase.child("users").child(mUserId).child("items")
            ) {
                @Override
                protected void populateView(View v, ServiceExchangeItem model, int position) {
//                    ImageView userPhoto = (ImageView) v.findViewById(R.id.user_photo);
                    TextView description = (TextView) v.findViewById(R.id.description);
                    TextView price = (TextView) v.findViewById(R.id.cost);
                    description.setText(model.description);
                    Log.d("ServiceExchange",model.description+"@"+model.price);
                    price.setText(R.string.currency+model.price);
                }
            };
            seList.setAdapter(adapter);

            final EditText editDescription = (EditText) findViewById(R.id.edit_description);
            final EditText editPrice = (EditText) findViewById(R.id.edit_price);
            final Button seSubmit = (Button) findViewById(R.id.se_submit);
            seSubmit.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    mDatabase.child("users").child(mUserId).child("items")
                            .push().setValue(new ServiceExchangeItem(editDescription.getText().toString(),editPrice.getText().toString()));
                    editDescription.setText("");
                    editPrice.setText("");
                }
            });
        }
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
