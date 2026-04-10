package fr.modulefans.dao;

import fr.modulefans.models.GameRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicTacToeDAO {

    private Connection getConn() throws SQLException {
        return DatabaseManager.getInstance().getConnection();
    }

    public void saveGame(String winner, String difficulty) {
        try (PreparedStatement ps = getConn().prepareStatement(
                "INSERT INTO game_history(winner, difficulty) VALUES(?, ?)")) {
            ps.setString(1, winner);
            ps.setString(2, difficulty);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public int countByWinner(String winner) {
        try (PreparedStatement ps = getConn().prepareStatement(
                "SELECT COUNT(*) FROM game_history WHERE winner=?")) {
            ps.setString(1, winner);
            ResultSet rs = ps.executeQuery();
            return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public List<GameRecord> getRecentGames(int limit) {
        List<GameRecord> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(
                "SELECT * FROM game_history ORDER BY id DESC LIMIT ?")) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new GameRecord(rs.getInt("id"), rs.getString("winner"),
                        rs.getString("difficulty"), rs.getString("played_at")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
