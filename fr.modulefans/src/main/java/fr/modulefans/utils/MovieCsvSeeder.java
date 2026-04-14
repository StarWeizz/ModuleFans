package fr.modulefans.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.regex.*;

public class MovieCsvSeeder {

    /**
     * Seeds the movies table from movies.csv + ratings.csv found in the working directory.
     * Tries ./movies.csv then ../movies.csv.
     * Returns the number of movies inserted, or 0 if files were not found.
     */
    public static int seedFromCsv(Connection conn) throws SQLException {
        File moviesFile = findFile("movies.csv");
        File ratingsFile = findFile("ratings.csv");

        if (moviesFile == null || ratingsFile == null) {
            System.out.println("[MovieCsvSeeder] CSV files not found, skipping CSV seed.");
            return 0;
        }

        System.out.println("[MovieCsvSeeder] Found: " + moviesFile.getAbsolutePath());

        // Step 1: compute average rating per movieId from ratings.csv
        // Ratings are on a 0.5-5.0 scale (MovieLens)
        Map<Integer, double[]> ratingData = new HashMap<>(); // [sum, count]
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(ratingsFile), StandardCharsets.UTF_8))) {
            String line;
            boolean header = true;
            while ((line = br.readLine()) != null) {
                if (header) { header = false; continue; }
                String[] parts = line.split(",");
                if (parts.length < 3) continue;
                try {
                    int movieId = Integer.parseInt(parts[1].trim());
                    double rating = Double.parseDouble(parts[2].trim());
                    ratingData.computeIfAbsent(movieId, k -> new double[]{0.0, 0.0});
                    ratingData.get(movieId)[0] += rating;
                    ratingData.get(movieId)[1] += 1;
                } catch (NumberFormatException ignored) {}
            }
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }

        // Step 2: parse movies.csv and batch-insert
        // Format: movieId,title,genres  (title contains year like "Toy Story (1995)")
        Pattern yearPattern = Pattern.compile("\\((\\d{4})\\)\\s*$");

        conn.setAutoCommit(false);
        int count = 0;

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO movies(title, genres, year, rating, duration) VALUES(?, ?, ?, ?, ?)");
             BufferedReader br = new BufferedReader(
                     new InputStreamReader(new FileInputStream(moviesFile), StandardCharsets.UTF_8))) {

            String line;
            boolean header = true;
            while ((line = br.readLine()) != null) {
                if (header) { header = false; continue; }
                line = line.trim();
                if (line.isEmpty()) continue;

                // Split on first and last comma: movieId | title (may contain commas) | genres
                int firstComma = line.indexOf(',');
                int lastComma = line.lastIndexOf(',');
                if (firstComma < 0 || firstComma == lastComma) continue;

                String movieIdStr = line.substring(0, firstComma).trim();
                String rawTitle = line.substring(firstComma + 1, lastComma).trim();
                String genres = line.substring(lastComma + 1).trim();

                // Strip surrounding quotes (CSV quoting)
                if (rawTitle.startsWith("\"") && rawTitle.endsWith("\"")) {
                    rawTitle = rawTitle.substring(1, rawTitle.length() - 1).replace("\"\"", "\"");
                }

                int movieId;
                try {
                    movieId = Integer.parseInt(movieIdStr);
                } catch (NumberFormatException ignored) { continue; }

                // Extract year from title
                Matcher matcher = yearPattern.matcher(rawTitle);
                int year = 0;
                String title = rawTitle;
                if (matcher.find()) {
                    year = Integer.parseInt(matcher.group(1));
                    title = rawTitle.substring(0, matcher.start()).trim();
                }

                // Average rating (0.5-5.0 scale, 0 if no ratings)
                double rating = 0.0;
                double[] data = ratingData.get(movieId);
                if (data != null && data[1] > 0) {
                    rating = data[0] / data[1];
                }

                ps.setString(1, title);
                ps.setString(2, genres);
                ps.setInt(3, year);
                ps.setDouble(4, rating);
                ps.setInt(5, 0); // duration not available in MovieLens CSV
                ps.addBatch();
                count++;

                if (count % 500 == 0) {
                    ps.executeBatch();
                }
            }
            ps.executeBatch();
            conn.commit();

        } catch (IOException e) {
            conn.rollback();
            e.printStackTrace();
            return 0;
        } finally {
            conn.setAutoCommit(true);
        }

        System.out.println("[MovieCsvSeeder] Inserted " + count + " movies.");
        return count;
    }

    private static File findFile(String name) {
        for (String prefix : new String[]{".", ".."}) {
            File f = new File(prefix + File.separator + name);
            if (f.exists()) return f;
        }
        return null;
    }
}