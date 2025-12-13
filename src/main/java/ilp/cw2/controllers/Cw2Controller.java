package ilp.cw2.controllers;

import ilp.cw2.cw3.Cw3;
import ilp.cw2.dtos.*;
import ilp.cw2.dtos.Point;
import ilp.cw2.utils.*;
import lombok.extern.apachecommons.CommonsLog;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

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
                        if(Objects.equals(query.getOperator(), "=")) {
                            matchesCurrent = d.getCapability().isCooling() == Boolean.parseBoolean(query.getValue());
                            break;
                        }else if (Objects.equals(query.getOperator(), "!=")) {
                            matchesCurrent = d.getCapability().isCooling() != Boolean.parseBoolean(query.getValue());
                            break;
                        }

                    case "heating":
                        if(Objects.equals(query.getOperator(), "=")) {
                            matchesCurrent = d.getCapability().isHeating() == Boolean.parseBoolean(query.getValue());
                            break;
                        }else if (Objects.equals(query.getOperator(), "!=")) {
                            matchesCurrent = d.getCapability().isHeating() != Boolean.parseBoolean(query.getValue());
                            break;
                        }
                    case "capacity":
                        switch (query.getOperator()){
                            case "=":
                                matchesCurrent = d.getCapability().getCapacity() == Double.parseDouble(query.getValue());
                                break;
                            case "!=":
                                matchesCurrent =d.getCapability().getCapacity() != Double.parseDouble(query.getValue());
                                break;
                            case "<":
                                matchesCurrent = d.getCapability().getCapacity() < Double.parseDouble(query.getValue());
                                break;
                            case ">":
                                matchesCurrent = d.getCapability().getCapacity() > Double.parseDouble(query.getValue());

                                break;

                        }
                        break;
                    case "maxMoves":
                        switch(query.getOperator()){
                            case "=":
                                matchesCurrent = d.getCapability().getMaxMoves() == Double.parseDouble(query.getValue());
                                break;
                            case  "!=":
                                matchesCurrent = d.getCapability().getMaxMoves() != Double.parseDouble(query.getValue());
                                break;
                            case  "<":
                                matchesCurrent = d.getCapability().getMaxMoves() < Double.parseDouble(query.getValue());
                                break;
                            case ">":
                                matchesCurrent = d.getCapability().getMaxMoves() > Double.parseDouble(query.getValue());
                        }
                        break;
                    case  "costPerMove":
                        switch(query.getOperator()){
                            case "=":
                                matchesCurrent = d.getCapability().getCostPerMove() == Double.parseDouble(query.getValue());
                                break;
                            case "!=":
                                matchesCurrent = d.getCapability().getCostPerMove() != Double.parseDouble(query.getValue());
                                break;
                            case "<":
                                matchesCurrent = d.getCapability().getCostPerMove() < Double.parseDouble(query.getValue());
                                break;
                            case ">":
                                matchesCurrent = d.getCapability().getCostPerMove() > Double.parseDouble(query.getValue());
                                break;

                        }
                        break;
                    case "costInitial":
                        switch(query.getOperator()){
                            case "=":
                                matchesCurrent = d.getCapability().getCostInitial() == Double.parseDouble(query.getValue());
                                break;
                            case "!=":
                                matchesCurrent = d.getCapability().getCostInitial() != Double.parseDouble(query.getValue());
                                break;
                            case ">":
                                matchesCurrent = d.getCapability().getCostInitial() > Double.parseDouble(query.getValue());
                                break;
                            case "<":
                                matchesCurrent = d.getCapability().getCostInitial() < Double.parseDouble(query.getValue());
                                break;
                        }
                        break;
                    case "costFinal":
                        switch(query.getOperator()){
                            case "=":
                                matchesCurrent = d.getCapability().getCostFinal() == Double.parseDouble(query.getValue());
                                break;
                            case "!=":
                                matchesCurrent = d.getCapability().getCostFinal() != Double.parseDouble(query.getValue());
                                break;
                            case "<":
                                matchesCurrent = d.getCapability().getCostFinal() < Double.parseDouble(query.getValue());
                                break;
                            case  ">":
                                matchesCurrent = d.getCapability().getCostFinal() > Double.parseDouble(query.getValue());
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

        if(droneWithPoint.isEmpty()){
            DeliveryOutput deliveryOutput = Multi.getMultiPath(reqlist,drones,dronesForServicePoints,ServicePoints,areas);
            return ResponseEntity.ok(deliveryOutput);
        }else{

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
        deliveryOutput.dronePaths = new ArrayList<>();

        // Create a new drone path (A drone id and it's associated deliveries)
        DronePath dronePath = new DronePath();
        dronePath.droneId = drone.getId();
        dronePath.deliveries = new ArrayList<>();

        for (int i = 0; i < reqlist.size(); i++){
            // Create new delivery (A med delivery id and its associated route)
            Delivery delivery = new Delivery();
            delivery.flightPath = paths.get(i);
            delivery.flightPath.add(paths.get(i).getLast());
            delivery.deliveryId = String.valueOf(reqlist.get(i).getId());
            dronePath.deliveries.add(delivery);

            totalMoves += paths.get(i).size() - 1;
        }

        // We also need the return journey
        Delivery delivery = new Delivery();
        delivery.flightPath = paths.getLast();
        delivery.flightPath.add(paths.getLast().getLast());
        delivery.deliveryId = null;
        dronePath.deliveries.add(delivery);
        totalMoves += paths.getLast().size() - 1;

        // Finally we add the drone path
        deliveryOutput.dronePaths.add(dronePath);

        totalMoves += paths.getLast().size() - 1;

        totalCost = (totalMoves * drone.getCapability().getCostPerMove()) +drone.getCapability().getCostInitial() + drone.getCapability().getCostFinal();

        deliveryOutput.totalCost = totalCost;
        deliveryOutput.totalMoves = totalMoves;

        Double totalKm = 0d;
        Double distance = 0d;
        for (List<Point> path : paths) {
            for (int i = 1; i < path.size(); i++) {
                Point p1 = path.get(i - 1);
                Point p2 = path.get(i);
                distance = haversineFunc(p1.getlat(), p1.getlng(), p2.getlat(), p2.getlng());
                totalKm += distance;
            }
        }

        Cw3.updateDroneStats(drone.getId(), totalKm);

        List<Point> newList = new ArrayList<>(deliveryLocations.subList(1, deliveryLocations.size() - 1));
        Cw3.logIndividualJourney(drone.getId(),start,newList,totalKm);

        return ResponseEntity.ok(deliveryOutput);
    }}

    @PostMapping(startPoint + "/calcMultiDeliveryPath")
    public ResponseEntity<DeliveryOutput> calcMultiDeliveryPath(@RequestBody List<MedDispatchRec> reqlist) throws JSONException {
        Drone[] drones = restTemplate.getForObject((ilpEndpoint + "/drones"), Drone[].class);
        DroneForServicePoint[] dronesForServicePoints = restTemplate.getForObject((ilpEndpoint + "/drones-for-service-points"), DroneForServicePoint[].class);
        ServicePoint[] ServicePoints = restTemplate.getForObject((ilpEndpoint + "/service-points"), ServicePoint[].class);
        RestrictedArea[] areas = restTemplate.getForObject((ilpEndpoint + "/restricted-areas"), RestrictedArea[].class);

        //split records

        List<MedDispatchRec> group1 =  new ArrayList<>();
        group1 = reqlist.subList(0, reqlist.size()/2);
        //System.out.println(group1.size());
        List<MedDispatchRec> group2 = new ArrayList<>();
        group2 = reqlist.subList(reqlist.size()/2, reqlist.size());
        //System.out.println(group2.size());

        ArrayList<List<MedDispatchRec>> groups = new ArrayList<>();
        groups.add(group1);
        groups.add(group2);


        Double totalCost = 0d;
        Integer totalMoves = 0;
        DeliveryOutput deliveryOutput = new DeliveryOutput();
        //get path for each group

        for(List<MedDispatchRec> group: groups){


            ArrayList<Pair<Drone,Point>>  droneWithPoint;
            droneWithPoint = QueryAvailable.query(drones, dronesForServicePoints,ServicePoints, group);


            if(droneWithPoint.isEmpty()){

                return ResponseEntity.ok(new DeliveryOutput());
            }

            Drone drone = droneWithPoint.getFirst().first;
            Point start = droneWithPoint.getFirst().second;

            ArrayList<Point> deliveryLocations = new ArrayList<>();
            for(MedDispatchRec medDispatchRec: group){
                deliveryLocations.add(medDispatchRec.getDelivery());
            }
            deliveryLocations.addFirst(start);
            deliveryLocations.addLast(start);

            //System.out.println("Locations size" + deliveryLocations.size());

            List<Raycasting.Line> lines = new ArrayList<>();
            for(RestrictedArea area: areas) {
                lines.addAll(area.areaToLines());
            }

            List<List<Point>> paths = new ArrayList<>();

            for (int i = 0; i<deliveryLocations.size()-1; i++){
                if(i == 0) {
                    paths.add(Astar.getPath(deliveryLocations.get(0), deliveryLocations.get(1), lines).first);

                } else {
                    paths.add(Astar.getPath(paths.getLast().getLast(), deliveryLocations.get(i + 1), lines).first);
                }
            }

            deliveryOutput.dronePaths = new ArrayList<>();
            DronePath dronePath = new DronePath();
            dronePath.droneId = drone.getId();
            dronePath.deliveries = new ArrayList<>();

            for (int i = 0; i < group.size(); i++){
                Delivery delivery = new Delivery();
                delivery.flightPath = paths.get(i);
                delivery.flightPath.add(paths.get(i).getLast());
                delivery.deliveryId = String.valueOf(group.get(i).getId());
                dronePath.deliveries.add(delivery);
                deliveryOutput.dronePaths.add(dronePath);

                Integer moves = paths.get(i).size() - 1;
                totalMoves += paths.get(i).size() - 1;
                Double cost = moves * drone.getCapability().getCostPerMove() +  drone.getCapability().getCostInitial() + drone.getCapability().getCostFinal();
                totalCost +=  cost;
            }
            // We also need the return journey
            Delivery delivery = new Delivery();
            delivery.flightPath = paths.getLast();
            delivery.flightPath.add(paths.getLast().getLast());
            delivery.deliveryId = null;
            dronePath.deliveries.add(delivery);
            totalMoves += paths.getLast().size() - 1;

            // Finally we add the drone path
            deliveryOutput.dronePaths.add(dronePath);




            totalMoves += paths.getLast().size() - 1;

        }
        deliveryOutput.totalCost = totalCost;
        deliveryOutput.totalMoves = totalMoves;



        return ResponseEntity.ok(deliveryOutput);
        }

    @PostMapping(startPoint + "/calcDeliveryPathAsGeoJson")
    public ResponseEntity<String> calcDeliveryPathAsGeoJson(@RequestBody List<MedDispatchRec> reqlist) throws JSONException{
        Drone[] drones = restTemplate.getForObject((ilpEndpoint + "/drones"), Drone[].class);
        DroneForServicePoint[] dronesForServicePoints = restTemplate.getForObject((ilpEndpoint + "/drones-for-service-points"), DroneForServicePoint[].class);
        ServicePoint[] ServicePoints = restTemplate.getForObject((ilpEndpoint + "/service-points"), ServicePoint[].class);
        RestrictedArea[] areas = restTemplate.getForObject((ilpEndpoint + "/restricted-areas"), RestrictedArea[].class);

        ArrayList<Pair<Drone,Point>>  droneWithPoint = new ArrayList<>();

        droneWithPoint = QueryAvailable.query(drones, dronesForServicePoints,ServicePoints, reqlist);

        if (droneWithPoint.isEmpty()){
            return ResponseEntity.ok("");
        }

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
            ArrayList<Point> path = new ArrayList<>();

            for(List<Point> Path: paths){
                for(Point point: Path){
                    path.add(point);
                }
            }


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
    }





