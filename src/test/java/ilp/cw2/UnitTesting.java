package ilp.cw2;


import ilp.cw2.dtos.Point;
import ilp.cw2.dtos.RestrictedArea;
import ilp.cw2.utils.Astar;
import ilp.cw2.utils.Pair;
import ilp.cw2.utils.Raycasting;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UnitTesting {

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @LocalServerPort
    private int port;

    private final String ilpEndpoint;

    @Autowired
    UnitTesting(String ilpEndpoint){ this.ilpEndpoint = ilpEndpoint; }

    @Test
    void checkMoveSize(){
        RestrictedArea[] areas = restTemplate.getForObject((ilpEndpoint + "/restricted-areas"), RestrictedArea[].class);

        List<Raycasting.Line> lines = new ArrayList<>();
        for(RestrictedArea area: areas) {
            lines.addAll(area.areaToLines());
        }

        Pair<List<Point>, Double> output = Astar.getPath(generatePoint(),generatePoint(),lines);
        List<Point> path = output.first;

        Boolean allWithinCorrectDistance = true;

        for(int i = 1; i < path.size(); i++){
            Point p1 = path.get(i);
            Point p2 = path.get(i-1);
            if(getDistance(p1,p2) != 0.00015){
                allWithinCorrectDistance = false;
                System.out.println(getDistance(p1,p2));
            }
        }
        System.out.println(allWithinCorrectDistance);

        assert(allWithinCorrectDistance);
    }

    @Test
    void checkMoveAngle(){
        RestrictedArea[] areas = restTemplate.getForObject((ilpEndpoint + "/restricted-areas"), RestrictedArea[].class);

        List<Raycasting.Line> lines = new ArrayList<>();
        for(RestrictedArea area: areas) {
            lines.addAll(area.areaToLines());
        }

        Pair<List<Point>, Double> output = Astar.getPath(generatePoint(),generatePoint(),lines);
        List<Point> path = output.first;
        Boolean allCorrectAngle= true;

        for(int i = 1; i < path.size()-1; i++){
            Point p1 = path.get(i-1);
            Point p2 = path.get(i);
            Point p3 = path.get(i+1);

            double angle = getAngle(p1,p2,p3);

            if(!angles.contains(angle)){
                allCorrectAngle = false;
            }
        }

        assert(allCorrectAngle);
    }

    @Test
    void checkNoFly(){
        RestrictedArea[] areas = restTemplate.getForObject((ilpEndpoint + "/restricted-areas"), RestrictedArea[].class);

        List<Raycasting.Line> lines = new ArrayList<>();
        for(RestrictedArea area: areas) {
            lines.addAll(area.areaToLines());
        }
        Pair<List<Point>, Double> output = Astar.getPath(generatePoint(),generatePoint(),lines);
        List<Point> path = output.first;
        Boolean intersects = false;

        for(int i = 1; i < path.size(); i++){
            Point start = path.get(i-1);
            Point end = path.get(i);
            Raycasting.Line newLine = new Raycasting.Line(start, end);
            for(Raycasting.Line line: lines) {
                intersects = Raycasting.intersects(newLine, line);
                if(intersects) break;
            }
        }
        assert(!intersects);

    }

    Point generatePoint(){
        double minLng = -3.0868645249437066;
        double maxLng = -3.283441268117258;

        double minLat = 55.899409291104945;
        double maxLat = 55.988184242060754;

        double randomLng = Math.random()*(maxLng-minLng)+minLng;
        double randomLat = Math.random()*(maxLat-minLat)+minLat;
        Point randomPoint = new Point(randomLng,randomLat);

        return randomPoint;
    }

    Double getDistance(Point p1, Point p2){
        double dist = Math.sqrt(Math.pow(p2.getlng() - p1.getlng(), 2)
                + Math.pow(p2.getlat() - p1.getlat(), 2));
        dist = Math.round(dist*Math.pow(10, 5))/Math.pow(10, 5);
        return dist;
    }

    double getAngle(Point p1, Point p2, Point p3){

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



