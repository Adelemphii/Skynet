package tech.adelemphii.skynet.discord.global.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import tech.adelemphii.skynet.Skynet;
import tech.adelemphii.skynet.discord.forumscraper.objects.ForumScraperServer;
import tech.adelemphii.skynet.discord.global.enums.CommandType;
import tech.adelemphii.skynet.discord.global.objects.Server;
import tech.adelemphii.skynet.discord.global.utility.GeneralUtility;
import tech.adelemphii.skynet.discord.global.utility.data.ServerConfiguration;
import tech.adelemphii.skynet.discord.yuh4j.objects.Yuh4jServer;

import java.util.List;

public class GlobalModule implements BaseCommand {
    @Override
    public void execute(MessageReceivedEvent event) {
        Guild guild = event.getGuild();

        ServerConfiguration serverConfiguration = Skynet.getInstance().getDiscordBot().getServerConfiguration();
        Server server = serverConfiguration.getServer(guild.getIdLong());

        Message message = event.getMessage();
        String[] args = event.getMessage().getContentRaw()
                .replace(server.getPrefix() + name() + " ", "").split(" ");

        if(args.length < 2) {
            if(args.length == 1 && args[0].equalsIgnoreCase("display")) {
                message.replyEmbeds(display(server)).queue();
                return;
            }

            message.replyEmbeds(help(server)).queue();
            return;
        }

        if(args[0].equalsIgnoreCase("toggle")) {
            if(args[1].isEmpty()) {
                message.replyEmbeds(help(server)).queue();
            } else if(determineType(args[1], server)) {
                GeneralUtility.addPositiveReaction(message);
                serverConfiguration.addServer(server);
            }
        }
    }

    private boolean determineType(String type, Server server) {
        switch(type.toUpperCase()) {
            case "YUH4J":
                Yuh4jServer yuh4jServer = server.getYuh4jServer();
                yuh4jServer.setEnabled(!server.getYuh4jServer().isEnabled());
                server.setYuh4jServer(yuh4jServer);
                return true;
            case "FORUMSCRAPER":
                ForumScraperServer forumScraperServer = server.getForumScraperServer();
                forumScraperServer.setEnabled(!server.getForumScraperServer().isEnabled());
                server.setForumScraperServer(forumScraperServer);
                return true;
            default:
                return false;
        }
    }

    private MessageEmbed display(Server server) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle("Module Status");

        builder.addField("Yuh4j", "" + server.getYuh4jServer().isEnabled(), true);
        builder.addField("ForumScraper",server.getForumScraperServer().isEnabled() + "", true);

        return builder.build();
    }

    private MessageEmbed help(Server server) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle("Module Commands");

        builder.addField("Toggle", server.getPrefix() + name() + " toggle yuh4j", false);
        builder.addField("Display", server.getPrefix() + name() + " display", false);

        return builder.build();
    }

    @Override
    public boolean requireAdmin() {
        return false;
    }

    @Override
    public String name() {
        return "module";
    }

    @Override
    public List<String> subCommands() {
        return List.of("toggle");
    }

    @Override
    public CommandType getCommanedType() {
        return CommandType.GLOBAL;
    }
}
