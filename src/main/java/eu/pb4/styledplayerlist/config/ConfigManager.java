package eu.pb4.styledplayerlist.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.pb4.styledplayerlist.PlayerList;

import eu.pb4.styledplayerlist.config.data.ConfigData;
import eu.pb4.styledplayerlist.config.data.StyleData;


import java.io.*;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedHashMap;


public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static ConfigData CONFIG;
    private static boolean ENABLED = false;
    private static final LinkedHashMap<String, PlayerListStyle> STYLES = new LinkedHashMap<>();

    public static ConfigData getConfig() {
        return CONFIG;
    }

    public static boolean isEnabled() {
        return ENABLED;
    }

    public static boolean loadConfig() {
        ENABLED = false;

        CONFIG = null;
        try {
            File configStyle = Paths.get("", "config", "styledplayerlist", "styles").toFile();
            File configDir = Paths.get("", "config", "styledplayerlist").toFile();

            if (configStyle.mkdirs()) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(new File(configStyle, "default.json")));
                writer.write(GSON.toJson(DefaultValues.exampleStyleData()));
                writer.close();
            }


            File configFile = new File(configDir, "config.json");

            if (configFile.exists()) {
                CONFIG = GSON.fromJson(new FileReader(configFile), ConfigData.class);
            } else {
                CONFIG = new ConfigData();
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));
            writer.write(GSON.toJson(CONFIG));
            writer.close();

            STYLES.clear();

            FilenameFilter filter = (dir, name) -> name.endsWith(".json");

            for (String fileName : configStyle.list(filter)) {
                PlayerListStyle style = new PlayerListStyle(GSON.fromJson(new FileReader(new File(configStyle, fileName)), StyleData.class));

                STYLES.put(style.id, style);
            }

            PlayerList.PLAYER_LIST_STYLE_LOAD.invoker().onPlayerListUpdate(new PlayerList.StyleHelper(STYLES));

            ENABLED = true;
        }
        catch(IOException exception) {
            ENABLED = false;
            PlayerList.LOGGER.error("Something went wrong while reading config!");
            exception.printStackTrace();
        }

        return ENABLED;
    }

    public static PlayerListStyle getStyle(String key) {
        return STYLES.containsKey(key) ? STYLES.get(key) : DefaultValues.EMPTY_STYLE;
    }

    public static boolean styleExist(String key) {
        return  STYLES.containsKey(key);
    }

    public static Collection<PlayerListStyle> getStyles() { return STYLES.values(); }

    public static String getDefault() {
        return ENABLED ? CONFIG.defaultStyle : "default";
    }
}
