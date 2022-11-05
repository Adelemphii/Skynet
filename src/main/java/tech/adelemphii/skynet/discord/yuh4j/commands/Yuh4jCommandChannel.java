package tech.adelemphii.skynet.discord.yuh4j.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import tech.adelemphii.skynet.discord.global.commands.BaseCommand;
import tech.adelemphii.skynet.discord.DiscordBot;
import tech.adelemphii.skynet.discord.global.enums.CommandType;
import tech.adelemphii.skynet.discord.yuh4j.objects.Yuh4jServer;
import tech.adelemphii.skynet.discord.yuh4j.utility.Yuh4jMessageUtility;
import tech.adelemphii.skynet.discord.global.objects.Server;
import tech.adelemphii.skynet.discord.global.utility.GeneralUtility;

import java.util.Arrays;
import java.util.List;

public class Yuh4jCommandChannel implements BaseCommand {

    private final DiscordBot discordBot;

    public Yuh4jCommandChannel(DiscordBot discordBot) {
        this.discordBot = discordBot;
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        Guild guild = event.getGuild();
        Server server = discordBot.getServerConfiguration().getServer(guild.getIdLong());

        String[] args = event.getMessage().getContentRaw()
                .replace(server.getPrefix() + name(), "").split(" ");
        if(args.length == 0) {
            GeneralUtility.addNegativeReaction(event.getMessage());
            help(server, event.getMessage());
            return;
        }
        if(args[0].equalsIgnoreCase("set")) {
            if(args.length < 3) {
                GeneralUtility.addNegativeReaction(event.getMessage());
                help(server, event.getMessage());
                return;
            }
            try {
                Long.parseLong(args[2]);
            } catch(NumberFormatException e) {
                GeneralUtility.addNegativeReaction(event.getMessage());
                help(server, event.getMessage());
                return;
            }
            if(set(args, server)) {
                GeneralUtility.addPositiveReaction(event.getMessage());
                discordBot.getServerConfiguration().addServer(server);
            } else {
                GeneralUtility.addNegativeReaction(event.getMessage());
            }
        } else if(args[0].equalsIgnoreCase("saveconfig")) {
            GeneralUtility.addPositiveReaction(event.getMessage());
            saveConfig();
        } else if(args[0].equalsIgnoreCase("update")) {
            GeneralUtility.addPositiveReaction(event.getMessage());
            update(guild, event.getMessage());
        } else if(args[0].equalsIgnoreCase("help")) {
            help(server, event.getMessage());
        } else {
            help(server, event.getMessage());
        }
    }

    private void help(Server server, Message message) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle("Channel Command Args");
        builder.setDescription(server.getPrefix() + name() + " <command> <type> <id>");

        builder.addField("Argument 1", "Valid Subcommands: set, saveconfig, update, help", false);
        builder.addField("Argument 2", "Valid types: schedule_channel, timeline_channel", false);
        builder.addField("Argument 3", "Channel ID, such as 819699195681832991", false);

        builder.setColor(Yuh4jMessageUtility.getTheme());
        message.reply("Examples: " + server.getPrefix() + name() + " set schedule_channel 819699195681832991 or " +
                server.getPrefix() + name() + " update").setEmbeds(builder.build()).queue();
    }

    private void saveConfig() {
        discordBot.getServerConfiguration().saveServers();
    }

    private void update(Guild guild, Message message) {
        String error = Yuh4jMessageUtility.sendMessage(guild, discordBot);
        if(error != null) {
            message.reply("Error: " + error).queue();
        }
    }

    private boolean set(String[] args, Server server) {
        Yuh4jServer yuh4jServer = server.getYuh4jServer();
        switch (args[1].toUpperCase()) {
            case "SCHEDULE_CHANNEL":
                yuh4jServer.setScheduleChannel(Long.parseLong(args[2]));
                server.setYuh4jServer(yuh4jServer);
                return true;
            case "TIMELINE_CHANNEL":
                yuh4jServer.setTimeline(Long.parseLong(args[2]));
                server.setYuh4jServer(yuh4jServer);
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean requireAdmin() {
        return true;
    }

    @Override
    public String name() {
        return "y4jchannel";
    }

    @Override
    public List<String> subCommands() {
        return Arrays.asList("set", "saveconfig", "update", "help");
    }

    @Override
    public CommandType getCommanedType() {
        return CommandType.YUH4J;
    }
}
