package eu.pb4.styledplayerlist;

import eu.pb4.styledplayerlist.command.Commands;
import eu.pb4.styledplayerlist.compability.Compability;
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
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
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
		Compability.register();
	}


	public static FabricServerAudiences getAdventure() {
		return INSTANCE.audiences;
	}

	public static Text parseMessage(String minimessage, List<Template> templates) {
		return INSTANCE.audiences.toNative(miniMessage.parse(minimessage, templates));
	}

	public static Text parseMessage(String minimessage) {
		return INSTANCE.audiences.toNative(miniMessage.parse(minimessage));
	}

	public static List<Template> getTemplates(ServerPlayerEntity player) {
		List<Template> templates = new ArrayList<>();

		templates.add(Template.of("player_name", getAdventure().toAdventure(player.getName())));
		templates.add(Template.of("player_display", getAdventure().toAdventure(player.getDisplayName())));
		templates.add(Template.of("player_ping", String.valueOf(player.pingMilliseconds)));

		templates.add(Template.of("server_online", String.valueOf(player.getServer().getPlayerManager().getCurrentPlayerCount())));
		templates.add(Template.of("server_max_online", String.valueOf(player.getServer().getPlayerManager().getMaxPlayerCount())));

		templates.add(Template.of("server_ram_max_mb", String.format("%d", Runtime.getRuntime().totalMemory() / 1048576 )));
		templates.add(Template.of("server_ram_max_gb", String.format("%.1f", (float) Runtime.getRuntime().totalMemory() / 1073741824)));
		templates.add(Template.of("server_ram_used_mb", String.format("%d", (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576 )));
		templates.add(Template.of("server_ram_used_gb", String.format("%.1f", (float) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1073741824)));

		float tps = 1000/Math.max(player.getServer().getTickTime(), 50);
		templates.add(Template.of("server_tps", String.format("%.2f", tps)));

		if (tps > 19) {
			templates.add(Template.of("server_tps_colored", String.format("§a%.1f", tps)));
		} else if (tps > 16) {
			templates.add(Template.of("server_tps_colored", String.format("§6%.1f", tps)));
		} else {
			templates.add(Template.of("server_tps_colored", String.format("§c%.1f", tps)));
		}

		PLAYER_LIST_UPDATE.invoker().onPlayerListUpdate(player, templates);

		return templates;
	}

	public static final Event<PlayerList.PlayerListUpdate> PLAYER_LIST_UPDATE = EventFactory.createArrayBacked(PlayerList.PlayerListUpdate.class, (callbacks) -> (player, templates) -> {
		PlayerListUpdate[] callbackArray = callbacks;
		int length = callbacks.length;

		for(int x = 0; x < length; ++length) {
			PlayerListUpdate callback = callbackArray[x];
			callback.onPlayerListUpdate(player, templates);
		}

	});

	@FunctionalInterface
	public interface PlayerListUpdate {
		void onPlayerListUpdate(ServerPlayerEntity player, List<Template> templates);
	}

}
