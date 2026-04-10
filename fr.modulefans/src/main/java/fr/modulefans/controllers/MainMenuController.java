package fr.modulefans.controllers;

import fr.modulefans.utils.NavigationManager;
import javafx.fxml.FXML;

public class MainMenuController {

    @FXML
    public void openMeteo() {
        NavigationManager.navigateTo("meteo");
    }

    @FXML
    public void openChatbot() {
        NavigationManager.navigateTo("chatbot");
    }

    @FXML
    public void openTicTacToe() {
        NavigationManager.navigateTo("tictactoe");
    }

    @FXML
    public void openMovies() {
        NavigationManager.navigateTo("movies");
    }
}
