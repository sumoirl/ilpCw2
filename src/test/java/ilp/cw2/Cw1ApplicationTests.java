package ilp.cw2;

import ilp.cw2.dtos.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class Cw1ApplicationTests {

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @LocalServerPort
    private int port;

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    // ---------- /actuator/health ----------
    @Test
    void actuatorHealthTest() {
        ResponseEntity<String> res = restTemplate.getForEntity(url("/actuator/health"), String.class);
        assert res.getStatusCode() == HttpStatus.OK;
        assert res.getBody() != null && res.getBody().contains("UP");
    }

    // ---------- /api/v1/uid ----------
    @Test
    void uidTest() {
        ResponseEntity<String> res = restTemplate.getForEntity(url("/api/v1/uid"), String.class);
        assert res.getStatusCode() == HttpStatus.OK;
        assert Objects.equals(res.getBody(), "s2544630");
    }

    // ---------- /distanceTo ----------
    @Test
    void distanceTo_Valid() {
        PositionRequest req = new PositionRequest(new Point(0.0, 0.0), new Point(1.0, 1.0));
        ResponseEntity<String> res = restTemplate.postForEntity(url("/api/v1/distanceTo"), req, String.class);
        assert res.getStatusCode() == HttpStatus.OK;
        assert res.getBody() != null && !res.getBody().isEmpty();
    }

    @Test
    void distanceTo_Invalid() {
        PositionRequest req = new PositionRequest(new Point(0.0, 0.0), null);
        ResponseEntity<String> res = restTemplate.postForEntity(url("/api/v1/distanceTo"), req, String.class);
        assert res.getStatusCode() == HttpStatus.BAD_REQUEST;
    }

    @Test
    void distanceTo_Null() {
        ResponseEntity<String> res = restTemplate.postForEntity(url("/api/v1/distanceTo"), null, String.class);
        assert res.getStatusCode() == HttpStatus.BAD_REQUEST;
    }

    // ---------- /isCloseTo ----------
    @Test
    void isCloseTo_Valid() {
        PositionRequest req = new PositionRequest(new Point(0.0, 0.0), new Point(0.0001, 0.0001));
        ResponseEntity<Boolean> res = restTemplate.postForEntity(url("/api/v1/isCloseTo"), req, Boolean.class);
        assert res.getStatusCode() == HttpStatus.OK;
        assert Boolean.TRUE.equals(res.getBody());
    }

    @Test
    void isCloseTo_Invalid() {
        PositionRequest req = new PositionRequest(new Point(0.0, 0.0), null);
        ResponseEntity<Boolean> res = restTemplate.postForEntity(url("/api/v1/isCloseTo"), req, Boolean.class);
        assert res.getStatusCode() == HttpStatus.BAD_REQUEST;
    }

    @Test
    void isCloseTo_SemanticError() {
        PositionRequest req = new PositionRequest(new Point(6000, 5000.0), null);
        ResponseEntity<Boolean> res = restTemplate.postForEntity(url("/api/v1/isCloseTo"), req, Boolean.class);
        assert res.getStatusCode() == HttpStatus.BAD_REQUEST;
    }

    @Test
    void isCloseTo_Null() {
        ResponseEntity<Boolean> res = restTemplate.postForEntity(url("/api/v1/isCloseTo"), new PositionRequest(null, null), Boolean.class);
        assert res.getStatusCode() == HttpStatus.BAD_REQUEST;
    }

    // ---------- /nextPosition ----------
    @Test
    void nextPosition_Valid() {
        NextPositionRequest req = new NextPositionRequest(new Point(126.9780, 37.5665), 90);
        ResponseEntity<Point> res = restTemplate.postForEntity(url("/api/v1/nextPosition"), req, Point.class);
        assert res.getStatusCode() == HttpStatus.OK;
        assert res.getBody() != null;
    }

    @Test
    void nextPosition_InvalidAngle() {
        NextPositionRequest req = new NextPositionRequest(new Point(0.0, 0.0), 999);
        ResponseEntity<Point> res = restTemplate.postForEntity(url("/api/v1/nextPosition"), req, Point.class);
        assert res.getStatusCode() == HttpStatus.BAD_REQUEST;
    }

    @Test
    void nextPosition_Null() {
        ResponseEntity<Point> res = restTemplate.postForEntity(url("/api/v1/nextPosition"), null, Point.class);
        assert res.getStatusCode() == HttpStatus.BAD_REQUEST;
    }

    // ---------- /isInRegion ----------
    @Test
    void isInRegion_ValidInside() {
        ArrayList<Point> vertices = new ArrayList<>();
        vertices.add(new Point(0, 0));
        vertices.add(new Point(0, 1));
        vertices.add(new Point(1, 1));
        vertices.add(new Point(1, 0));
        vertices.add(new Point(0, 0)); // closed polygon

        Region region = new Region("polygon", vertices);
        inRegionReq req = new inRegionReq(region, new Point(0.5, 0.5)); // clearly inside
        ResponseEntity<Boolean> res = restTemplate.postForEntity(url("/api/v1/isInRegion"), req, Boolean.class);
        assert res.getStatusCode() == HttpStatus.OK;
        assert Boolean.TRUE.equals(res.getBody());
    }

    @Test
    void isInRegion_ValidOutside() {
        ArrayList<Point> vertices = new ArrayList<>();
        vertices.add(new Point(0, 0));
        vertices.add(new Point(0, 1));
        vertices.add(new Point(1, 1));
        vertices.add(new Point(1, 0));
        vertices.add(new Point(0, 0)); // closed polygon

        Region region = new Region("polygon", vertices);
        inRegionReq req = new inRegionReq(region, new Point(1.5, 1.5)); // clearly outside
        ResponseEntity<Boolean> res = restTemplate.postForEntity(url("/api/v1/isInRegion"), req, Boolean.class);
        assert res.getStatusCode() == HttpStatus.OK;
        assert Boolean.FALSE.equals(res.getBody());
    }

    @Test
    void isInRegion_PointOnEdge() {
        ArrayList<Point> vertices = new ArrayList<>();
        vertices.add(new Point(0, 0));
        vertices.add(new Point(0, 1));
        vertices.add(new Point(1, 1));
        vertices.add(new Point(1, 0));
        vertices.add(new Point(0, 0)); // closed polygon

        Region region = new Region("polygon", vertices);
        inRegionReq req = new inRegionReq(region, new Point(0, 0.5)); // point exactly on left edge
        ResponseEntity<Boolean> res = restTemplate.postForEntity(url("/api/v1/isInRegion"), req, Boolean.class);
        assert res.getStatusCode() == HttpStatus.OK;
        // Depending on your implementation, edge points may count as inside → adapt if needed
        assert res.getBody() != null;
    }

    @Test
    void isInRegion_Invalid_UnclosedPolygon() {
        ArrayList<Point> vertices = new ArrayList<>();
        vertices.add(new Point(0, 0));
        vertices.add(new Point(0, 1));
        vertices.add(new Point(1, 1));
        vertices.add(new Point(1, 0)); // not closed

        Region region = new Region("polygon", vertices);
        inRegionReq req = new inRegionReq(region, new Point(0.5, 0.5));
        ResponseEntity<Boolean> res = restTemplate.postForEntity(url("/api/v1/isInRegion"), req, Boolean.class);
        assert res.getStatusCode() == HttpStatus.BAD_REQUEST;
    }

    @Test
    void isInRegion_Null() {
        ResponseEntity<Boolean> res = restTemplate.postForEntity(url("/api/v1/isInRegion"), new inRegionReq(null, null), Boolean.class);
        assert res.getStatusCode() == HttpStatus.BAD_REQUEST;
    }
    @Test
    void isInRegion_SemanticError() {
        ArrayList<Point> vertices = new ArrayList<>();
        vertices.add(new Point(50000, 0));
        vertices.add(new Point(0, 1));
        vertices.add(new Point(1, 1));
        vertices.add(new Point(1, 0)); // not closed

        Region region = new Region("polygon", vertices);
        inRegionReq req = new inRegionReq(region, new Point(0.5, 0.5));
        ResponseEntity<Boolean> res = restTemplate.postForEntity(url("/api/v1/isInRegion"), req, Boolean.class);
        assert res.getStatusCode() == HttpStatus.BAD_REQUEST;
    }

}
