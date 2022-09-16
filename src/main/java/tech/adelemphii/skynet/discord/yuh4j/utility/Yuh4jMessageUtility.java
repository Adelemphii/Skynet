package tech.adelemphii.skynet.discord.yuh4j.utility;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.requests.restaction.pagination.ReactionPaginationAction;
import org.apache.commons.lang.StringUtils;
import tech.adelemphii.skynet.discord.yuh4j.Yuh4j;
import tech.adelemphii.skynet.objects.Server;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Yuh4jMessageUtility {

    public static String sendMessage(Guild guild, Yuh4j discordBot) {
        Server server = discordBot.getServerConfiguration().getServer(guild.getIdLong());
        if(server == null) {
            return "SERVER IS NULL";
        }

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

    public static String sendBreadPings(Guild guild, Yuh4j discordBot, MessageEmbed embed, ArrayList<Mission> missions,
                                        String text, long delay) {
        Server server = discordBot.getServerConfiguration().getServer(guild.getIdLong());

        String checks = doChecks(server, guild, missions);
        if(checks != null) {
            return checks;
        }

        TextChannel channel = guild.getTextChannelById(server.getTimeline());
        assert channel != null;
        channel.sendMessage(text).addEmbeds(embed).queue(message -> message.delete().queueAfter(delay, TimeUnit.MINUTES));
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
        if(server == null) {
            return null;
        }
        ArrayList<Mission> missions = new ArrayList<>();

        TextChannel textChannel = guild.getTextChannelById(server.getScheduleChannel());
        if(textChannel == null) {
            return null;
        }

        String regex = "@everyone(.*?)\\s*[,|-]*\\s*<t:([0-9]*(?=(?::[tTdDfFR])?>))";

        Pattern pattern = Pattern.compile(regex);

        MessageHistory messageHistory = textChannel.getHistory();
        messageHistory.retrievePast(50).complete();
        for(Message message : messageHistory.getRetrievedHistory()) {
            String messageRaw = message.getContentStripped();
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
        embedBuilder.setColor(Color.decode("#5377ff"));
        return embedBuilder.build();
    }

    public static long getTimeUntilMission(Mission mission) {
        long timestamp = mission.getTimestampInMilliseconds();
        long currentTimestamp = System.currentTimeMillis() / 1000L;

        return timestamp - currentTimestamp;
    }

    public static Map<String, MessageEmbed> getPingsWhenTime(Mission mission, long timestamp) {
        long timeUntilMission = getTimeUntilMission(mission);

        if(timeUntilMission >= timestamp) {
            return null;
        }

        Message message = mission.getMessage();
        List<MessageReaction> reactions = message.getReactions();

        List<User> reactedUsers = new ArrayList<>();
        for(MessageReaction messageReaction : reactions) {
            if(messageReaction.getEmoji().asUnicode().getName().equalsIgnoreCase("\uD83C\uDF5E")) {
                ReactionPaginationAction reactionPaginationAction = messageReaction.retrieveUsers();
                reactionPaginationAction.forEach(reactedUsers::add);
            }
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle("Mission Reminder");
        StringBuilder sb = new StringBuilder();

        int count = 0;
        for(User user : reactedUsers) {
            sb.append(user.getAsMention()).append(", ");
            count++;

            if(count % 5 == 0) {
                sb.append("\n");
            }
        }
        sb.deleteCharAt(sb.length() - 2);

        embedBuilder.setDescription("[" + mission.getMissionName() + "](" + mission.getMessage().getJumpUrl() + ") begins <" + mission.getTimestamp() + ":R>");

        embedBuilder.setColor(Color.decode("#5377ff"));
        Map<String, MessageEmbed> embedMap = new HashMap<>();
        embedMap.put(sb.toString(), embedBuilder.build());
        return embedMap;
    }

}
