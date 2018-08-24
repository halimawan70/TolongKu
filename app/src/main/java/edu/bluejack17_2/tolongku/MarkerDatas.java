package edu.bluejack17_2.tolongku;

public class MarkerDatas {

    long areaStatus,markerId,radius;
    String userId,latitude, longitude;

    public MarkerDatas()
    {}


    public MarkerDatas(long areaStatus, String latitude, String longitude, long markerId, long radius, String userId) {
        this.areaStatus = areaStatus;
        this.latitude = latitude;
        this.longitude = longitude;
        this.markerId = markerId;
        this.radius = radius;
        this.userId = userId;
    }

    public long getAreaStatus() {
        return areaStatus;
    }

    public void setAreaStatus(long areaStatus) {
        this.areaStatus = areaStatus;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public long getMarkerId() {
        return markerId;
    }

    public void setMarkerId(long markerId) {
        this.markerId = markerId;
    }

    public long getRadius() {
        return radius;
    }

    public void setRadius(long radius) {
        this.radius = radius;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
