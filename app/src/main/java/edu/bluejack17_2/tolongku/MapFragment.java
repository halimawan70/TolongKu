package edu.bluejack17_2.tolongku;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
//import android.os.Message;
import android.os.Messenger;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import java.util.Vector;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static GoogleMap mMap;
    private MapView mapView;
    private LatLng mDefaultLocation;
    private View view;

    private GeofencingClient mGeofencingClient;
    private Vector<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;


    public static final int NEW_MARKER_REQUEST_CODE = 2;
    public static final int MARKER_ACTION_REQUEST_CODE = 3;
    public static String GEOFENCE_REQUEST_CODE = "1";

    public final static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public final static int AREA_DANGEROUS = 1;
    public final static int AREA_SHELTER = 2;
    public final static int AREA_HELP = 3;
    public final static int AREA_MARKING_CANCELLED = 13;
    public final int MY_PERMISSIONS_REQUEST_PHONE_CALL = 0;

    private Circle currCircle;
    private Geofence currGeofence;
    private Marker currMarker;
    private LatLng currPosition;
    private User markerCreator;

    private DatabaseReference userRef;
    private DatabaseReference rootRef;
    private User currentUser;

    private User toCreateUser;

    LocationManager locationManager;

    Vector<MarkerDatas> md;
    Vector<Message> messages;

    private int markerId;
    private String phoneNumber;

    Intent intent;

    private boolean mLocationPermissionGranted = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGeofencingClient = LocationServices.getGeofencingClient(this.getContext());
        mGeofenceList = new Vector<Geofence>();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(MainActivity.authID);
        rootRef = FirebaseDatabase.getInstance().getReference();
        md = new Vector<>();
        messages = new Vector<>();
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User u = dataSnapshot.getValue(User.class);
                if(u.getUserID().compareTo(MainActivity.authID)==0)
                {
                    currentUser = u;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public static Circle circleLocation(int radius, LatLng location, int strokeColor, int fillColor){

        if(mMap == null){
            Log.d("Geofence Circling", "Map have not been initialized.");
            return null;
        }

        CircleOptions circleOptions = new CircleOptions().
                center(location)
                .strokeColor(strokeColor)
                .fillColor(fillColor)
                .radius(radius);

        return mMap.addCircle(circleOptions);
    }

    private BitmapDescriptor getMarkerIcon(String color){
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }

    private Marker addMarker(LatLng position, String title, String snippet){
        Marker marker = mMap.addMarker(new MarkerOptions().position(position)
                .title(title));

        marker.setSnippet(snippet);

        return marker;
    }

    private Marker addMarker(LatLng position, String title, String snippet, String color){
        Marker marker = mMap.addMarker(new MarkerOptions().position(position)
                .title(title).icon(getMarkerIcon(color)));

        marker.setSnippet(snippet);

        return marker;
    }

    private boolean checkPermission(){
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            getLocationPermission();
            return false;
        }
        return true;
    }

    public void resetCurrentDeleteTarget(){
        currGeofence = null;
        currCircle = null;
        currPosition = null;
        currMarker = null;
        markerCreator = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(!checkPermission())
            return;

        if(data == null)
        {
            Toast.makeText(getContext(), "Action Cancelled.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(requestCode == NEW_MARKER_REQUEST_CODE){
            final int status = data.getIntExtra("status", -1);
            final int radius = data.getIntExtra("radius", -1);
            Log.d("Map", "Add New Marker Status: " + status);
            final LatLng position = new LatLng(data.getDoubleExtra("latitude", -1),
                    data.getDoubleExtra("longitude", -1));

            rootRef.child("markerCount").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!checkPermission())
                        return;
                    markerId = ((Long)dataSnapshot.getValue()).intValue();
                    markerId++;
                    if(status != -1){

                        if(status == AREA_DANGEROUS){

                            Marker marker = addMarker(position, "Dangerous Area",
                                    "This place is marked as dangerous", "#c62828");

                            Log.d("Map", "Dangerous marker successfully placed.");

                            Geofence geofence = new Geofence.Builder().setRequestId(GEOFENCE_REQUEST_CODE).
                                    setCircularRegion(position.latitude, position.longitude, radius)
                                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                            Geofence.GEOFENCE_TRANSITION_EXIT)
                                    .build();

                            GEOFENCE_REQUEST_CODE = String.valueOf((Integer.parseInt(GEOFENCE_REQUEST_CODE)
                                    + 1));

                            mGeofenceList.add(geofence);

                            mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("Geofence", "Success!");
                                        }
                                    }).addOnFailureListener(getActivity(),
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("Geofence", "Failed!");
                                        }
                                    });

                            Circle circle = circleLocation(radius, position,
                                    Color.argb(255 ,183, 28, 28),
                                    Color.argb(50 ,244,67,54));

                            if(currentUser != null)
                            {
                                marker.setTag(new MarkerData(markerId,currentUser, MarkerData.DANGEROUS, marker,
                                        position, circle, geofence));
                            }
                            else
                            {
                                Log.d("Map","Current User is null!");
                            }

                        }else if(status == AREA_SHELTER){

                            Marker marker = addMarker(position, "Shelter Area",
                                    "This place is marked as a shelter", "#1976D2");

                            Circle circle = circleLocation(radius, position,
                                    Color.argb(255,25,118,210),
                                    Color.argb(50 ,33,150,243));

                            Log.d("Map", "Shelter marker successfully placed.");


                            if(currentUser != null)
                            {
                                marker.setTag(new MarkerData(markerId,currentUser, MarkerData.HELP, marker, position, circle));
                            }
                            else
                            {
                                Log.d("Map","Current User is null!");
                            }
                        }else if(status == AREA_HELP){

                            Marker marker = addMarker(position, "Help Area",
                                    "This place is offering help", "#1976D2");

                            Circle circle = circleLocation(radius, position,
                                    Color.argb(255,46,125,50),
                                    Color.argb(50 ,76,175,80));

                            Log.d("Map", "Help marker successfully placed.");

                            if(currentUser != null)
                            {
                                marker.setTag(new MarkerData(markerId,currentUser, MarkerData.HELP, marker, position, circle));
                            }
                            else
                            {
                                Log.d("Map","Current User is null!");
                            }
                        }
                        DatabaseReference markerRef = FirebaseDatabase.getInstance().getReference().child("MarkerDatas").child(String.valueOf(markerId));

                        markerRef.child("userId").setValue(MainActivity.authID);
                        markerRef.child("areaStatus").setValue(status);
                        markerRef.child("radius").setValue(radius);
                        markerRef.child("latitude").setValue(String.valueOf(position.latitude));
                        markerRef.child("longitude").setValue(String.valueOf(position.longitude));
                        markerRef.child("markerId").setValue(markerId);

                        rootRef.child("markerCount").setValue(markerId);
                    }else{
                        Log.d("Map", "Status return was erroneous.");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }else if(requestCode == MARKER_ACTION_REQUEST_CODE){

            int action = data.getIntExtra("action", -1);

            if(action != -1){

                if(action == MarkerActions.REMOVE_CIRCLE){
                    Log.d("Map",markerCreator.getUserID()+":"+MainActivity.authID);
                    if(markerCreator.getUserID().compareTo(MainActivity.authID)!=0)
                    {
                        Toast.makeText(getContext(), "Could not delete other user's marker!",
                                Toast.LENGTH_SHORT).show();
                        resetCurrentDeleteTarget();
                        return;
                    }
                    Circle circle = currCircle;
                    Marker marker = currMarker;

                    if(circle != null){
                        circle.remove();
                    }else{
                        Log.d("Map", "Circle is already NULL!");
                    }

                    rootRef.child("MarkerDatas").child(""+((MarkerData)marker.getTag()).getMarkerId()).removeValue();

                    if(marker != null){
                        marker.remove();
                    }else{
                        Log.d("Map", "Marker is already NULL!");
                    }

                    resetCurrentDeleteTarget();

                    Log.d("Map","Successfully removed Marker and Circle!");
                }else if(action == MarkerActions.REMOVE_ALL){
                    if(markerCreator.getUserID().compareTo(MainActivity.authID)!=0)
                    {
                        Toast.makeText(getContext(), "Could not delete other user's marker!",
                                Toast.LENGTH_SHORT).show();
                        resetCurrentDeleteTarget();
                        return;
                    }
                    Circle circle = currCircle;
                    Marker marker = currMarker;
                    Geofence geofence = currGeofence;

                    if(circle != null){
                        circle.remove();
                    }else{
                        Log.d("Map", "Circle is already NULL!");
                    }

                    rootRef.child("MarkerDatas").child(""+((MarkerData)marker.getTag()).getMarkerId()).removeValue();

                    if(marker != null){
                        marker.remove();
                    }else{
                        Log.d("Map", "Marker is already NULL!");
                    }

                    if(geofence != null){
                        ArrayList<String> ids = new ArrayList<>();
                        ids.add(geofence.getRequestId());
                        mGeofencingClient.removeGeofences(ids);
                    }

                    resetCurrentDeleteTarget();

                    Log.d("Map","Successfully removed Marker, Geofence and Circle!");
                }else if(action == MarkerActions.MESSAGE_USER){
                    intent = new Intent(getActivity().getApplicationContext(), ChatActivity.class);
                    intent.putExtra("username",markerCreator.getUserName());
                    intent.putExtra("id",markerCreator.getUserID());

                    FirebaseDatabase.getInstance().getReference().child("Messages").child(MainActivity.authID).child(markerCreator.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds : dataSnapshot.getChildren())
                            {
                                edu.bluejack17_2.tolongku.Message m  = ds.getValue(edu.bluejack17_2.tolongku.Message.class);
                                messages.add(m);
                            }
                            FirebaseDatabase.getInstance().getReference().child("Messages").child(markerCreator.getUserID()).child(MainActivity.authID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot ds : dataSnapshot.getChildren())
                                    {
                                        edu.bluejack17_2.tolongku.Message m = ds.getValue(edu.bluejack17_2.tolongku.Message.class);
                                        messages.add(m);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            Collections.sort(messages);
                            intent.putExtra("messages",messages);
                            Toast.makeText(getActivity().getApplicationContext(),"initiate",Toast.LENGTH_SHORT).show();
                            Log.d("FriendsFragment","initiate");
                            startActivity(intent);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }else if(action == MarkerActions.CALL_USER){
                    makeCall();
                }else if(action == MarkerActions.DO_NOTHING){
                    Log.d("Map", "Successfully did nothing.");
                }else{
                    Log.d("Map", "Action code is not working properly.");
                }
            }else{
                Log.d("Map", "Marker Data is NULL or action is -1");
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(!checkPermission())
            return;

        mDefaultLocation = new LatLng(40.689247, -74.044502);

        MapsInitializer.initialize(getContext());

        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if(location != null){
            mDefaultLocation = new LatLng(location.getLatitude(), location.getLongitude());
        }

//        mDefaultLocation = new LatLng(-6.203549, 106.7846679);

        mMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d("Map", "Touched the map at " + latLng.latitude + " "
                + latLng.longitude);

                Intent intent = new Intent(getActivity(), NewMarkerActivity.class);
                intent.putExtra("latitude", latLng.latitude);
                intent.putExtra("longitude", latLng.longitude);
                startActivityForResult(intent, NEW_MARKER_REQUEST_CODE);
            }
        });

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                MarkerData markerData = (MarkerData) marker.getTag();

                resetCurrentDeleteTarget();
                currMarker = marker;
                currCircle = markerData.getCircle();
                currGeofence = markerData.getGeofence();
                currPosition = markerData.getPosition();
                markerCreator = markerData.getUser();
                int radius = ((Double)currCircle.getRadius()).intValue();

                Log.d("Map", currCircle.toString());

                Intent intent = new Intent(getActivity(), MarkerActions.class);
                intent.putExtra("status", markerData.getStatus());
                intent.putExtra("radius", radius);
                startActivityForResult(intent, MARKER_ACTION_REQUEST_CODE);
            }
        });

        Toast.makeText(getActivity(), "Done", Toast.LENGTH_SHORT).show();
        CameraPosition cameraPosition = CameraPosition.builder().target(mDefaultLocation)
                .zoom(16).bearing(0).build();

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        mGeofenceList.clear();

        rootRef.child("MarkerDatas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    MarkerDatas mdd = ds.getValue(MarkerDatas.class);
                    md.add(mdd);
                }

                for(final MarkerDatas toCreate: md){
                    final String userId = toCreate.getUserId();
                    rootRef.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds : dataSnapshot.getChildren())
                            {
                                if(ds.child("userID").getValue(String.class).compareTo(userId) ==0)
                                {
                                    toCreateUser = ds.getValue(User.class);
                                }
                            }
                            if(!checkPermission())
                                return;
                            LatLng position = new LatLng(Double.parseDouble(toCreate.getLatitude()), Double.parseDouble(toCreate.getLongitude()));

                            int markerId = ((Long)toCreate.getMarkerId()).intValue();
                            int radius = ((Long)toCreate.getRadius()).intValue();
                            Marker marker;
                            Circle circle;
                            Geofence geofence;

                            Log.d("Map","Punya user : "+toCreateUser.getUserID());


                            Log.d("Map", "Map Load Running, Lat: " + position.latitude +
                            "Longitude: " + position.longitude);
                            Log.d("Map", "Status was: " + toCreate.getAreaStatus());

                            if(toCreate.getAreaStatus() == AREA_DANGEROUS){
                                marker = addMarker(position, "Dangerous Area",
                                        "This place is marked as dangerous", "#c62828");

                                circle = circleLocation(radius, position,
                                        Color.argb(255 ,183, 28, 28),
                                        Color.argb(50 ,244,67,54));

                                geofence = new Geofence.Builder().setRequestId(GEOFENCE_REQUEST_CODE).
                                        setCircularRegion(position.latitude, position.longitude, radius)
                                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                                Geofence.GEOFENCE_TRANSITION_EXIT)
                                        .build();

                                GEOFENCE_REQUEST_CODE = String.valueOf((Integer.parseInt(GEOFENCE_REQUEST_CODE)
                                        + 1));

                                mGeofenceList.add(geofence);

                                if(toCreateUser != null)
                                {
                                    marker.setTag(new MarkerData(markerId, toCreateUser, MarkerData.DANGEROUS, marker,
                                            position, circle, geofence));
                                }
                                else
                                {
                                    Log.d("Map","Current User is null!");
                                }

                                mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                                        .addOnSuccessListener(getActivity(), new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("Geofence", "Success!");
                                            }
                                        }).addOnFailureListener(getActivity(),
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("Geofence", "Failed!");
                                            }
                                        });

                            }else if(toCreate.getAreaStatus() == AREA_SHELTER){

                                marker = addMarker(position, "Shelter Area",
                                        "This place is marked as a shelter", "#1976D2");

                                circle = circleLocation(radius, position,
                                        Color.argb(255,25,118,210),
                                        Color.argb(50 ,33,150,243));

                                Log.d("Map", "Shelter marker successfully placed.");


                                if(toCreateUser != null)
                                {
                                    marker.setTag(new MarkerData(markerId,toCreateUser, MarkerData.HELP, marker, position, circle));
                                }
                                else
                                {
                                    Log.d("Map","Current User is null!");
                                }

                            }else if(toCreate.getAreaStatus() == AREA_HELP){
                                marker = addMarker(position, "Help Area",
                                        "This place is offering help", "#1976D2");

                                circle = circleLocation(radius, position,
                                        Color.argb(255,46,125,50),
                                        Color.argb(50 ,76,175,80));

                                Log.d("Map", "Help marker successfully placed.");

                                if(toCreateUser != null)
                                {
                                    marker.setTag(new MarkerData(markerId,toCreateUser, MarkerData.HELP, marker, position, circle));
                                }
                                else
                                {
                                    Log.d("Map","Current User is null!");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private GeofencingRequest getGeofencingRequest(){
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent(){
        // Reuse the PendingIntent if we already have it.
        if(mGeofencePendingIntent != null){
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this.getContext(), GeofenceTransitionService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences()
        mGeofencePendingIntent = PendingIntent.getService(this.getContext(), 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_map, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = view.findViewById(R.id.mapView);

        if(mapView != null){
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode)
        {
            case MY_PERMISSIONS_REQUEST_PHONE_CALL:

                userRef.child("userContactNumber").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        phoneNumber = dataSnapshot.getValue(String.class);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(callIntent);
                break;
        }

    }

    public void makeCall()
    {
        userRef.child("userContactNumber").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                phoneNumber = dataSnapshot.getValue(String.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
        {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CALL_PHONE},
                    MY_PERMISSIONS_REQUEST_PHONE_CALL);
        }
        else
        {

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:"+phoneNumber));
            startActivity(callIntent);
        }

    }
}
