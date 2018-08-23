package edu.bluejack17_2.tolongku;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.Serializable;

public class MarkerData implements Serializable{

    private Circle circle;
    private Geofence geofence;
    private LatLng position;
    private Marker marker;
    private int status;

    public static final int DANGEROUS = 1;
    public static final int SHELTER = 2;
    public static final int HELP = 3;

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setCircle(Circle circle){
        this.circle = circle;
    }

    public void setGeofence(Geofence geofence){
        this.geofence = geofence;
    }

    public void setPosition(LatLng position){
        this.position = position;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Circle getCircle(){
        return circle;
    }

    public Geofence getGeofence() {
        return geofence;
    }

    public LatLng getPosition() {
        return position;
    }

    public double getLatitude(){
        return position.latitude;
    }

    public double getLongitude(){
        return position.longitude;
    }

    public int getStatus() {
        return status;
    }

    public MarkerData(){

    }

    public MarkerData(int status, Marker marker, LatLng position, Circle circle){
        setPosition(position);
        setCircle(circle);
        setStatus(status);
        setMarker(marker);
    }

    public MarkerData(int status, Marker marker, LatLng position, Circle circle, Geofence geofence){
        setPosition(position);
        setCircle(circle);
        setGeofence(geofence);
        setStatus(status);
        setMarker(marker);
    }
}
