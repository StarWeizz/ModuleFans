package fr.modulefans.services;

import fr.modulefans.dao.MovieDAO;
import fr.modulefans.models.Movie;
import fr.modulefans.models.UserPreferences;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class MovieService {

    private final MovieDAO dao = new MovieDAO();

    /**
     * Score formula:
     * - Genre match: +30 per matching genre (max 90)
     * - Year range: +20 if within range, +10 if within ±10y
     * - Rating:     +10 if rating >= minRating, +5 if within 0.5 below
     * Max = 120
     */
    public List<Movie> getTop3Recommendations(UserPreferences prefs) {
        List<Movie> movies = dao.getAllMovies();
        for (Movie m : movies) {
            m.setScore(computeScore(m, prefs));
        }
        movies.sort(Comparator.comparingDouble(Movie::getScore).reversed());
        return movies.stream().limit(3).toList();
    }

    public List<Movie> getAllRecommendations(UserPreferences prefs) {
        List<Movie> movies = dao.getAllMovies();
        for (Movie m : movies) {
            m.setScore(computeScore(m, prefs));
        }
        movies.sort(Comparator.comparingDouble(Movie::getScore).reversed());
        return movies;
    }

    private double computeScore(Movie movie, UserPreferences prefs) {
        double score = 0;

        // Genre score
        List<String> movieGenres = Arrays.asList(movie.getGenreArray());
        for (String preferred : prefs.getFavoriteGenres()) {
            boolean match = movieGenres.stream()
                .anyMatch(g -> g.equalsIgnoreCase(preferred.trim()));
            if (match) score += 30;
        }
        score = Math.min(score, 90); // cap genre score

        // Year score
        if (movie.getYear() >= prefs.getMinYear() && movie.getYear() <= prefs.getMaxYear()) {
            score += 20;
        } else {
            int diff = Math.min(
                Math.abs(movie.getYear() - prefs.getMinYear()),
                Math.abs(movie.getYear() - prefs.getMaxYear())
            );
            if (diff <= 10) score += 10;
        }

        // Rating score
        if (movie.getRating() >= prefs.getMinRating()) {
            score += 10;
        } else if (movie.getRating() >= prefs.getMinRating() - 0.5) {
            score += 5;
        }

        return score;
    }
}
