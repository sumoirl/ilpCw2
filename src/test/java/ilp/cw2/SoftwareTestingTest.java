package ilp.cw2;

import ilp.cw2.dtos.DeliveryOutput;
import ilp.cw2.dtos.MedDispatchRec;
import ilp.cw2.dtos.Point;
import ilp.cw2.dtos.Requirements;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Random;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class SoftwareTestingTest {

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @LocalServerPort
    private int port;

    @Test
    void under30SecondsTest(){
        long start = System.currentTimeMillis();

        String url = "http://localhost:"+port+"/api/v1/calcDeliveryPath";

        ArrayList<MedDispatchRec> medDispatchRecs = generateDispatches(10);
        restTemplate.postForEntity(url,medDispatchRecs, DeliveryOutput.class);

        long endTime = System.currentTimeMillis();
        long duration = endTime - start;

        System.out.println("Duration " +  duration / 1000.0 + " seconds");
        assert(duration < 30000);

    }

    ArrayList<MedDispatchRec> generateDispatches(int num){

        double minLng = -3.0868645249437066;
        double maxLng = -3.283441268117258;

        double minLat = 55.899409291104945;
        double maxLat = 55.988184242060754;


        ArrayList<MedDispatchRec> dispatches = new ArrayList<>();

        for(int i = 0; i < num; i++){
            double randomLng = Math.random()*(maxLng-minLng)+minLng;
            double randomLat = Math.random()*(maxLat-minLat)+minLat;
            Point randomPoint = new Point(randomLng,randomLat);

            MedDispatchRec medDispatchRec = MedDispatchRec.builder()
                    .id((int)(Math.random() * 101))
                    .date(LocalDate.of(2025,1,5))
                    .time(LocalTime.of(12,0))
                    .requirements(Requirements.builder().capacity(0.0).cooling(false).heating(false).build())
                    .delivery(randomPoint)
                    .build();

            dispatches.add(medDispatchRec);
        }
        return dispatches;
    }





}
