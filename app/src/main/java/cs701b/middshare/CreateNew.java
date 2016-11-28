package cs701b.middshare;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class CreateNew extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String mUserId;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private final String TAG = "Create_New";
    private String buyOrSell = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new);

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

            editPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b){
                        if (editPrice.getText().toString().equals("")) {
                            editPrice.setText(R.string.currency);
                            editPrice.setSelection(editPrice.getText().length());
                        }
                        InputMethodManager keyboard = (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                        keyboard.showSoftInput(editPrice,0);
                    }
                }
            });

            seSubmit.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    String key = mDatabase.child("items").push().getKey();
                    String cost = editPrice.getText().toString();
                    // Don't store the dollar sign or any preset currency sign
                    if (cost.startsWith(getString(R.string.currency))){
                        cost = cost.substring(1);
                    }
                    ServiceExchangeItem newItem = new ServiceExchangeItem(editDescription.getText().toString(), cost,
                            mFirebaseUser.getPhotoUrl().toString(),mFirebaseUser.getDisplayName(),editExtra.getText().toString(),ServerValue.TIMESTAMP);
                    Map<String, Object> newItemValues = newItem.toMap();

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/service_exchange_items/" + key, newItemValues);
                    childUpdates.put("/user-service_exchange_items/" + mUserId + "/" + key, newItemValues);

                    mDatabase.updateChildren(childUpdates);
                    editDescription.setText("");
                    editPrice.setText("");
                    editExtra.setText("");

                    Toast.makeText(CreateNew.this, "" + buyOrSell, Toast.LENGTH_SHORT).show();

                    finish();
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
                if (checked) {
                    buyOrSell = "buy";
                    break;
                }
            case R.id.sell_boolean:
                if (checked) {
                    buyOrSell = "sell";
                    break;
                }
        }
    }
    private void loadLoginView() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
