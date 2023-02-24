package tech.adelemphii.skynet.discord.yuh4j.events;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import tech.adelemphii.skynet.discord.DiscordBot;
import tech.adelemphii.skynet.discord.yuh4j.objects.Mission;
import tech.adelemphii.skynet.discord.yuh4j.utility.Yuh4jMessageUtility;
import tech.adelemphii.skynet.discord.global.objects.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScheduleListener implements EventListener {

    private final DiscordBot discordBot;
    private final List<Long> remindersSent = new ArrayList<>();

    public ScheduleListener(DiscordBot discordBot) {
        this.discordBot = discordBot;
    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if(event instanceof MessageReceivedEvent) {
            MessageReceivedEvent messageReceivedEvent = (MessageReceivedEvent) event;
            Guild guild = messageReceivedEvent.getGuild();
            Server server = discordBot.getServerConfiguration().getServer(guild.getIdLong());
            Message message = messageReceivedEvent.getMessage();

            if(messageReceivedEvent.getChannelType() == ChannelType.TEXT) {
                if(messageReceivedEvent.getChannel().asTextChannel().getIdLong() == server.getYuh4jServer().getScheduleChannel()) {
                    String error = Yuh4jMessageUtility.sendMessage(guild, discordBot);
                    if(error != null) {
                        message.reply("Error: " + error).queue();
                    }
                }
            }
        }

        if(event instanceof MessageUpdateEvent) {
            MessageUpdateEvent messageUpdateEvent = (MessageUpdateEvent) event;
            Guild guild = messageUpdateEvent.getGuild();
            Server server = discordBot.getServerConfiguration().getServer(guild.getIdLong());
            Message message = messageUpdateEvent.getMessage();

            if(messageUpdateEvent.getChannelType() == ChannelType.TEXT) {
                if(messageUpdateEvent.getChannel().asTextChannel().getIdLong() == server.getYuh4jServer().getScheduleChannel()) {
                    String error = Yuh4jMessageUtility.sendMessage(guild, discordBot);
                    if(error != null) {
                        message.reply("Error: " + error).queue();
                    }
                }
            }
        }
        if(event instanceof MessageDeleteEvent) {
            MessageDeleteEvent messageDeleteEvent = (MessageDeleteEvent) event;

            Guild guild = messageDeleteEvent.getGuild();
            Server server = discordBot.getServerConfiguration().getServer(guild.getIdLong());

            if(messageDeleteEvent.getChannelType() == ChannelType.TEXT) {
                if(messageDeleteEvent.getChannel().asTextChannel().getIdLong() == server.getYuh4jServer().getScheduleChannel()) {
                    String error = Yuh4jMessageUtility.sendMessage(guild, discordBot);
                    if(error != null) {
                        discordBot.getPlugin().getLogger().severe("[DiscordBot] Error:" + error);
                    }
                }
            }
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
        }
    }
}
