package fr.modulefans.models;

import java.util.List;

public class UserPreferences {
    private List<String> favoriteGenres;
    private int minYear;
    private int maxYear;
    private double minRating;

    public UserPreferences(List<String> favoriteGenres, int minYear, int maxYear, double minRating) {
        this.favoriteGenres = favoriteGenres;
        this.minYear = minYear;
        this.maxYear = maxYear;
        this.minRating = minRating;
    }

    public List<String> getFavoriteGenres() { return favoriteGenres; }
    public int getMinYear() { return minYear; }
    public int getMaxYear() { return maxYear; }
    public double getMinRating() { return minRating; }
}
