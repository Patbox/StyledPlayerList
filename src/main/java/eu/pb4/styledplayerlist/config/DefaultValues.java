package eu.pb4.styledplayerlist.config;

import eu.pb4.styledplayerlist.config.data.StyleData;

public class DefaultValues {
    public static PlayerListStyle EMPTY_STYLE = new PlayerListStyle(new StyleData());


    public static StyleData exampleStyleData() {
        StyleData data = new StyleData();
        data.header.add("");
        data.header.add("<gr:#4adeff:#3d8eff><bold> Styled Player List</bold></gr> ⛏ ");
        data.header.add("");
        data.header.add("<color:#555555><strikethrough>        </strikethrough>[ </color><color:#FF5555>%server:online%<color:#6666676>/</color>%server:max_players%</color><color:#555555> ]<strikethrough>        </strikethrough></color>");
        data.header.add("");

        data.footer.add("");
        data.footer.add("<color:#555555><strikethrough>                          </strikethrough></color>");
        data.footer.add("");
        data.footer.add("<gray>TPS: %server:tps_colored% <dark_gray>|</dark_gray> <gray>Ping: <color:#ffba26>%player:ping%</color>");
        data.footer.add("");

        return data;
    }
}
