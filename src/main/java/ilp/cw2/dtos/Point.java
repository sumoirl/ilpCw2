package ilp.cw2.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Point {
    @JsonProperty("lng")
    private double lng;
    @JsonProperty("lat")
    private double lat;

    public double getlng() {
        return lng;
    }
    public void setlng(double lng) {}

    public double getlat() {
        return lat;
    }
    public void setlat(double lat) {}
    public Point(double lng, double lat) {
        this.lng = lng;
        this.lat = lat;
    }

    public boolean equal(Point p) {
        return lng == p.getlng() && lat == p.getlat();
    } // checks if two values are equal

    public boolean isValid() {
        return !(lat > 90) && !(lat < -90) && !(lng > 180) && !(lng < -180);
    }
}
