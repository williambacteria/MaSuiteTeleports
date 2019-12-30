package fi.matiaspaavilainen.masuiteteleports.core.services;

import fi.matiaspaavilainen.masuitecore.core.channels.BungeePluginChannel;
import fi.matiaspaavilainen.masuitecore.core.objects.Location;
import fi.matiaspaavilainen.masuiteteleports.bungee.MaSuiteTeleports;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.UUID;

public class PlayerPositionService {

    private HashMap<UUID, Runnable> positionRunnables = new HashMap<>();
    public HashMap<UUID, Location> positions = new HashMap<>();

    private MaSuiteTeleports plugin;

    public PlayerPositionService(MaSuiteTeleports plugin) {
        this.plugin = plugin;
    }

    /**
     * Request {@link ProxiedPlayer} location from server side
     *
     * @param player player to use
     */
    public void requestPosition(ProxiedPlayer player) {
        new BungeePluginChannel(plugin,
                player.getServer().getInfo(),
                "MaSuiteTeleports",
                "GetLocation",
                player.getName(),
                player.getServer().getInfo().getName()).send();
    }


    /**
     * Called when {@link Location} of the {@link ProxiedPlayer} has received
     *
     * @param player player whose location was received
     * @param loc    location of the {@link ProxiedPlayer}
     */
    public void locationReceived(ProxiedPlayer player, Location loc) {
        if (positionRunnables.containsKey(player.getUniqueId())) {
            positionRunnables.remove(player.getUniqueId()).run();
            return;
        }
        positions.put(player.getUniqueId(), loc);
    }
}