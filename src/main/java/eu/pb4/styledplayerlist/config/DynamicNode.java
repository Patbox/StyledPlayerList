package eu.pb4.styledplayerlist.config;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import java.util.Map;
import net.minecraft.network.chat.Component;

public record DynamicNode(String key, Component text) implements TextNode {
    public static DynamicNode of(String key) {
        return new DynamicNode(key, Component.literal("${" + key + "}"));
    }

    public static final ParserContext.Key<Map<String, Component>> NODES = new ParserContext.Key<>("styled_player_list:dynamic", null);

    @Override
    public Component toText(ParserContext context, boolean removeBackslashes) {
        return context.get(NODES).getOrDefault(this.key, text);
    }

    @Override
    public boolean isDynamic() {
        return true;
    }
}
