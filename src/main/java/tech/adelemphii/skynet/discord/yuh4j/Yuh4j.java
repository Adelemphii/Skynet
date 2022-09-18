package tech.adelemphii.skynet.discord.yuh4j;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import tech.adelemphii.skynet.Skynet;
import tech.adelemphii.skynet.discord.BaseCommand;
import tech.adelemphii.skynet.discord.yuh4j.commands.CommandChannel;
import tech.adelemphii.skynet.discord.yuh4j.commands.CommandConfig;
import tech.adelemphii.skynet.discord.yuh4j.commands.CommandHelp;
import tech.adelemphii.skynet.discord.yuh4j.events.CommandListener;
import tech.adelemphii.skynet.discord.yuh4j.events.ReadyListener;
import tech.adelemphii.skynet.discord.yuh4j.events.ScheduleListener;
import tech.adelemphii.skynet.objects.Server;
import tech.adelemphii.skynet.utility.data.Configuration;
import tech.adelemphii.skynet.utility.data.ServerConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Yuh4j {

    private JDA api;
    private final Map<String, BaseCommand> commands = new HashMap<>();
    private Map<Server, Runnable> updateRunnables = new HashMap<>();

    private final Skynet plugin;
    private final Configuration configuration;
    private final ServerConfiguration serverConfiguration;

    public Yuh4j(Skynet plugin) {

        this.plugin = plugin;
        this.serverConfiguration = new ServerConfiguration("yuh4j");
        this.serverConfiguration.loadServers();

        File file;
        file = new File(plugin.getDataFolder().getAbsolutePath());
        file = new File(file.getPath() + "/configs/yuh4j.yml");
        this.configuration = new Configuration(file);

        if(login(configuration.getDiscordBotToken())) {
            plugin.getLogger().info("Yuh4j: " + "Successfully logged in.");
        } else {
            plugin.getLogger().severe("Yuh4j: " + "An error has occurred while attempting to log in.");
            return;
        }
        registerEvents();
        registerCommands();
        api.getPresence().setActivity(Activity.playing("Yuh 2.0 by Adelemphii"));

    }

    public void stop(boolean now) {
        if(now) {
            api.shutdownNow();
        } else {
            api.shutdown();
        }
        serverConfiguration.saveServers();
    }

    private void registerEvents() {
        api.addEventListener(new ReadyListener(this));
        api.addEventListener(new CommandListener(this));
        api.addEventListener(new ScheduleListener(this));
    }

    private void registerCommands() {
        commands.put("channel", new CommandChannel(this));
        commands.put("config", new CommandConfig(this));
        commands.put("help", new CommandHelp(this));
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

    public ServerConfiguration getServerConfiguration() {
        return serverConfiguration;
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
