package cs701b.middshare;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.facebook.login.widget.ProfilePictureView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CreateNew extends BaseActivity {

    private DatabaseReference mDatabase;
    private String mUserId;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private final String TAG = "Create_New";
    private boolean buy = true;
    private TextView untilTime;
    private LinearLayout llValidTime;
    private EditText editValidHour;
    private TextView hour;
    private EditText editValidMinute;
    private EditText editExtra;
    private EditText editDescription;
    private TextView minute;
    private Button seSubmit;
    private long timeLimit = -1;
    private boolean priceConfirmed = false;
    private static final int SELECT_PHOTO = 100;
    private String encodedImage = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // log in again
            loadLoginView();
        } else {
            mUserId = mFirebaseUser.getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference();


            editDescription = (EditText) findViewById(R.id.edit_message_request);
            editDescription.requestFocus();
            final EditText editPrice = (EditText) findViewById(R.id.edit_message_cost);
            editExtra = (EditText) findViewById(R.id.edit_message_extrainfo);
            seSubmit = (Button) findViewById(R.id.submit_new);
            final RadioButton validFor = (RadioButton) findViewById(R.id.rb_valid_for);
            final RadioButton validUntil = (RadioButton) findViewById(R.id.rb_valid_until);
            final Button addImage = (Button) findViewById(R.id.add_image);

            untilTime = (TextView) findViewById(R.id.until_time);
            llValidTime = (LinearLayout) findViewById(R.id.ll_valid_time);
            editValidHour = (EditText) llValidTime.findViewById(R.id.valid_hour);
            hour = (TextView) llValidTime.findViewById(R.id.hour);
            editValidMinute = (EditText) llValidTime.findViewById(R.id.valid_minute);
            minute = (TextView) llValidTime.findViewById(R.id.minute);
            editValidHour.setVisibility(View.GONE);
            hour.setVisibility(View.GONE);
            editValidMinute.setVisibility(View.GONE);
            minute.setVisibility(View.GONE);


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
                    } else {
                        if (!editPrice.getText().toString().isEmpty()) {
                            String priceEntered = editPrice.getText().toString().substring(1);
                            if (!priceEntered.isEmpty()) {
                                if (Integer.parseInt(priceEntered) > 5000 && !priceConfirmed) {
                                    new AlertDialog.Builder(CreateNew.this)
                                            .setTitle("Price too high")
                                            .setMessage("The price you entered, " + getString(R.string.currency) + priceEntered + ", is higher than" +
                                                    " what we normally expect in our app. Please confirm that you entered a reasonable price.")
                                            .setPositiveButton(R.string.confirm_price, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    priceConfirmed = true;
                                                }
                                            })
                                            .setNegativeButton(R.string.change_price, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    editPrice.requestFocus();
                                                    editPrice.setText("$");
                                                    editPrice.setSelection(editPrice.getText().length());
                                                }
                                            })
                                            .show();
                                }
                            }
                        }
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

                    // If the user entered how long the request is valid for
                    if (validFor.isChecked() && (!(editValidHour.getText().toString().equals("")) || !(editValidMinute.getText().toString().equals("")))){
                        Calendar currentTime = Calendar.getInstance();
                        currentTime.setTime(new Date());
                        Log.d(TAG,"current time before adding hour"+currentTime.getTime());
                        if (!editValidHour.getText().toString().equals("")){
                            currentTime.add(Calendar.HOUR_OF_DAY,Integer.parseInt(editValidHour.getText().toString()));
                            Log.d(TAG,"current time before adding minute"+currentTime.getTime());
                        }
                        if (!editValidMinute.getText().toString().equals("")){
                            currentTime.add(Calendar.MINUTE,Integer.parseInt(editValidMinute.getText().toString()));
                            Log.d(TAG,"current time final"+currentTime.getTime());
                        }
                        timeLimit = currentTime.getTimeInMillis();
                    }

                    if (validFor.isChecked() && ((editValidHour.getText().toString().equals("")) && (editValidMinute.getText().toString().equals("")))) {
                        timeLimit = -1;
                    }

                    if (validUntil.isChecked() && untilTime.getText().toString().equals("")){
                        timeLimit = -1;
                    }

                    String photoUrl = "";
                    for (UserInfo profile : mFirebaseUser.getProviderData()) {
                        String uid = profile.getUid();
                        photoUrl = "http://graph.facebook.com/" + uid + "/picture?type=large";
                    }


                    Log.d(TAG,mFirebaseUser.getPhotoUrl().toString());
                    ServiceExchangeItem newItem = new ServiceExchangeItem(editDescription.getText().toString(), cost,
                            mFirebaseUser.getPhotoUrl().toString(),mFirebaseUser.getDisplayName(),editExtra.getText().toString(),ServerValue.TIMESTAMP, buy, timeLimit, encodedImage);
                    Map<String, Object> newItemValues = newItem.toMap();

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/service_exchange_items/" + key, newItemValues);
                    childUpdates.put("/user-service_exchange_items/" + mUserId + "/" + key, newItemValues);

                    mDatabase.updateChildren(childUpdates);
                    editDescription.setText("");
                    editPrice.setText("");
                    editExtra.setText("");
                    finish();
                }
            });

            addImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
