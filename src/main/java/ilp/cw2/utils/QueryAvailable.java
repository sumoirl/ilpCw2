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
            for(MedDispatchRec rec : reqlist) {
                if(rec.getRequirements().maxCostNull()){
                    System.out.println("meow");
                    continue;
                }
                System.out.println(drone.first.getId());
                System.out.println("ProRata Cost" + proRataCost);
                System.out.println("Max Cost" + rec.getRequirements().getMaxCost());
                if (proRataCost <= rec.getRequirements().getMaxCost()){
                    droneWithPoint.add(new Pair<>(drone.first, new Point(drone.second.getLng(), drone.second.getLat())));

                }
            }
        }
        return droneWithPoint;
    }
}
