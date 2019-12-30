package fi.matiaspaavilainen.masuiteteleports.core.handlers;

import fi.matiaspaavilainen.masuitecore.core.channels.BungeePluginChannel;
import fi.matiaspaavilainen.masuiteteleports.bungee.MaSuiteTeleports;
import fi.matiaspaavilainen.masuiteteleports.core.services.TeleportRequestService;
import fi.matiaspaavilainen.masuiteteleports.core.objects.TeleportType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class TeleportHandler {

    public static List<TeleportRequestService> requests = new ArrayList<>();
    public static HashMap<UUID, Boolean> lock = new HashMap<>();
    public static HashSet<UUID> toggles = new HashSet<>();

    private MaSuiteTeleports plugin;

    public TeleportHandler(MaSuiteTeleports plugin) {
        this.plugin = plugin;
    }

    public void teleportPlayerToPlayer(ProxiedPlayer sender, ProxiedPlayer receiver) {
        TeleportRequestService request = getTeleportRequest(receiver);
        if (request == null || !request.getSender().equals(sender)) {
            teleport(sender, receiver);
            return;
        }
        if (request.getType().equals(TeleportType.REQUEST_HERE)) {
            teleport(receiver, sender);
        } else if (request.getType().equals(TeleportType.REQUEST_TO)) {
            teleport(sender, receiver);
        }
    }


    public void teleport(ProxiedPlayer sender, ProxiedPlayer receiver) {
        BungeePluginChannel bpc = new BungeePluginChannel(plugin, receiver.getServer().getInfo(),
                "MaSuiteTeleports",
                "PlayerToPlayer",
                sender.getName(),
                receiver.getName()
        );
        if (!sender.getServer().getInfo().getName().equals(receiver.getServer().getInfo().getName())) {
            sender.connect(ProxyServer.getInstance().getServerInfo(receiver.getServer().getInfo().getName()));
            ProxyServer.getInstance().getScheduler().schedule(plugin, bpc::send, plugin.config.load("teleports", "settings.yml").getInt("teleport-delay"), TimeUnit.MILLISECONDS);
        } else {
            bpc.send();
        }
    }

    public static TeleportRequestService getTeleportRequest(ProxiedPlayer player) {
        for (TeleportRequestService request : requests) {
            if (request.getReceiver().equals(player)) {
                return request;
            }
        }
        return null;
    }

}
