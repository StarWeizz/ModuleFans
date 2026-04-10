package fr.modulefans.models;

import javafx.beans.property.*;

public class WeatherRecord {

    private final StringProperty date;
    private final StringProperty ville;
    private final StringProperty region;
    private final DoubleProperty tempMax;
    private final DoubleProperty tempMin;
    private final DoubleProperty precipitation;
    private final DoubleProperty humidite;
    private boolean anomalie;

    public WeatherRecord(String date, String ville, String region,
                         double tempMax, double tempMin,
                         double precipitation, double humidite) {
        this.date = new SimpleStringProperty(date);
        this.ville = new SimpleStringProperty(ville);
        this.region = new SimpleStringProperty(region);
        this.tempMax = new SimpleDoubleProperty(tempMax);
        this.tempMin = new SimpleDoubleProperty(tempMin);
        this.precipitation = new SimpleDoubleProperty(precipitation);
        this.humidite = new SimpleDoubleProperty(humidite);
        this.anomalie = false;
    }

    public String getDate() { return date.get(); }
    public StringProperty dateProperty() { return date; }

    public String getVille() { return ville.get(); }
    public StringProperty villeProperty() { return ville; }

    public String getRegion() { return region.get(); }
    public StringProperty regionProperty() { return region; }

    public double getTempMax() { return tempMax.get(); }
    public DoubleProperty tempMaxProperty() { return tempMax; }

    public double getTempMin() { return tempMin.get(); }
    public DoubleProperty tempMinProperty() { return tempMin; }

    public double getPrecipitation() { return precipitation.get(); }
    public DoubleProperty precipitationProperty() { return precipitation; }

    public double getHumidite() { return humidite.get(); }
    public DoubleProperty humiditeProperty() { return humidite; }

    public boolean isAnomalie() { return anomalie; }
    public void setAnomalie(boolean anomalie) { this.anomalie = anomalie; }
}
