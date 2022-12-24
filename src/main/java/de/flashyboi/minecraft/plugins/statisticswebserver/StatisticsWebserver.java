package de.flashyboi.minecraft.plugins.statisticswebserver;

import de.flashyboi.minecraft.plugins.statisticswebserver.exceptions.PlayerNotFoundException;
import io.undertow.Undertow;
import io.undertow.util.Headers;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import de.myzelyam.api.vanish.VanishAPI;

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
    private static final String KILL_QUERY = "kills";
    private static final String DEATH_QUERY = "deaths";
    private static final String PLAYTIME_QUERY = "playtime";
    private static final String MOBKILL_QUERY = "mobkills";
    private static final String LASTPLAYED_QUERY = "lastplayed";
    private static final String TIMESINCELASTONLINE_QUERY = "timesincelastonline";
    protected static void startWebserver(int port, String host) {
        Undertow server = Undertow.builder()
                .addHttpListener(port, host)
                .setHandler(exchange -> {
                    exchange.getResponseHeaders()
                            .put(Headers.CONTENT_TYPE, "text/html");
                    Map<String, Deque<String>> queryParams = exchange.getQueryParameters();
                    try {
                        String playerQuery = queryParams.get("player").element();
                        String statisticQuery = queryParams.get("stat").element();
                        String response = "";
                        switch (statisticQuery) {
                            case KILL_QUERY:
                                try {
                                    response = PlayerStatisticsManager.getPlayerKills(playerQuery);
                                } catch (PlayerNotFoundException playerNotFoundException) {
                                    exchange.setStatusCode(404);
                                }
                                break;
                            case DEATH_QUERY:
                                try {
                                    response = PlayerStatisticsManager.getPlayerDeaths(playerQuery);
                                } catch (PlayerNotFoundException playerNotFoundException) {
                                    exchange.setStatusCode(404);
                                }
                                break;
                            case PLAYTIME_QUERY:
                                try {
                                    response = PlayerStatisticsManager.getPlayTime(playerQuery);
                                } catch (PlayerNotFoundException playerNotFoundException) {
                                    exchange.setStatusCode(404);
                                }
                                break;
                            case MOBKILL_QUERY:
                                try {
                                    response = PlayerStatisticsManager.getMobKills(playerQuery);
                                } catch (PlayerNotFoundException playerNotFoundException) {
                                    exchange.setStatusCode(404);
                                }
                                break;
                            case LASTPLAYED_QUERY:
                                try {
                                    response = PlayerStatisticsManager.getLastPlayed(playerQuery);
                                } catch (PlayerNotFoundException playerNotFoundException) {
                                    exchange.setStatusCode(404);
                                }
                                break;
                            case TIMESINCELASTONLINE_QUERY:
                                try {
                                    response = PlayerStatisticsManager.getTimeSinceLastOnline(playerQuery);
                                } catch (PlayerNotFoundException playerNotFoundException) {
                                    exchange.setStatusCode(404);
                                }
                                break;
                            default:
                                exchange.setStatusCode(400);
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
    public static String getPlayerKills(String playerName) throws PlayerNotFoundException {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        try {
            if (!player.hasPlayedBefore()) {
                throw new PlayerNotFoundException();
            }
            return String.valueOf(player.getStatistic(Statistic.PLAYER_KILLS));
        } catch (NullPointerException npe) {

            return String.valueOf(player.getStatistic(Statistic.PLAYER_KILLS));
        }
    }

    public static String getPlayerDeaths(String playerName) throws PlayerNotFoundException {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        try {
            if (!player.hasPlayedBefore()) {
                throw new PlayerNotFoundException();
            }
            return String.valueOf(player.getStatistic(Statistic.DEATHS));
        } catch (NullPointerException npe) {

            return String.valueOf(player.getStatistic(Statistic.DEATHS));
        }
    }

    public static String getPlayTime(String playerName) throws PlayerNotFoundException {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        try {
            if (!player.hasPlayedBefore()) {
                throw new PlayerNotFoundException();
            }
            return String.valueOf(player.getStatistic(Statistic.TOTAL_WORLD_TIME)/20);
        } catch (NullPointerException npe) {

                return String.valueOf(player.getStatistic(Statistic.TOTAL_WORLD_TIME)/20);
        }
    }
    public static String getMobKills(String playerName) throws PlayerNotFoundException {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        try {
            if (!player.hasPlayedBefore()) {
                throw new PlayerNotFoundException();
            }
            return String.valueOf(player.getStatistic(Statistic.MOB_KILLS));
        } catch (NullPointerException npe) {

            return String.valueOf(player.getStatistic(Statistic.MOB_KILLS));
        }
    }
    public static String getLastPlayed(String playerName) throws PlayerNotFoundException {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        try {
            if (!player.hasPlayedBefore()) {
                throw new PlayerNotFoundException();
            }
            return String.valueOf(player.getLastPlayed());
        } catch (NullPointerException npe) {

            return String.valueOf(player.getLastPlayed());
        }
    }
    public static String getTimeSinceLastOnline(String playerName) throws PlayerNotFoundException {
        // This one is a bit more tricky
        // As Bukkit only return the last time the player was logged in, not in real time,
        // we need to return this with the value of 0 if the player is online
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        try {
            if (!player.hasPlayedBefore()) {
                throw new PlayerNotFoundException();
            }
            if (player.isOnline() && !VanishAPI.isInvisible(player.getPlayer())) {
                return String.valueOf(0);
            } else {
                return String.valueOf(System.currentTimeMillis() - player.getLastPlayed());
            }
        } catch (NullPointerException npe) {

            if (player.isOnline() && !VanishAPI.isInvisible(player.getPlayer())) {
                return String.valueOf(0);
            } else {
                return String.valueOf(System.currentTimeMillis() - player.getLastPlayed());
            }
        }
    }
}