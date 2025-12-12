package ilp.cw2.utils;

import ilp.cw2.dtos.*;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public class Multi {
    public static DeliveryOutput getMultiPath(List<MedDispatchRec> reqlist, Drone[] drones,DroneForServicePoint[] dronesForServicePoints, ServicePoint[] ServicePoints,RestrictedArea[] areas ) {
        List<MedDispatchRec> group1 =  new ArrayList<>();
        group1 = reqlist.subList(0, reqlist.size()/2);
        //System.out.println(group1.size());
        List<MedDispatchRec> group2 = new ArrayList<>();
        group2 = reqlist.subList(reqlist.size()/2, reqlist.size());
        //System.out.println(group2.size());

        ArrayList<List<MedDispatchRec>> groups = new ArrayList<>();
        groups.add(group1);
        groups.add(group2);

        System.out.println("i hate being in a list" + groups);

        Double totalCost = 0d;
        Integer totalMoves = 0;
        DeliveryOutput deliveryOutput = new DeliveryOutput();
        //get path for each group

        for(List<MedDispatchRec> group: groups){

            System.out.println(group);
            ArrayList<Pair<Drone, Point>>  droneWithPoint;
            droneWithPoint = QueryAvailable.query(drones, dronesForServicePoints,ServicePoints, group);
            System.out.println(droneWithPoint);

            if(droneWithPoint.isEmpty()){
                System.out.println("MEOW");
                return new DeliveryOutput();
            }

            Drone drone = droneWithPoint.getFirst().first;
            Point start = droneWithPoint.getFirst().second;

            ArrayList<Point> deliveryLocations = new ArrayList<>();
            for(MedDispatchRec medDispatchRec: group){
                deliveryLocations.add(medDispatchRec.getDelivery());
            }
            deliveryLocations.addFirst(start);
            deliveryLocations.addLast(start);

            for(Point p: deliveryLocations){
                System.out.println("Location "+p.getlng()+" "+p.getlat());
            }
            //System.out.println("Locations size" + deliveryLocations.size());

            List<Raycasting.Line> lines = new ArrayList<>();
            for(RestrictedArea area: areas) {
                lines.addAll(area.areaToLines());
            }

            List<List<Point>> paths = new ArrayList<>();

            for (int i = 0; i<deliveryLocations.size()-1; i++){
                System.out.println("i: "+i);
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

        System.out.println(totalMoves);
        System.out.println(totalCost);

        return deliveryOutput;
    }
}
