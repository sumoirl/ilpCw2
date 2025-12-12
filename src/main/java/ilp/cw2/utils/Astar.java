package ilp.cw2.utils;

import ilp.cw2.dtos.Point;

import java.util.*;

public class Astar {

    public static class Node{

        public Point Location;
        public Node previous;
        public Double g,h,f;

        public Node(Point Location, Node previous, Double g, Double h, Double f){
            this.Location = Location;
            this.previous = previous;
            this.g = g;
            this.h = h;
            this.f = f;
        }
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

    public static ArrayList<Point> getNeighbors(Node n){
        ArrayList<Point> neighbors = new ArrayList<>();

        for(Double angle: angles){
            double rad = Math.toRadians(angle);
            double newLat = n.Location.getlat() + (0.00015 * Math.sin(rad));
            double newLng = n.Location.getlng() + (0.00015 * Math.cos(rad));
            neighbors.add(new Point(newLng, newLat));
        }

        return neighbors;
    }

    public static Double getDistance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p1.getlng() - p2.getlng(), 2) + Math.pow(p1.getlat() - p2.getlat(), 2));
    }

    public static Pair<List<Point>, Double> getPath(Point start, Point end, List<Raycasting.Line> lines) {
        Set<Point> visited = new HashSet<>();
        HashMap<Point,Node> map = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingDouble(node -> node.f));

        Node last = null;

        Node startNode = new Node(start, null, 0d, getDistance(start, end), getDistance(start, end));
        pq.add(startNode);
        map.put(start, startNode);

        while(!pq.isEmpty()){
            Node current = pq.poll();
            if(visited.contains(current.Location))  continue;
            visited.add(current.Location);
            map.remove(current.Location);

            if(getDistance(current.Location, end) <= 0.00015){
              last = current;
              break;
            }

            ArrayList<Point> neighbors = getNeighbors(current);

            for(Point neighbor: neighbors){

                if(visited.contains(neighbor)) continue;

                Raycasting.Line currentLine = new Raycasting.Line(current.Location, neighbor);
                Boolean intersects = false;
                for(Raycasting.Line line: lines) {
                    intersects = Raycasting.intersects(currentLine, line);
                    if(intersects) break;
                }
                if(intersects) continue;
                
                Double g = current.g + 0.00015;
                Double h = getDistance(neighbor, end) * 1.1;
                Double f = g + h;
                Node existingNode = map.get(neighbor);

                if(existingNode == null){
                    existingNode = new Node(neighbor, current, g, h, f);
                    map.put(neighbor, existingNode);
                    pq.add(existingNode);
                }
                else{
                    if(f < existingNode.f){
                        existingNode = new Node(neighbor, current, g, h, f);
                        map.put(neighbor, existingNode);
                        pq.add(existingNode);
                    }
                }
            }
        }

        ArrayList<Point> path = new ArrayList<>();

        if(last == null){

            return new Pair<>(path, -1.0);
        }


        double finalCost = last.g;
        while(last!= null){
            path.add(last.Location);
            last = last.previous;
        }

        return new Pair<>(path.reversed(), finalCost);
    }

}
