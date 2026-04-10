package fr.modulefans.services;

import fr.modulefans.dao.ChatbotDAO;
import fr.modulefans.models.FaqEntry;
import fr.modulefans.utils.OpenAIClient;

import java.util.Arrays;
import java.util.List;

public class ChatbotService {

    private final ChatbotDAO dao = new ChatbotDAO();
    private final OpenAIClient openAI = new OpenAIClient();

    private static final String[] FALLBACK_UNKNOWN = {
        "Hmm... Cette question dépasse mon niveau d'abonnement actuel. 🤔 Votre question a été transmise à notre équipe premium.",
        "Je ne connais pas encore la réponse à ça. 😅 Mais j'ai mémorisé votre question pour une réponse exclusive bientôt.",
        "Excellente question ! Malheureusement, la réponse est protégée par notre offre Ultra-Premium. 📋"
    };

    private int fallbackIndex = 0;

    /**
     * Tente OpenAI en priorité. Si non configuré ou en erreur, utilise le matching FAQ local.
     */
    public String processMessage(String userMessage) {
        if (userMessage == null || userMessage.isBlank()) {
            return "Je vous entends... mais je n'entends rien. Essayez de taper quelque chose. 🎤";
        }

        if (openAI.isConfigured()) {
            try {
                return openAI.chat(userMessage);
            } catch (Exception e) {
                System.err.println("[MIA] OpenAI unavailable, falling back to FAQ: " + e.getMessage());
            }
        }

        return processWithFaq(userMessage);
    }

    private String processWithFaq(String userMessage) {
        String lowerMsg = userMessage.toLowerCase().trim();

        if (isGreeting(lowerMsg)) {
            return "Coucou chéri(e) ! Je suis MIA, ton assistante ModuleFans 💫 Comment puis-je t'aider ?";
        }

        List<FaqEntry> faqList = dao.getAllFaq();
        FaqEntry bestMatch = null;
        int bestScore = 0;

        for (FaqEntry entry : faqList) {
            int score = computeScore(lowerMsg, entry.getKeywordArray());
            if (score > bestScore) {
                bestScore = score;
                bestMatch = entry;
            }
        }

        if (bestMatch != null && bestScore >= 1) {
            return bestMatch.getResponse();
        }

        dao.saveUnknownQuestion(userMessage);
        return FALLBACK_UNKNOWN[fallbackIndex++ % FALLBACK_UNKNOWN.length];
    }

    private int computeScore(String message, String[] keywords) {
        int score = 0;
        for (String kw : keywords) {
            String keyword = kw.trim();
            if (!keyword.isEmpty() && message.contains(keyword)) {
                score++;
                if (message.matches(".*\\b" + java.util.regex.Pattern.quote(keyword) + "\\b.*")) {
                    score++;
                }
            }
        }
        return score;
    }

    private boolean isGreeting(String msg) {
        return Arrays.asList("bonjour", "salut", "hello", "coucou", "hey", "hi", "bonsoir")
                .stream().anyMatch(msg::contains);
    }
}
