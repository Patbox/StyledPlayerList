package eu.pb4.styledplayerlist.config;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import net.minecraft.text.Text;

import java.util.Map;

public record DynamicNode(String key, Text text) implements TextNode {
    public static DynamicNode of(String key) {
        return new DynamicNode(key, Text.literal("${" + key + "}"));
    }

    public static final ParserContext.Key<Map<String, Text>> NODES = new ParserContext.Key<>("styled_player_list:dynamic", null);

    @Override
    public Text toText(ParserContext context, boolean removeBackslashes) {
        return context.get(NODES).getOrDefault(this.key, text);
    }

    @Override
    public boolean isDynamic() {
        return true;
    }
}
