package fr.modulefans.services;

import fr.modulefans.dao.ChatbotDAO;
import fr.modulefans.models.FaqEntry;

import java.util.Arrays;
import java.util.List;

public class ChatbotService {

    private final ChatbotDAO dao = new ChatbotDAO();

    private static final String[] GREETINGS = {
        "Bonjour ! Je suis ARIA, votre assistante premium ModuleFans. 💫 Posez-moi une question !",
        "Salut ! ARIA à votre service — le support le plus disponible entre deux abonnements. 🌟",
        "Hey ! Bienvenue dans la zone VIP du support client. Comment puis-je vous aider ? 👑"
    };

    private static final String[] UNKNOWN_RESPONSES = {
        "Hmm... Cette question dépasse mon niveau d'abonnement actuel. 🤔 Votre question a été transmise à notre équipe premium pour enrichissement futur.",
        "Je ne connais pas encore la réponse à ça. 😅 Mais j'ai mémorisé votre question pour une réponse exclusive bientôt.",
        "Excellente question ! Malheureusement, la réponse est protégée par notre offre Ultra-Premium. Votre curiosité a été enregistrée. 📋"
    };

    private int unknownResponseIndex = 0;

    public String processMessage(String userMessage) {
        if (userMessage == null || userMessage.isBlank()) {
            return "Je vous entends... mais je n'entends rien. Essayez de taper quelque chose. 🎤";
        }

        String lowerMsg = userMessage.toLowerCase().trim();

        // Check greeting
        if (isGreeting(lowerMsg)) {
            return GREETINGS[(int)(Math.random() * GREETINGS.length)];
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

        // No match — save to unknown
        dao.saveUnknownQuestion(userMessage);
        String response = UNKNOWN_RESPONSES[unknownResponseIndex % UNKNOWN_RESPONSES.length];
        unknownResponseIndex++;
        return response;
    }

    private int computeScore(String message, String[] keywords) {
        int score = 0;
        for (String kw : keywords) {
            String keyword = kw.trim();
            if (!keyword.isEmpty() && message.contains(keyword)) {
                score++;
                // Bonus for exact word match
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
