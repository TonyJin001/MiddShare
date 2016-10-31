package cs701b.middshare;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class CreateNew extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String mUserId;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private final String TAG = "Create_New";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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


            final EditText editDescription = (EditText) findViewById(R.id.edit_message_request);
            final EditText editPrice = (EditText) findViewById(R.id.edit_message_cost);
            final EditText editExtra = (EditText) findViewById(R.id.edit_message_extrainfo);
            final Button seSubmit = (Button) findViewById(R.id.submit_new);
            seSubmit.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    String key = mDatabase.child("items").push().getKey();
                    ServiceExchangeItem newItem = new ServiceExchangeItem(editDescription.getText().toString(), editPrice.getText().toString(),
                            mFirebaseUser.getPhotoUrl().toString());
                    Map<String, Object> newItemValues = newItem.toMap();

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/service_exchange_items/" + key, newItemValues);
                    childUpdates.put("/user-service_exchange_items/" + mUserId + "/" + key, newItemValues);

                    mDatabase.updateChildren(childUpdates);
                    editDescription.setText("");
                    editPrice.setText("");
                    editExtra.setText("");
                }
            });
        }
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.buy_boolean:
                if (checked)
                    // Pirates are the best
                    break;
            case R.id.sell_boolean:
                if (checked)
                    // Ninjas rule
                    break;
        }
    }
    private void loadLoginView() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
