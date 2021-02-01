package eu.pb4.styledplayerlist.config;

import eu.pb4.styledplayerlist.config.data.StyleData;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerListStyle {
    public final String id;
    public final String name;

    public final String header;
    public final String footer;

    public final Boolean hidden;
    private final String permission;


    public PlayerListStyle(StyleData data) {
        this.id = data.id;
        this.name = data.name;
        this.header = String.join("\n", data.header);
        this.footer = String.join("\n", data.footer);
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
}
