package tech.adelemphii.skynet.discord.global.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import tech.adelemphii.skynet.Skynet;
import tech.adelemphii.skynet.discord.global.enums.CommandType;
import tech.adelemphii.skynet.discord.global.objects.Server;
import tech.adelemphii.skynet.discord.global.utility.GeneralUtility;
import tech.adelemphii.skynet.discord.global.utility.data.ServerConfiguration;

import java.util.Arrays;
import java.util.List;

public class GlobalConfig implements BaseCommand {

    @Override
    public void execute(MessageReceivedEvent event) {
        Guild guild = event.getGuild();

        ServerConfiguration serverConfiguration = Skynet.getInstance().getDiscordBot().getServerConfiguration();
        Server server = serverConfiguration.getServer(guild.getIdLong());

        Message message = event.getMessage();
        String[] args = event.getMessage().getContentRaw()
                .replace(server.getPrefix() + name() + " ", "").split(" ");

        if(args[0].equalsIgnoreCase("display")) {
            MessageEmbed embed = display(server);
            event.getChannel().sendMessageEmbeds(embed).queue();
            return;
        }
        if(args[0].equalsIgnoreCase("help") || args.length < 3) {
            MessageEmbed embed = help(server);
            message.replyEmbeds(embed).queue();
        } else if(args[0].equalsIgnoreCase("set")) {
            String type = args[1];
            String value = args[2];
            if(determineType(type, value, server)) {
                GeneralUtility.addPositiveReaction(message);
                serverConfiguration.addServer(server);
            } else {
                event.getMessage().reply("config " + type + " " + value + " is not proper usage.")
                        .setEmbeds(help(server)).queue();
            }
        }
    }

    private boolean determineType(String type, String value, Server server) {
        switch(type.toUpperCase()) {
            case "PREFIX":
                if(value.length() != 1) {
                    return false;
                }

                server.setPrefix(value);
                return true;
            case "ADMIN_ROLE":
                try {
                    long roleID = Long.parseLong(value);
                    server.setAdminRoleID(roleID);
                    return true;
                } catch(NumberFormatException e) {
                    return false;
                }
            case "COMMANDS_CHANNEL":
                try {
                    long commandChannelID = Long.parseLong(value);
                    server.setCommandChannelID(commandChannelID);
                    return true;
                } catch(NumberFormatException e) {
                    return false;
                }
            default:
                return false;
        }
    }

    private MessageEmbed display(Server server) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle("Server Config");

        builder.addField("Prefix", "'" + server.getPrefix() + "'", true);
        builder.addField("Admin Role",  server.getAdminRoleID() + "", true);
        builder.addField("Commands Channel", "<#" + server.getCommandChannelID() + ">", false);

        return builder.build();
    }

    private MessageEmbed help(Server server) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle("Config Commands");

        builder.setDescription("*Note: The prefix can only be 1 character long*");

        builder.addField("Prefix", server.getPrefix() + name() + " set prefix ?", true);
        builder.addField("Admin Role", server.getPrefix() + name() + " set admin_role 866292160495353876", true);
        builder.addField("Commands Channel", server.getPrefix() + name() + " set commands_channel 993383846135476366", true);
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
        return Arrays.asList("set", "display", "help");
    }

    @Override
    public CommandType getCommanedType() {
        return CommandType.GLOBAL;
    }
}
