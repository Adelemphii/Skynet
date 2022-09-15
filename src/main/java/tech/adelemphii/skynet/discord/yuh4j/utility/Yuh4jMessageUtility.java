package tech.adelemphii.skynet.discord.yuh4j.utility;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.apache.commons.lang.StringUtils;
import tech.adelemphii.skynet.discord.yuh4j.Yuh4j;
import tech.adelemphii.skynet.objects.Server;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Yuh4jMessageUtility {

    public static String sendMessage(Guild guild, Yuh4j discordBot) {
        Server server = discordBot.getServerConfiguration().getServer(guild.getIdLong());
        ArrayList<Mission> messageList = scrapeMessages(guild, server);

        String checks = doChecks(server, guild, messageList);

        if(checks != null) {
            return checks;
        }

        TextChannel channel = guild.getTextChannelById(server.getTimeline());

        assert messageList != null;
        MessageEmbed embedList = createEmbed(messageList);

        assert channel != null;
        if(server.getTimelineMessage() == 0) {
            channel.sendMessageEmbeds(embedList).queue(message -> server.setTimelineMessage(message.getIdLong()));
            discordBot.getServerConfiguration().addServer(server);
        } else {
            channel.retrieveMessageById(server.getTimelineMessage())
                    .queue(topicMessage -> topicMessage.editMessageEmbeds(embedList).queue(),
                            new ErrorHandler().handle(ErrorResponse.UNKNOWN_MESSAGE, (e) -> {
                                channel.sendMessageEmbeds(embedList).queue(message
                                        -> server.setTimelineMessage(message.getIdLong()));
                                discordBot.getServerConfiguration().addServer(server);
                            })
                    );
        }
        return null;
    }

    private static String doChecks(Server server, Guild guild, ArrayList<Mission> messageMap) {
        if(server == null) {
            return "SERVER IS NULL";
        }

        TextChannel channel = guild.getTextChannelById(server.getTimeline());
        if(channel == null) {
            return "TIMELINE CHANNEL NOT SPECIFIED";
        }

        if(messageMap == null) {
            return "MESSAGES NOT LOADED";
        }
        return null;
    }

    public static ArrayList<Mission> scrapeMessages(Guild guild, Server server) {
        ArrayList<Mission> missions = new ArrayList<>();

        TextChannel textChannel = guild.getTextChannelById(server.getScheduleChannel());
        if(textChannel == null) {
            return null;
        }

        String regex = "@everyone(.*?)\\s*[,|-]*\\s*<t:([0-9]*(?=(?::[tTdDfFR])?>))";

        Pattern pattern = Pattern.compile(regex);

        MessageHistory messageHistory = textChannel.getHistory();
        messageHistory.retrievePast(10).complete();
        for(Message message : messageHistory.getRetrievedHistory()) {
            String messageRaw = message.getContentRaw();
            Matcher matcher = pattern.matcher(messageRaw);
            while(matcher.find()) {
                String match = matcher.group();

                String timestamp = StringUtils.substringAfter(match, "<");
                String name = StringUtils.substringBefore(match, " <");
                name = name.replace("@everyone ", "");

                long timestampMillis;
                try {
                    timestampMillis = Long.parseLong(timestamp.replace("t:", ""));
                } catch(NumberFormatException e) {
                    continue;
                }
                if(timestampMillis != 0) {
                    Mission mission = new Mission(name, timestamp, timestampMillis, message);
                    missions.add(mission);
                }
            }
        }

        return missions;
    }

    public static MessageEmbed createEmbed(ArrayList<Mission> missions) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        missions.sort(Comparator.comparing(Mission::getTimestampInMilliseconds));

        embedBuilder.setTitle("**Missions**");

        for(Mission mission : missions) {
            Message message = mission.getMessage();
            String timestamp = mission.getTimestamp();
            String name = StringUtils.substringBefore(mission.getMissionName(), " <");
            name = name.replace("@everyone ", "");

            embedBuilder.addField("<" + timestamp + ">", "**[" + name + "](" + message.getJumpUrl() + ")**", false);
        }
        embedBuilder.setColor(Color.BLUE);
        return embedBuilder.build();
    }

}
