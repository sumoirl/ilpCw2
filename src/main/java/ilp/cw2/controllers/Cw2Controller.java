package ilp.cw2.controllers;

import ilp.cw2.cw3.Cw3;
import ilp.cw2.dtos.*;
import ilp.cw2.dtos.Point;
import ilp.cw2.utils.Astar;
import ilp.cw2.utils.Pair;
import ilp.cw2.utils.QueryAvailable;
import ilp.cw2.utils.Raycasting;
import lombok.extern.apachecommons.CommonsLog;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.json.JSONObject;

import static ilp.cw2.utils.haversine.haversineFunc;
import static java.lang.Boolean.valueOf;

@CommonsLog
@RestController
public class Cw2Controller {

    private final String startPoint = "/api/v1";

    private final RestTemplate restTemplate = new RestTemplate();

    private final String ilpEndpoint;

    @Autowired
    Cw2Controller(String ilpEndpoint){ this.ilpEndpoint = ilpEndpoint; }

    @GetMapping(startPoint + "/dronesWithCooling/{state}")
    public ResponseEntity<ArrayList<String>> getDronesWithCooling(@PathVariable Boolean state){
        Drone[] drones = restTemplate.getForObject((ilpEndpoint + "/drones"),Drone[].class);

        ArrayList<String> droneIds = new ArrayList<>();
        for (Drone drone : drones) {
            if (drone.getCapability().isCooling() == state){
                droneIds.add(drone.getId());
            }
        }
        return ResponseEntity.ok(droneIds);
    }

    @GetMapping (startPoint + "/droneDetails/{id}")
    public ResponseEntity<Drone> getDroneDetails(@PathVariable String id){
        Drone[] drones = restTemplate.getForObject((ilpEndpoint + "/drones"),Drone[].class);

        Drone drone = null;

        for (Drone d : drones){
            if (Objects.equals(d.getId(), id)){
                drone = d;
            }
        }
       if  (drone!=null){
           return ResponseEntity.ok(drone);
       }
       else  {
           return ResponseEntity.notFound().build();
       }
    }

    @GetMapping (startPoint + "/queryAsPath/{attribute}/{value}")
    public ResponseEntity<ArrayList<String>>  getQueryAsPath(@PathVariable String attribute, @PathVariable String value){
        Drone[] drones = restTemplate.getForObject((ilpEndpoint + "/drones"),Drone[].class);

        ArrayList<String> droneIds = new ArrayList<>();
        for (Drone d : drones){
            switch(attribute){
                case "cooling":
                    if(d.getCapability().isCooling() == Boolean.parseBoolean(value)){
                        droneIds.add(d.getId());}
                        break;

                case "heating":
                    if(d.getCapability().isHeating() == Boolean.parseBoolean(value)){
                        droneIds.add(d.getId());}
                        break;

                case "capacity":
                    if(d.getCapability().getCapacity() == Double.parseDouble(value)){
                        droneIds.add(d.getId());}
                        break;

                case "maxMoves":
                    if(d.getCapability().getMaxMoves() == Integer.parseInt(value)){
                        droneIds.add(d.getId());}
                        break;

                case "costPerMove":
                    if(d.getCapability().getCostPerMove() == Double.parseDouble(value)){
                        droneIds.add(d.getId());}
                        break;

                case "costInitial":
                    if(d.getCapability().getCostInitial() == Double.parseDouble(value)){
                        droneIds.add(d.getId());}
                        break;

                case "costFinal":
                    if(d.getCapability().getCostFinal() == Double.parseDouble(value)){
                        droneIds.add(d.getId());}
                        break;

            }
        }
        return ResponseEntity.ok(droneIds);
    }

