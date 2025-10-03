package tetrisclient;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ScoreManager {

    private static final String SCORE_FILE = "JavaTetrisScore.json";

    public record ScoreEntry(String name, int score) {}

    private static List<ScoreEntry> highScores = new ArrayList<>();

    public static void load() {
        try {
            String json = Files.readString(Path.of(SCORE_FILE));
            Type listType = new TypeToken<List<ScoreEntry>>(){}.getType();
            highScores = new Gson().fromJson(json, listType);
            if (highScores == null) highScores = new ArrayList<>();
        } catch (IOException e) {
            highScores = new ArrayList<>();
        }
    }

    public static void save() {
        try {
            String json = new Gson().toJson(highScores);
            Files.writeString(Path.of(SCORE_FILE), json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void add(String name, int score) {
        highScores.add(new ScoreEntry(name, score));
        highScores.sort((a, b) -> Integer.compare(b.score(), a.score())); // sort descending
        if (highScores.size() > 10) {
            highScores = new ArrayList<>(highScores.subList(0, 10));
        }
        save();
    }

    public static List<ScoreEntry> getScores() {
        return highScores;
    }

    public static void clear() {
        highScores.clear();
        save();
    }
}
