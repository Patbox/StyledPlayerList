package eu.pb4.styledplayerlist.config.data.legacy;

import eu.pb4.predicate.api.BuiltinPredicates;
import eu.pb4.styledplayerlist.config.data.ConfigData;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class LegacyConfigData {
    public int CONFIG_VERSION_DONT_TOUCH_THIS = 1;
    public String _comment = "Before changing anything, see https://github.com/Patbox/StyledPlayerList#configuration";
    public String defaultStyle = "default";
    public int updateRate = 20;
    public boolean displayOnSingleplayer = true;
    public String switchMessage = "Your player list style has been changed to: <gold>${style}</gold>";
    public String unknownStyleMessage = "<red>This style doesn't exist!</red>";
    public String permissionMessage = "<red>You don't have required permissions!</red>";
    public boolean changePlayerName = false;
    public String playerNameFormat = "%player:displayname%";
    public boolean updatePlayerNameEveryChatMessage = false;
    public long playerNameUpdateRate = -1;
    public List<PermissionNameFormat> permissionNameFormat = new ArrayList<>();

    public ConfigData convert() {
        var x = new ConfigData();
        x.displayOnSingleplayer = this.displayOnSingleplayer;
        x.defaultStyle = this.defaultStyle;
        x.messages.permissionMessage = this.permissionMessage;
        x.messages.switchMessage = this.switchMessage;
        x.messages.unknownStyleMessage = this.unknownStyleMessage;
        x.playerName.changePlayerName = this.changePlayerName;
        x.playerName.playerNameFormat = this.playerNameFormat;
        x.playerName.updatePlayerNameEveryChatMessage = this.updatePlayerNameEveryChatMessage;
        x.playerName.playerNameUpdateRate = this.playerNameUpdateRate;

        for (var perm : this.permissionNameFormat) {
            var a = new ConfigData.PermissionNameFormat();
            a.require = BuiltinPredicates.modPermissionApi(perm.permission, perm.opLevel);
            a.format = perm.style;
            x.playerName.permissionNameFormat.add(a);
        }

        return x;
    }

    public static class PermissionNameFormat {
        public String permission = "";
        public int opLevel = -1;
        public String style = "";
    }
}
