package tech.adelemphii.skynet.discord.forumscraper.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import tech.adelemphii.skynet.Skynet;
import tech.adelemphii.skynet.discord.global.commands.BaseCommand;
import tech.adelemphii.skynet.discord.forumscraper.objects.ForumScraperServer;
import tech.adelemphii.skynet.discord.forumscraper.utility.ScrapeUtility;
import tech.adelemphii.skynet.discord.global.enums.CommandType;
import tech.adelemphii.skynet.discord.global.utility.data.ServerConfiguration;
import tech.adelemphii.skynet.discord.global.objects.Server;
import tech.adelemphii.skynet.discord.global.utility.GeneralUtility;

import java.util.Arrays;
import java.util.List;

public class FSChannel implements BaseCommand {

    @Override
    public void execute(MessageReceivedEvent event) {
        Guild guild = event.getGuild();
        ServerConfiguration serverConfiguration = Skynet.getInstance().getDiscordBot().getServerConfiguration();
        Server server = serverConfiguration.getServer(event.getGuild().getIdLong());

        String[] args = event.getMessage().getContentRaw()
                .replace(server.getPrefix() + name() + " ", "").split(" ");

        if(args.length != 0 && args.length < 3) {
            if(args[0].equalsIgnoreCase("saveconfig")) {
                GeneralUtility.addPositiveReaction(event.getMessage());
                saveConfig();
            } else if(args[0].equalsIgnoreCase("update")) {
                GeneralUtility.addPositiveReaction(event.getMessage());
                update(guild, event.getMessage());
            } else {
                help(server, event.getMessage());
            }
            return;
        }
        if(args.length >= 3 && args[0].equalsIgnoreCase("set")) {
            if(Long.getLong(args[2]) != null) {
                GeneralUtility.addNegativeReaction(event.getMessage());
                return;
            }
            if(set(args, server)) {
                GeneralUtility.addPositiveReaction(event.getMessage());
                serverConfiguration.addServer(server);
            } else {
                GeneralUtility.addNegativeReaction(event.getMessage());
            }
        }
    }

    private void help(Server server, Message message) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle("Channel Command Args");
        builder.setDescription(server.getPrefix() + name() + " <command> <type> <id>");

        builder.addField("Argument 1", "Valid Subcommands: set, saveconfig, update, help", false);
        builder.addField("Argument 2", "Valid types: popular_topics, latest_topics, status_updates, ping_updates", false);
        builder.addField("Argument 3", "Channel ID, such as 819699195681832991", false);

        message.reply("Examples: " + server.getPrefix() + name() + " set popular_topics 910726981610512415 or " +
                server.getPrefix() + name() + " update").setEmbeds(builder.build()).queue();
    }

    private void saveConfig() {
        Skynet.getInstance().getDiscordBot().getServerConfiguration().saveServers();
    }

    private void update(Guild guild, Message message) {
        String popularTopicError = ScrapeUtility.sendPopularTopics(guild);
        String latestTopicError = ScrapeUtility.sendLatestTopics(guild);
        String statusUpdateError = ScrapeUtility.sendStatusUpdates(guild);
        String pingUpdateError = ScrapeUtility.sendPingUpdate(guild);

        StringBuilder sb = new StringBuilder();

        if(popularTopicError != null) {
            sb.append(popularTopicError).append(", ");
        }

        if(latestTopicError != null) {
            sb.append(latestTopicError).append(", ");
        }

        if(statusUpdateError != null) {
            sb.append(statusUpdateError).append(", ");
        }

        if(pingUpdateError != null) {
            sb.append(pingUpdateError).append(", ");
        }

        if(sb.length() != 0) {
            sb.replace(sb.charAt(sb.indexOf(",")), sb.charAt(sb.indexOf(",")) + 1, "");
            message.reply(sb.toString()).queue();
        }
    }

    private boolean set(String[] args, Server server) {
        ForumScraperServer forumScraperServer = server.getForumScraperServer();
        switch (args[1].toUpperCase()) {
            case "POPULAR_TOPICS": {
                forumScraperServer.setPopularTopicsChannel(Long.parseLong(args[2]));
                server.setForumScraperServer(forumScraperServer);
                return true;
            }
            case "LATEST_TOPICS": {
                forumScraperServer.setLatestTopicsChannel(Long.parseLong(args[2]));
                server.setForumScraperServer(forumScraperServer);
                return true;
            }
            case "STATUS_UPDATES": {
                forumScraperServer.setStatusUpdatesChannel(Long.parseLong(args[2]));
                server.setForumScraperServer(forumScraperServer);
                return true;
            }
            case "PING_UPDATES": {
                forumScraperServer.setPingUpdateChannel(Long.parseLong(args[2]));
                server.setForumScraperServer(forumScraperServer);
                return true;
            }
            default: {
                return false;
            }
        }
    }

    @Override
    public boolean requireAdmin() {
        return true;
    }

    @Override
    public String name() {
        return "fschannel";
    }

    @Override
    public List<String> subCommands() {
        return Arrays.asList("set", "saveconfig", "update", "help");
    }

    @Override
    public CommandType getCommanedType() {
        return CommandType.FORUMSCRAPER;
    }
}
