package cs701b.middshare;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.Date;

public class NotificationListener extends Service {
    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
    private final String TAG = "NotificationListener";

    public NotificationListener() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Calculates the time on the device.
        final long time = new Date().getTime();
        Log.d(TAG,"Time on machine: " + time);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Log.d(TAG,"in on start command");
        mDatabase.child("service_exchange_items").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                ServiceExchangeItemNoTime item = dataSnapshot.getValue(ServiceExchangeItemNoTime.class);
                Log.d(TAG,"Added Time: " + item.getTime()+"");

                // Only sends a notification to the user if the child is added after the app starts
                // and if the child was not added by the user him/herself
                if ((item.getTime() > time) && !mFirebaseUser.getDisplayName().equals(item.getName())) {
                    showNotification(item.getDescription(),item.getName(),item.getPhotoUrl(),item.getPrice(),item.getDetails());
                }
                if (mFirebaseUser.getDisplayName().equals(item.getName())){
                    Log.d(TAG,"Didn't show notification because user posted him/herself");
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return START_STICKY;
    }

    private void showNotification(String description, String user, String url, String price, String details) {
        Intent intent = new Intent(this,ServiceExchangeDetails.class);
        intent.addFlags((Intent.FLAG_ACTIVITY_CLEAR_TOP));
        Bundle extras = new Bundle();
        extras.putString("EXTRA_DESCRIPTION",description);
        extras.putString("EXTRA_PRICE",price);
        extras.putString("EXTRA_PHOTOURL",url);
        extras.putString("EXTRA_NAME", user);
        extras.putString("EXTRA_DETAILS",details);
        intent.putExtras(extras);
        PendingIntent pendeningIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(user + " requests on MiddShare")
                .setAutoCancel(true)
                .setContentText(description)
                .setSound(defaultSoundUri)
                .setContentIntent(pendeningIntent)
                .setPriority(Notification.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,notificationBuilder.build());
        Log.d(TAG,"notification shown");
    }
}
