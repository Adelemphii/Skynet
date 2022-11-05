package tech.adelemphii.skynet.discord.yuh4j.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
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

public class Yuh4jCommandConfig implements BaseCommand {

    private final DiscordBot discordBot;
    public Yuh4jCommandConfig(DiscordBot discordBot) {
        this.discordBot = discordBot;
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        Guild guild = event.getGuild();
        Server server = discordBot.getServerConfiguration().getServer(guild.getIdLong());

        String[] args = event.getMessage().getContentRaw()
                .replace(server.getPrefix() + name() + " ", "").split(" ");

        if(args.length == 0) {
            event.getMessage().reply("Example: " + server.getPrefix() + name() + " set reminder_time_mins 30")
                    .setEmbeds(getHelpEmbed(server)).queue();
            return;
        }
        String arg1 = args[0];

        if(arg1.equalsIgnoreCase("display")) {
            event.getMessage().replyEmbeds(getDisplayEmbed(server)).queue();
            return;
        }

        if(arg1.equalsIgnoreCase("help") || args.length < 3) {
            event.getMessage().reply("Example: " + server.getPrefix() + name() + " set reminder_time_mins 30")
                    .setEmbeds(getHelpEmbed(server)).queue();
            return;
        }

        String arg2 = args[1];
        String arg3 = args[2];
        if(arg2 == null || arg3 == null) {
            event.getMessage().reply("Example: " + server.getPrefix() + name() + " set reminder_time_mins 30")
                    .setEmbeds(getHelpEmbed(server)).queue();
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
        if("REMINDER_TIME_MINS".equalsIgnoreCase(type)) {
            int time;
            try {
                time = Integer.parseInt(argument);
            } catch (NumberFormatException e) {
                return argument + " is not a valid number. Example: 30";
            }

            Yuh4jServer yuh4jServer = server.getYuh4jServer();
            yuh4jServer.setMinutesBeforeOpTimer(time);
            server.setYuh4jServer(yuh4jServer);

            GeneralUtility.addPositiveReaction(message);
            discordBot.getServerConfiguration().addServer(server);
            discordBot.getServerConfiguration().saveServers();
            return null;
        }
        return argument + " is not a valid argument.";
    }

    private MessageEmbed getHelpEmbed(Server server) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle("Config Command Args");
        builder.setDescription(server.getPrefix() + name() + " <command> <type> <id>");

        builder.addField("Argument 1", "Valid Subcommands: set, help, display", false);
        builder.addField("Argument 2", "Valid types: reminder_time_mins", false);
        builder.addField("Argument 3", "<value>", false);

        builder.setColor(Yuh4jMessageUtility.getTheme());
        return builder.build();
    }

    private MessageEmbed getDisplayEmbed(Server server) {
        EmbedBuilder builder = new EmbedBuilder();

        Yuh4jServer yuh4jServer = server.getYuh4jServer();

        builder.setTitle("Yuh4j Config");
        builder.addField("Minutes Before Op Reminder", yuh4jServer.getMinutesBeforeOpTimer() + "", false);

        builder.addField("Timeline Channel", "<#" + yuh4jServer.getTimeline() + ">", false);
        builder.addField("Schedule Channel", "<#" + yuh4jServer.getScheduleChannel() + ">", true);

        builder.setColor(Yuh4jMessageUtility.getTheme());
        return builder.build();
    }

    @Override
    public boolean requireAdmin() {
        return true;
    }

    @Override
    public String name() {
        return "y4jconfig";
    }

    @Override
    public List<String> subCommands() {
        return Arrays.asList("set", "help", "display");
    }

    @Override
    public CommandType getCommanedType() {
        return CommandType.YUH4J;
    }
}
