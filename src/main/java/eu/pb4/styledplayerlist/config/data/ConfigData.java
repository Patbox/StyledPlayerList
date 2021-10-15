package eu.pb4.styledplayerlist.config.data;

import java.util.ArrayList;
import java.util.List;

public class ConfigData {
    public int CONFIG_VERSION_DONT_TOUCH_THIS = 1;
    public String _comment = "Before changing anything, see https://github.com/Patbox/StyledPlayerList#configuration";
    public String defaultStyle = "default";
    public long updateRate = 20;
    public String switchMessage = "Your player list style has been changed to: <gold>${style}</gold>";
    public String unknownStyleMessage = "<red>This style doesn't exist!</red>";
    public String permissionMessage = "<red>You don't have required permissions!</red>";
    public boolean changePlayerName = false;
    public String playerNameFormat = "%player:displayname%";
    public boolean updatePlayerNameEveryChatMessage = false;
    public long playerNameUpdateRate = -1;
    public List<PermissionNameFormat> permissionNameFormat = new ArrayList<>();

    public static class PermissionNameFormat {
        public String permission = "";
        public int opLevel = -1;
        public String style = "";
    }
}
