package tech.adelemphii.skynet.discord.forumscraper;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import tech.adelemphii.skynet.Skynet;
import tech.adelemphii.skynet.discord.BaseCommand;
import tech.adelemphii.skynet.discord.forumscraper.commands.*;
import tech.adelemphii.skynet.discord.forumscraper.events.MessageListener;
import tech.adelemphii.skynet.discord.forumscraper.events.ReadyListener;
import tech.adelemphii.skynet.discord.forumscraper.utility.data.ServerStorageUtility;
import tech.adelemphii.skynet.discord.forumscraper.objects.Server;
import tech.adelemphii.skynet.utility.data.Configuration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForumScraper {

    private JDA api;
    private final Map<String, BaseCommand> commands = new HashMap<>();
    private Map<Server, Runnable> updateRunnables = new HashMap<>();

    private final Skynet plugin;
    private final Configuration configuration;
    private final ServerStorageUtility serverStorageUtility;

    public ForumScraper(Skynet plugin) {
        this.plugin = plugin;
        this.serverStorageUtility = new ServerStorageUtility("forumscraper");
        this.serverStorageUtility.loadServers();

        File file;
        file = new File(plugin.getDataFolder().getAbsolutePath());
        file = new File(file.getPath() + "/configs/forumscraper.yml");
        this.configuration = new Configuration(file);

        if(login(configuration.getDiscordBotToken())) {
            plugin.getLogger().info("ForumScraper: " + "Successfully logged in.");
        } else {
            plugin.getLogger().severe("ForumScraper: " + "An error has occurred while attempting to log in.");
            return;
        }
        registerEvents();
        registerCommands();
        api.getPresence().setActivity(Activity.playing("ForumScraper by Adelemphii"));
    }

    public void stop(boolean now) {
        if(now) {
            api.shutdownNow();
        } else {
            api.shutdown();
        }
        serverStorageUtility.saveServers();
    }

    private void registerEvents() {
        api.addEventListener(List.of(new MessageListener(this), new ReadyListener(this)));
    }

    private void registerCommands() {
        commands.put("channel", new CommandChannel());
        commands.put("config", new CommandConfig());
        commands.put("credits", new CommandCredits());
        commands.put("help", new CommandHelp());
        commands.put("ping", new CommandPing());
    }

    public boolean login(String token) {
        try {
            api = JDABuilder.createDefault(token, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS,
                    GatewayIntent.GUILD_MESSAGES).disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOJI, CacheFlag.STICKER).build();
            return true;
        } catch(InvalidTokenException e) {
            e.printStackTrace();
            return false;
        }
    }

    public JDA getApi() {
        return api;
    }

    public Map<String, BaseCommand> getCommands() {
        return commands;
    }

    public Skynet getPlugin() {
        return plugin;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public ServerStorageUtility getServerStorageUtility() {
        return serverStorageUtility;
    }

    public Map<Server, Runnable> getUpdateRunnables() {
        return updateRunnables;
    }

    public void setUpdateRunnables(Map<Server, Runnable> updateRunnables) {
        this.updateRunnables = updateRunnables;
    }

    public void addUpdateRunnable(Server server, Runnable runnable) {
        this.updateRunnables.put(server, runnable);
    }
}
