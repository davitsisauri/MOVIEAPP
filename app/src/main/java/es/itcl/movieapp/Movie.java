package es.itcl.movieapp;

/**
 * Created by Davit.Sisauri on 04/09/2015.
 */
public class Movie {

    private int id_movie;
    private String title;
    private String original_title;
    private boolean adult;
    private String backdrop_path;
    private String poster_path;
    private String overview;
    private String release_date;
    private String original_language;
    private Double popularity;
    private boolean video;
    private Double vote_average;
    private int vote_count;

    public int getIdMovie() {
        return id_movie;
    }
    public void setIdMovie(int id_movie) {
        this.id_movie = id_movie;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginalTitle() {
        return original_title;
    }
    public void setOriginalTitle(String original_title) {
        this.original_title = original_title;
    }

    public boolean getAdult() {
        return adult;
    }
    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getBackdropPath() {
        return backdrop_path;
    }
    public void setBackdropPath(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public String getPosterPath() {
        return poster_path;
    }
    public void setPosterPath(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getOverview() {
        return overview;
    }
    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return release_date;
    }
    public void setReleaseDate(String release_date) {
        this.release_date = release_date;
    }

    public String getOriginalLanguage() {
        return original_language;
    }
    public void setOriginalLanguage(String original_language) {
        this.original_language = original_language;
    }

    public Double getPopularity() {
        return popularity;
    }
    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public boolean getVideo() {
        return video;
    }
    public void setVideo(boolean video) {
        this.video = video;
    }


    public Double getVoteAvarage() {
        return vote_average;
    }
    public void setVoteAverage(Double vote_average) {
        this.vote_average = vote_average;
    }

    public int getVoteCount() {
        return vote_count;
    }
    public void setVoteCount(int vote_count) {
        this.vote_count = vote_count;
    }
}
