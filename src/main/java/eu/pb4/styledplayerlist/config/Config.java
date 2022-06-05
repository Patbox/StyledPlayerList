package eu.pb4.styledplayerlist.config;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.TextParserUtils;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.styledplayerlist.config.data.ConfigData;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Config {
    public static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{(?<id>[^}]+)}");

    public final ConfigData configData;
    public final TextNode playerNameFormat;
    public final TextNode switchMessage;
    public final Text unknownStyleMessage;
    public final Text permissionMessage;
    private final List<PermissionNameFormat> permissionNameFormat;


    public Config(ConfigData data) {
        this.configData = data;
        this.playerNameFormat = Placeholders.parseNodes(TextParserUtils.formatNodes(data.playerNameFormat));
        this.switchMessage = Placeholders.parseNodes(TextParserUtils.formatNodes(data.switchMessage));
        this.unknownStyleMessage = TextParserUtils.formatText(data.unknownStyleMessage);
        this.permissionMessage = TextParserUtils.formatText(data.permissionMessage);

        this.permissionNameFormat = new ArrayList<>();

        for (ConfigData.PermissionNameFormat entry : data.permissionNameFormat) {
            this.permissionNameFormat.add(new PermissionNameFormat(entry.permission, entry.opLevel == -1 ? 5 : entry.opLevel, Placeholders.parseNodes(TextParserUtils.formatNodes(entry.style))));
        }
    }

    public Text getSwitchMessage(ServerPlayerEntity player, String target) {
        return Placeholders.parseText(this.switchMessage, PLACEHOLDER_PATTERN, Map.of("style", Text.literal(target)));
    }

    public Text formatPlayerUsername(ServerPlayerEntity player) {
        ServerCommandSource source = player.getCommandSource();
        for (PermissionNameFormat entry : this.permissionNameFormat) {
            if (Permissions.check(source, entry.permission, entry.opLevel)) {
                Text text = Placeholders.parseText(entry.style, PlaceholderContext.of(player));
                return text;
            }
        }

        return Placeholders.parseText(this.playerNameFormat, PlaceholderContext.of(player));
    }


    record PermissionNameFormat(String permission, int opLevel, TextNode style) {
    }
}
