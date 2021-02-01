# Styled Player List
is a simple mod that allows server owners to style their player list as they like!

If you hav any questions, you can ask thm on my [Discord](https://discord.com/invite/AbqPPppgrd)

![Example image](https://i.imgur.com/hxZjOzY.png)


## Commands (and permissions):
- `/styledplayerlist` - Main command (`styledplayerlist.main`, available by default)
- `/styledplayerlist reload` - Reloads configuration and styles (requires `styledplayerlist.reload`)
- `/styledplayerlist switch <style>` or `/plstyle <style>` - Changes style (`styledplayerlist.switch`, available by default)

## Configuration:
You can find config file in `./config/styledplayerlist/`.
```json5
{
  "defaultStyle": "default", // allows to select id of default player list
  "updateRate": 20,          // change how often player list is updated (20 - 1 second)
  "...Message": "..."        // allows to change messages
}
```
### Styles:
This mod allows having multiple styles, that can be selected by players (just put them in `./config/styledplayerlist/styles/` and use `/styledplayerlist reload` command)
Formatting uses MiniMessage for which docs you can find [here](https://docs.adventure.kyori.net/minimessage.html#format).

```json5
{
  "id": "default",   // used internally and for commands
  "name": "Default", // used is messages
  "header": [        // header of player list, every element is in new line 
    "",
    "<gradient:#4adeff:#3d8eff><bold> Styled Player List</bold></gradient> ⛏ ",
    "",
    "<color:#555555><strikethrough>        </strikethrough>[ </color><color:#FF5555><server_online><color:#6666676>/</color><server_max_online></color><color:#555555> ]<strikethrough>        </strikethrough></color>",
    ""
  ],
  "footer": [        // footer of player list, every element is in new line 
    "",
    "<color:#555555><strikethrough>                          </strikethrough></color>",
    "",
    "<gray>TPS: <server_tps_colored> <dark_gray>|</dark_gray> <gray>Ping: <color:#ffba26><player_ping></color>",
    ""
  ],
  "hidden": false,   // hides in commands
  "permission": ""   // required permission, leave empty if you want to allow everyone
}
```

## Build in placeholders:
### General:
- `<player_name>` - player's name
- `<player_display>` - player's display name
- `<player_ping>` - player's ping
- `<server_online>` - number of online players
- `<server_max_online>` - maximal player count
- `<server_ram_max_mb>/<server_ram_max_gb>` - maximal amount of ram server can use
- `<server_ram_used_mb>/<server_ram_used_gb>` - used amount of ram
- `<server_tps>` - number of ticks per second
- `<server_tps_colored>` - number of ticks per second with colors