    @PostMapping(startPoint + "/query")
    public ResponseEntity<ArrayList<String>> query(@RequestBody List<Query> req){
        Drone[] drones = restTemplate.getForObject((ilpEndpoint + "/drones"),Drone[].class);
        ArrayList<String> droneIds = new ArrayList<>();
        for (Drone d : drones){

            Boolean matchesAll = true;

            for(Query query : req){

                Boolean matchesCurrent = false;

                switch(query.getAttribute()){
                    case "cooling":
                        matchesCurrent = d.getCapability().isCooling() == Boolean.parseBoolean(query.getValue());
                        break;
                    case "heating":
                        matchesCurrent = d.getCapability().isHeating() == Boolean.parseBoolean(query.getValue());
                        break;
                    case "capacity":
                        switch (query.getOperator()){
                            case "=":
                               matchesCurrent = d.getCapability().getCapacity() == Integer.parseInt(query.getValue());
                                break;
                            case "!=":
                                matchesCurrent =d.getCapability().getCapacity() != Double.parseDouble(query.getValue());
                                break;
                            case "<":
                               matchesCurrent = d.getCapability().getCapacity() < Integer.parseInt(query.getValue());
                                break;
                            case ">":
                                matchesCurrent = d.getCapability().getCapacity() > Integer.parseInt(query.getValue());

                                break;

                        }
                        break;
                    case "maxMoves":
                        switch(query.getOperator()){
                            case "=":
                                matchesCurrent = d.getCapability().getMaxMoves() == Integer.parseInt(query.getValue());
                                break;
                            case  "!=":
                                matchesCurrent = d.getCapability().getMaxMoves() != Integer.parseInt(query.getValue());
                                break;
                            case  "<":
                                matchesCurrent = d.getCapability().getMaxMoves() < Integer.parseInt(query.getValue());
                                break;
                            case ">":
                                matchesCurrent = d.getCapability().getMaxMoves() > Integer.parseInt(query.getValue());
                        }
                        break;
                    case  "costPerMove":
                        switch(query.getOperator()){
                            case "=":
                                matchesCurrent = d.getCapability().getCostPerMove() == Integer.parseInt(query.getValue());
                                break;
                            case "!=":
                                matchesCurrent = d.getCapability().getCostPerMove() != Integer.parseInt(query.getValue());
                                break;
                            case "<":
                                matchesCurrent = d.getCapability().getCostPerMove() < Integer.parseInt(query.getValue());
                                break;
                            case ">":
                                matchesCurrent = d.getCapability().getCostPerMove() > Integer.parseInt(query.getValue());
                                break;

                        }
                        break;
                    case "costInitial":
                        switch(query.getOperator()){
                            case "=":
                                matchesCurrent = d.getCapability().getCostInitial() == Integer.parseInt(query.getValue());
                                break;
                            case "!=":
                                matchesCurrent = d.getCapability().getCostInitial() != Integer.parseInt(query.getValue());
                                break;
                            case ">":
                                matchesCurrent = d.getCapability().getCostInitial() > Integer.parseInt(query.getValue());
                                break;
                            case "<":
                                matchesCurrent = d.getCapability().getCostInitial() < Integer.parseInt(query.getValue());
                                break;
                        }
                        break;
                    case "costFinal":
                        switch(query.getOperator()){
                            case "=":
                                matchesCurrent = d.getCapability().getCostFinal() == Integer.parseInt(query.getValue());
                                break;
                            case "!=":
                                matchesCurrent = d.getCapability().getCostFinal() != Integer.parseInt(query.getValue());
                                break;
                            case "<":
                                matchesCurrent = d.getCapability().getCostFinal() < Integer.parseInt(query.getValue());
                                break;
                            case  ">":
                                matchesCurrent = d.getCapability().getCostFinal() > Integer.parseInt(query.getValue());
                                break;
                        }
                        break;
                }
                if (!matchesCurrent) {
                    matchesAll = false;
                    break;
                 }
            }

        if (matchesAll) {
            droneIds.add(d.getId());
        }

        }
        return ResponseEntity.ok(droneIds);
    }

    @PostMapping(startPoint + "/queryAvailableDrones")
    public ResponseEntity<ArrayList<String>> queryAvailableDrones(@RequestBody List<MedDispatchRec> reqlist) {
        Drone[] drones = restTemplate.getForObject((ilpEndpoint + "/drones"), Drone[].class);
        DroneForServicePoint[] dronesForServicePoints = restTemplate.getForObject((ilpEndpoint + "/drones-for-service-points"), DroneForServicePoint[].class);
        ServicePoint[] ServicePoints = restTemplate.getForObject((ilpEndpoint + "/service-points"), ServicePoint[].class);

       ArrayList<Pair<Drone, Point>> droneWithPoint = new ArrayList<>();
       droneWithPoint = QueryAvailable.query(drones, dronesForServicePoints,ServicePoints, reqlist);


        ArrayList<String> ids = new ArrayList<>();
        for(Pair<Drone, Point> d : droneWithPoint){
            ids.add(d.first.getId());
        }

        return ResponseEntity.ok(ids);

    }

