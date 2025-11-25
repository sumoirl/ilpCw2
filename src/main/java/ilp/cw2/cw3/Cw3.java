package ilp.cw2.cw3;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import com.opencsv.*;
import com.opencsv.exceptions.CsvException;


public class Cw3 {

    private static final String FILE_NAME = "drone_data.csv";

    static{
        File f = new File(FILE_NAME);
        if(!f.exists()){
            try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                pw.println("droneId,totalJourneys,totalDistanceKm");
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static synchronized void updateDroneStats(String droneId, double newDistanceKm) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


