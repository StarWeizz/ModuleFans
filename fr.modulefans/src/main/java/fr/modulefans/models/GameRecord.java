package fr.modulefans.models;

public class GameRecord {
    private int id;
    private String winner; // "PLAYER", "AI", "DRAW"
    private String difficulty; // "EASY", "HARD"
    private String playedAt;

    public GameRecord(int id, String winner, String difficulty, String playedAt) {
        this.id = id;
        this.winner = winner;
        this.difficulty = difficulty;
        this.playedAt = playedAt;
    }

    public int getId() { return id; }
    public String getWinner() { return winner; }
    public String getDifficulty() { return difficulty; }
    public String getPlayedAt() { return playedAt; }
}
