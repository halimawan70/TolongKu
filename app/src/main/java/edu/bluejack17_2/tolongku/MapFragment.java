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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import java.util.ArrayList;
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
    public static String GEOFENCE_REQUEST_CODE = "TolongKuGeofence";

    public final static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public final static int AREA_DANGEROUS = 10;
    public final static int AREA_SHELTER = 11;
    public final static int AREA_HELP = 12;
    public final static int AREA_MARKING_CANCELLED = 13;

    LocationManager locationManager;

    private boolean mLocationPermissionGranted = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGeofencingClient = LocationServices.getGeofencingClient(this.getContext());
        mGeofenceList = new Vector<Geofence>();
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

    public static Circle circleLocation(LatLng location, int strokeColor, int fillColor){

        if(mMap == null){
            Log.d("Geofence Circling", "Map have not been initialized.");
            return null;
        }

        CircleOptions circleOptions = new CircleOptions().
                center(location)
                .strokeColor(strokeColor)
                .fillColor(fillColor)
                .radius(100);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(!checkPermission())
            return;
        if(requestCode == NEW_MARKER_REQUEST_CODE){

            int status = data.getIntExtra("status", -1);
            Log.d("Map", "Add New Marker Status: " + status);
            LatLng position = new LatLng(data.getDoubleExtra("latitude", -1),
                    data.getDoubleExtra("longitude", -1));

            if(status != -1){

                if(status == AREA_DANGEROUS){

                    Marker marker = addMarker(position, "Dangerous Area",
                            "This place is marked as dangerous", "#c62828");

                    Log.d("Map", "Dangerous marker successfully placed.");

                    Geofence geofence = new Geofence.Builder().setRequestId(GEOFENCE_REQUEST_CODE).
                            setCircularRegion(position.latitude, position.longitude, 100)
                            .setExpirationDuration(Geofence.NEVER_EXPIRE)
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                    Geofence.GEOFENCE_TRANSITION_EXIT)
                            .build();

//                    GEOFENCE_REQUEST_CODE = String.valueOf((Integer.parseInt(GEOFENCE_REQUEST_CODE)
//                            + 1));

                    mGeofenceList.add(geofence);

                    mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                            .addOnSuccessListener(this.getActivity(), new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("Geofence", "Success!");
                                }
                            }).addOnFailureListener(this.getActivity(),
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("Geofence", "Failed!");
                                }
                            });

                    Circle circle = circleLocation(position,
                        Color.argb(255 ,183, 28, 28),
                        Color.argb(50 ,244,67,54));

                    marker.setTag(new MarkerData(MarkerData.DANGEROUS, marker, position, circle,
                            geofence));

                }else if(status == AREA_SHELTER){

                    Marker marker = addMarker(position, "Shelter Area",
                            "This place is marked as a shelter", "#1976D2");

                    Circle circle = circleLocation(position,
                            Color.argb(255,25,118,210),
                            Color.argb(50 ,33,150,243));

                    Log.d("Map", "Shelter marker successfully placed.");

                    marker.setTag(new MarkerData(MarkerData.SHELTER, marker, position, circle));

                }else if(status == AREA_HELP){

                    Marker marker = addMarker(position, "Help Area",
                            "This place is offering help", "#1976D2");

                    Circle circle = circleLocation(position,
                            Color.argb(255,46,125,50),
                            Color.argb(50 ,76,175,80));

                    Log.d("Map", "Help marker successfully placed.");

                    marker.setTag(new MarkerData(MarkerData.HELP, marker, position, circle));

                }
            }else{
                Log.d("Map", "Status return was erroneous.");
            }

        }else if(requestCode == MARKER_ACTION_REQUEST_CODE){

            MarkerData markerData = (MarkerData) data.getSerializableExtra("markerData");
            int action = data.getIntExtra("action", -1);

            if(markerData != null && action != -1){

                if(action == MarkerActions.REMOVE_CIRCLE){
                    Circle circle = markerData.getCircle();
                    Marker marker = markerData.getMarker();

                    if(circle != null){
                        circle.remove();
                    }else{
                        Log.d("Map", "Circle is already NULL!");
                    }

                    if(marker != null){
                        marker.remove();
                    }else{
                        Log.d("Map", "Marker is already NULL!");
                    }

                    Log.d("Map","Successfully removed Marker and Circle!");
                }else if(action == MarkerActions.REMOVE_ALL){
                    Circle circle = markerData.getCircle();
                    Marker marker = markerData.getMarker();
                    Geofence geofence = markerData.getGeofence();

                    if(circle != null){
                        circle.remove();
                    }else{
                        Log.d("Map", "Circle is already NULL!");
                    }

                    if(marker != null){
                        marker.remove();
                    }else{
                        Log.d("Map", "Marker is already NULL!");
                    }

                    if(geofence != null){
                        ArrayList<String> ids = new ArrayList<>();
                        ids.add(geofence.getRequestId());
                        mGeofencingClient.removeGeofences(ids);

                        // TODO - Remove Geofence from database stored in variable geofence
                    }
                    Log.d("Map","Successfully removed Marker, Geofence and Circle!");
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

                Intent intent = new Intent(getActivity(), MarkerActions.class);
                intent.putExtra("markerData", markerData);
                startActivityForResult(intent, MARKER_ACTION_REQUEST_CODE);
            }
        });

        Toast.makeText(getActivity(), "Done", Toast.LENGTH_SHORT).show();
        CameraPosition cameraPosition = CameraPosition.builder().target(mDefaultLocation)
                .zoom(16).bearing(0).build();

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


//        Marker playerMarker = googleMap.addMarker(new MarkerOptions().position(mDefaultLocation)
//                .title("You are here!"));
//
//        playerMarker.setSnippet("This is your approximate current location.");



//        Geofence geofence = new Geofence.Builder().setRequestId(GEOFENCE_REQUEST_CODE).
//                setCircularRegion(mDefaultLocation.latitude, mDefaultLocation.longitude,
//                        100).setExpirationDuration(Geofence.NEVER_EXPIRE)
//                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
//                        Geofence.GEOFENCE_TRANSITION_EXIT)
//                .build();
//
//        mGeofenceList.add(geofence);

//        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
//                .addOnSuccessListener(this.getActivity(), new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d("Geofence", "Success!");
//                    }
//                }).addOnFailureListener(this.getActivity(),
//                new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d("Geofence", "Failed!");
//                    }
//                });

//        Circle geoFenceCircle = circleLocation(playerMarker.getPosition(),
//                Color.argb(255 ,3, 169, 244),
//                Color.argb(50 ,41,182,246));
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
}
