package tech.adelemphii.skynet.discord.forumscraper.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import tech.adelemphii.skynet.Skynet;
import tech.adelemphii.skynet.discord.forumscraper.objects.ForumScraperServer;
import tech.adelemphii.skynet.discord.forumscraper.objects.exceptions.ScrapeException;
import tech.adelemphii.skynet.discord.forumscraper.utility.FSGeneralUtility;
import tech.adelemphii.skynet.discord.global.commands.BaseCommand;
import tech.adelemphii.skynet.discord.global.enums.CommandType;
import tech.adelemphii.skynet.discord.global.objects.Server;
import tech.adelemphii.skynet.discord.global.utility.GeneralUtility;
import tech.adelemphii.skynet.discord.global.utility.data.ServerConfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
        try {
            FSGeneralUtility.sendUpdates(guild);
            GeneralUtility.addPositiveReaction(message);
        } catch(ScrapeException e) {
            message.reply(e.getMessage()).queue(msg -> msg.delete().queueAfter(30, TimeUnit.SECONDS));
            GeneralUtility.addNegativeReaction(message);
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
