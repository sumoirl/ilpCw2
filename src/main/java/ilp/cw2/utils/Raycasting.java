package ilp.cw2.utils;

import ilp.cw2.dtos.Point;

public class Raycasting {
    public static class Line{
          public Point p1;
          public Point p2;

          public Line(Point p1, Point p2){
              if(p1.getlng() > p2.getlng()) {
                  this.p1 = p2;
                  this.p2 = p1;
              } else {
                  this.p1 = p1;
                  this.p2 = p2;
              }

          }
    }

    public static Boolean intersects (Line l1, Line l2){

        Double m1 = (l1.p2.getlat() - l1.p1.getlat()) / (l1.p2.getlng() - l1.p1.getlng());
        Double m2 = (l2.p2.getlat() - l2.p1.getlat()) / (l2.p2.getlng() - l2.p1.getlng());

        Double c1 = l1.p1.getlat() - (m1 * l1.p1.getlng());
        Double c2 = l2.p1.getlat() - (m2 * l2.p1.getlng());

        Double lng =(c2 - c1) / (m1 - m2);

        if(lng < l1.p1.getlng())  return false;
        if(lng > l1.p2.getlng())  return false;
        if(lng < l2.p1.getlng())  return false;
        if(lng > l2.p2.getlng())  return false;

        return true;
    }

}
