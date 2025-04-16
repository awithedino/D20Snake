package gamesrc.util;

import com.google.gson.*;
import gamesrc.entities.Ladder;
import gamesrc.entities.Snake;

import java.io.FileReader;
import java.util.*;

public class PresetLoader {
    public static Map<String, List<Snake>> snakePresets = new HashMap<>();
    public static Map<String, List<Ladder>> ladderPresets = new HashMap<>();
    private static List<String> presetNames = new ArrayList<>();

    public static void loadPresets(String path) {
        try {
            JsonObject root = JsonParser.parseReader(new FileReader(path)).getAsJsonObject();
            JsonArray presetsArray = root.getAsJsonArray("presets");

            for (JsonElement presetElem : presetsArray) {
                JsonObject preset = presetElem.getAsJsonObject();
                String name = preset.get("name").getAsString();
                presetNames.add(name);

                List<Snake> snakes = new ArrayList<>();
                Set<Integer> snakePositions = new HashSet<>(); // To track snake positions
                for (JsonElement e : preset.getAsJsonArray("snakes")) {
                    JsonObject s = e.getAsJsonObject();
                    int start = s.get("start").getAsInt();
                    int end = s.get("end").getAsInt();

                    if (!snakePositions.add(start) || !snakePositions.add(end)) {
                        System.err.println("Duplicate snake position found in preset: " + name);
                        continue; // Skip this snake if duplicate found
                    }
                    snakes.add(new Snake(start, end));
                }

                List<Ladder> ladders = new ArrayList<>();
                Set<Integer> ladderPositions = new HashSet<>(); // To track ladder positions
                for (JsonElement e : preset.getAsJsonArray("ladders")) {
                    JsonObject l = e.getAsJsonObject();
                    int start = l.get("start").getAsInt();
                    int end = l.get("end").getAsInt();

                    if (!ladderPositions.add(start) || !ladderPositions.add(end)) {
                        System.err.println("Duplicate ladder position found in preset: " + name);
                        continue; // Skip this ladder if duplicate found
                    }
                    ladders.add(new Ladder(start, end));
                }

                snakePresets.put(name, Collections.unmodifiableList(snakes));
                ladderPresets.put(name, Collections.unmodifiableList(ladders));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getRandomPresetName() {
        if (presetNames.isEmpty()) {
            return null;
        }
        Random rand = new Random();
        int index = rand.nextInt(presetNames.size());
        return presetNames.get(index);
    }
}