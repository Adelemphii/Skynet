package tech.adelemphii.skynet.discord.yuh4j.events;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import tech.adelemphii.skynet.discord.yuh4j.Yuh4j;
import tech.adelemphii.skynet.discord.yuh4j.utility.Yuh4jMessageUtility;
import tech.adelemphii.skynet.discord.yuh4j.objects.Server;

public class ScheduleListener implements EventListener {

    private final Yuh4j discordBot;

    public ScheduleListener(Yuh4j discordBot) {
        this.discordBot = discordBot;
    }

    @Override
    public void onEvent(@NotNull GenericEvent genericEvent) {
        if(genericEvent instanceof MessageReceivedEvent) {
            MessageReceivedEvent event = (MessageReceivedEvent) genericEvent;
            Guild guild = event.getGuild();
            Server server = discordBot.getServerConfiguration().getServer(guild.getIdLong());
            Message message = event.getMessage();

            if(event.getChannelType() == ChannelType.TEXT) {
                if(event.getChannel().asTextChannel().getIdLong() == server.getScheduleChannel()) {
                    String error = Yuh4jMessageUtility.sendMessage(guild, discordBot);
                    if(error != null) {
                        message.reply("Error: " + error).queue();
                    }
                }
            }
        }
        if(genericEvent instanceof MessageUpdateEvent) {
            MessageUpdateEvent event = (MessageUpdateEvent) genericEvent;
            Guild guild = event.getGuild();
            Server server = discordBot.getServerConfiguration().getServer(guild.getIdLong());
            Message message = event.getMessage();

            if(event.getChannelType() == ChannelType.TEXT) {
                if(event.getChannel().asTextChannel().getIdLong() == server.getScheduleChannel()) {
                    String error = Yuh4jMessageUtility.sendMessage(guild, discordBot);
                    if(error != null) {
                        message.reply("Error: " + error).queue();
                    }
                }
            }
        }
        if(genericEvent instanceof MessageDeleteEvent) {
            MessageDeleteEvent event = (MessageDeleteEvent) genericEvent;

            Guild guild = event.getGuild();
            Server server = discordBot.getServerConfiguration().getServer(guild.getIdLong());

            if(event.getChannelType() == ChannelType.TEXT) {
                if(event.getChannel().asTextChannel().getIdLong() == server.getScheduleChannel()) {
                    String error = Yuh4jMessageUtility.sendMessage(guild, discordBot);
                    if(error != null) {
                        discordBot.getPlugin().getLogger().severe("[Yuh4j] Error:" + error);
                    }
                }
            }
        }
    }
}
