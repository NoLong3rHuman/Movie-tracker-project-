package org.example.movietracker;

public class Movie {
    private int id;
    private String title;
    private String year;
    private String posterUrl;
    private String type; // "Movie" or "Show"
    private double rating;
    private boolean watched;
    private int sortOrder;


    private String genre;

    public Movie(String title, String year, String posterUrl) {
        this.title = title;
        this.year = year;
        this.posterUrl = posterUrl;
        this.type = "Movie";
        this.rating = 0;
        this.watched = false;
    }
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }

    public Movie(String title, String year, String posterUrl, double rating, boolean watched) {
        this.title = title;
        this.year = year;
        this.posterUrl = posterUrl;
        this.type = "Movie";
        this.rating = rating;
        this.watched = watched;
    }

    public Movie(String title, String year, String posterUrl, String type, double rating, boolean watched) {
        this.title = title;
        this.year = year;
        this.posterUrl = posterUrl;
        this.type = type;
        this.rating = rating;
        this.watched = watched;
    }

    public int getId() {return id;}
    public Movie(String title, String year, String posterUrl, String type, int rating, boolean watched, String genre) {
        this.title = title;
        this.year = year;
        this.posterUrl = posterUrl;
        this.type = type;
        this.rating = rating;
        this.watched = watched;
        this.genre = genre;
    }

    public String getGenre() {return genre;}
    public void setGenre(String genre) {this.genre = genre;}
    public String getTitle() { return title; }
    public String getYear() { return year; }
    public String getPosterUrl() { return posterUrl; }
    public String getType() { return type; }
    public double getRating() { return rating; }
    public boolean isWatched() { return watched; }

    public void setId(int id) { this.id = id;}
    public void setTitle(String title) { this.title = title; }
    public void setYear(String year) { this.year = year; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }
    public void setType(String type) { this.type = type; }
    public void setRating(double rating) { this.rating = rating; }
    public void setWatched(boolean watched) { this.watched = watched; }

    @Override
    public String toString() {

        return title + " (" + year + ")";
    }
}
