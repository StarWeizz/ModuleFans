package fr.modulefans;

import fr.modulefans.dao.DatabaseManager;
import fr.modulefans.utils.EnvLoader;
import fr.modulefans.utils.NavigationManager;
import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        EnvLoader.load();
        Font.loadFont(App.class.getResourceAsStream("/fr/modulefans/assets/fonts/unifont_upper.otf"), 13);
        DatabaseManager.getInstance().initialize();
        NavigationManager.setStage(stage);
        NavigationManager.navigateTo("main_menu");
        stage.setTitle("ModuleFans — Your Premium Data Experience");
        stage.setMinWidth(950);
        stage.setMinHeight(680);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
