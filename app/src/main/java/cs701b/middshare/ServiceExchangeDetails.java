package cs701b.middshare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ServiceExchangeDetails extends AppCompatActivity {

    private ImageView userPhoto;
    private TextView description;
    private TextView price;
    private ListView comments;
    private EditText writeComment;
    private Button submitComment;
    private LinearLayout outsideLL;
    private LinearLayout itemInfoLL;
    private LinearLayout buttomLL;

    public ServiceExchangeDetails() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_exchange_details);
        initView();
    }

    private void initView() {
        outsideLL = (LinearLayout) findViewById(R.id.outside_ll);
        itemInfoLL = (LinearLayout) outsideLL.findViewById(R.id.item_info_ll);
        userPhoto = (ImageView) itemInfoLL.findViewById(R.id.user_photo_details);
        description = (TextView) itemInfoLL.findViewById(R.id.description_details);
        price = (TextView) itemInfoLL.findViewById(R.id.cost_details);
        comments = (ListView) outsideLL.findViewById(R.id.comments);
        buttomLL = (LinearLayout) findViewById(R.id.buttom_ll);
        writeComment = (EditText) findViewById(R.id.write_comments);
        submitComment = (Button) findViewById(R.id.submit_comments);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        description.setText(extras.getString("EXTRA_DESCRIPTION"));
        price.setText(extras.getString("EXTRA_PRICE"));
        new GetProfilePhoto(userPhoto).execute(extras.getString("EXTRA_PHOTOURL"));
    }
}
