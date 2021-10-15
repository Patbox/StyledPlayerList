package eu.pb4.styledplayerlist.config;

import carpet.script.language.Sys;
import eu.pb4.placeholders.PlaceholderAPI;
import eu.pb4.placeholders.PlaceholderResult;
import eu.pb4.placeholders.TextParser;
import eu.pb4.styledplayerlist.config.data.ConfigData;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Config {
    public static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{(?<id>[^}]+)}");

    public final ConfigData configData;
    public final Text playerNameFormat;
    public final Text switchMessage;
    public final Text unknownStyleMessage;
    public final Text permissionMessage;
    private final List<PermissionNameFormat> permissionNameFormat;


    public Config(ConfigData data) {
        this.configData = data;
        this.playerNameFormat = TextParser.parse(data.playerNameFormat);
        this.switchMessage = TextParser.parse(data.switchMessage);
        this.unknownStyleMessage = TextParser.parse(data.unknownStyleMessage);
        this.permissionMessage = TextParser.parse(data.permissionMessage);

        this.permissionNameFormat = new ArrayList<>();

        for (ConfigData.PermissionNameFormat entry : data.permissionNameFormat) {
            this.permissionNameFormat.add(new PermissionNameFormat(entry.permission, entry.opLevel == -1 ? 5 : entry.opLevel, TextParser.parse(entry.style)));
        }
    }

    public Text getSwitchMessage(ServerPlayerEntity player, String target) {
        return PlaceholderAPI.parseTextCustom(this.switchMessage, player, Map.of(new Identifier("style"), (ctx) -> PlaceholderResult.value(target)), PLACEHOLDER_PATTERN);
    }

    public Text formatPlayerUsername(ServerPlayerEntity player) {
        ServerCommandSource source = player.getCommandSource();
        for (PermissionNameFormat entry : this.permissionNameFormat) {
            if (Permissions.check(source, entry.permission, entry.opLevel)) {
                Text text = PlaceholderAPI.parseText(entry.style, player);
                return text;
            }
        }

        return PlaceholderAPI.parseText(this.playerNameFormat, player);
    }


    record PermissionNameFormat(String permission, int opLevel, Text style) {}
}
