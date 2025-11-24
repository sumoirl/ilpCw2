package ilp.cw2.controllers;

import ilp.cw2.dtos.*;
import ilp.cw2.utils.Astar;
import ilp.cw2.utils.Pair;
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

        ArrayList<Drone> requirementDrones = new ArrayList<>();
        ArrayList<Pair<String, Integer>> timeDrones = new ArrayList<>();

        for (Drone drone : drones) {
            Boolean matchesAll = true;
            for (MedDispatchRec req : reqlist) {
                Boolean matches = true;
                while (matches) {
                    matches = (req.getRequirements().getCooling() == null || req.getRequirements().getCooling() == drone.getCapability().isCooling());
                    if (!matches) {
                        break;
                    }
                    matches = (req.getRequirements().getHeating() == null || req.getRequirements().getHeating() == drone.getCapability().isHeating());
                    break;
                }
                if (matches == false) {
                    matchesAll = false;
                    break;
                }
            }
            if (matchesAll) {
                requirementDrones.add(drone);
            }
        }

        ArrayList<Pair<DronesAvailibility, Integer>> DroneAvail = new ArrayList();

        for (DroneForServicePoint d : dronesForServicePoints) {
            for (DronesAvailibility drone : d.getDrones()) {
                DroneAvail.add(new Pair<>(drone, d.getServicePointId()));
            }
        }

         for (Pair<DronesAvailibility,Integer> drone : DroneAvail) {

            boolean coversAll = true;

            for (MedDispatchRec req : reqlist) {
                boolean covered = false;

                for (Availibility a : drone.first.getAvailability()) {
                    if (a.getDayOfWeek().equals(req.getDate().getDayOfWeek())
                            && !req.getTime().isBefore(a.getFrom())
                            && !req.getTime().isAfter(a.getUntil())) {
                        covered = true;
                        break;
                    }
                }
                if (!covered) {
                    coversAll = false;
                    break;
                }
            }
            if (coversAll) {
                timeDrones.add(new Pair<>(drone.first.getId(), drone.second));
            }
        }
         ArrayList<Pair<Drone, Integer>> availableDrones = new ArrayList<>();

        for (Drone d : requirementDrones) {
            for (Pair<String,Integer> t : timeDrones) {
                if(d.getId().equals(t.first)) {
                    availableDrones.add(new Pair<>(d,t.second));
                }
            }
        }

        Double totalCapacity = 0.0;
        for (MedDispatchRec rec : reqlist) {
            totalCapacity += rec.getRequirements().getCapacity();
        }

        Double finalTotalCapacity = totalCapacity;
        availableDrones.removeIf(d -> d.first.getCapability().getCapacity() < finalTotalCapacity);

        ArrayList<Pair<Drone, LngLatAlt>> DispatchLocations =  new ArrayList<>();

        for(Pair<Drone, Integer> drone:  availableDrones) {
            for(ServicePoint servicePoint: ServicePoints) {
                if(drone.second.equals(servicePoint.getId())) {
                    DispatchLocations.add(new Pair<>(drone.first, servicePoint.getLocation()));
                }
            }
        }

        ArrayList<String> ids = new ArrayList<>();

        for(Pair<Drone, LngLatAlt> drone : DispatchLocations) {
           Double totalDist = 0d;
            for(MedDispatchRec rec : reqlist) {
                totalDist += 2*(Math.sqrt(Math.pow((rec.getDelivery().getlng()-drone.second.getLng()),2)+Math.pow((rec.getDelivery().getlat()-drone.second.getLat()),2)));
            }
            Double moves = totalDist/0.00015;
            Double totalCost = moves * drone.first.getCapability().getCostPerMove();
            totalCost += drone.first.getCapability().getCostInitial() + drone.first.getCapability().getCostFinal();
            Double proRataCost = totalCost / reqlist.size();
            for(MedDispatchRec rec : reqlist) {
                if(rec.getRequirements().maxCostNull()){
                    System.out.println("meow");
                    continue;
                }
                System.out.println(drone.first.getId());
                System.out.println("ProRata Cost" + proRataCost);
                System.out.println("Max Cost" + rec.getRequirements().getMaxCost());
                if (proRataCost <= rec.getRequirements().getMaxCost()){
                    ids.add(drone.first.getId());
                }
            }
        }




        //for(Pair<Drone, LngLatAlt> d : DispatchLocations){
          //  ids.add(d.first.getId());
        //}

        return ResponseEntity.ok(ids);

    }

    @GetMapping("/test")
    public ResponseEntity<String> test() throws JSONException {
        Point start = new Point(-3.1894032589570713, 55.946379242828925);
        //Point end = new Point(-3.18505627826093, 55.94458510360218);
        Point end = new Point(-4.035788102304167, 55.764845368154454);
        //Point end = new Point(-3.177111113717501, 55.98152418168104);
        List<Point> path = Astar.getPath(start, end).first;

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