    @GetMapping("/test")
    public ResponseEntity<String> test() throws JSONException {
        RestrictedArea[] areas = restTemplate.getForObject((ilpEndpoint + "/restricted-areas"), RestrictedArea[].class);

        Point start = new Point(-3.1867334497823663, 55.94432778784571);
        //Point end = new Point(-3.18505627826093, 55.94458510360218);
        Point A = new Point(-3.1902699046107728, 55.9519373497846);
        //Point end = new Point(-3.177111113717501, 55.98152418168104);
        Point B = new Point(-4.035788102304167, 55.764845368154454);

        List<Raycasting.Line> lines = new ArrayList<>();
        for(RestrictedArea area: areas) {
            lines.addAll(area.areaToLines());
        }
        List<Point> path = Astar.getPath(start, A, lines).first;
        path.addAll(Astar.getPath(path.getLast(),B, lines ).first);
        path.addAll(Astar.getPath(path.getLast(),start, lines ).first);

        JSONObject response =  new JSONObject();
        response.put("type", "FeatureCollection");

        JSONArray featuresArray = new JSONArray();

        JSONObject feature = new JSONObject();
        feature.put("type", "Feature");
        feature.put("properties", new JSONObject());
        JSONObject geometry = new JSONObject();
        JSONArray coordinates = new JSONArray();
        for (Point point : path) {
            JSONArray coordinate = new JSONArray();
            coordinate.put(point.getlng());
            coordinate.put(point.getlat());
            coordinates.put(coordinate);
        }
        geometry.put("coordinates", coordinates);
        geometry.put("type", "LineString");
        feature.put("geometry", geometry);
        featuresArray.put(feature);

        response.put("features", featuresArray);

        return  ResponseEntity.ok(response.toString());
    }

    @PostMapping(startPoint + "/calcDeliveryPath")
    public ResponseEntity<DeliveryOutput> calcDeliveryPath(@RequestBody List<MedDispatchRec> reqlist) throws JSONException {
        Drone[] drones = restTemplate.getForObject((ilpEndpoint + "/drones"), Drone[].class);
        DroneForServicePoint[] dronesForServicePoints = restTemplate.getForObject((ilpEndpoint + "/drones-for-service-points"), DroneForServicePoint[].class);
        ServicePoint[] ServicePoints = restTemplate.getForObject((ilpEndpoint + "/service-points"), ServicePoint[].class);
        RestrictedArea[] areas = restTemplate.getForObject((ilpEndpoint + "/restricted-areas"), RestrictedArea[].class);

        ArrayList<Pair<Drone,Point>>  droneWithPoint = new ArrayList<>();

        droneWithPoint = QueryAvailable.query(drones, dronesForServicePoints,ServicePoints, reqlist);

        Drone drone = droneWithPoint.getFirst().first;
        Point start = droneWithPoint.getFirst().second;

        ArrayList<Point> deliveryLocations = new ArrayList<>();

        for(MedDispatchRec medDispatchRec: reqlist){
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

        Double totalCost = 0d;
        Integer totalMoves = 0;
        DeliveryOutput deliveryOutput = new DeliveryOutput();
        deliveryOutput.dronePaths = new DronePath();
        deliveryOutput.dronePaths.deliveries = new ArrayList<>();


        for (int i = 0; i< reqlist.size(); i++){

            DronePath dronePath = new DronePath();
            Deliveries deliveries = new Deliveries();
            deliveries.flightPath = paths.get(i);
            deliveries.flightPath.add(paths.get(i).getLast());
            deliveries.deliveryId = String.valueOf(reqlist.get(i).getId());


            deliveryOutput.dronePaths.deliveries.add(deliveries);

            totalMoves += paths.get(i).size() - 1;
        }

        deliveryOutput.dronePaths.droneId= drone.getId();
        Deliveries deliveries = new Deliveries();
        deliveries.flightPath = paths.getLast();
        deliveries.flightPath.add(paths.getLast().getLast());
        deliveries.deliveryId = null;


        deliveryOutput.dronePaths.deliveries.add(deliveries);

        totalMoves += paths.getLast().size() - 1;

        totalCost = (totalMoves * drone.getCapability().getCostPerMove()) +drone.getCapability().getCostInitial() + drone.getCapability().getCostFinal();

        deliveryOutput.totalCost = totalCost;
        deliveryOutput.totalMoves = totalMoves;

        Double totalKm = 0d;
        for (List<Point> path : paths) {
            for (int i = 1; i < path.size(); i++) {
                Point p1 = path.get(i - 1);
                Point p2 = path.get(i);
                totalKm += haversineFunc(p1.getlat(), p1.getlng(), p2.getlat(), p2.getlng());
            }
        }

        Cw3.updateDroneStats(drone.getId(), totalKm);

        return ResponseEntity.ok(deliveryOutput);
    }


}




