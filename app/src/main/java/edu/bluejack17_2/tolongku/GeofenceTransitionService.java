package edu.bluejack17_2.tolongku;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class GeofenceTransitionService extends IntentService {

    private NotificationManager notificationManager;

    public GeofenceTransitionService(String name) {
        super(name);
    }

    public GeofenceTransitionService(){
        super("untitled");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Log.d("Geofence", "Handling Intent.");

        GeofencingEvent mGeofencingEvent = GeofencingEvent.fromIntent(intent);

        if(mGeofencingEvent.hasError()){
            String error = String.valueOf(mGeofencingEvent.getErrorCode());
            Toast.makeText(getApplicationContext(), "Error code =  " + error,
                    Toast.LENGTH_SHORT).show();
            Log.d("Geofence", "Error code = " + error);
            return;
        }

        int geofenceTransition = mGeofencingEvent.getGeofenceTransition();

        if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition
                == Geofence.GEOFENCE_TRANSITION_EXIT){
            List<Geofence> triggeringGeofences = mGeofencingEvent.getTriggeringGeofences();
            String geoTransDetails = getGeofenceTransitionDetails(geofenceTransition, triggeringGeofences);

            Location triggeringLocation = mGeofencingEvent.getTriggeringLocation();

            sendNotification(geoTransDetails);

            Toast.makeText(this,  "Kena!", Toast.LENGTH_SHORT).show();
            Log.d("Geofence", "In area of an existing Geofence!");
            Log.d("Geofence", geoTransDetails);
        }else{
            Toast.makeText(this, "Not of interest!", Toast.LENGTH_SHORT).show();
            Log.d("Geofence", "Geofence is not of interest.");
        }
    }

    private void createNotificationChannel(){
        // Create NotificationChannel for APi 26+
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = getString(R.string.notification_channel_name);
            String description = "Notification Channel of TolongKu App";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);

            // Register the channel
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendNotification(String msg){
        Context ctx = getApplicationContext();
        createNotificationChannel();
        notificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MapFragment.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ctx, "1")
                .setContentTitle("Dangerous Area Alert!")
                .setContentText("You have entered a dangerous area zone. Be aware.")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setSmallIcon(R.drawable.default_profile)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        mBuilder.setContentIntent(contentIntent);
        notificationManager.notify(1, mBuilder.build());
    }

    private String getGeofenceTransitionDetails(int geofenceTransition, List<Geofence> triggeringGeofences){
        ArrayList<String> triggerfenceList = new ArrayList<>();

        for(Geofence geofence : triggeringGeofences){
            triggerfenceList.add(geofence.getRequestId());
        }

        String status = null;

        if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER){
            status = "You are within 100m radius of a dangerous area marked by other users.";
        }else if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT){
            status = "You are exiting the dangerous area radius.";
        }
        return status;
//        return status + TextUtils.join(", ", triggerfenceList);
    }
}
