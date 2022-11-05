package tech.adelemphii.skynet.discord.forumscraper.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import tech.adelemphii.skynet.Skynet;
import tech.adelemphii.skynet.discord.global.commands.BaseCommand;
import tech.adelemphii.skynet.discord.forumscraper.objects.ForumScraperServer;
import tech.adelemphii.skynet.discord.forumscraper.utility.FSGeneralUtility;
import tech.adelemphii.skynet.discord.global.enums.CommandType;
import tech.adelemphii.skynet.discord.global.utility.data.ServerConfiguration;
import tech.adelemphii.skynet.discord.global.objects.Server;

import java.util.Arrays;
import java.util.List;

public class FSConfig implements BaseCommand {

    @Override
    public void execute(MessageReceivedEvent event) {
        Guild guild = event.getGuild();

        ServerConfiguration serverConfiguration = Skynet.getInstance().getDiscordBot().getServerConfiguration();
        Server server = serverConfiguration.getServer(guild.getIdLong());

        Message message = event.getMessage();
        String[] args = event.getMessage().getContentRaw()
                .replace(server.getPrefix() + name() + " " , "").split(" ");

        if(args[0].equalsIgnoreCase("display")) {
            MessageEmbed embed = display(server);
            event.getChannel().sendMessageEmbeds(embed).queue();
        } else {
            MessageEmbed embed = help(server);
            message.replyEmbeds(embed).queue();
        }
    }

    private MessageEmbed display(Server server) {
        EmbedBuilder builder = new EmbedBuilder();

        ForumScraperServer forumScraperServer = server.getForumScraperServer();

        builder.setTitle("ForumScraper Config");

        Long popTop = forumScraperServer.getPopularTopicsChannel();
        Long latTop = forumScraperServer.getLatestTopicsChannel();
        Long statUp = forumScraperServer.getStatusUpdatesChannel();
        Long pingUpdate = forumScraperServer.getPingUpdateChannel();

        builder.addField("Popular Topics Channel", "<#" + popTop + ">", true);
        builder.addField("Latest Topics Channel",  "<#" + latTop + ">", true);
        builder.addField("Status Updates Channel", "<#" + statUp + ">", true);
        builder.addField("Ping Update Channel", "<#" + pingUpdate + ">", true);

        Message popTopMessage = FSGeneralUtility.getUpdateMessage(server, popTop, forumScraperServer.getPopularTopicMessage());
        if(popTopMessage != null) {
            builder.addField("Popular Topics Message", popTopMessage.getJumpUrl(), false);
        }
        Message latTopMessage = FSGeneralUtility.getUpdateMessage(server, latTop, forumScraperServer.getLatestTopicsMessage());
        if(latTopMessage != null) {
            builder.addField("Latest Topics Message", latTopMessage.getJumpUrl(), false);
        }
        Message statUpMessage = FSGeneralUtility.getUpdateMessage(server, statUp, forumScraperServer.getStatusUpdatesMessage());
        if(statUpMessage != null) {
            builder.addField("Status Updates Message", statUpMessage.getJumpUrl(), false);
        }
        Message pingUpdateMessage = FSGeneralUtility.getUpdateMessage(server, pingUpdate, forumScraperServer.getPingUpdateMessage());
        if(pingUpdateMessage != null) {
            builder.addField("Ping Updates Message", pingUpdateMessage.getJumpUrl(), false);
        }

        return builder.build();
    }

    private MessageEmbed help(Server server) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle("Config Commands");

        builder.addField("Display", server.getPrefix() + name() + " display", false);
        builder.addField("Help", server.getPrefix() + name() + " help", false);
        return builder.build();
    }

    @Override
    public boolean requireAdmin() {
        return true;
    }

    @Override
    public String name() {
        return "fsconfig";
    }

    @Override
    public List<String> subCommands() {
        return Arrays.asList("display", "help");
    }

    @Override
    public CommandType getCommanedType() {
        return CommandType.FORUMSCRAPER;
    }
}
