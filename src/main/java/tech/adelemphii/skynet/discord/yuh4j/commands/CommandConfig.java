package tech.adelemphii.skynet.discord.yuh4j.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import tech.adelemphii.skynet.discord.BaseCommand;
import tech.adelemphii.skynet.discord.yuh4j.Yuh4j;
import tech.adelemphii.skynet.discord.yuh4j.utility.Yuh4jMessageUtility;
import tech.adelemphii.skynet.discord.yuh4j.objects.Server;
import tech.adelemphii.skynet.utility.GeneralUtility;

import java.util.Arrays;
import java.util.List;

public class CommandConfig implements BaseCommand {

    private final Yuh4j discordBot;
    public CommandConfig(Yuh4j discordBot) {
        this.discordBot = discordBot;
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        Guild guild = event.getGuild();
        Server server = discordBot.getServerConfiguration().getServer(guild.getIdLong());

        String[] args = event.getMessage().getContentRaw()
                .replace(server.getPrefix() + name() + " ", "").split(" ");

        if(args.length == 0) {
            event.getMessage().reply("Examples: " + server.getPrefix() + "config set reminder_time_mins 30 or " +
                    server.getPrefix() + "config set prefix @").setEmbeds(getHelpEmbed(server)).queue();
            return;
        }
        String arg1 = args[0];

        if(arg1.equalsIgnoreCase("display")) {
            event.getMessage().replyEmbeds(getDisplayEmbed(server)).queue();
            return;
        }

        if(arg1.equalsIgnoreCase("help") || args.length < 3) {
            event.getMessage().reply("Examples: " + server.getPrefix() + "config set reminder_time_mins 30 or " +
                    server.getPrefix() + "config set prefix @").setEmbeds(getHelpEmbed(server)).queue();
            return;
        }

        String arg2 = args[1];
        String arg3 = args[2];
        if(arg2 == null || arg3 == null) {
            event.getMessage().reply("Examples: " + server.getPrefix() + "config set reminder_time_mins 30 or " +
                    server.getPrefix() + "config set prefix @").setEmbeds(getHelpEmbed(server)).queue();
            return;
        }
        if(arg1.equalsIgnoreCase("set")) {
            String error = set(arg2, arg3, server, event.getMessage());
            if(error != null) {
                event.getMessage().reply(error).setEmbeds(getHelpEmbed(server)).queue();
            }
        }
    }

    private String set(String type, String argument, Server server, Message message) {
        switch (type.toUpperCase()) {
            case "PREFIX":
                server.setPrefix(argument);
                GeneralUtility.addPositiveReaction(message);
                discordBot.getServerConfiguration().addServer(server);
                discordBot.getServerConfiguration().saveServers();
                return null;
            case "REMINDER_TIME_MINS":
                int time;
                try {
                    time = Integer.parseInt(argument);
                } catch(NumberFormatException e) {
                    return argument + " is not a valid number. Example: 30";
                }

                server.setMinutesBeforeOpTimer(time);
                GeneralUtility.addPositiveReaction(message);
                discordBot.getServerConfiguration().addServer(server);
                discordBot.getServerConfiguration().saveServers();
                return null;
            case "ADMIN_ROLE":
                long roleID;
                try {
                    roleID = Long.parseLong(argument);
                } catch(NumberFormatException e) {
                    return argument + " is not a valid number. Example: 866292160495353876";
                }

                server.setAdminRoleID(roleID);
                GeneralUtility.addPositiveReaction(message);
                discordBot.getServerConfiguration().addServer(server);
                discordBot.getServerConfiguration().saveServers();
                return null;
            default:
                return argument + " is not a valid argument.";
        }
    }

    private MessageEmbed getHelpEmbed(Server server) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle("Config Command Args");
        builder.setDescription(server.getPrefix() + "config <command> <type> <id>");

        builder.addField("Argument 1", "Valid Subcommands: set, help, display", false);
        builder.addField("Argument 2", "Valid types: prefix, reminder_time_mins, admin_role", false);
        builder.addField("Argument 3", "<value>", false);

        builder.setColor(Yuh4jMessageUtility.getTheme());
        return builder.build();
    }

    private MessageEmbed getDisplayEmbed(Server server) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle("Server Config");
        builder.addField("Prefix", server.getPrefix(), true);
        builder.addField("Admin Role", server.getAdminRoleID() + "", true);
        builder.addField("Minutes Before Op Reminder", server.getMinutesBeforeOpTimer() + "", false);

        builder.addField("Timeline Channel", "<#" + server.getTimeline() + ">", false);
        builder.addField("Schedule Channel", "<#" + server.getScheduleChannel() + ">", true);

        builder.setColor(Yuh4jMessageUtility.getTheme());
        return builder.build();
    }

    @Override
    public boolean requireAdmin() {
        return true;
    }

    @Override
    public String name() {
        return "config";
    }

    @Override
    public List<String> subCommands() {
        return Arrays.asList("set", "help", "display");
    }
}
