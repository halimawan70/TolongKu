package edu.bluejack17_2.tolongku;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MapView mapView;
    private LatLng mDefaultLocation;
    private View view;


    public final static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    LocationManager locationManager;

    private boolean mLocationPermissionGranted = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        // Construct a GeoDataClient.
//        mGeoDataClient = Places.getGeoDataClient(getContext(), null);
//
//        // Construct a PlaceDetectionClient.
//        mPlaceDetectionClient = Places.getPlaceDetectionClient(getContext(), null);
//
//        // Construct a FusedLocationProviderClient.
//        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
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

//        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
//
//        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(),
//                new OnSuccessListener<Location>() {
//                    @Override
//                    public void onSuccess(Location location) {
//                        Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
//                        if(location != null){
//                            mDefaultLocation = new LatLng(
//                                    location.getLongitude(),
//                                    location.getLatitude()
//                            );
//                            Toast.makeText(getActivity(), "Updated", Toast.LENGTH_SHORT).show();
//                            Toast.makeText(getActivity(), mDefaultLocation.latitude + " " + mDefaultLocation.longitude, Toast.LENGTH_SHORT).show();
//
//                            CameraPosition cameraPosition = CameraPosition.builder().target(mDefaultLocation)
//                                    .zoom(16).bearing(0).build();
//
//                            mMap.addMarker(new MarkerOptions().position(mDefaultLocation)
//                                    .title("You are here!")).setSnippet("This is your approximate current location.");
//
//                            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//                        }
//                    }
//                });

        Toast.makeText(getActivity(), "Done", Toast.LENGTH_SHORT).show();
        CameraPosition cameraPosition = CameraPosition.builder().target(mDefaultLocation)
                .zoom(16).bearing(0).build();

        googleMap.addMarker(new MarkerOptions().position(mDefaultLocation)
                .title("You are here!")).setSnippet("This is your approximate current location.");

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

//        getLocationPermission();
//        updateLocationUI();
//        getDeviceLocation();
//
//        showCurrentPlace();
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
