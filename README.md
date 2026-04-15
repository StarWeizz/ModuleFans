# ModuleFans — Your Premium Data Experience

> Une application JavaFX parodique transformant un projet académique Data & IA en expérience "premium" inspirée des plateformes d'abonnement modernes.

---

## 🎯 Concept

**ModuleFans** est une application desktop Java qui réinvente l'expérience utilisateur d'une toolbox Data & IA traditionnelle en adoptant un univers parodique inspiré des plateformes premium modernes.

Le projet transforme quatre modules techniques sérieux en une expérience cohérente, fun et engageante, tout en respectant rigoureusement les exigences académiques d'architecture logicielle, de traitement de données et d'intelligence artificielle.

### Baseline
> **"Débloquez l'accès exclusif à vos données les plus hot"**

---

## ✨ Les 4 Modules Premium

### 🌡️ 1. Météo VIP — *"Découvre les températures les plus hot"*
- **Visualisateur de données météorologiques**
- Analyse de données CSV avec statistiques avancées
- Détection d'anomalies thermiques (écart-type)
- Interface TableView avec mise en évidence des données exceptionnelles
- **Slogan** : *"Passe premium pour débloquer toute ta région préférée"*

### 💬 2. Support Premium — *"Notre service client vous écoute presque"*
- **Chatbot de support client intelligent**
- Base de connaissances SQLite avec système de FAQ
- Matching par mots-clés et scoring de pertinence
- Interface chat moderne avec bulles de conversation
- Intégration optionnelle OpenAI API pour réponses avancées
- **Slogan** : *"Des réponses premium à vos questions les plus brûlantes"*

### 🎮 3. Tic-Tac-Toe Elite — *"Affronte une créatrice de coups tactiques"*
- **IA joueuse de Morpion**
- Deux niveaux de difficulté (aléatoire / tactique)
- Algorithme de blocage et de victoire
- Historique des parties en base SQLite
- Statistiques joueur vs IA
- **Slogan** : *"Match privé contre l'IA — Abonnement mental requis"*

### 🎬 4. CinéMatch — *"Tes films les plus compatibles"*
- **Système de recommandation de films**
- Base de données films SQLite (titre, genres, année, note)
- Questionnaire de préférences utilisateur
- Algorithme de scoring de compatibilité
- Top 3 des recommandations personnalisées
- **Slogan** : *"Cette œuvre te désire émotionnellement"*

---

## 🚀 Installation et Lancement

### Prérequis
- **Java JDK 21** ou supérieur ([Télécharger](https://openjdk.org/projects/jdk/21/))
- **Maven 3.8+** ([Télécharger](https://maven.apache.org/download.cgi))
- **Git** (optionnel, pour cloner le projet)

### Étapes d'Installation

#### 1. Cloner le projet (ou télécharger le ZIP)
```bash
git clone <url-du-repo>
cd ModuleFans
```

#### 2. Se placer dans le module Maven
```bash
cd fr.modulefans
```

#### 3. Compiler le projet
```bash
mvn clean compile
```

#### 4. Lancer l'application
```bash
mvn javafx:run
```

### Configuration Optionnelle (Chatbot OpenAI)

Pour activer l'intégration OpenAI dans le chatbot :

1. Créer un fichier `.env` à la racine du projet
2. Ajouter votre clé API :
```env
OPENAI_API_KEY=sk-votre-cle-ici
```

Sans cette clé, le chatbot utilisera uniquement la base de connaissances locale (mode par défaut).

---

**ModuleFans** — *Parce que vos données méritent un traitement premium* 🔥
