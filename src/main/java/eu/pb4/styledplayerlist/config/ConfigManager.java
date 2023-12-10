package eu.pb4.styledplayerlist.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import eu.pb4.predicate.api.GsonPredicateSerializer;
import eu.pb4.predicate.api.MinecraftPredicate;
import eu.pb4.styledplayerlist.PlayerList;

import eu.pb4.styledplayerlist.config.data.ConfigData;
import eu.pb4.styledplayerlist.config.data.StyleData;
import eu.pb4.styledplayerlist.config.data.legacy.LegacyConfigData;
import eu.pb4.styledplayerlist.config.data.legacy.LegacyStyleData;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;


import java.io.*;
import java.nio.file.Files;
import java.util.Collection;
import java.util.LinkedHashMap;


public class ConfigManager {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().setLenient()
            .registerTypeHierarchyAdapter(MinecraftPredicate.class, GsonPredicateSerializer.INSTANCE)
            .registerTypeAdapter(StyleData.ElementList.class, new StyleData.ElementList.Serializer())
            .create();

    private static Config CONFIG;
    private static boolean ENABLED = false;
    private static final LinkedHashMap<String, PlayerListStyle> STYLES = new LinkedHashMap<>();
    private static final LinkedHashMap<String, StyleData> STYLES_DATA = new LinkedHashMap<>();

    public static Config getConfig() {
        return CONFIG;
    }

    public static boolean isEnabled() {
        return ENABLED;
    }

    public static boolean loadConfig() {
        ENABLED = false;

        CONFIG = null;
        try {
            var configDir =  FabricLoader.getInstance().getConfigDir().resolve("styledplayerlist");

            var configStyle = configDir.resolve("styles");
            var configStyleLegacy = configDir.resolve("styles_old");

            if (!Files.exists(configStyle)) {
                Files.createDirectories(configStyle);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(configStyle.resolve("default.json")), "UTF-8"));
                writer.write(GSON.toJson(DefaultValues.exampleStyleData()));
                writer.close();

                writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(configStyle.resolve("animated.json")), "UTF-8"));
                writer.write(GSON.toJson(DefaultValues.exampleAnimatedStyleData()));
                writer.close();
            }

            ConfigData config;

            var configFile = configDir.resolve("config.json");
            LegacyConfigData legacyConfigData = null;
            if (Files.exists(configFile)) {
                var data = JsonParser.parseString(Files.readString(configFile));

                if (data.getAsJsonObject().has("CONFIG_VERSION_DONT_TOUCH_THIS")) {
                    legacyConfigData = GSON.fromJson(data, LegacyConfigData.class);
                    Files.writeString(configDir.resolve("config.json_old"), data.toString());
                    config = legacyConfigData.convert();
                } else {
                    config = GSON.fromJson(data, ConfigData.class);
                }

            } else {
                config = new ConfigData();
            }

            Files.writeString(configFile, GSON.toJson(config));

            STYLES.clear();

            var finalLegacyConfigData = legacyConfigData;
            Files.list(configStyle).filter((name) -> !name.endsWith(".json")).forEach((path) -> {
                String data;
                try {
                    data = Files.readString(path);
                } catch (IOException e) {
                    if (path.endsWith(".DS_Store")) return;
                    e.printStackTrace();
                    return;
                }

                var json = JsonParser.parseString(data);
                StyleData styleData;

                if (json.getAsJsonObject().has("permission")) {
                    styleData = GSON.fromJson(data, LegacyStyleData.class).convert(finalLegacyConfigData);

                    try {
                        Files.createDirectories(configStyleLegacy);
                        Files.writeString(configStyleLegacy.resolve(path.getFileName().toString()), data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    styleData = GSON.fromJson(data, StyleData.class);
                }

                try {
                    Files.writeString(path, GSON.toJson(styleData));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                var name = path.getFileName().toString();
                name = name.substring(0, name.length() - 5);
                var style = new PlayerListStyle(name, styleData);
                STYLES.put(name, style);
                STYLES_DATA.put(name, styleData);
            });

            PlayerList.PLAYER_LIST_STYLE_LOAD.invoker().onPlayerListUpdate(new PlayerList.StyleHelper(STYLES));
            CONFIG = new Config(config);
            ENABLED = true;
        } catch(Throwable exception) {
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
        return STYLES.containsKey(key);
    }

    public static Collection<PlayerListStyle> getStyles() { return STYLES.values(); }

    public static String getDefault() {
        return ENABLED ? CONFIG.configData.defaultStyle : "default";
    }

    public static void rebuildStyled() {
        if (CONFIG != null) {
            CONFIG = new Config(CONFIG.configData);
        }
        for (var e : STYLES_DATA.entrySet()) {
            STYLES.put(e.getKey(), new PlayerListStyle(e.getKey(), e.getValue()));
        }
    }
}
