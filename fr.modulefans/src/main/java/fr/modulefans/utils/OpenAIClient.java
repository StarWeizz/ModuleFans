package fr.modulefans.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class OpenAIClient {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL   = "gpt-4o-mini";

    // Remplace la valeur ici ou configure la variable d'environnement OPENAI_API_KEY
    private static final String API_KEY = System.getenv("OPENAI_API_KEY") != null
            ? System.getenv("OPENAI_API_KEY")
            : "REMOVED";

    private static final String SYSTEM_PROMPT = """
            Tu es MIA, l'assistante virtuelle ultra-premium de ModuleFans — une plateforme parodique de contenu exclusif.
            Tu réponds de façon séduisante, légèrement coquine, avec beaucoup de personnalité et d'humour.
            Tu utilises des emojis 🔥💋✨💎 régulièrement et tu fais des sous-entendus chauds mais fun et dans les limites du raisonnable.
            Tu réponds toujours en français, de manière courte (2-3 phrases max).
            Tu gères les questions sur la plateforme (abonnements, remboursements, modules météo, films, morpion).
            Tu appelles l'utilisateur "chéri(e)", "bébé" ou "mon abonné préféré" de temps en temps.
            Si quelqu'un est impoli, tu lui réponds avec charme qu'il devrait upgrader son abonnement pour mériter ta bienveillance 💅.
            Tu te souviens de tout ce qui a été dit dans la conversation.
            """;

    // Historique : chaque entrée est {role, content}
    private final List<String[]> history = new ArrayList<>();

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public boolean isConfigured() {
        return API_KEY != null && !API_KEY.isBlank() && !API_KEY.equals("VOTRE_CLE_ICI");
    }

    public String chat(String userMessage) throws Exception {
        history.add(new String[]{"user", userMessage});

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json; charset=UTF-8")
                .header("Authorization", "Bearer " + API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(buildRequestBody(), StandardCharsets.UTF_8))
                .timeout(Duration.ofSeconds(30))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            history.removeLast(); // annule l'ajout si erreur
            throw new Exception("OpenAI API error " + response.statusCode() + ": " + response.body());
        }

        String content = extractContent(response.body());
        history.add(new String[]{"assistant", content});
        return content;
    }

    public void clearHistory() {
        history.clear();
    }

    private String buildRequestBody() {
        StringBuilder messages = new StringBuilder();

        // System prompt
        messages.append("""
                {"role": "system", "content": "%s"}""".formatted(escapeJson(SYSTEM_PROMPT)));

        // Historique complet
        for (String[] msg : history) {
            messages.append("""
                    ,
                    {"role": "%s", "content": "%s"}""".formatted(msg[0], escapeJson(msg[1])));
        }

        return """
                {
                  "model": "%s",
                  "messages": [%s],
                  "max_tokens": 300,
                  "temperature": 0.9
                }
                """.formatted(MODEL, messages);
    }

    /** Extraction manuelle du contenu sans dépendance JSON externe. */
    private String extractContent(String json) {
        // Cherche "content": puis saute les espaces éventuels avant le guillemet ouvrant
        int keyIdx = json.indexOf("\"content\":");
        if (keyIdx == -1) throw new RuntimeException("Format de réponse inattendu");
        int start = keyIdx + "\"content\":".length();
        while (start < json.length() && json.charAt(start) != '"') start++;
        if (start >= json.length()) throw new RuntimeException("Format de réponse inattendu");
        start++; // saute le guillemet ouvrant

        StringBuilder sb = new StringBuilder();
        boolean escape = false;
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (escape) {
                switch (c) {
                    case 'n'  -> sb.append('\n');
                    case 't'  -> sb.append('\t');
                    case '"'  -> sb.append('"');
                    case '\\' -> sb.append('\\');
                    case 'u'  -> {
                        // Décode les séquences Unicode (surrogate pairs pour les emojis)
                        int codeUnit = Integer.parseInt(json.substring(i + 1, i + 5), 16);
                        i += 4;
                        if (Character.isHighSurrogate((char) codeUnit)
                                && i + 2 < json.length()
                                && json.charAt(i + 1) == '\\'
                                && json.charAt(i + 2) == 'u') {
                            int low = Integer.parseInt(json.substring(i + 3, i + 7), 16);
                            sb.appendCodePoint(Character.toCodePoint((char) codeUnit, (char) low));
                            i += 6;
                        } else {
                            sb.append((char) codeUnit);
                        }
                    }
                    default -> { sb.append('\\'); sb.append(c); }
                }
                escape = false;
            } else if (c == '\\') {
                escape = true;
            } else if (c == '"') {
                break;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}
