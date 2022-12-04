package eu.pb4.styledplayerlist;

import eu.pb4.styledplayerlist.access.PlayerListViewerHolder;
import eu.pb4.styledplayerlist.command.Commands;
import eu.pb4.styledplayerlist.config.ConfigManager;
import eu.pb4.styledplayerlist.config.PlayerListStyle;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashMap;

public class PlayerList implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("Styled Player List");
	public static final String ID = "styledplayerlist";

	public static String VERSION = FabricLoader.getInstance().getModContainer(ID).get().getMetadata().getVersion().getFriendlyString();

	@Override
	public void onInitialize() {
		this.crabboardDetection();
		Commands.register();
		ServerLifecycleEvents.SERVER_STARTING.register((s) -> {
			this.crabboardDetection();
			ConfigManager.loadConfig();
		});
	}

	public static Identifier id(String path) {
		return new Identifier(ID, path);
	}

	public static final Event<PlayerList.PlayerListStyleLoad> PLAYER_LIST_STYLE_LOAD = EventFactory.createArrayBacked(PlayerList.PlayerListStyleLoad.class, (callbacks) -> (styleHelper) -> {
		for(PlayerListStyleLoad callback : callbacks ) {
			callback.onPlayerListUpdate(styleHelper);
		}

	});

	@FunctionalInterface
	public interface PlayerListStyleLoad {
		void onPlayerListUpdate(StyleHelper styleHelper);
	}


	public record StyleHelper(LinkedHashMap<String, PlayerListStyle> styles) {
		public void addStyle(PlayerListStyle style) {
			this.styles.put(style.id, style);
		}

		public void removeStyle(PlayerListStyle style) {
			this.styles.remove(style.id, style);
		}
	}


	public static String getPlayersStyle(ServerPlayerEntity player) {
		return ((PlayerListViewerHolder) player.networkHandler).styledPlayerList$getStyle();
	}

	public static void setPlayersStyle(ServerPlayerEntity player, String key) {
		((PlayerListViewerHolder) player).styledPlayerList$setStyle(key);
	}

	public static void addUpdateSkipCheck(ModCompatibility check) {
		SPLHelper.COMPATIBILITY.add(check);
	}

	public interface ModCompatibility {
		boolean check(ServerPlayerEntity player);
	}

	private void crabboardDetection() {
		if (FabricLoader.getInstance().isModLoaded("cardboard")) {
			LOGGER.error("");
			LOGGER.error("Cardboard detected! This mod doesn't work with it!");
			LOGGER.error("You won't get any support as long as it's present!");
			LOGGER.error("");
			LOGGER.error("Read more: https://gist.github.com/Patbox/e44844294c358b614d347d369b0fc3bf");
			LOGGER.error("");
		}
	}
}
