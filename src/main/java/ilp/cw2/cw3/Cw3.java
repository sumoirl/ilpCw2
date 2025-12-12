package ilp.cw2.cw3;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import com.opencsv.*;
import com.opencsv.exceptions.CsvException;
import ilp.cw2.dtos.Point;


public class Cw3 {




    public static synchronized void updateDroneStats(String droneId, double newDistanceKm) {

        String home = System.getProperty("user.home");
        String folder = home + File.separator + "ILP_DroneLogs";

        File dir = new File(folder);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String FILE_NAME = folder + File.separator + "drone_data.csv";

        File f = new File(FILE_NAME);
            if(!f.exists()){
                try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                    pw.println("droneId,totalJourneys,totalDistanceKm");
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }


        Map<String, double[]> stats = new HashMap<>();

        // Read existing data
        try (CSVReader reader = new CSVReader(new FileReader(FILE_NAME))) {
            List<String[]> rows = reader.readAll();
            for (int i = 1; i < rows.size(); i++) { // skip header
                String[] r = rows.get(i);
                String id = r[0];
                double journeys = Double.parseDouble(r[1]);
                double dist = Double.parseDouble(r[2]);
                stats.put(id, new double[]{journeys, dist});
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }

        // Update stats
        double[] entry = stats.getOrDefault(droneId, new double[]{0, 0});
        entry[0] += 1;
        entry[1] += newDistanceKm;
        stats.put(droneId, entry);

        // Write updated data back
        try (CSVWriter writer = new CSVWriter(new FileWriter(FILE_NAME))) {
            writer.writeNext(new String[]{"droneId", "totalJourneys", "totalDistanceKm"});
            for (Map.Entry<String, double[]> e : stats.entrySet()) {
                writer.writeNext(new String[]{
                        String.valueOf(e.getKey()),
                        String.valueOf((int) e.getValue()[0]),
                        String.format("%.3f", e.getValue()[1])
                });
            }
            System.out.println("Data written to drone_data.csv");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void logIndividualJourney(String droneId, Point startLocation, List<Point> deliveryLocations,double distanceTravelledKm) {
        String home = System.getProperty("user.home");
        String folder = home + File.separator + "ILP_DroneLogs";

        File dir = new File(folder);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String FILE_PATH = folder + File.separator + "journey_log.csv";

        // Ensure file exists and has header
        File f = new File(FILE_PATH);
        if (!f.exists()) {
            try (CSVWriter writer = new CSVWriter(new FileWriter(f))) {
                writer.writeNext(new String[]{
                        "droneID",
                        "startLocation lng lat",
                        "deliveryLocations lng lat",
                        "distanceTravelled"
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Convert the deliveryLocations list into a single CSV-safe string
        String deliveryLocationsString = formatLocationsList(deliveryLocations);

        // Append the new journey log entry
        try (CSVWriter writer = new CSVWriter(new FileWriter(FILE_PATH, true))) {
            writer.writeNext(new String[]{
                    droneId,
                    (startLocation.getlng() +" " + startLocation.getlat()),
                    deliveryLocationsString,
                    String.format("%.3f", distanceTravelledKm)
            });
            System.out.println("Data written to journey_log.csv");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String formatLocationsList(List<Point> locations) {
        if (locations == null || locations.isEmpty()) {
            return "";
        }

        List<String> formatted = new ArrayList<>();

        for (Point p : locations) {
            formatted.add("(" + p.getlng() + ", " + p.getlat() + ")");
        }

        return String.join("; ", formatted);
    }
}
