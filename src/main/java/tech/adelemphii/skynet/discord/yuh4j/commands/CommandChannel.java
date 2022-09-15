package tech.adelemphii.skynet.discord.yuh4j.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import tech.adelemphii.skynet.discord.BaseCommand;
import tech.adelemphii.skynet.discord.yuh4j.Yuh4j;
import tech.adelemphii.skynet.discord.yuh4j.utility.Yuh4jMessageUtility;
import tech.adelemphii.skynet.objects.Server;
import tech.adelemphii.skynet.utility.GeneralUtility;

import java.util.Arrays;
import java.util.List;

public class CommandChannel implements BaseCommand {

    private final Yuh4j discordBot;

    public CommandChannel(Yuh4j discordBot) {
        this.discordBot = discordBot;
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        Guild guild = event.getGuild();
        Server server = discordBot.getServerConfiguration().getServer(guild.getIdLong());

        String[] args = event.getMessage().getContentRaw()
                .replace(server.getPrefix() + "channel ", "").split(" ");
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
        builder.setDescription(server.getPrefix() + "channel <command> <type> <id>");

        builder.addField("Argument 1", "Valid Subcommands: set, saveconfig, update, help", false);
        builder.addField("Argument 2", "Valid types: schedule_channel, timeline_channel", false);
        builder.addField("Argument 3", "Channel ID, such as 819699195681832991", false);

        message.reply("Examples: " + server.getPrefix() + "channel set schedule_channel 819699195681832991 or " +
                server.getPrefix() + "channel update").setEmbeds(builder.build()).queue();
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
        switch (args[1].toUpperCase()) {
            case "SCHEDULE_CHANNEL":
                server.setScheduleChannel(Long.parseLong(args[2]));
                return true;
        case "TIMELINE_CHANNEL":
                server.setTimeline(Long.parseLong(args[2]));
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
        return "channel";
    }

    @Override
    public List<String> subCommands() {
        return Arrays.asList("set", "saveconfig", "update", "help");
    }
}
