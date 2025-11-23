package ilp.cw2.cw3;

import java.io.*;
import java.nio.file.*;
import java.util.*;

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

}
