package fr.modulefans.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class NavigationManager {

    private static Stage stage;

    public static void setStage(Stage s) {
        stage = s;
    }

    public static Stage getStage() {
        return stage;
    }

    public static void navigateTo(String viewName) {
        try {
            URL url = NavigationManager.class.getResource("/fr/modulefans/views/" + viewName + ".fxml");
            if (url == null) throw new IOException("FXML not found: " + viewName);
            FXMLLoader loader = new FXMLLoader(url);
            Scene scene = new Scene(loader.load());
            URL css = NavigationManager.class.getResource("/fr/modulefans/assets/css/modulefans.css");
            if (css != null) scene.getStylesheets().add(css.toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
