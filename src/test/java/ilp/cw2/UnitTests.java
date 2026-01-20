package ilp.cw2;


import ilp.cw2.dtos.*;
import ilp.cw2.utils.Astar;
import ilp.cw2.utils.Pair;
import ilp.cw2.utils.Raycasting;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;


import java.util.ArrayList;
import java.util.List;



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
     class UnitTests {

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @LocalServerPort
    private int port;

    private final String ilpEndpoint;

    @Autowired
    UnitTests(String ilpEndpoint){ this.ilpEndpoint = ilpEndpoint; }

    @RepeatedTest(10)
    void checkMoveSize(){
        RestrictedArea[] areas = restTemplate.getForObject((ilpEndpoint + "/restricted-areas"), RestrictedArea[].class);

        List<Raycasting.Line> lines = new ArrayList<>();
        for(RestrictedArea area: areas) {
            lines.addAll(area.areaToLines());
        }

        Pair<List<Point>, Double> output = Astar.getPath(TestUtils.generatePoint(),TestUtils.generatePoint(),lines);
        List<Point> path = output.first;

        Boolean allWithinCorrectDistance = true;

        for(int i = 1; i < path.size(); i++){
            Point p1 = path.get(i);
            Point p2 = path.get(i-1);
            if(TestUtils.getDistance(p1,p2) != 0.00015){
                allWithinCorrectDistance = false;
                //System.out.println(TestUtils.getDistance(p1,p2));
            }
        }
        System.out.println(allWithinCorrectDistance);

        assert(allWithinCorrectDistance);
    }

    @RepeatedTest(10)
    void checkMoveAngle(){
        RestrictedArea[] areas = restTemplate.getForObject((ilpEndpoint + "/restricted-areas"), RestrictedArea[].class);

        List<Raycasting.Line> lines = new ArrayList<>();
        for(RestrictedArea area: areas) {
            lines.addAll(area.areaToLines());
        }

        Pair<List<Point>, Double> output = Astar.getPath(TestUtils.generatePoint(),TestUtils.generatePoint(),lines);
        List<Point> path = output.first;
        Boolean allCorrectAngle= true;

        for(int i = 1; i < path.size()-1; i++){
            Point p1 = path.get(i-1);
            Point p2 = path.get(i);
            Point p3 = path.get(i+1);

            double angle = TestUtils.getAngle(p1,p2,p3);

            if(!TestUtils.angles.contains(angle)){
                allCorrectAngle = false;
            }
        }

        assert(allCorrectAngle);
    }

    @RepeatedTest(10)
    void checkNoFly(){
        RestrictedArea[] areas = restTemplate.getForObject((ilpEndpoint + "/restricted-areas"), RestrictedArea[].class);

        List<Raycasting.Line> lines = new ArrayList<>();
        for(RestrictedArea area: areas) {
            lines.addAll(area.areaToLines());
        }
        Pair<List<Point>, Double> output = Astar.getPath(TestUtils.generatePoint(),TestUtils.generatePoint(),lines);
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
            if(intersects) break;
        }
        assert(!intersects);

    }

    /*@Test
    void checkValidDrone(){
        Drone[] drones = restTemplate.getForObject((ilpEndpoint + "/drones"), Drone[].class);
        DroneForServicePoint[] dronesForServicePoints = restTemplate.getForObject((ilpEndpoint + "/drones-for-service-points"), DroneForServicePoint[].class);
        ServicePoint[] ServicePoints = restTemplate.getForObject((ilpEndpoint + "/service-points"), ServicePoint[].class);

        int num = 5;

        ArrayList<MedDispatchRec> reclist = TestUtils.generateDispatches(num);


        ArrayList<Pair<Drone, Point>> droneWithPoint = new ArrayList<>();
        droneWithPoint = QueryAvailable.query(drones, dronesForServicePoints,ServicePoints, reclist);


        ArrayList<Drone> availDrones = new ArrayList<>();
        for(Pair<Drone, Point> d : droneWithPoint){
            availDrones.add(d.first);
        }

        Double dist = 0d;


        Boolean allMatches = true;

        double totalCap = 0;
        double cost = 0;
        for(MedDispatchRec rec: reclist){
            totalCap += rec.getRequirements().getCapacity();
            cost += rec.getRequirements().getMaxCost();
        }

        for(Drone drone: availDrones){

            double totalCost = cost;
            totalCost += drone.getCapability().getCostInitial() + drone.getCapability().getCostFinal();
            double proRataCost = totalCost/num;
            for(MedDispatchRec m: reclist){

                if(m.getRequirements().getMaxCost()< proRataCost){
                   System.out.println(m.getRequirements().getMaxCost());
                   System.out.println(proRataCost);
                    allMatches = false;
                    break;
                }
                if(drone.getCapability().getCapacity()<totalCap){
                    System.out.println("cap");
                    System.out.println(drone.getCapability().getCapacity()<totalCap);
                    allMatches = false;
                    break;
                }
                if(m.getRequirements().getHeating()&&!drone.getCapability().isHeating()){
                    System.out.println("heating");
                    System.out.println((m.getRequirements().getHeating()&&!drone.getCapability().isHeating()));
                    allMatches = false;
                    break;
                }
                if(m.getRequirements().getCooling()&&!drone.getCapability().isCooling()){
                    System.out.println("cooling");
                    System.out.println((m.getRequirements().getCooling()&&!drone.getCapability().isCooling()));
                    allMatches = false;
                    break;
                }
            }
        }
        assert(allMatches);
    } */



}



