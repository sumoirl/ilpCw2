package ilp.cw2.controllers;

import ilp.cw2.dtos.NextPositionRequest;
import ilp.cw2.dtos.Point;
import ilp.cw2.dtos.PositionRequest;
import ilp.cw2.dtos.inRegionReq;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class BasicController {
    private final String word = "/api/v1";

    @GetMapping("/actuator/health")
    public String health(){
        return "{\"status\": \"UP\"}";
    }

    @GetMapping(word + "/uid")
    public String uid(){
        return "s2544630";
    }

    @PostMapping(word + "/distanceTo")
    public ResponseEntity<String> distanceTo(@RequestBody PositionRequest req) {
        if (req == null || req.getPosition1() == null || req.getPosition2() == null ||  !req.getPosition1().isValid() || !req.getPosition2().isValid()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        double dist = Math.sqrt(Math.pow(req.getPosition2().getlng() - req.getPosition1().getlng(), 2)
                + Math.pow(req.getPosition2().getlat() - req.getPosition1().getlat(), 2));
        return ResponseEntity.status(HttpStatus.OK).body(String.valueOf(dist));
    }

    @PostMapping(word + "/isCloseTo")
    public ResponseEntity<Boolean> isCloseTo(@RequestBody PositionRequest req){
        if (req == null || req.getPosition1() == null || req.getPosition2() == null|| !req.getPosition1().isValid() || !req.getPosition2().isValid()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        double dist = Math.sqrt(Math.pow(req.getPosition2().getlng() - req.getPosition1().getlng(), 2)
                + Math.pow(req.getPosition2().getlat() - req.getPosition1().getlat(), 2));
        return ResponseEntity.status(HttpStatus.OK).body(dist < 0.00015);
    }

    @PostMapping(word + "/nextPosition")
    public ResponseEntity<Point> nextPosition(@RequestBody NextPositionRequest req){
        if (req == null || req.getStart() == null || req.getAngle() > 360 || req.getAngle() < 0 || !req.getStart().isValid()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        double rad = Math.toRadians(req.getAngle());
        double newLat = req.getStart().getlat() + (0.00015 * Math.sin(rad));
        double newLng = req.getStart().getlng() + (0.00015 * Math.cos(rad));
        return ResponseEntity.status(HttpStatus.OK).body(new Point(newLng, newLat));
    }

    @PostMapping(word + "/isInRegion")
    public ResponseEntity<Boolean> isInRegion(@RequestBody inRegionReq req) {
        // Check if request or its components are null
        if (req == null || req.getRegion() == null || req.getPosition() == null|| !req.getRegion().isValid()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }

        // Get vertices safely
        ArrayList<Point> vertices = req.getRegion().getVertices();
        if (vertices == null || vertices.size() < 3) { // need at least 3 points for a polygon
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }

        // Ensure polygon is closed
        if (!vertices.get(0).equal(vertices.get(vertices.size() - 1))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }

        boolean result = false;
        for (int i = 0; i < vertices.size() - 1; i++){
            Point point1 = vertices.get(i);
            Point point2 = vertices.get(i + 1);

            // Extra null check for each vertex just in case
            if (point1 == null || point2 == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
            }

            if (req.intersects(point1, point2, req.getPosition())){
                result = !result;
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
