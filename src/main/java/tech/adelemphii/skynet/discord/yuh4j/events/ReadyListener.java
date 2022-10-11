package tech.adelemphii.skynet.discord.yuh4j.events;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import tech.adelemphii.skynet.discord.yuh4j.Yuh4j;
import tech.adelemphii.skynet.discord.yuh4j.utility.Mission;
import tech.adelemphii.skynet.discord.yuh4j.utility.Yuh4jMessageUtility;
import tech.adelemphii.skynet.objects.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ReadyListener implements EventListener {

    private final List<Long> remindersSent;

    private final Yuh4j discordBot;

    public ReadyListener(Yuh4j discordBot) {
        this.discordBot = discordBot;
        this.remindersSent = new ArrayList<>();
    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if(event instanceof ReadyEvent) {
            discordBot.getPlugin().getLogger().info("Yuh4j: API is ready!");
        }

        if(event instanceof GuildReadyEvent) {
            GuildReadyEvent guildReadyEvent = (GuildReadyEvent) event;

            Server server = discordBot.getServerConfiguration().getServer(guildReadyEvent.getGuild().getIdLong());

            if(server != null) {
                Yuh4jMessageUtility.sendMessage(guildReadyEvent.getGuild(), discordBot);
            }

            Runnable runnable = () -> {
                if(server == null) {
                    return;
                }
                int timeBeforeOp = server.getMinutesBeforeOpTimer();

                ArrayList<Mission> missions = Yuh4jMessageUtility.scrapeMessages(guildReadyEvent.getGuild(), server);

                if(missions == null) {
                    return;
                }

                for(Mission mission : missions) {
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
            };

            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            executor.scheduleAtFixedRate(runnable, 0, 10, TimeUnit.SECONDS);
        }
    }
}
