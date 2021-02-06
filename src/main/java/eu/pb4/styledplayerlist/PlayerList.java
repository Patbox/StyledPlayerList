package eu.pb4.styledplayerlist;

import eu.pb4.styledplayerlist.access.SPEPlayerList;
import eu.pb4.styledplayerlist.command.Commands;
import eu.pb4.styledplayerlist.config.ConfigManager;
import eu.pb4.styledplayerlist.config.PlayerListStyle;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.kyori.adventure.platform.fabric.FabricServerAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashMap;

public class PlayerList implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("Styled Player List");
	public static PlayerList INSTANCE;

	public static final MiniMessage miniMessage = MiniMessage.get();
	public static String VERSION = FabricLoader.getInstance().getModContainer("styledplayerlist").get().getMetadata().getVersion().getFriendlyString();
	private FabricServerAudiences audiences;


	public PlayerList() {
		if (INSTANCE != null) {
			throw new IllegalStateException("Cannot create a second instance of " + this.getClass().getName());
		}
		INSTANCE = this;
	}


	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTING.register(server -> this.audiences = FabricServerAudiences.of(server));
		ServerLifecycleEvents.SERVER_STOPPED.register(server -> this.audiences = null);

		ConfigManager.loadConfig();
		Commands.register();
		Compatibility.register();
	}


	public static FabricServerAudiences getAdventure() {
		return INSTANCE.audiences;
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


	public static class StyleHelper {
		private final LinkedHashMap<String, PlayerListStyle> styles;

		public StyleHelper(LinkedHashMap<String, PlayerListStyle> styles) {
			this.styles = styles;
		}

		public void addStyle(PlayerListStyle style) {
			this.styles.put(style.id, style);
		}

		public void removeStyle(PlayerListStyle style) {
			this.styles.remove(style.id, style);
		}
	}


	public static String getPlayersStyle(ServerPlayerEntity player) {
		return ((SPEPlayerList) player).styledPlayerList$getActivePlayerListStyle();
	}

	public static void setPlayersStyle(ServerPlayerEntity player, String key) {
		((SPEPlayerList) player).styledPlayerList$setPlayerListStyle(key);
	}

	public interface ModCompatibility {
		boolean check(ServerPlayerEntity player);
	}

}
