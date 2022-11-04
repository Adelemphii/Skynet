package tech.adelemphii.skynet.discord.forumscraper.events;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import tech.adelemphii.skynet.discord.forumscraper.ForumScraper;
import tech.adelemphii.skynet.discord.forumscraper.objects.Server;
import tech.adelemphii.skynet.discord.forumscraper.utility.ScrapeUtility;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ReadyListener implements EventListener {

    private final ForumScraper forumScraper;

    public ReadyListener(ForumScraper forumScraper) {
        this.forumScraper = forumScraper;
    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if(event instanceof ReadyEvent) {
            System.out.println("API is ready!");
        }

        if(event instanceof GuildReadyEvent) {
            GuildReadyEvent guildReadyEvent = (GuildReadyEvent) event;
            Server server = forumScraper.getServerStorageUtility().getServer(guildReadyEvent.getGuild().getIdLong());

            Runnable statusRunnable = () -> {
                ScrapeUtility.sendStatusUpdates(guildReadyEvent.getGuild());
                ScrapeUtility.sendPopularTopics(guildReadyEvent.getGuild());
                ScrapeUtility.sendLatestTopics(guildReadyEvent.getGuild());
                ScrapeUtility.sendPingUpdate(guildReadyEvent.getGuild());
            };

            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            executor.scheduleAtFixedRate(statusRunnable, 0, 5, TimeUnit.MINUTES);

            forumScraper.addUpdateRunnable(server, statusRunnable);
        }

    }
}
