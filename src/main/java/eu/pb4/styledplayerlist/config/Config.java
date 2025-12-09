package eu.pb4.styledplayerlist.config;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.predicate.api.BuiltinPredicates;
import eu.pb4.predicate.api.MinecraftPredicate;
import eu.pb4.predicate.api.PredicateContext;
import eu.pb4.styledplayerlist.SPLHelper;
import eu.pb4.styledplayerlist.config.data.ConfigData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class Config {
    public static final NodeParser PARSER = NodeParser.builder()
            .simplifiedTextFormat()
            .quickText()
            .globalPlaceholders()
            .staticPreParsing()
            .build();

    public final ConfigData configData;
    public final TextNode playerNameFormat;

    public final TextNode rightFormat;
    public final TextNode switchMessage;
    public final Component unknownStyleMessage;
    public final Component permissionMessage;
    private final List<PermissionNameFormat> permissionNameFormat;
    public final boolean isHiddenDefault;
    private final boolean passthroughDefault;


    public Config(ConfigData data) {
        this.configData = data;
        this.playerNameFormat = parseText(data.playerName.playerNameFormat);
        this.rightFormat = parseText(data.playerName.rightTextFormat);
        this.switchMessage = parseText(data.messages.switchMessage);
        this.unknownStyleMessage = parseText(data.messages.unknownStyleMessage).toText();
        this.permissionMessage = parseText(data.messages.permissionMessage).toText();
        this.isHiddenDefault = data.playerName.hidePlayer;
        this.passthroughDefault = data.playerName.ignoreFormatting;

        this.permissionNameFormat = new ArrayList<>();

        for (ConfigData.PermissionNameFormat entry : data.playerName.permissionNameFormat) {
            this.permissionNameFormat.add(new PermissionNameFormat(entry.require != null ? entry.require : BuiltinPredicates.operatorLevel(5),
                    parseText(entry.format), parseText(entry.rightTextFormat), entry.index, entry.ignoreFormatting, entry.hidePlayer != null ? entry.hidePlayer : isHiddenDefault));
        }
    }

    @Nullable
    public static TextNode parseText(@Nullable String string) {
        if (string == null) {
            return null;
        }
        return PARSER.parseNode(string);
    }

    public Component getSwitchMessage(ServerPlayer player, String target) {
        return this.switchMessage.toText(ParserContext.of(DynamicNode.NODES, Map.of("name", Component.literal(target))));
    }

    @Nullable
    public Component formatPlayerUsername(ServerPlayer player) {
        var context = PredicateContext.of(player);
        for (PermissionNameFormat entry : this.permissionNameFormat) {
            if (entry.name != null && entry.predicate.test(context).success()) {
                return entry.passthrough ? null : entry.name.toText(PlaceholderContext.of(player, SPLHelper.PLAYER_NAME_VIEW));
            }
        }

        return this.passthroughDefault ? null : this.playerNameFormat.toText(PlaceholderContext.of(player, SPLHelper.PLAYER_NAME_VIEW));
    }

    public Component formatPlayerRightText(ServerPlayer player) {
        var context = PredicateContext.of(player);
        for (PermissionNameFormat entry : this.permissionNameFormat) {
            if (entry.right != null && entry.predicate.test(context).success()) {
                return entry.right.toText(PlaceholderContext.of(player, SPLHelper.PLAYER_NAME_VIEW));
            }
        }

        return this.rightFormat.toText(PlaceholderContext.of(player, SPLHelper.PLAYER_NAME_VIEW));
    }

    public boolean isPlayerHidden(ServerPlayer player) {
        var context = PredicateContext.of(player);
        for (PermissionNameFormat entry : this.permissionNameFormat) {
            if (entry.predicate.test(context).success()) {
                return entry.hidden;
            }
        }

        return this.isHiddenDefault;
    }

    @Nullable
    public Integer sortingIndex(ServerPlayer player) {
        var context = PredicateContext.of(player);
        for (PermissionNameFormat entry : this.permissionNameFormat) {
            if (entry.index != null && entry.predicate.test(context).success()) {
                return entry.index;
            }
        }

        return null;
    }


    record PermissionNameFormat(MinecraftPredicate predicate, @Nullable TextNode name, @Nullable TextNode right, @Nullable Integer index, boolean passthrough, boolean hidden) {
    }
}
