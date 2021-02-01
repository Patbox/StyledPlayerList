package eu.pb4.styledplayerlist.config;

import eu.pb4.styledplayerlist.config.data.StyleData;

public class DefaultValues {
    public static PlayerListStyle EMPTY_STYLE = new PlayerListStyle(new StyleData());


    public static StyleData exampleStyleData() {
        StyleData data = new StyleData();
        data.header.add("");
        data.header.add("<gradient:#4adeff:#3d8eff><bold> Styled Player List</bold></gradient> ⛏ ");
        data.header.add("");
        data.header.add("<color:#555555><strikethrough>        </strikethrough>[ </color><color:#FF5555><server_online><color:#6666676>/</color><server_max_online></color><color:#555555> ]<strikethrough>        </strikethrough></color>");
        data.header.add("");

        data.footer.add("");
        data.footer.add("<color:#555555><strikethrough>                          </strikethrough></color>");
        data.footer.add("");
        data.footer.add("<gray>TPS: <server_tps_colored> <dark_gray>|</dark_gray> <gray>Ping: <color:#ffba26><player_ping></color>");
        data.footer.add("");

        return data;
    }
}
