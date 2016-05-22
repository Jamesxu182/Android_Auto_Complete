package com.example.james.autocomplete;

/**
 * Created by James on 15/11/24.
 */
public class GooglePlaceItem extends Item {
    private String place_id;
    private double longitude = 0;
    private double latitude = 0;

    public GooglePlaceItem(String content, int source, String place_id) {
        super(content, source);

        this.place_id = place_id;
    };

    public GooglePlaceItem(String content, int source, boolean star, String place_id) {
        super(content, source, star);

        this.place_id = place_id;
    }

    public String getPlaceId() {
        return this.place_id;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public double getLatitude() {
        return this.latitude;
    }
}