//                    InputStream imageStream = null;
//                    try {
//                        imageStream = getContentResolver().openInputStream(selectedImage);
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
                    Bitmap imageSelected = null;
                    try {
                        imageSelected = decodeUri(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
                    imageSelected.compress(Bitmap.CompressFormat.PNG, 100, bYtE);
                    imageSelected.recycle();
                    byte[] byteArray = bYtE.toByteArray();
                    encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                }
        }

    }

    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 160;

        // Find the correct scale value. It should be the power of 2.
        double width_tmp = o.outWidth, height_tmp = o.outHeight;
        double scale = 1;
        while (true) {
            if (width_tmp / 1.1 < REQUIRED_SIZE
                    || height_tmp / 1.1 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 1.1;
            height_tmp /= 1.1;
            scale *= 1.1;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = (int) scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);

    }

    public void onBuySellRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.buy_boolean:
                if (checked) {
                    buy = true;
                    break;
                }
            case R.id.sell_boolean:
                if (checked) {
                    buy = false;
                    break;
                }
        }
    }

    public void onTimeLimitRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        final Calendar newDate = Calendar.getInstance();


        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.rb_valid_until:
                if (checked) {
                    // Hide keyboard
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    final Calendar newCalendar = Calendar.getInstance();
                    final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM-dd-yyyy hh:mm aa", Locale.US);
                    editValidHour.setVisibility(View.GONE);
                    hour.setVisibility(View.GONE);
                    editValidMinute.setVisibility(View.GONE);
                    minute.setVisibility(View.GONE);
                    untilTime.setVisibility(View.VISIBLE);

                    DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                        public void onDateSet(DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {

                            TimePickerDialog timePicker = new TimePickerDialog(CreateNew.this, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                    newDate.set(year,monthOfYear,dayOfMonth,i,i1,0);
                                    untilTime.setText(dateFormatter.format(newDate.getTime()));
                                    Log.d(TAG,newDate.getTimeInMillis()+"");
                                    Log.d(TAG,newDate.getTime().toString());
                                    timeLimit = newDate.getTimeInMillis();
                                }
                            },newCalendar.get(Calendar.HOUR_OF_DAY),newCalendar.get(Calendar.MINUTE),false);
                            timePicker.show();
                        }

                    },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                    datePicker.show();
                    break;
                }
            case R.id.rb_valid_for:
                if (checked) {
                    //Hide keyboard
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    untilTime.setVisibility(View.INVISIBLE);
                    editValidHour.setVisibility(View.VISIBLE);
                    hour.setVisibility(View.VISIBLE);
                    editValidMinute.setVisibility(View.VISIBLE);
                    minute.setVisibility(View.VISIBLE);

                    editValidHour.requestFocus();
                    // Show keyboard focusing on number of hours
                    InputMethodManager keyboard = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.showSoftInput(editValidHour,0);
                    break;
                }
        }
    }




    public void loadLoginView() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
