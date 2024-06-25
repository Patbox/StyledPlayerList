package eu.pb4.styledplayerlist.config;

import eu.pb4.styledplayerlist.config.data.StyleData;

import java.util.List;

public class DefaultValues {
    public static PlayerListStyle EMPTY_STYLE = new PlayerListStyle("", new StyleData());

    public static StyleData exampleStyleData() {
        StyleData data = new StyleData();
        data.header.values.add(List.of("",
                "<gr #4adeff #3d8eff><bold> Styled Player List</bold></gr> ⛏ ",
                "",
                "<color #555555><strikethrough>        </strikethrough>[ </color><color #FF5555>%server:online%<color #6666676>/</color>%server:max_players%</color><color #555555> ]<strikethrough>        </strikethrough></color>",
                ""));

        data.footer.values.add(List.of("",
                "<color #555555><strikethrough>                          </strikethrough></color>",
                "",
                "<gray>TPS: %server:tps_colored% <dark_gray>|</dark_gray> <gray>Ping: <color:#ffba26>%player:ping%</color>",
                ""));

        return data;
    }

    public static StyleData exampleAnimatedStyleData() {
        StyleData data = new StyleData();
        data.updateRate = 2;
        data.header.values.add(List.of("",
                "<rb:1:1:0><bold> Styled Player List</bold></rb> ⛏ ",
                "",
                "<color:#555555><strikethrough>        </strikethrough>[ </color><color:#FF5555>%server:online%<color:#6666676>/</color>%server:max_players%</color><color:#555555> ]<strikethrough>        </strikethrough></color>",
                ""));
        data.header.values.add(List.of("",
                "<rb:1:1:0.1><bold> Styled Player List</bold></rb> ⛏ ",
                "",
                "<color:#555555><strikethrough>        </strikethrough>[ </color><color:#FF5555>%server:online%<color:#6666676>/</color>%server:max_players%</color><color:#555555> ]<strikethrough>        </strikethrough></color>",
                ""));
        data.header.values.add(List.of("",
                "<rb:1:1:0.2><bold> Styled Player List</bold></rb> ⛏ ",
                "",
                "<color:#555555><strikethrough>        </strikethrough>[ </color><color:#FF5555>%server:online%<color:#6666676>/</color>%server:max_players%</color><color:#555555> ]<strikethrough>        </strikethrough></color>",
                ""));
        data.header.values.add(List.of("",
                "<rb:1:1:0.3><bold> Styled Player List</bold></rb> ⛏ ",
                "",
                "<color:#555555><strikethrough>        </strikethrough>[ </color><color:#FF5555>%server:online%<color:#6666676>/</color>%server:max_players%</color><color:#555555> ]<strikethrough>        </strikethrough></color>",
                ""));
        data.header.values.add(List.of("",
                "<rb:1:1:0.4><bold> Styled Player List</bold></rb> ⛏ ",
                "",
                "<color:#555555><strikethrough>        </strikethrough>[ </color><color:#FF5555>%server:online%<color:#6666676>/</color>%server:max_players%</color><color:#555555> ]<strikethrough>        </strikethrough></color>",
                ""));
        data.header.values.add(List.of("",
                "<rb:1:1:0.5><bold> Styled Player List</bold></rb> ⛏ ",
                "",
                "<color:#555555><strikethrough>        </strikethrough>[ </color><color:#FF5555>%server:online%<color:#6666676>/</color>%server:max_players%</color><color:#555555> ]<strikethrough>        </strikethrough></color>",
                ""));
        data.header.values.add(List.of("",
                "<rb:1:1:0.6><bold> Styled Player List</bold></rb> ⛏ ",
                "",
                "<color:#555555><strikethrough>        </strikethrough>[ </color><color:#FF5555>%server:online%<color:#6666676>/</color>%server:max_players%</color><color:#555555> ]<strikethrough>        </strikethrough></color>",
                ""));
        data.header.values.add(List.of("",
                "<rb:1:1:0.7><bold> Styled Player List</bold></rb> ⛏ ",
                "",
                "<color:#555555><strikethrough>        </strikethrough>[ </color><color:#FF5555>%server:online%<color:#6666676>/</color>%server:max_players%</color><color:#555555> ]<strikethrough>        </strikethrough></color>",
                ""));
        data.header.values.add(List.of("",
                "<rb:1:1:0.8><bold> Styled Player List</bold></rb> ⛏ ",
                "",
                "<color:#555555><strikethrough>        </strikethrough>[ </color><color:#FF5555>%server:online%<color:#6666676>/</color>%server:max_players%</color><color:#555555> ]<strikethrough>        </strikethrough></color>",
                ""));


        data.footer.values.add(List.of("",
                "<color:#555555>          <strikethrough>                          </strikethrough>          </color>",
                "",
                "<gray>TPS: %server:tps_colored% <dark_gray>|</dark_gray> <gray>Ping: <color:#ffba26>%player:ping%</color>",
                ""));

        data.footer.values.add(List.of("",
                "<color:#555555>          <strikethrough>                          </strikethrough>          </color>",
                "",
                "<gray>Health: <yellow>%player:health%</yellow> <dark_gray>|</dark_gray> <gray>Playtime: <yellow>%player:playtime%</yellow>",
                ""));

        data.footer.values.add(List.of("",
                "<color:#555555>          <strikethrough>                          </strikethrough>          </color>",
                "",
                "<gray>Time: <yellow>%world:time%</yellow> <dark_gray>|</dark_gray> <gray>World: <yellow>%world:name%</yellow>",
                ""));

        data.footer.changeRate = 20;

        return data;
    }
}
