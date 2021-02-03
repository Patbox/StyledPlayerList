package eu.pb4.styledplayerlist.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.styledplayerlist.Helper;
import eu.pb4.styledplayerlist.PlayerList;
import eu.pb4.styledplayerlist.access.SPEPlayerList;
import eu.pb4.styledplayerlist.config.ConfigManager;
import eu.pb4.styledplayerlist.config.PlayerListStyle;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.kyori.adventure.text.minimessage.Template;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Locale;

import static net.minecraft.server.command.CommandManager.literal;

public class Commands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(
                    literal("styledplayerlist")
                            .requires(Permissions.require("styledplayerlist.main", true))
                            .executes(Commands::about)
                            .then(literal("switch")
                                    .requires(Permissions.require("styledplayerlist.switch", true))
                                    .then(switchArgument("style")
                                            .executes(Commands::switchStyle)
                                    )
                            )
                            .then(literal("reload")
                                    .requires(Permissions.require("styledplayerlist.reload", 3))
                                    .executes(Commands::reloadConfig)
                            )
            );

            dispatcher.register(
                    literal("plstyle")
                            .requires(Permissions.require("styledplayerlist.switch", true))
                            .then(switchArgument("style")
                            .executes(Commands::switchStyle)
                    )
            );

        });
    }

    private static int reloadConfig(CommandContext<ServerCommandSource> context) {
        if (ConfigManager.loadConfig()) {
            context.getSource().sendFeedback(new LiteralText("Reloaded config!"), false);
        } else {
            context.getSource().sendError(new LiteralText("Error accrued while reloading config!").formatted(Formatting.RED));

        }
        return 1;
    }

    private static int about(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(Helper.parseMessage("<blue>Styled Player List</blue> - " + PlayerList.VERSION), false);
        return 1;
    }

    public static int switchStyle(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        String styleId = context.getArgument("style", String.class);
        if (!ConfigManager.styleExist(styleId)) {
            source.sendFeedback(Helper.parseMessage(ConfigManager.getConfig().unknownStyleMessage), false);
            return 0;
        }

        PlayerListStyle style = ConfigManager.getStyle(styleId);
        ServerPlayerEntity player = source.getPlayer();

        if (player != null && player instanceof ServerPlayerEntity) {
            if (style.hasPermission(player)) {
                ((SPEPlayerList) player).styledPlayerList$setPlayerListStyle(styleId);

                ArrayList templates = new ArrayList();
                templates.add(Template.of("style", style.name));
                source.sendFeedback(Helper.parseMessage(ConfigManager.getConfig().switchMessage, templates), false);
                return 1;
            } else {
                source.sendFeedback(Helper.parseMessage(ConfigManager.getConfig().permissionMessage), false);
            }
        } else {
            source.sendFeedback(new LiteralText("Only players can use this command!"), false);
        }

        return 0;
    }

    public static RequiredArgumentBuilder<ServerCommandSource, String> switchArgument(String name) {
        return CommandManager.argument(name, StringArgumentType.word())
                .suggests((ctx, builder) -> {
                    String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);

                    for (PlayerListStyle style : ConfigManager.getStyles()) {
                        if (style.id.contains(remaining) && style.hasPermission(ctx.getSource())) {
                            builder.suggest(style.id);
                        }
                    }

                    return builder.buildFuture();
                });
    }


}
