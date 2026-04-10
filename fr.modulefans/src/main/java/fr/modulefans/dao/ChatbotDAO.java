package fr.modulefans.dao;

import fr.modulefans.models.FaqEntry;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatbotDAO {

    private Connection getConn() throws SQLException {
        return DatabaseManager.getInstance().getConnection();
    }

    public List<FaqEntry> getAllFaq() {
        List<FaqEntry> list = new ArrayList<>();
        try (Statement s = getConn().createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM faq ORDER BY id")) {
            while (rs.next()) {
                list.add(new FaqEntry(rs.getInt("id"), rs.getString("keywords"),
                        rs.getString("response"), rs.getString("category")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public void saveUnknownQuestion(String question) {
        try (PreparedStatement ps = getConn().prepareStatement(
                "INSERT INTO unknown_questions(question) VALUES(?)")) {
            ps.setString(1, question);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<String[]> getUnknownQuestions() {
        List<String[]> list = new ArrayList<>();
        try (Statement s = getConn().createStatement();
             ResultSet rs = s.executeQuery(
                     "SELECT id, question, timestamp FROM unknown_questions WHERE answered=0 ORDER BY id DESC")) {
            while (rs.next()) {
                list.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    rs.getString("question"),
                    rs.getString("timestamp")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
