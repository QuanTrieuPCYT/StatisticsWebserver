package de.flashyboi.minecraft.plugins.statisticswebserver;

import de.flashyboi.minecraft.plugins.statisticswebserver.exceptions.PlayerNotFoundException;
import io.undertow.Undertow;
import io.undertow.util.Headers;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import de.myzelyam.api.vanish.VanishAPI;
import org.json.JSONObject;

import java.util.*;
import java.util.logging.Logger;

public final class StatisticsWebserver extends JavaPlugin {
    public static Plugin plugin;
    public final Logger logger = this.getLogger();

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        logger.info("Starting StatisticsWebserver...");
        try {
            WebserverManager.startWebserver(7272, "0.0.0.0");
        } catch (Exception e) {
            logger.severe("Could not start webserver!");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        logger.info("Stopping StatisticsWebserver...");
    }
}

class WebserverManager {
    protected static void startWebserver(int port, String host) {
        Undertow server = Undertow.builder()
                .addHttpListener(port, host)
                .setHandler(exchange -> {
                    exchange.getResponseHeaders()
                            .put(Headers.CONTENT_TYPE, "application/json");
                    Map<String, Deque<String>> queryParams = exchange.getQueryParameters();
                    try {
                        String playerQuery = queryParams.get("player").element();
                        String response = "";
                        try {
                            response = PlayerStatisticsManager.response(playerQuery);
                        } catch (PlayerNotFoundException playerNotFoundException) {
                            exchange.setStatusCode(404);
                        }
                        exchange.getResponseSender()
                                .send(response);
                    } catch (IllegalArgumentException | NullPointerException e) {
                        exchange.setStatusCode(400);
                    }
                }).build();

        server.start();
    }
}

class PlayerStatisticsManager {
    public static String response(String playerName) throws PlayerNotFoundException {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        try {
            if (!player.hasPlayedBefore()) {
                throw new PlayerNotFoundException();
            }
            return JSONMaker.json(playerName);
        } catch (NullPointerException npe) {
            return JSONMaker.json(playerName);
        }
    }
}

class JSONMaker {
    public static String json(String playerName) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        JSONObject playerresponse = new JSONObject();
        playerresponse.put("kills", player.getStatistic(Statistic.PLAYER_KILLS));
        playerresponse.put("deaths", player.getStatistic(Statistic.DEATHS));
        playerresponse.put("mobkills", player.getStatistic(Statistic.MOB_KILLS));
        playerresponse.put("lastplayed", player.getLastPlayed());
        playerresponse.put("playtime", player.getStatistic(Statistic.TOTAL_WORLD_TIME)/20);
        playerresponse.put("timesincelastonline", JSONMaker.getTimeSinceLastOnline(playerName));
        return playerresponse.toString();
    }
    private static Long getTimeSinceLastOnline(String playerlastonlineName) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerlastonlineName);
        if (player.isOnline() && !VanishAPI.isInvisible(Objects.requireNonNull(player.getPlayer()))) {
            return Long.valueOf("0");
        } else {
            return Long.valueOf(System.currentTimeMillis() - player.getLastPlayed());
        }
    }
}