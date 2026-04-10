package fr.modulefans.models;

public class Movie {

    private int id;
    private String title;
    private String genres; // pipe-separated: "Action|Comedy"
    private int year;
    private double rating;
    private int duration; // minutes
    private double score; // computed match score

    public Movie(int id, String title, String genres, int year, double rating, int duration) {
        this.id = id;
        this.title = title;
        this.genres = genres;
        this.year = year;
        this.rating = rating;
        this.duration = duration;
        this.score = 0.0;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getGenres() { return genres; }
    public int getYear() { return year; }
    public double getRating() { return rating; }
    public int getDuration() { return duration; }
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    public String[] getGenreArray() {
        return genres.split("\\|");
    }
}
