package fr.modulefans.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses a .env file and stores the values so they can be retrieved
 * via EnvLoader.get("KEY"), since Java's System.getenv() only reads
 * OS-level environment variables, not .env files.
 */
public class EnvLoader {

    private static final Map<String, String> env = new HashMap<>();

    /**
     * Loads .env from the working directory (tries ./.env then ../.env).
     * Call once at application startup.
     */
    public static void load() {
        File file = findFile(".env");
        if (file == null) {
            System.out.println("[EnvLoader] No .env file found, skipping.");
            return;
        }
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                int eq = line.indexOf('=');
                if (eq < 1) continue;
                String key = line.substring(0, eq).trim();
                String value = line.substring(eq + 1).trim();
                env.put(key, value);
            }
            System.out.println("[EnvLoader] Loaded " + env.size() + " variable(s) from " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the value for the given key, checking the .env first,
     * then falling back to the actual OS environment variable.
     */
    public static String get(String key) {
        String val = env.get(key);
        if (val != null && !val.isBlank()) return val;
        return System.getenv(key);
    }

    private static File findFile(String name) {
        for (String prefix : new String[]{".", ".."}) {
            File f = new File(prefix + File.separator + name);
            if (f.exists()) return f;
        }
        return null;
    }
}
