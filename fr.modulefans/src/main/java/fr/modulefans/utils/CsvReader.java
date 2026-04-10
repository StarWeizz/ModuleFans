package fr.modulefans.utils;

import fr.modulefans.models.WeatherRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CsvReader {

    /**
     * Reads the embedded meteo.csv from resources.
     * Format: date,ville,region,temp_max,temp_min,precipitation,humidite
     */
    public static List<WeatherRecord> readMeteoFromResources() {
        List<WeatherRecord> records = new ArrayList<>();
        InputStream is = CsvReader.class.getResourceAsStream("/fr/modulefans/assets/data/meteo.csv");
        if (is == null) return records;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            boolean header = true;
            while ((line = br.readLine()) != null) {
                if (header) { header = false; continue; }
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length < 7) continue;
                try {
                    WeatherRecord r = new WeatherRecord(
                        parts[0].trim(),
                        parts[1].trim(),
                        parts[2].trim(),
                        Double.parseDouble(parts[3].trim()),
                        Double.parseDouble(parts[4].trim()),
                        Double.parseDouble(parts[5].trim()),
                        Double.parseDouble(parts[6].trim())
                    );
                    records.add(r);
                } catch (NumberFormatException ignored) {}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }

    /**
     * Reads a user-provided CSV file.
     */
    public static List<WeatherRecord> readMeteoFromFile(java.io.File file) {
        List<WeatherRecord> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new java.io.FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            boolean header = true;
            while ((line = br.readLine()) != null) {
                if (header) { header = false; continue; }
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length < 7) continue;
                try {
                    WeatherRecord r = new WeatherRecord(
                        parts[0].trim(), parts[1].trim(), parts[2].trim(),
                        Double.parseDouble(parts[3].trim()),
                        Double.parseDouble(parts[4].trim()),
                        Double.parseDouble(parts[5].trim()),
                        Double.parseDouble(parts[6].trim())
                    );
                    records.add(r);
                } catch (NumberFormatException ignored) {}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }
}
