package fr.modulefans.dao;

import fr.modulefans.models.Movie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO {

    private Connection getConn() throws SQLException {
        return DatabaseManager.getInstance().getConnection();
    }

    public List<Movie> getAllMovies() {
        List<Movie> list = new ArrayList<>();
        try (Statement s = getConn().createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM movies ORDER BY rating DESC")) {
            while (rs.next()) {
                list.add(new Movie(
                    rs.getInt("id"), rs.getString("title"),
                    rs.getString("genres"), rs.getInt("year"),
                    rs.getDouble("rating"), rs.getInt("duration")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
