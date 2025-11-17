package ilp.cw2.controllers;


import ilp.cw2.dtos.Capabilities;
import ilp.cw2.dtos.Drone;
import ilp.cw2.dtos.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.Boolean.valueOf;

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


}
