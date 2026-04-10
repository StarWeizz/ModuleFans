package fr.modulefans.services;

import fr.modulefans.models.WeatherRecord;

import java.util.List;

public class MeteoService {

    private static final double ANOMALY_THRESHOLD = 15.0; // °C above mean = anomaly (per spec)
    private static final double STD_MULTIPLIER = 2.0;     // also flag if > mean + 2*stdDev

    public double calcMoyenne(List<WeatherRecord> records) {
        if (records.isEmpty()) return 0;
        return records.stream().mapToDouble(WeatherRecord::getTempMax).average().orElse(0);
    }

    public double calcMin(List<WeatherRecord> records) {
        return records.stream().mapToDouble(WeatherRecord::getTempMax).min().orElse(0);
    }

    public double calcMax(List<WeatherRecord> records) {
        return records.stream().mapToDouble(WeatherRecord::getTempMax).max().orElse(0);
    }

    public double calcEcartType(List<WeatherRecord> records) {
        if (records.isEmpty()) return 0;
        double mean = calcMoyenne(records);
        double variance = records.stream()
            .mapToDouble(r -> Math.pow(r.getTempMax() - mean, 2))
            .average().orElse(0);
        return Math.sqrt(variance);
    }

    /**
     * Flags anomalies: temp_max > mean + ANOMALY_THRESHOLD  OR  temp_max > mean + 2*stdDev
     */
    public void detectAnomalies(List<WeatherRecord> records) {
        double mean = calcMoyenne(records);
        double std = calcEcartType(records);
        double upperBound = mean + Math.min(ANOMALY_THRESHOLD, STD_MULTIPLIER * std);
        double lowerBound = mean - STD_MULTIPLIER * std;
        for (WeatherRecord r : records) {
            r.setAnomalie(r.getTempMax() > upperBound || r.getTempMax() < lowerBound);
        }
    }

    public long countAnomalies(List<WeatherRecord> records) {
        return records.stream().filter(WeatherRecord::isAnomalie).count();
    }

    public WeatherRecord getHottestDay(List<WeatherRecord> records) {
        return records.stream()
            .max((a, b) -> Double.compare(a.getTempMax(), b.getTempMax()))
            .orElse(null);
    }

    public WeatherRecord getColdestDay(List<WeatherRecord> records) {
        return records.stream()
            .min((a, b) -> Double.compare(a.getTempMax(), b.getTempMax()))
            .orElse(null);
    }
}
