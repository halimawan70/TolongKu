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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

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


    public final static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            getLocationPermission();
            return;
        }
        mDefaultLocation = new LatLng(40.689247, -74.044502);

        MapsInitializer.initialize(getContext());

        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if(location != null){
            mDefaultLocation = new LatLng(location.getLatitude(), location.getLongitude());
        }

        mMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);

        Toast.makeText(getActivity(), "Done", Toast.LENGTH_SHORT).show();
        CameraPosition cameraPosition = CameraPosition.builder().target(mDefaultLocation)
                .zoom(16).bearing(0).build();

        Marker playerMarker = googleMap.addMarker(new MarkerOptions().position(mDefaultLocation)
                .title("You are here!"));

        playerMarker.setSnippet("This is your approximate current location.");

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        Geofence geofence = new Geofence.Builder().setRequestId("test").setCircularRegion(mDefaultLocation.latitude,
                mDefaultLocation.longitude, 100).setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();

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

        Circle geoFenceCircle = circleLocation(playerMarker.getPosition(),
                Color.argb(255 ,3, 169, 244),
                Color.argb(50 ,41,182,246));

//        CircleOptions circleOptions = new CircleOptions().center(playerMarker.getPosition())
//                .strokeColor(Color.argb(255, 70, 70, 70))
//                .fillColor(Color.argb(180, 150, 150, 150))
//                .radius(100);
//
//        Circle geofenceLimits = googleMap.addCircle(circleOptions);
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
