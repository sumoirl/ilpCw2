package ilp.cw2.utils;

import ilp.cw2.dtos.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public class QueryAvailable {

    public static ArrayList<Pair<Drone, Point>>query(Drone[] drones, DroneForServicePoint[] dronesForServicePoints, ServicePoint[] ServicePoints,  List<MedDispatchRec> reqlist){
        ArrayList<Drone> requirementDrones = new ArrayList<>();
        ArrayList<Pair<String, Integer>> timeDrones = new ArrayList<>();

        for (Drone drone : drones) {
            Boolean matchesAll = true;
            for (MedDispatchRec req : reqlist) {
                Boolean matches = true;
                if (req.getRequirements().getCooling() != null) {
                    if (req.getRequirements().getCooling() && !drone.getCapability().isCooling()) matches = false;
                }
                if (req.getRequirements().getHeating() != null) {
                    if (req.getRequirements().getHeating() && !drone.getCapability().isHeating()) matches = false;
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
        
        //testing meeting

        // Print all the drones!!!
        List<String> ids = new ArrayList<>();
        for (Drone drone : requirementDrones) {
            ids.add(drone.getId());
        }


        ArrayList<Pair<DronesAvailibility, Integer>> DroneAvail = new ArrayList<>();

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
                    if (req.isDateNull() && req.isTimeNull()) {
                        covered = true;
                        break;
                    } else if (req.isDateNull()) {
                        if (!req.getTime().isBefore(a.getFrom()) && !req.getTime().isAfter(a.getUntil())) {
                            covered = true;
                            break;
                        }
                    } else if (req.isTimeNull()) {
                        if (a.getDayOfWeek().equals(req.getDate().getDayOfWeek())) {
                            covered = true;
                            break;
                        }
                    } else if (a.getDayOfWeek().equals(req.getDate().getDayOfWeek())
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

        // Print time drones
        ids = new ArrayList<>();
        for (Pair<String, Integer> drone : timeDrones) {
            ids.add(drone.first);
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

        // Print available drones
        ids = new ArrayList<>();
        for (Pair<Drone, Integer> drone : availableDrones) {
            ids.add(drone.first.getId());
        }


        ArrayList<Pair<Drone, LngLatAlt>> DispatchLocations =  new ArrayList<>();

        for(Pair<Drone, Integer> drone:  availableDrones) {
            for(ServicePoint servicePoint: ServicePoints) {
                if(drone.second.equals(servicePoint.getId())) {
                    DispatchLocations.add(new Pair<>(drone.first, servicePoint.getLocation()));
                }
            }
        }

        // Print droneWithPoint
        ids = new ArrayList<>();
        for (Pair<Drone, LngLatAlt> drone : DispatchLocations) {
            ids.add(drone.first.getId());
        }


        ArrayList<Pair<Drone, Point>> droneWithPoint =  new ArrayList<>();

        for(Pair<Drone, LngLatAlt> drone : DispatchLocations) {
            Double totalDist = 0d;
            for(MedDispatchRec rec : reqlist) {
                totalDist += 2*(Math.sqrt(Math.pow((rec.getDelivery().getlng()-drone.second.getLng()),2)+Math.pow((rec.getDelivery().getlat()-drone.second.getLat()),2)));
            }
            Double moves = totalDist/0.00015;
            Double totalCost = moves * drone.first.getCapability().getCostPerMove();
            totalCost += drone.first.getCapability().getCostInitial() + drone.first.getCapability().getCostFinal();
            Double proRataCost = totalCost / reqlist.size();
            Boolean matchesAll = true;
            for(MedDispatchRec rec : reqlist) {
                if(rec.getRequirements().maxCostNull()){

                    continue;
                }

                if (proRataCost > rec.getRequirements().getMaxCost()) {
                    matchesAll = false;
                    break;
                }
            }
            if (matchesAll) {
                droneWithPoint.add(new Pair<>(drone.first, new Point(drone.second.getLng(), drone.second.getLat())));
            }
        }

        // Print droneWithPoint
        ids = new ArrayList<>();
        for (Pair<Drone, Point> drone : droneWithPoint) {
            ids.add(drone.first.getId());
        }


        return droneWithPoint;
    }
}
