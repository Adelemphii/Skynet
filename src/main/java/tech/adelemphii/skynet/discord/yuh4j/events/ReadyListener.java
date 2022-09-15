package tech.adelemphii.skynet.discord.yuh4j.events;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import tech.adelemphii.skynet.discord.yuh4j.Yuh4j;
import tech.adelemphii.skynet.discord.yuh4j.utility.Yuh4jMessageUtility;

public class ReadyListener implements EventListener {

    private final Yuh4j discordBot;

    public ReadyListener(Yuh4j discordBot) {
        this.discordBot = discordBot;
    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if(event instanceof ReadyEvent) {
            discordBot.getPlugin().getLogger().info("Yuh4j: API is ready!");
        }

        if(event instanceof GuildReadyEvent guildReadyEvent) {
            Yuh4jMessageUtility.sendMessage(guildReadyEvent.getGuild(), discordBot);
        }
    }
}
