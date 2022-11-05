package tech.adelemphii.skynet;

import org.bukkit.plugin.java.JavaPlugin;
import tech.adelemphii.skynet.discord.DiscordBot;

public final class Skynet extends JavaPlugin {

    private static Skynet INSTANCE;

    private DiscordBot discordBot;

    @Override
    public void onEnable() {
        INSTANCE = this;

        this.discordBot = new DiscordBot(this);
    }

    @Override
    public void onDisable() {
        discordBot.stop(false);
    }

    public static Skynet getInstance() {
        return INSTANCE;
    }

    public DiscordBot getDiscordBot() {
        return discordBot;
    }
}
