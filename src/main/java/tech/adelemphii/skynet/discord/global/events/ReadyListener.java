package tech.adelemphii.skynet.discord.global.events;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import tech.adelemphii.skynet.discord.DiscordBot;

public class ReadyListener implements EventListener {

    private final DiscordBot discordBot;

    public ReadyListener(DiscordBot discordBot) {
        this.discordBot = discordBot;
    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if(event instanceof ReadyEvent) {
            discordBot.getPlugin().getLogger().info("API is ready!");
        }
    }
}
