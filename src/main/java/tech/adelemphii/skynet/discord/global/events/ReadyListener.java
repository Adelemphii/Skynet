package tech.adelemphii.skynet.discord.global.events;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import tech.adelemphii.skynet.discord.DiscordBot;
import tech.adelemphii.skynet.discord.global.objects.Server;
import tech.adelemphii.skynet.discord.yuh4j.objects.Mission;
import tech.adelemphii.skynet.discord.yuh4j.utility.Yuh4jMessageUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReadyListener implements EventListener {

    private final List<Long> remindersSent;

    private final DiscordBot discordBot;

    public ReadyListener(DiscordBot discordBot) {
        this.discordBot = discordBot;
        this.remindersSent = new ArrayList<>();
    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if(event instanceof ReadyEvent) {
            discordBot.getPlugin().getLogger().info("API is ready!");
        }

        if(event instanceof GuildReadyEvent) {
            GuildReadyEvent guildReadyEvent = (GuildReadyEvent) event;

            Server server = discordBot.getServerConfiguration().getServer(guildReadyEvent.getGuild().getIdLong());

            if(server != null) {
                Yuh4jMessageUtility.sendMessage(guildReadyEvent.getGuild(), discordBot);
            }

            BukkitRunnable yuh4jRunnable = new BukkitRunnable() {
                @Override
                public void run() {
                    if(server == null || !server.getYuh4jServer().isEnabled()) {
                        return;
                    }

                    int timeBeforeOp = server.getYuh4jServer().getMinutesBeforeOpTimer();

                    ArrayList<Mission> missions = Yuh4jMessageUtility.scrapeMessages(guildReadyEvent.getGuild(), server);
                    if(missions == null) {
                        return;
                    }

                    for (Mission mission : missions) {
                        if(remindersSent.contains(mission.getMessage().getIdLong())) {
                            continue;
                        }

                        // -------------------------------------------------------------------- X minutes in UNIX timestamp
                        Map<String, MessageEmbed> messageEmbedMap = Yuh4jMessageUtility.getPingsWhenTime(mission,
                                timeBeforeOp * 60000L / 1000L);
                        if(messageEmbedMap == null || messageEmbedMap.isEmpty()) {
                            return;
                        }

                        String text = messageEmbedMap.keySet().stream().findFirst().get();
                        MessageEmbed messageEmbed = messageEmbedMap.values().stream().findFirst().get();

                        String error = Yuh4jMessageUtility.sendBreadPings(guildReadyEvent.getGuild(), discordBot, messageEmbed,
                                missions, text, timeBeforeOp);
                        if(error != null) {
                            discordBot.getPlugin().getLogger().severe("Error: " + error);
                            return;
                        }

                        remindersSent.add(mission.getMessage().getIdLong());
                    }
                }
            };

            // 10 seconds
            int yuh4jLength = 10 * 20;
            yuh4jRunnable.runTaskTimerAsynchronously(discordBot.getPlugin(), 0, yuh4jLength);
            discordBot.addUpdateRunnable(server, yuh4jRunnable);

            BukkitRunnable forumScraperRunnable = new BukkitRunnable() {
                @Override
                public void run() {
                    /* TODO: Errors when this runs, disable for now; it also doesn't save the message when
                            running the command, sometimes?.
                    if(server == null || !server.getForumScraperServer().isEnabled()) {
                        return;
                    }
                    ScrapeUtility.sendStatusUpdates(guildReadyEvent.getGuild());
                    ScrapeUtility.sendPopularTopics(guildReadyEvent.getGuild());
                    ScrapeUtility.sendLatestTopics(guildReadyEvent.getGuild());
                    ScrapeUtility.sendPingUpdate(guildReadyEvent.getGuild());
                    */
                }
            };

            // 5 minutes
            int forumScraperLength = 5 * 20 * 60;
            forumScraperRunnable.runTaskTimerAsynchronously(discordBot.getPlugin(), 0, forumScraperLength);
            discordBot.addUpdateRunnable(server, forumScraperRunnable);
        }
    }
}
