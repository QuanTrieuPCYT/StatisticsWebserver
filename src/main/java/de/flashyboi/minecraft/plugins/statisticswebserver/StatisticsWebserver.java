package de.flashyboi.minecraft.plugins.statisticswebserver;

import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import de.flashyboi.minecraft.plugins.statisticswebserver.exceptions.PlayerNotFoundException;
import io.undertow.Undertow;
import io.undertow.util.Headers;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import de.myzelyam.api.vanish.VanishAPI;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;

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
    private static final String ISLANDNAME_QUERY = "islandname";
    private static final String ISLANDSCHEMATIC_QUERY = "islandschematic";
    private static final String ISLANDOWNER_QUERY = "islandowner";
    private static final String ISLANDLEVEL_QUERY = "islandlevel";
    private static final String ISLANDMEMBERS_QUERY = "islandmembers";
    private static final String ISLANDWORTH_QUERY = "islandworth";
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
                            case KILL_QUERY -> {
                                try {
                                    response = PlayerStatisticsManager.getPlayerKills(playerQuery);
                                } catch (PlayerNotFoundException playerNotFoundException) {
                                    exchange.setStatusCode(404);
                                }
                            }
                            case DEATH_QUERY -> {
                                try {
                                    response = PlayerStatisticsManager.getPlayerDeaths(playerQuery);
                                } catch (PlayerNotFoundException playerNotFoundException) {
                                    exchange.setStatusCode(404);
                                }
                            }
                            case PLAYTIME_QUERY -> {
                                try {
                                    response = PlayerStatisticsManager.getPlayTime(playerQuery);
                                } catch (PlayerNotFoundException playerNotFoundException) {
                                    exchange.setStatusCode(404);
                                }
                            }
                            case MOBKILL_QUERY -> {
                                try {
                                    response = PlayerStatisticsManager.getMobKills(playerQuery);
                                } catch (PlayerNotFoundException playerNotFoundException) {
                                    exchange.setStatusCode(404);
                                }
                            }
                            case LASTPLAYED_QUERY -> {
                                try {
                                    response = PlayerStatisticsManager.getLastPlayed(playerQuery);
                                } catch (PlayerNotFoundException playerNotFoundException) {
                                    exchange.setStatusCode(404);
                                }
                            }
                            case TIMESINCELASTONLINE_QUERY -> {
                                try {
                                    response = PlayerStatisticsManager.getTimeSinceLastOnline(playerQuery);
                                } catch (PlayerNotFoundException playerNotFoundException) {
                                    exchange.setStatusCode(404);
                                }
                            }
                            case ISLANDNAME_QUERY -> {
                                try {
                                    response = PlayerStatisticsManager.getIslandName(playerQuery);
                                } catch (PlayerNotFoundException playerNotFoundException) {
                                    exchange.setStatusCode(404);
                                }
                            }
                            case ISLANDSCHEMATIC_QUERY -> {
                                try {
                                    response = PlayerStatisticsManager.getIslandSchematicName(playerQuery);
                                } catch (PlayerNotFoundException playerNotFoundException) {
                                    exchange.setStatusCode(404);
                                }
                            }
                            case ISLANDOWNER_QUERY -> {
                                try {
                                    response = PlayerStatisticsManager.getIslandOwnerName(playerQuery);
                                } catch (PlayerNotFoundException playerNotFoundException) {
                                    exchange.setStatusCode(404);
                                }
                            }
                            case ISLANDLEVEL_QUERY -> {
                                try {
                                    response = PlayerStatisticsManager.getIslandLevel(playerQuery);
                                } catch (PlayerNotFoundException playerNotFoundException) {
                                    exchange.setStatusCode(404);
                                }
                            }
                            case ISLANDMEMBERS_QUERY -> {
                                try {
                                    response = PlayerStatisticsManager.getIslandMembers(playerQuery);
                                } catch (PlayerNotFoundException playerNotFoundException) {
                                    exchange.setStatusCode(404);
                                }
                            }
                            case ISLANDWORTH_QUERY -> {
                                try {
                                    response = PlayerStatisticsManager.getIslandWorth(playerQuery);
                                } catch (PlayerNotFoundException playerNotFoundException) {
                                    exchange.setStatusCode(404);
                                }
                            }
                            default -> exchange.setStatusCode(400);
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
            if (player.isOnline() && !VanishAPI.isInvisible(Objects.requireNonNull(player.getPlayer()))) {
                return String.valueOf(0);
            } else {
                return String.valueOf(System.currentTimeMillis() - player.getLastPlayed());
            }
        } catch (NullPointerException npe) {

            if (player.isOnline() && !VanishAPI.isInvisible(Objects.requireNonNull(player.getPlayer()))) {
                return String.valueOf(0);
            } else {
                return String.valueOf(System.currentTimeMillis() - player.getLastPlayed());
            }
        }
    }
    public static String getIslandName(String playerName) throws PlayerNotFoundException {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        // We need to also bring SuperiorSkyblock2 into the mix
        SuperiorPlayer skyplayer = SuperiorSkyblockAPI.getPlayer(playerName);
        try {
            // If the player hasn't joined, OR doesn't have an island
            // returns 404 error code
            if (!player.hasPlayedBefore() || !skyplayer.hasIsland()) {
                throw new PlayerNotFoundException();
            }
            return Objects.requireNonNull(skyplayer.getIsland()).getName();
        } catch (NullPointerException npe) {

            return Objects.requireNonNull(skyplayer.getIsland()).getName();
        }
    }
    public static String getIslandSchematicName(String playerName) throws PlayerNotFoundException {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        SuperiorPlayer skyplayer = SuperiorSkyblockAPI.getPlayer(playerName);
        try {
            if (!player.hasPlayedBefore() || !skyplayer.hasIsland()) {
                throw new PlayerNotFoundException();
            }
            return Objects.requireNonNull(skyplayer.getIsland()).getSchematicName();
        } catch (NullPointerException npe) {

            return Objects.requireNonNull(skyplayer.getIsland()).getSchematicName();
        }
    }
    public static String getIslandOwnerName(String playerName) throws PlayerNotFoundException {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        SuperiorPlayer skyplayer = SuperiorSkyblockAPI.getPlayer(playerName);
        try {
            if (!player.hasPlayedBefore() || !skyplayer.hasIsland()) {
                throw new PlayerNotFoundException();
            }
            return Objects.requireNonNull(skyplayer.getIsland()).getOwner().getName();
        } catch (NullPointerException npe) {

            return Objects.requireNonNull(skyplayer.getIsland()).getOwner().getName();
        }
    }
    public static String getIslandLevel(String playerName) throws PlayerNotFoundException {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        SuperiorPlayer skyplayer = SuperiorSkyblockAPI.getPlayer(playerName);
        try {
            if (!player.hasPlayedBefore() || !skyplayer.hasIsland()) {
                throw new PlayerNotFoundException();
            }
            return String.valueOf(Objects.requireNonNull(skyplayer.getIsland()).getIslandLevel());
        } catch (NullPointerException npe) {

            return String.valueOf(Objects.requireNonNull(skyplayer.getIsland()).getIslandLevel());
        }
    }
    public static String getIslandMembers(String playerName) throws PlayerNotFoundException {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        SuperiorPlayer skyplayer = SuperiorSkyblockAPI.getPlayer(playerName);
        try {
            if (!player.hasPlayedBefore() || !skyplayer.hasIsland()) {
                throw new PlayerNotFoundException();
            }
            return Objects.requireNonNull(skyplayer.getIsland()).getIslandMembers().toString();
        } catch (NullPointerException npe) {

            return Objects.requireNonNull(skyplayer.getIsland()).getIslandMembers().toString();
        }
    }
    public static String getIslandWorth(String playerName) throws PlayerNotFoundException {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        SuperiorPlayer skyplayer = SuperiorSkyblockAPI.getPlayer(playerName);
        try {
            if (!player.hasPlayedBefore() || !skyplayer.hasIsland()) {
                throw new PlayerNotFoundException();
            }
            return String.valueOf(Objects.requireNonNull(skyplayer.getIsland()).getWorth());
        } catch (NullPointerException npe) {

            return String.valueOf(Objects.requireNonNull(skyplayer.getIsland()).getWorth());
        }
    }
}