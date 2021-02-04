package eu.pb4.styledplayerlist;

import eu.pb4.styledplayerlist.command.Commands;
import eu.pb4.styledplayerlist.config.ConfigManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.kyori.adventure.platform.fabric.FabricServerAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

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


	@Deprecated
	public static final Event<PlayerList.PlayerListUpdate> PLAYER_LIST_UPDATE = EventFactory.createArrayBacked(PlayerList.PlayerListUpdate.class, (callbacks) -> (player, templates) -> {
		PlayerListUpdate[] callbackArray = callbacks;
		int length = callbacks.length;

		for(int x = 0; x < length; ++length) {
			PlayerListUpdate callback = callbackArray[x];
			callback.onPlayerListUpdate(player, templates);
		}

	});

	@Deprecated
	@FunctionalInterface
	public interface PlayerListUpdate {
		void onPlayerListUpdate(ServerPlayerEntity player, List<Template> templates);
	}

	public interface ModCompatibility {
		boolean check(ServerPlayerEntity player);
	}

}
