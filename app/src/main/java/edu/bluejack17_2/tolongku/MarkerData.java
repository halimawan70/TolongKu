package edu.bluejack17_2.tolongku;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
;

public class MarkerData implements Parcelable{

    private Circle circle;
    private Geofence geofence;
    private LatLng position;
    private Marker marker;
    private int status;

    public static final int DANGEROUS = 1;
    public static final int SHELTER = 2;
    public static final int HELP = 3;

    protected MarkerData(Parcel in) {
        position = in.readParcelable(LatLng.class.getClassLoader());
        status = in.readInt();
    }

    public static final Creator<MarkerData> CREATOR = new Creator<MarkerData>() {
        @Override
        public MarkerData createFromParcel(Parcel in) {
            return new MarkerData(in);
        }

        @Override
        public MarkerData[] newArray(int size) {
            return new MarkerData[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(position, i);
        parcel.writeInt(status);
    }
}
