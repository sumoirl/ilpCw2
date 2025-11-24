package ilp.cw2.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Point)) return false;
        if (Math.abs(((Point) o).lat - this.lat) > 0.00001) return false;
        if (Math.abs(((Point) o).lng - this.lng) > 0.00001) return false;
        return true;
    }

    @JsonIgnore
    public boolean isValid() {
        return !(lat > 90) && !(lat < -90) && !(lng > 180) && !(lng < -180);
    }

    @Override
    public int hashCode() {
        return Double.valueOf(lng).hashCode() + Double.valueOf(lat).hashCode();
    }
}
