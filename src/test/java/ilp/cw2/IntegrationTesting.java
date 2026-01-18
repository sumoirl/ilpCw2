package ilp.cw2;

import ilp.cw2.dtos.*;
import ilp.cw2.utils.Astar;
import ilp.cw2.utils.Pair;
import ilp.cw2.utils.QueryAvailable;
import ilp.cw2.utils.Raycasting;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTesting {

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @LocalServerPort
    private int port;

    private final String ilpEndpoint;

    @Autowired
    IntegrationTesting(String ilpEndpoint){ this.ilpEndpoint = ilpEndpoint; }

    @Test
    void validDeliveryPath() {
        RestrictedArea[] areas = restTemplate.getForObject((ilpEndpoint + "/restricted-areas"), RestrictedArea[].class);
        Drone[] drones = restTemplate.getForObject((ilpEndpoint + "/drones"), Drone[].class);
        DroneForServicePoint[] dronesForServicePoints = restTemplate.getForObject((ilpEndpoint + "/drones-for-service-points"), DroneForServicePoint[].class);
        ServicePoint[] ServicePoints = restTemplate.getForObject((ilpEndpoint + "/service-points"), ServicePoint[].class);

        int num = 5;

        ArrayList<MedDispatchRec> reclist = TestUtils.generateDispatches(num);

        ArrayList<Pair<Drone, Point>> droneWithPoint = new ArrayList<>();
        droneWithPoint = QueryAvailable.query(drones, dronesForServicePoints,ServicePoints, reclist);
        System.out.println("droneWithPoint.size(): " + droneWithPoint.size());

        ArrayList<Drone> availDrones = new ArrayList<>();
        for(Pair<Drone, Point> d : droneWithPoint){
            availDrones.add(d.first);
        }

        Boolean allWithinCorrectDistance = true;
        Boolean allCorrectAngle = true;
        Boolean intersects = false;

        Drone drone = droneWithPoint.getFirst().first;
        Point start = droneWithPoint.getFirst().second;

        ArrayList<Point> deliveryLocations = new ArrayList<>();

        for(MedDispatchRec medDispatchRec: reclist){
            deliveryLocations.add(medDispatchRec.getDelivery());
        }

        deliveryLocations.addFirst(start);
        deliveryLocations.addLast(start);

        List<Raycasting.Line> lines = new ArrayList<>();
        for(RestrictedArea area: areas) {
            lines.addAll(area.areaToLines());
        }

        List<List<Point>> paths = new ArrayList<>();

        for (int i = 0; i<deliveryLocations.size()-1; i++){
            if(i == 0){
                paths.add(Astar.getPath(deliveryLocations.get(0),deliveryLocations.get(1),lines).first);}
            else{
                paths.add(Astar.getPath(paths.getLast().getLast(), deliveryLocations.get(i+1),lines).first);
            }
        }

        for(List<Point> path: paths){
            for(int i = 1; i < path.size(); i++){
                Point p1 = path.get(i);
                Point p2 = path.get(i-1);
                if(TestUtils.getDistance(p1,p2) != 0.00015) {
                    allWithinCorrectDistance = false;
                    //System.out.println(TestUtils.getDistance(p1,p2));
                 }
                Raycasting.Line newLine = new Raycasting.Line(p1,p2);
                for(Raycasting.Line line: lines) {
                    intersects = Raycasting.intersects(newLine, line);
                    if(intersects) break;
                }
            }
        }
        for(List<Point> path: paths){
            for(int i = 1; i < path.size()-1; i++){
                Point p1 = path.get(i-1);
                Point p2 = path.get(i);
                Point p3 = path.get(i+1);

                double angle = TestUtils.getAngle(p1,p2,p3);

                if(!TestUtils.angles.contains(angle)) {
                    allCorrectAngle = false;
                }
            }
        }

        assert(allWithinCorrectDistance);
        assert(allCorrectAngle);
        assert(!intersects);

    }

    @Test
    void validDataRetrieval(){
        Drone[] drones = restTemplate.getForObject((ilpEndpoint + "/drones"), Drone[].class);
        DroneForServicePoint[] dronesForServicePoints = restTemplate.getForObject((ilpEndpoint + "/drones-for-service-points"), DroneForServicePoint[].class);
        ServicePoint[] ServicePoints = restTemplate.getForObject((ilpEndpoint + "/service-points"), ServicePoint[].class);
        RestrictedArea[] areas = restTemplate.getForObject((ilpEndpoint + "/restricted-areas"), RestrictedArea[].class);

        // Values found from manually checking ILP REST service
        int expectedDrones = 10;
        int expectedServicePoints = 2;
        int expectedAreas = 4;
        int expectedDronesForServicePoints = 2;

        assert(drones.length == expectedDrones);
        assert(dronesForServicePoints.length == expectedDronesForServicePoints);
        assert(ServicePoints.length == expectedServicePoints);
        assert(areas.length == expectedAreas);
    }

}
