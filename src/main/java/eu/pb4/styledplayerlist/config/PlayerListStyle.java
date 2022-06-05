package eu.pb4.styledplayerlist.config;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.TextParserUtils;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.styledplayerlist.config.data.StyleData;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class PlayerListStyle {
    public final String id;
    public final String name;

    public final TextNode header;
    public final TextNode footer;

    public final Boolean hidden;
    private final String permission;


    public PlayerListStyle(StyleData data) {
        this.id = data.id;
        this.name = data.name;
        this.header = Placeholders.parseNodes(TextParserUtils.formatNodes(String.join("\n", data.header)));
        this.footer = Placeholders.parseNodes(TextParserUtils.formatNodes(String.join("\n", data.footer)));
        this.hidden = data.hidden;
        this.permission = data.permission;
    }

    public boolean hasPermission(ServerPlayerEntity player) {
        if (this.permission.length() == 0) {
            return true;
        } else {
            return Permissions.check(player, this.permission, 2);
        }
    }

    public boolean hasPermission(ServerCommandSource source) {
        if (this.permission.length() == 0) {
            return true;
        } else {
            return Permissions.check(source, this.permission, 2);
        }
    }

    public Text getHeader(ServerPlayerEntity player) {
        return this.header.toText(PlaceholderContext.of(player).asParserContext(), true);
    }

    public Text getFooter(ServerPlayerEntity player) {
        return this.footer.toText(PlaceholderContext.of(player).asParserContext(), true);
    }
}
