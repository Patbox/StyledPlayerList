package eu.pb4.styledplayerlist.command;


import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.styledplayerlist.GenericModInfo;
import eu.pb4.styledplayerlist.access.PlayerListViewerHolder;
import eu.pb4.styledplayerlist.config.ConfigManager;
import eu.pb4.styledplayerlist.config.PlayerListStyle;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import java.util.Collection;
import java.util.Locale;

import static net.minecraft.commands.Commands.literal;

public class Commands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
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

                            .then(literal("switchothers")
                                    .requires(Permissions.require("styledplayerlist.switch.others", 2))
                                    .then(net.minecraft.commands.Commands.argument("targets", EntityArgument.players())
                                            .then(switchArgument("style")
                                                    .executes(Commands::switchStyleOthers)
                                            )
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

    private static int reloadConfig(CommandContext<CommandSourceStack> context) {
        if (ConfigManager.loadConfig()) {
            context.getSource().sendSuccess(() -> Component.literal("Reloaded config!"), false);
        } else {
            context.getSource().sendFailure(Component.literal("Error accrued while reloading config!").withStyle(ChatFormatting.RED));
        }
        for (var player : context.getSource().getServer().getPlayerList().getPlayers()) {
            ((PlayerListViewerHolder) player.connection).styledPlayerList$reloadStyle();
            ((PlayerListViewerHolder) player.connection).styledPlayerList$setupRightText();
        }

        return 1;
    }

    private static int about(CommandContext<CommandSourceStack> context) {
        for (var text : (context.getSource().getEntity() instanceof ServerPlayer ? GenericModInfo.getAboutFull() : GenericModInfo.getAboutConsole())) {
            context.getSource().sendSuccess(() -> text, false);
        };

        return 1;
    }

    public static int switchStyleOthers(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        String styleId = context.getArgument("style", String.class);
        Collection<ServerPlayer> target = EntityArgument.getPlayers(context, "targets");

        if (!ConfigManager.styleExist(styleId)) {
            source.sendSuccess(() -> ConfigManager.getConfig().unknownStyleMessage, false);
            return 0;
        }

        PlayerListStyle style = ConfigManager.getStyle(styleId);

        for (ServerPlayer player : target) {
            ((PlayerListViewerHolder) player.connection).styledPlayerList$setStyle(styleId);
        }

        source.sendSuccess(() -> Component.literal("Changed player list style of targets to " + style.name), false);


        return 2;
    }

    private static int switchStyle(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        try {
            CommandSourceStack source = context.getSource();
            String styleId = context.getArgument("style", String.class);

            if (!ConfigManager.styleExist(styleId)) {
                source.sendSuccess(() -> ConfigManager.getConfig().unknownStyleMessage, false);
                return 0;
            }

            PlayerListStyle style = ConfigManager.getStyle(styleId);
            ServerPlayer player = source.getPlayer();

            if (player != null && player instanceof ServerPlayer) {
                if (style.hasPermission(player)) {
                    ((PlayerListViewerHolder) player.connection).styledPlayerList$setStyle(styleId);

                    source.sendSuccess(() -> ConfigManager.getConfig().getSwitchMessage(player, style.name), false);
                    return 1;
                } else {
                    source.sendSuccess(() -> ConfigManager.getConfig().permissionMessage, false);
                }
            } else {
                source.sendSuccess(() -> Component.literal("Only players can use this command!"), false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static RequiredArgumentBuilder<CommandSourceStack, String> switchArgument(String name) {
        return net.minecraft.commands.Commands.argument(name, StringArgumentType.word())
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
