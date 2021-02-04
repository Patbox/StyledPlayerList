package eu.pb4.styledplayerlist;

import eu.pb4.placeholders.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.Template;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.*;

public class Helper {
    public static Set<PlayerList.ModCompatibility> COMPATIBILITY = new HashSet<>();

    public static List<Template> getTemplates(ServerPlayerEntity player) {
        List<Template> templates = new ArrayList<>();

        templates.add(Template.of("player_name", PlayerList.getAdventure().toAdventure(player.getName())));
        templates.add(Template.of("player_display", PlayerList.getAdventure().toAdventure(player.getDisplayName())));
        templates.add(Template.of("player_ping", String.valueOf(player.pingMilliseconds)));

        templates.add(Template.of("server_online", String.valueOf(player.getServer().getPlayerManager().getCurrentPlayerCount())));
        templates.add(Template.of("server_max_online", String.valueOf(player.getServer().getPlayerManager().getMaxPlayerCount())));

        templates.add(Template.of("server_ram_max_mb", String.format("%d", Runtime.getRuntime().totalMemory() / 1048576 )));
        templates.add(Template.of("server_ram_max_gb", String.format("%.1f", (float) Runtime.getRuntime().totalMemory() / 1073741824)));
        templates.add(Template.of("server_ram_used_mb", String.format("%d", (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576 )));
        templates.add(Template.of("server_ram_used_gb", String.format("%.1f", (float) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1073741824)));

        float tps = 1000/Math.max(player.getServer().getTickTime(), 50);
        templates.add(Template.of("server_tps", String.format("%.2f", tps)));

        if (tps > 19) {
            templates.add(Template.of("server_tps_colored", String.format("§a%.1f", tps)));
        } else if (tps > 16) {
            templates.add(Template.of("server_tps_colored", String.format("§6%.1f", tps)));
        } else {
            templates.add(Template.of("server_tps_colored", String.format("§c%.1f", tps)));
        }

        PlayerList.PLAYER_LIST_UPDATE.invoker().onPlayerListUpdate(player, templates);

        return templates;
    }

    public static Text parseMessage(String minimessage, Collection<Template> templates) {
        return PlayerList.getAdventure().toNative(PlayerList.miniMessage.parse(minimessage, templates));
    }

    public static Text parseMessage(String minimessage) {
        return PlayerList.getAdventure().toNative(PlayerList.miniMessage.parse(minimessage));
    }

    public static Text parseMessageWithPlaceholders(String minimessage, List<Template> templates, ServerPlayerEntity player) {
        Text text = PlayerList.getAdventure().toNative(PlayerList.miniMessage.parse(minimessage, templates));
        return PlaceholderAPI.parseText(text, player);
    }


    public static boolean shouldSendPlayerList(ServerPlayerEntity player) {
        for (PlayerList.ModCompatibility mod : COMPATIBILITY) {
            boolean value = mod.check(player);

            if (value) {
                return false;
            }
        }
        return true;
    }
}
