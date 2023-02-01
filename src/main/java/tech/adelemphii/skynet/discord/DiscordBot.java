package tech.adelemphii.skynet.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import tech.adelemphii.skynet.Skynet;
import tech.adelemphii.skynet.discord.forumscraper.commands.FSChannel;
import tech.adelemphii.skynet.discord.forumscraper.commands.FSConfig;
import tech.adelemphii.skynet.discord.global.commands.*;
import tech.adelemphii.skynet.discord.global.events.CommandListener;
import tech.adelemphii.skynet.discord.global.events.ReadyListener;
import tech.adelemphii.skynet.discord.global.objects.Server;
import tech.adelemphii.skynet.discord.global.utility.data.Configuration;
import tech.adelemphii.skynet.discord.yuh4j.commands.Yuh4jCommandChannel;
import tech.adelemphii.skynet.discord.yuh4j.commands.Yuh4jCommandConfig;
import tech.adelemphii.skynet.discord.global.utility.data.ServerConfiguration;
import tech.adelemphii.skynet.discord.yuh4j.events.ScheduleListener;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DiscordBot {

    private JDA api;
    private final Map<String, BaseCommand> commands = new HashMap<>();
    private Map<Server, Runnable> updateRunnables = new HashMap<>();

    private final Skynet plugin;
    private final Configuration configuration;
    private final ServerConfiguration serverConfiguration;

    public DiscordBot(Skynet plugin) {

        this.plugin = plugin;
        this.serverConfiguration = new ServerConfiguration("skynet");
        this.serverConfiguration.loadServers();

        File file;
        file = new File(plugin.getDataFolder().getAbsolutePath());
        file = new File(file.getPath() + "/configs/skynet.yml");
        this.configuration = new Configuration(file);

        if(login(configuration.getDiscordBotToken())) {
            plugin.getLogger().info("Successfully logged in.");
        } else {
            plugin.getLogger().severe("An error has occurred while attempting to log in.");
            return;
        }
        registerEvents();
        registerCommands();
        api.getPresence().setActivity(Activity.playing("Skynet by Adelemphii"));

    }

    public void stop(boolean now) {
        if(api == null) {
            return;
        }
        if(now) {
            api.shutdownNow();
        } else {
            api.shutdown();
        }
        serverConfiguration.saveServers();
    }

    private void registerEvents() {
        // global events
        api.addEventListener(new CommandListener(this));
        api.addEventListener(new ReadyListener(this));

        // yuh4j events
        api.addEventListener(new ScheduleListener(this));
    }

    private void registerCommands() {
        // global commands
        commands.put("module", new GlobalModule());
        commands.put("help", new GlobalHelp(this));
        commands.put("credits", new GlobalCredits());
        commands.put("config", new GlobalConfig());

        // yuh4j commands
        commands.put("y4jchannel", new Yuh4jCommandChannel(this));
        commands.put("y4jconfig", new Yuh4jCommandConfig(this));

        // forumscraper commands
        commands.put("fschannel", new FSChannel());
        commands.put("fsconfig", new FSConfig());
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
