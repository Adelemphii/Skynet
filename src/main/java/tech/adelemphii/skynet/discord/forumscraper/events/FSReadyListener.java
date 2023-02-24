package tech.adelemphii.skynet.discord.forumscraper.events;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import tech.adelemphii.skynet.discord.DiscordBot;

public class FSReadyListener implements EventListener {

    //private final DiscordBot discordBot;
    public FSReadyListener(DiscordBot discordBot) {
        //this.discordBot = discordBot;
    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        /*
        if(event instanceof GuildReadyEvent) {
            GuildReadyEvent guildReadyEvent = (GuildReadyEvent) event;

            /*
            Server server = discordBot.getServerConfiguration().getServer(guildReadyEvent.getGuild().getIdLong());

            BukkitRunnable forumScraperRunnable = new BukkitRunnable() {
                @Override
                public void run() {
                    // TODO: Errors when this runs, disable for now; it also doesn't save the message when
                             running the command, sometimes?.
                    if(server == null || !server.getForumScraperServer().isEnabled()) {
                        return;
                    }
                    ScrapeUtility.sendStatusUpdates(guildReadyEvent.getGuild());
                    ScrapeUtility.sendPopularTopics(guildReadyEvent.getGuild());
                    ScrapeUtility.sendLatestTopics(guildReadyEvent.getGuild());
                    ScrapeUtility.sendPingUpdate(guildReadyEvent.getGuild());
                }
            };

            // 5 minutes
            int forumScraperLength = 5 * 20 * 60;
            forumScraperRunnable.runTaskTimerAsynchronously(discordBot.getPlugin(), 0, forumScraperLength);
            discordBot.addUpdateRunnable(server, forumScraperRunnable);
        }
        */
    }
}
