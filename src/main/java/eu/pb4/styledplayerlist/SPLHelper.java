package eu.pb4.styledplayerlist;

import carpet.logging.HUDController;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;

public class SPLHelper {
    public static final PlaceholderContext.ViewObject PLAYER_LIST_VIEW = PlaceholderContext.ViewObject.of(new Identifier("styled_player_list", "player_list"));
    public static final PlaceholderContext.ViewObject PLAYER_NAME_VIEW = PlaceholderContext.ViewObject.of(new Identifier("styled_player_list", "player_name"));
    public static Set<PlayerList.ModCompatibility> COMPATIBILITY = new HashSet<>();

    private static final Set<ServerPlayerEntity> BLOCKED_LAST_TIME = new HashSet<>();

    static {
        FabricLoader loader = FabricLoader.getInstance();
        if (loader.getModContainer("carpet").isPresent()) {
            SPLHelper.COMPATIBILITY.add(player -> {
                boolean block = HUDController.player_huds.containsKey(player);
                boolean block2 = BLOCKED_LAST_TIME.contains(player);

                if (block) {
                    BLOCKED_LAST_TIME.add(player);
                } else {
                    BLOCKED_LAST_TIME.remove(player);
                }

                return block || block2;
            });
        }
    }

    public static boolean shouldSendPlayerList(ServerPlayerEntity player) {
        for (PlayerList.ModCompatibility mod : COMPATIBILITY) {
            boolean value = mod.check(player);

            if (value) {
                return false;
            }
        }
        return true;
    }
}
