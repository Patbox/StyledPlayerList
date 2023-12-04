package eu.pb4.styledplayerlist.config.data;

import com.google.gson.annotations.SerializedName;
import eu.pb4.predicate.api.MinecraftPredicate;

import java.util.ArrayList;
import java.util.List;

public class ConfigData {
    @SerializedName("config_version")
    public int version = 2;
    @SerializedName("__comment")
    public String _comment = "Before changing anything, see https://github.com/Patbox/StyledPlayerList#configuration";
    @SerializedName("default_style")
    public String defaultStyle = "default";
    @SerializedName("messages")
    public Messages messages = new Messages();
    @SerializedName("player")
    public PlayerName playerName = new PlayerName();

    @SerializedName("client_show_in_singleplayer")
    public boolean displayOnSingleplayer = true;

    public static class Messages {
        @SerializedName("switch")
        public String switchMessage = "Your player list style has been changed to: <gold>${style}</gold>";
        @SerializedName("unknown")
        public String unknownStyleMessage = "<red>This style doesn't exist!</red>";
        @SerializedName("no_permission")
        public String permissionMessage = "<red>You don't have required permissions!</red>";
    }

    public static class PlayerName {
        @SerializedName("modify_name")
        public boolean changePlayerName = false;
        @SerializedName("modify_right_text")
        public boolean changeRightText = false;
        @SerializedName("passthrough")
        public boolean ignoreFormatting = false;
        @SerializedName("hidden")
        public boolean hidePlayer = false;
        @SerializedName("format")
        public String playerNameFormat = "%player:displayname%";
        @SerializedName("right_text")
        public String rightTextFormat = "";
        @SerializedName("update_on_chat_message")
        public boolean updatePlayerNameEveryChatMessage = false;
        @SerializedName("update_tick_time")
        public long playerNameUpdateRate = -1;
        @SerializedName("styles")
        public List<PermissionNameFormat> permissionNameFormat = new ArrayList<>();

    }

    public static class PermissionNameFormat {
        @SerializedName("require")
        public MinecraftPredicate require;
        @SerializedName("format")
        public String format = null;
        @SerializedName("right_text")
        public String rightTextFormat = null;
        @SerializedName("passthrough")
        public boolean ignoreFormatting = false;

        @SerializedName("hidden")
        public Boolean hidePlayer;

    }
}
