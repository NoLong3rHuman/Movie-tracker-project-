package org.example.movietracker;

public class Movie {
    private String title;
    private String year;
    private String posterUrl;
    private int rating;
    private boolean watched;

    public Movie(String title, String year, String posterUrl) {
        this.title = title;
        this.year = year;
        this.posterUrl = posterUrl;
        this.rating = 0;
        this.watched = false;
    }
    public Movie(String title, String year, String posterUrl, int rating, boolean watched) {
        this.title = title;
        this.year = year;
        this.posterUrl = posterUrl;
        this.rating = rating;
        this.watched = watched;
    }
    // Getters
    public String getTitle() { return title; }
    public String getYear() { return year; }
    public String getPosterUrl() { return posterUrl; }
    public int getRating() { return rating; }
    public boolean isWatched() { return watched; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setYear(String year) { this.year = year; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }
    public void setRating(int rating) { this.rating = rating; }
    public void setWatched(boolean watched) { this.watched = watched; }

    @Override
    public String toString() {
        return title + " (" + year + ")";
    }
}
