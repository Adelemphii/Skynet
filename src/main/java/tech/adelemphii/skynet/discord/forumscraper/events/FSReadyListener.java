package tech.adelemphii.skynet.discord.forumscraper.events;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import tech.adelemphii.skynet.discord.DiscordBot;
import tech.adelemphii.skynet.discord.forumscraper.objects.exceptions.ScrapeException;
import tech.adelemphii.skynet.discord.forumscraper.utility.FSGeneralUtility;
import tech.adelemphii.skynet.discord.global.objects.Server;

public class FSReadyListener implements EventListener {

    private final DiscordBot discordBot;
    public FSReadyListener(DiscordBot discordBot) {
        this.discordBot = discordBot;
    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {

        if(event instanceof GuildReadyEvent) {
            GuildReadyEvent guildReadyEvent = (GuildReadyEvent) event;
            Server server = discordBot.getServerConfiguration().getServer(guildReadyEvent.getGuild().getIdLong());

            BukkitRunnable forumScraperRunnable = new BukkitRunnable() {
                @Override
                public void run() {
                    if(server == null || !server.getForumScraperServer().isEnabled()) {
                        return;
                    }

                    try {
                        FSGeneralUtility.sendUpdates(guildReadyEvent.getGuild());
                    } catch(ScrapeException e) {
                        e.printStackTrace();
                    }
                }
            };

            // 5 minutes
            int forumScraperLength = 5 * 20 * 60;
            forumScraperRunnable.runTaskTimerAsynchronously(discordBot.getPlugin(), 0, forumScraperLength);
            discordBot.addUpdateRunnable(server, forumScraperRunnable);
        }
    }
}
