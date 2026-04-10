package fr.modulefans.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static DatabaseManager instance;
    private static final String DB_PATH = "modulefans.db";
    private Connection connection;

    private DatabaseManager() {}

    public static DatabaseManager getInstance() {
        if (instance == null) instance = new DatabaseManager();
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
        }
        return connection;
    }

    public void initialize() {
        try {
            Connection conn = getConnection();
            createTables(conn);
            seedData(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS faq (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    keywords TEXT NOT NULL,
                    response TEXT NOT NULL,
                    category TEXT DEFAULT 'general'
                )
            """);
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS unknown_questions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    question TEXT NOT NULL,
                    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
                    answered INTEGER DEFAULT 0
                )
            """);
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS movies (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    genres TEXT NOT NULL,
                    year INTEGER,
                    rating REAL,
                    duration INTEGER
                )
            """);
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS game_history (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    winner TEXT NOT NULL,
                    difficulty TEXT NOT NULL,
                    played_at DATETIME DEFAULT CURRENT_TIMESTAMP
                )
            """);
        }
    }

    private void seedData(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            var rsMovies = conn.createStatement().executeQuery("SELECT COUNT(*) FROM movies");
            if (rsMovies.getInt(1) == 0) {
                seedMovies(stmt);
            }
            var rsFaq = conn.createStatement().executeQuery("SELECT COUNT(*) FROM faq");
            if (rsFaq.getInt(1) == 0) {
                seedFaq(stmt);
            }
        }
    }

    private void seedMovies(Statement stmt) throws SQLException {
        String[][] movies = {
            {"Inception", "Action|Sci-Fi|Thriller", "2010", "8.8", "148"},
            {"The Dark Knight", "Action|Crime|Drama", "2008", "9.0", "152"},
            {"Interstellar", "Adventure|Drama|Sci-Fi", "2014", "8.6", "169"},
            {"Pulp Fiction", "Crime|Drama|Thriller", "1994", "8.9", "154"},
            {"The Matrix", "Action|Sci-Fi", "1999", "8.7", "136"},
            {"Forrest Gump", "Drama|Romance", "1994", "8.8", "142"},
            {"The Silence of the Lambs", "Crime|Drama|Thriller", "1991", "8.6", "118"},
            {"Schindler's List", "Biography|Drama|History", "1993", "8.9", "195"},
            {"The Lord of the Rings: The Fellowship", "Adventure|Drama|Fantasy", "2001", "8.8", "178"},
            {"Fight Club", "Drama|Thriller", "1999", "8.8", "139"},
            {"Goodfellas", "Biography|Crime|Drama", "1990", "8.7", "145"},
            {"The Shawshank Redemption", "Drama", "1994", "9.3", "142"},
            {"The Godfather", "Crime|Drama", "1972", "9.2", "175"},
            {"Parasite", "Drama|Thriller", "2019", "8.5", "132"},
            {"Avengers: Endgame", "Action|Adventure|Sci-Fi", "2019", "8.4", "181"},
            {"The Lion King", "Animation|Adventure|Drama", "1994", "8.5", "88"},
            {"Titanic", "Drama|Romance", "1997", "7.9", "194"},
            {"Jurassic Park", "Action|Adventure|Sci-Fi", "1993", "8.1", "127"},
            {"The Truman Show", "Comedy|Drama|Sci-Fi", "1998", "8.1", "103"},
            {"Gladiator", "Action|Adventure|Drama", "2000", "8.5", "155"},
            {"Memento", "Mystery|Thriller", "2000", "8.4", "113"},
            {"The Prestige", "Drama|Mystery|Sci-Fi", "2006", "8.5", "130"},
            {"Whiplash", "Drama|Music", "2014", "8.5", "107"},
            {"La La Land", "Comedy|Drama|Music|Romance", "2016", "8.0", "128"},
            {"Get Out", "Horror|Mystery|Thriller", "2017", "7.7", "104"},
            {"Mad Max: Fury Road", "Action|Adventure|Sci-Fi", "2015", "8.1", "120"},
            {"Blade Runner 2049", "Drama|Mystery|Sci-Fi", "2017", "8.0", "164"},
            {"The Grand Budapest Hotel", "Adventure|Comedy|Crime", "2014", "8.1", "99"},
            {"Dune", "Action|Adventure|Drama|Sci-Fi", "2021", "8.0", "155"},
            {"Everything Everywhere All at Once", "Action|Adventure|Comedy|Sci-Fi", "2022", "7.8", "139"},
            {"Coco", "Animation|Adventure|Comedy|Fantasy", "2017", "8.4", "105"},
            {"Spider-Man: Into the Spider-Verse", "Action|Animation|Adventure", "2018", "8.4", "117"},
            {"1917", "Drama|War", "2019", "8.2", "119"},
            {"Joker", "Crime|Drama|Thriller", "2019", "8.4", "122"},
            {"The Irishman", "Biography|Crime|Drama", "2019", "7.8", "209"},
            {"Once Upon a Time in Hollywood", "Comedy|Drama", "2019", "7.6", "161"},
            {"Knives Out", "Comedy|Crime|Drama|Mystery", "2019", "7.9", "130"},
            {"Soul", "Animation|Adventure|Comedy|Drama", "2020", "8.1", "100"},
            {"Tenet", "Action|Sci-Fi|Thriller", "2020", "7.3", "150"},
            {"No Time to Die", "Action|Adventure|Thriller", "2021", "7.3", "163"},
            {"The Batman", "Action|Crime|Drama", "2022", "7.8", "176"},
            {"Top Gun: Maverick", "Action|Drama", "2022", "8.3", "130"},
            {"RRR", "Action|Drama|History", "2022", "7.9", "187"},
            {"Avatar: The Way of Water", "Action|Adventure|Fantasy|Sci-Fi", "2022", "7.6", "192"},
            {"Oppenheimer", "Biography|Drama|History", "2023", "8.6", "180"},
            {"Barbie", "Adventure|Comedy|Fantasy", "2023", "6.9", "114"},
            {"Killers of the Flower Moon", "Crime|Drama|History", "2023", "7.6", "206"},
            {"Poor Things", "Comedy|Drama|Fantasy|Romance", "2023", "8.0", "141"},
            {"The Zone of Interest", "Drama|History|War", "2023", "7.4", "105"},
            {"Past Lives", "Drama|Romance", "2023", "7.9", "106"}
        };
        for (String[] m : movies) {
            stmt.executeUpdate(String.format(
                "INSERT INTO movies(title,genres,year,rating,duration) VALUES('%s','%s',%s,%s,%s)",
                m[0].replace("'", "''"), m[1], m[2], m[3], m[4]
            ));
        }
    }

    private void seedFaq(Statement stmt) throws SQLException {
        String[][] faqs = {
            {"abonnement,prix,tarif,coût,payer", "Nos abonnements premium débutent à 0€/mois (offre gratuite limitée) et montent jusqu'à l'infini selon votre niveau de désir de données. Contactez notre équipe premium pour un devis personnalisé.", "abonnement"},
            {"annuler,résiliation,supprimer,quitter", "Pour annuler votre abonnement, accédez à Paramètres > Mon Compte > Pleurer > Annuler. Attention : les données exclusives disparaîtront à jamais.", "abonnement"},
            {"remboursement,rembourser,argent", "Notre politique de remboursement est généreuse : nous la lisons parfois. Pour toute demande, envoyez un message dans le vide et attendez notre réponse premium.", "paiement"},
            {"mot de passe,mdp,connexion,login,accès", "Pour réinitialiser votre mot de passe, cliquez sur 'J'ai oublié mon accès VIP' sur l'écran de connexion. Un email de récupération vous sera envoyé... éventuellement.", "compte"},
            {"données,privacy,vie privée,rgpd,confidentialité", "Vos données sont aussi précieuses pour nous que pour vous. Nous les stockons localement en SQLite, loin des regards indiscrets. RGPD compliant (on fait de notre mieux).", "legal"},
            {"bug,erreur,problème,crash,plante", "Notre équipe technique de haute volée (une personne) traite les bugs dans un délai de 3 à 97 jours ouvrés. Merci de votre patience premium.", "support"},
            {"météo,temperature,climat,temps", "Notre module Météo analyse les températures les plus torrides de France. Accédez-y depuis le menu principal pour débloquer des statistiques brûlantes.", "modules"},
            {"film,cinéma,recommandation,voir", "Notre module Films vous recommande les œuvres les plus compatibles avec votre âme. Remplissez le questionnaire de goûts et laissez l'IA faire le matching.", "modules"},
            {"morpion,tictactoe,jeu,jouer", "Affrontez notre IA tactique en morpion ! Niveau Facile ou Difficile. Votre historique de victoires est enregistré pour l'éternité.", "modules"},
            {"chatbot,bot,assistant,aide", "Vous parlez actuellement à MIA, votre assistant premium ModuleFans. Je réponds à presque toutes les questions, sauf celles auxquelles je ne connais pas la réponse.", "support"},
            {"fonctionnalité,feature,nouveauté", "De nouvelles fonctionnalités exclusives arrivent bientôt™. Restez abonné pour ne rien manquer de nos mises à jour révolutionnaires.", "general"},
            {"contact,email,téléphone,humain", "Pour contacter un humain, veuillez remplir le formulaire de contact disponible dans Paramètres > Support > Crier dans le vide. Réponse garantie en 3-5 jours ouvrés.", "support"},
            {"gratuit,free,essai", "Notre offre gratuite inclut l'accès aux modules de base, la capacité de regarder sans toucher, et la frustration premium incluse.", "abonnement"},
            {"premium,vip,exclusif,accès", "L'accès VIP débloque tous les modules, les statistiques avancées, les badges dorés et la satisfaction d'avoir dépensé de l'argent virtuel.", "abonnement"},
            {"télécharger,export,exporter,csv,pdf", "L'export de données est disponible dans chaque module via le bouton 'Extraire les données brûlantes'. Format CSV et PDF supportés.", "general"},
            {"installation,installer,démarrer,lancer", "Pour lancer ModuleFans : compilez avec Maven (mvn javafx:run) ou ouvrez depuis IntelliJ IDEA. Java 21 requis, passion premium recommandée.", "technique"},
            {"mise à jour,update,version", "ModuleFans est constamment mis à jour par notre équipe dévouée. La version actuelle est 1.0-SNAPSHOT, ce qui signifie que tout peut changer.", "technique"},
            {"sécurité,secure,protection,hack", "ModuleFans stocke vos données en local uniquement. Aucune donnée n'est envoyée sur nos serveurs (nous n'en avons pas). Sécurité maximale par défaut.", "legal"},
            {"merci,super,bien,excellent,parfait", "Merci pour votre retour chaleureux ! Notre équipe de satisfaction client en est profondément émue. Continuez à consommer du contenu premium.", "general"},
            {"bonjour,salut,hello,coucou,hey", "Bienvenue dans le support premium ModuleFans ! Je suis MIA, votre assistante dédiée. Comment puis-je sublimer votre expérience aujourd'hui ?", "general"}
        };
        for (String[] faq : faqs) {
            stmt.executeUpdate(String.format(
                "INSERT INTO faq(keywords,response,category) VALUES('%s','%s','%s')",
                faq[0].replace("'", "''"),
                faq[1].replace("'", "''"),
                faq[2]
            ));
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
