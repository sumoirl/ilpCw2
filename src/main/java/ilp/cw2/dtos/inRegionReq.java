package ilp.cw2.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class inRegionReq {
    @JsonProperty(value = "region")
    private Region region;
    @JsonProperty(value = "position")
    private Point position;

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public inRegionReq(Region region, Point position) {
        this.region = region;
        this.position = position;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    // checks if the line from p1 -> p2 crosses or contains 'point'
    public boolean intersects(Point p1, Point p2, Point point) {

        double mp2;
        double mpoint;

        // make sure p1 is always left of p2
        if (p1.getlng() > p2.getlng()) {
            return intersects(p2, p1, point);
        }
        // check if point is literally on the line
        double cross = (p2.getlat() - p1.getlat()) * (point.getlng() - p1.getlng()) -
                (p2.getlng() - p1.getlng()) * (point.getlat() - p1.getlat());
        if (Math.abs(cross) < 1e-9) {
            if (point.getlng() >= Math.min(p1.getlng(), p2.getlng()) &&
                    point.getlng() <= Math.max(p1.getlng(), p2.getlng()) &&
                    point.getlat() >= Math.min(p1.getlat(), p2.getlat()) &&
                    point.getlat() <= Math.max(p1.getlat(), p2.getlat())) {
                return true; // on the line
            }
        }

        // outside the x-range
        if (point.getlng() < p1.getlng() || point.getlng() > p2.getlng()) {
            return false;
        }
        // above both points
        else if (point.getlat() >= Math.max(p1.getlat(), p2.getlat())) {
            return false;
        }
        // below both points
        else if (point.getlat() < Math.min(p1.getlat(), p2.getlat())) {
            return true;
        }
        // find slope of line
        else if (p1.getlat() != p2.getlat()) {
            mp2 = (p2.getlng() - p1.getlng()) / (p2.getlat() - p1.getlat());
        } else {
            mp2 = Float.MAX_VALUE; // vertical
        }

        // slope from p1 to point
        if (point.getlat() != p1.getlat()) {
            mpoint = (point.getlng() - p1.getlng()) / (point.getlat() - p1.getlat());
        } else {
            mpoint = Float.MAX_VALUE;
        }
        // compare slopes
        return mpoint >= mp2;
    }

}
