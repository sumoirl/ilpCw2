package ilp.cw2;

import ilp.cw2.dtos.MedDispatchRec;
import ilp.cw2.dtos.Point;
import ilp.cw2.dtos.Requirements;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class TestUtils {

    public static Point generatePoint(){
        double minLng = -3.0868645249437066;
        double maxLng = -3.283441268117258;

        double minLat = 55.899409291104945;
        double maxLat = 55.988184242060754;

        double randomLng = Math.random()*(maxLng-minLng)+minLng;
        double randomLat = Math.random()*(maxLat-minLat)+minLat;
        Point randomPoint = new Point(randomLng,randomLat);

        return randomPoint;
    }

    public static Double getDistance(Point p1, Point p2){
        double dist = Math.sqrt(Math.pow(p2.getlng() - p1.getlng(), 2)
                + Math.pow(p2.getlat() - p1.getlat(), 2));
        dist = Math.round(dist*Math.pow(10, 5))/Math.pow(10, 5);
        return dist;
    }

    public static Double getAngle(Point p1, Point p2, Point p3){

        double v1x = p1.getlng() - p2.getlng();
        double v1y = p1.getlat() - p2.getlat();

        double v2x = p3.getlng() - p2.getlng();
        double v2y = p3.getlat() - p2.getlat();

        double dot = v1x*v2x + v1y*v2y;

        double mag1 = Math.sqrt(v1x*v1x + v1y*v1y);
        double mag2 = Math.sqrt(v2x*v2x + v2y*v2y);

        double cosTheta = dot/(mag1*mag2);
        cosTheta = Math.max(-1.0, Math.min(1.0, cosTheta));

        double angle = Math.toDegrees(Math.acos(cosTheta));

        angle = Math.round(angle*Math.pow(10, 5))/Math.pow(10, 5);;

        return angle;
    }

    public static ArrayList<MedDispatchRec> generateDispatches(int num){

        double minLng = -3.0868645249437066;
        double maxLng = -3.283441268117258;

        double minLat = 55.899409291104945;
        double maxLat = 55.988184242060754;


        ArrayList<MedDispatchRec> dispatches = new ArrayList<>();

        for(int i = 0; i < num; i++){
            double randomLng = Math.random()*(maxLng-minLng)+minLng;
            double randomLat = Math.random()*(maxLat-minLat)+minLat;
            Point randomPoint = new Point(randomLng,randomLat);

            MedDispatchRec medDispatchRec = MedDispatchRec.builder()
                    .id((int)(Math.random() * 101))
                    .date(LocalDate.of(2025,1,5))
                    .time(LocalTime.of(12,0))
                    .requirements(Requirements.builder().capacity(Math.random()*5).cooling(false).heating(false).maxCost(Math.random()*15+1500).build())
                    .delivery(randomPoint)
                    .build();

            dispatches.add(medDispatchRec);
        }
        return dispatches;
    }

    static ArrayList<Double> angles = new ArrayList<Double>(){
        {
            add(0.0);
            add(22.5);
            add(45.0);
            add(67.5);
            add(90.0);
            add(112.5);
            add(135.0);
            add(157.5);
            add(180.0);
            add(202.5);
            add(225.0);
            add(247.5);
            add(270.0);
            add(292.5);
            add(315.0);
            add(337.5);

        }
    };
}
