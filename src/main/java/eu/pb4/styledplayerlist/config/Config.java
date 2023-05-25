package eu.pb4.styledplayerlist.config;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.api.parsers.PatternPlaceholderParser;
import eu.pb4.placeholders.api.parsers.StaticPreParser;
import eu.pb4.placeholders.api.parsers.TextParserV1;
import eu.pb4.predicate.api.BuiltinPredicates;
import eu.pb4.predicate.api.MinecraftPredicate;
import eu.pb4.predicate.api.PredicateContext;
import eu.pb4.styledplayerlist.SPLHelper;
import eu.pb4.styledplayerlist.config.data.ConfigData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Config {
    public static final NodeParser PARSER = NodeParser.merge(
            TextParserV1.DEFAULT, Placeholders.DEFAULT_PLACEHOLDER_PARSER,
            new PatternPlaceholderParser(PatternPlaceholderParser.PREDEFINED_PLACEHOLDER_PATTERN, DynamicNode::of),
            StaticPreParser.INSTANCE
    );

    public final ConfigData configData;
    public final TextNode playerNameFormat;
    public final TextNode switchMessage;
    public final Text unknownStyleMessage;
    public final Text permissionMessage;
    private final List<PermissionNameFormat> permissionNameFormat;
    public final boolean isHiddenDefault;
    private final boolean passthroughDefault;


    public Config(ConfigData data) {
        this.configData = data;
        this.playerNameFormat = parseText(data.playerName.playerNameFormat);
        this.switchMessage = parseText(data.messages.switchMessage);
        this.unknownStyleMessage = parseText(data.messages.unknownStyleMessage).toText();
        this.permissionMessage = parseText(data.messages.permissionMessage).toText();
        this.isHiddenDefault = data.playerName.hidePlayer;
        this.passthroughDefault = data.playerName.ignoreFormatting;

        this.permissionNameFormat = new ArrayList<>();

        for (ConfigData.PermissionNameFormat entry : data.playerName.permissionNameFormat) {
            this.permissionNameFormat.add(new PermissionNameFormat(entry.require != null ? entry.require : BuiltinPredicates.operatorLevel(5),
                    parseText(entry.format), entry.ignoreFormatting,  entry.hidePlayer != null ? entry.hidePlayer : isHiddenDefault));
        }
    }

    public static TextNode parseText(String string) {
        return PARSER.parseNode(string);
    }

    public Text getSwitchMessage(ServerPlayerEntity player, String target) {
        return this.switchMessage.toText(ParserContext.of(DynamicNode.NODES, Map.of("style", Text.literal(target))));
    }

    @Nullable
    public Text formatPlayerUsername(ServerPlayerEntity player) {
        var context = PredicateContext.of(player);
        for (PermissionNameFormat entry : this.permissionNameFormat) {
            if (entry.predicate.test(context).success()) {
                return entry.passthrough ? null : entry.style.toText(PlaceholderContext.of(player, SPLHelper.PLAYER_NAME_VIEW));
            }
        }

        return this.passthroughDefault ? null :this.playerNameFormat.toText(PlaceholderContext.of(player, SPLHelper.PLAYER_NAME_VIEW));
    }

    public boolean isPlayerHidden(ServerPlayerEntity player) {
        var context = PredicateContext.of(player);
        for (PermissionNameFormat entry : this.permissionNameFormat) {
            if (entry.predicate.test(context).success()) {
                return entry.hidden;
            }
        }

        return this.isHiddenDefault;
    }


    record PermissionNameFormat(MinecraftPredicate predicate, TextNode style, boolean passthrough, boolean hidden) {
    }
}
