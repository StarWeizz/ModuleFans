package fr.modulefans.controllers;

import fr.modulefans.models.Movie;
import fr.modulefans.models.UserPreferences;
import fr.modulefans.services.MovieService;
import fr.modulefans.utils.NavigationManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MovieController implements Initializable {

    @FXML private CheckBox cbAction, cbComedy, cbDrama, cbSciFi, cbThriller;
    @FXML private CheckBox cbRomance, cbAnimation, cbHorror, cbDocumentary, cbAdventure;
    @FXML private ComboBox<String> cmbMinYear, cmbMaxYear;
    @FXML private Slider sliderRating;
    @FXML private Label lblRatingValue;
    @FXML private VBox resultsContainer;
    @FXML private Label lblNoResult;

    private final MovieService service = new MovieService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Populate year combos
        for (int y = 1900; y <= 2020; y += 5) {
            cmbMinYear.getItems().add(String.valueOf(y));
            cmbMaxYear.getItems().add(String.valueOf(y));
        }
        cmbMinYear.setValue("1990");
        cmbMaxYear.setValue("2020");

        sliderRating.setMin(0.5);
        sliderRating.setMax(5.0);
        sliderRating.setValue(3.5);
        sliderRating.setBlockIncrement(0.5);
        lblRatingValue.setText("3.5 / 5");

        sliderRating.valueProperty().addListener((obs, o, n) ->
            lblRatingValue.setText(String.format("%.1f / 5", n.doubleValue()))
        );

        if (lblNoResult != null) lblNoResult.setVisible(false);
    }

    @FXML
    public void recommend() {
        List<String> genres = new ArrayList<>();
        if (cbAction.isSelected()) genres.add("Action");
        if (cbComedy.isSelected()) genres.add("Comedy");
        if (cbDrama.isSelected()) genres.add("Drama");
        if (cbSciFi.isSelected()) genres.add("Sci-Fi");
        if (cbThriller.isSelected()) genres.add("Thriller");
        if (cbRomance.isSelected()) genres.add("Romance");
        if (cbAnimation.isSelected()) genres.add("Animation");
        if (cbHorror.isSelected()) genres.add("Horror");
        if (cbDocumentary.isSelected()) genres.add("Documentary");
        if (cbAdventure.isSelected()) genres.add("Adventure");

        int minYear = Integer.parseInt(cmbMinYear.getValue());
        int maxYear = Integer.parseInt(cmbMaxYear.getValue());
        double minRating = sliderRating.getValue();

        UserPreferences prefs = new UserPreferences(genres, minYear, maxYear, minRating);
        List<Movie> top3 = service.getTop3Recommendations(prefs);

        resultsContainer.getChildren().clear();
        if (lblNoResult != null) lblNoResult.setVisible(false);

        if (top3.isEmpty() || top3.get(0).getScore() == 0) {
            if (lblNoResult != null) lblNoResult.setVisible(true);
            return;
        }

        String[] medals = {"🥇", "🥈", "🥉"};
        String[] labels = {"MATCH PARFAIT", "TRÈS COMPATIBLE", "BONNE PIOCHE"};
        for (int i = 0; i < top3.size(); i++) {
            resultsContainer.getChildren().add(buildMovieCard(top3.get(i), medals[i], labels[i]));
        }
    }

    private VBox buildMovieCard(Movie movie, String medal, String label) {
        VBox card = new VBox(6);
        card.getStyleClass().add("movie-card");
        card.setPadding(new Insets(15));

        // Header row: medal + title + score
        Label titleLabel = new Label(medal + " " + movie.getTitle());
        titleLabel.getStyleClass().add("movie-title");

        Label scoreLabel = new Label(String.format("%.0f pts", movie.getScore()));
        scoreLabel.getStyleClass().add("movie-score");

        HBox headerRow = new HBox(10, titleLabel, scoreLabel);
        headerRow.setStyle("-fx-alignment: center-left;");

        Label matchLabel = new Label("✦ " + label);
        matchLabel.setStyle("-fx-text-fill: #ff6b35; -fx-font-size: 10px; -fx-font-weight: bold;");

        Label genreLabel = new Label("🎬 " + movie.getGenres().replace("|", " · "));
        genreLabel.getStyleClass().add("movie-genre");

        String yearStr = movie.getYear() > 0 ? String.valueOf(movie.getYear()) : "?";
        String ratingStr = movie.getRating() > 0 ? String.format("%.2f/5", movie.getRating()) : "non noté";
        Label infoLabel = new Label("📅 " + yearStr + "  ·  ⭐ " + ratingStr);
        infoLabel.setStyle("-fx-text-fill: #a0a0a0; -fx-font-size: 11px;");

        card.getChildren().addAll(headerRow, matchLabel, genreLabel, infoLabel);
        return card;
    }

    @FXML
    public void goBack() {
        NavigationManager.navigateTo("main_menu");
    }
}
