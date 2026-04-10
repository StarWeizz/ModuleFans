package fr.modulefans.controllers;

import fr.modulefans.services.ChatbotService;
import fr.modulefans.utils.NavigationManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatbotController implements Initializable {

    @FXML private VBox messagesContainer;
    @FXML private ScrollPane scrollPane;
    @FXML private TextField inputField;

    private final ChatbotService service = new ChatbotService();
    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "mia-thread");
        t.setDaemon(true);
        return t;
    });

    private static final String TWEMOJI_CDN = "https://cdn.jsdelivr.net/npm/twemoji@14.0.2/assets/72x72/";
    private static final Map<String, Image> EMOJI_CACHE = new ConcurrentHashMap<>();
    private static final Font EMOJI_FALLBACK = Font.font("Unifont Upper", 14);
    private static final Font TEXT_FONT      = Font.font("System", 13);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        inputField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) sendMessage();
        });

        // Pré-charge les emojis du message de bienvenue sur le thread background
        String welcome = "Bienvenue dans la Zone VIP Support ! 👑\n" +
                         "Je suis MIA, ton assistante premium ModuleFans. 💋\n" +
                         "Pose-moi une question sur les abonnements, les modules, ou la vie en général... 🔥";
        executor.submit(() -> {
            preloadEmojiImages(welcome);
            Platform.runLater(() -> addBotMessage(welcome));
        });
    }

    @FXML
    public void sendMessage() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;

        addUserMessage(text);
        inputField.clear();
        inputField.setDisable(true);

        HBox typingRow = createTypingIndicator();
        messagesContainer.getChildren().add(typingRow);
        scrollToBottom();

        executor.submit(() -> {
            String response = service.processMessage(text);
            preloadEmojiImages(response); // synchrone sur le thread background
            Platform.runLater(() -> {
                messagesContainer.getChildren().remove(typingRow);
                addBotMessage(response);
                inputField.setDisable(false);
                inputField.requestFocus();
            });
        });
    }

    private void addUserMessage(String text) {
        TextFlow bubble = buildTextFlow(text, Color.WHITE);
        bubble.getStyleClass().add("bubble-user");
        bubble.setMaxWidth(420);

        HBox row = new HBox(bubble);
        row.setAlignment(Pos.CENTER_RIGHT);
        row.setPadding(new Insets(4, 10, 4, 60));

        messagesContainer.getChildren().add(row);
        scrollToBottom();
    }

    private void addBotMessage(String text) {
        TextFlow bubble = buildTextFlow(text, Color.web("#1A1A2E"));
        bubble.getStyleClass().add("bubble-bot");
        bubble.setMaxWidth(420);

        Label avatar = new Label("🤖");
        avatar.setStyle("-fx-font-size: 20px;");

        HBox row = new HBox(8, avatar, bubble);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(4, 60, 4, 10));

        messagesContainer.getChildren().add(row);
        scrollToBottom();
    }

    /**
     * Pré-charge les images Twemoji de manière SYNCHRONE sur le thread background.
     * Doit être appelée AVANT Platform.runLater pour que les images soient prêtes à l'affichage.
     */
    private void preloadEmojiImages(String text) {
        for (int i = 0; i < text.length(); ) {
            int cp = text.codePointAt(i);
            if (isEmojiCodepoint(cp)) {
                String key = buildEmojiKey(text, i);
                EMOJI_CACHE.computeIfAbsent(key, k ->
                    new Image(TWEMOJI_CDN + k + ".png", 18, 18, true, true, false) // false = synchrone
                );
                // Avance sur toute la séquence emoji
                i += Character.charCount(cp);
                while (i < text.length()) {
                    int next = text.codePointAt(i);
                    if (next == 0x200D || next == 0xFE0F || isEmojiCodepoint(next))
                        i += Character.charCount(next);
                    else break;
                }
            } else {
                i += Character.charCount(cp);
            }
        }
    }

    /** Construit la clé Twemoji (codepoints hex séparés par '-', FE0F ignoré). */
    private String buildEmojiKey(String text, int startIdx) {
        StringBuilder key = new StringBuilder();
        boolean first = true;
        for (int i = startIdx; i < text.length(); ) {
            int cp = text.codePointAt(i);
            if (!isEmojiCodepoint(cp) && cp != 0x200D && cp != 0xFE0F) break;
            if (cp != 0xFE0F) {
                if (!first) key.append('-');
                key.append(Integer.toHexString(cp).toLowerCase());
                first = false;
            }
            i += Character.charCount(cp);
        }
        return key.toString();
    }

    /**
     * Segmente le texte : emojis → ImageView Twemoji (couleur),
     * texte normal → Text node avec police système.
     */
    private TextFlow buildTextFlow(String text, Color textColor) {
        List<Node> nodes = new ArrayList<>();
        StringBuilder segment = new StringBuilder();

        for (int i = 0; i < text.length(); ) {
            int cp = text.codePointAt(i);

            if (isEmojiCodepoint(cp)) {
                if (!segment.isEmpty()) {
                    nodes.add(makeText(segment.toString(), textColor));
                    segment.setLength(0);
                }
                String key = buildEmojiKey(text, i);
                nodes.add(makeEmojiNode(key));
                // Avance sur toute la séquence
                i += Character.charCount(cp);
                while (i < text.length()) {
                    int next = text.codePointAt(i);
                    if (next == 0x200D || next == 0xFE0F || isEmojiCodepoint(next))
                        i += Character.charCount(next);
                    else break;
                }
            } else {
                segment.appendCodePoint(cp);
                i += Character.charCount(cp);
            }
        }

        if (!segment.isEmpty()) nodes.add(makeText(segment.toString(), textColor));

        TextFlow flow = new TextFlow(nodes.toArray(new Node[0]));
        flow.setPadding(new Insets(10, 14, 10, 14));
        return flow;
    }

    private Text makeText(String content, Color color) {
        Text t = new Text(content);
        t.setFont(TEXT_FONT);
        t.setFill(color);
        return t;
    }

    /** Retourne un ImageView Twemoji, ou un Text Unifont si l'image a échoué. */
    private Node makeEmojiNode(String key) {
        Image img = EMOJI_CACHE.get(key);

        if (img != null && !img.isError()) {
            ImageView iv = new ImageView(img);
            iv.setFitWidth(16);
            iv.setFitHeight(16);
            iv.setSmooth(true);
            return iv;
        }

        // Fallback : rendu monochrome via Unifont
        Text t = new Text(emojiFromKey(key));
        t.setFont(EMOJI_FALLBACK);
        return t;
    }

    /** Reconstruit la chaîne emoji à partir de la clé hex pour le fallback Unifont. */
    private String emojiFromKey(String key) {
        StringBuilder sb = new StringBuilder();
        for (String hex : key.split("-")) {
            if (!hex.isEmpty()) sb.appendCodePoint(Integer.parseInt(hex, 16));
        }
        return sb.toString();
    }

    private boolean isEmojiCodepoint(int cp) {
        return (cp >= 0x1F300 && cp <= 0x1FAFF)
            || (cp >= 0x2600  && cp <= 0x26FF)
            || (cp >= 0x2700  && cp <= 0x27BF)
            || (cp >= 0x1F1E0 && cp <= 0x1F1FF)
            || (cp >= 0x1F900 && cp <= 0x1F9FF);
    }

    private HBox createTypingIndicator() {
        Label bubble = new Label("MIA est en train d'écrire...");
        bubble.getStyleClass().add("bubble-bot");
        bubble.setStyle("-fx-text-fill: #7A8B9A; -fx-font-style: italic;");

        Label avatar = new Label("🤖");
        avatar.setStyle("-fx-font-size: 20px;");

        HBox row = new HBox(8, avatar, bubble);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(4, 60, 4, 10));
        return row;
    }

    private void scrollToBottom() {
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }

    @FXML
    public void goBack() {
        executor.shutdownNow();
        NavigationManager.navigateTo("main_menu");
    }
}
