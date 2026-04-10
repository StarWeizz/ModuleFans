package fr.modulefans.controllers;

import fr.modulefans.services.ChatbotService;
import fr.modulefans.utils.NavigationManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ChatbotController implements Initializable {

    @FXML private VBox messagesContainer;
    @FXML private ScrollPane scrollPane;
    @FXML private TextField inputField;

    private final ChatbotService service = new ChatbotService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Allow Enter key to send
        inputField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) sendMessage();
        });

        // Welcome message
        Platform.runLater(() -> addBotMessage(
            "Bienvenue dans la Zone VIP Support ! 👑\n" +
            "Je suis ARIA, votre assistante premium ModuleFans.\n" +
            "Posez-moi une question sur les abonnements, modules, ou la vie en général."
        ));
    }

    @FXML
    public void sendMessage() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;

        addUserMessage(text);
        inputField.clear();

        String response = service.processMessage(text);
        Platform.runLater(() -> addBotMessage(response));
    }

    private void addUserMessage(String text) {
        Label bubble = new Label(text);
        bubble.getStyleClass().add("bubble-user");
        bubble.setWrapText(true);
        bubble.setMaxWidth(420);

        HBox row = new HBox(bubble);
        row.setAlignment(Pos.CENTER_RIGHT);
        row.setPadding(new Insets(4, 10, 4, 60));

        messagesContainer.getChildren().add(row);
        scrollToBottom();
    }

    private void addBotMessage(String text) {
        Label bubble = new Label(text);
        bubble.getStyleClass().add("bubble-bot");
        bubble.setWrapText(true);
        bubble.setMaxWidth(420);

        // Bot avatar label
        Label avatar = new Label("🤖");
        avatar.setStyle("-fx-font-size: 20px;");

        HBox row = new HBox(8, avatar, bubble);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(4, 60, 4, 10));

        messagesContainer.getChildren().add(row);
        scrollToBottom();
    }

    private void scrollToBottom() {
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }

    @FXML
    public void goBack() {
        NavigationManager.navigateTo("main_menu");
    }
}
