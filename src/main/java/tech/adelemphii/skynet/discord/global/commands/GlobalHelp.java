package tech.adelemphii.skynet.discord.global.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import tech.adelemphii.skynet.discord.DiscordBot;
import tech.adelemphii.skynet.discord.global.enums.CommandType;
import tech.adelemphii.skynet.discord.global.objects.Server;
import tech.adelemphii.skynet.discord.yuh4j.utility.Yuh4jMessageUtility;

import java.util.*;

public class GlobalHelp implements BaseCommand {

    private Server server;
    private final DiscordBot discordBot;

    public GlobalHelp(DiscordBot discordBot) {
        this.discordBot = discordBot;
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        server = discordBot.getServerConfiguration().getServer(event.getGuild().getIdLong());

        event.getChannel().sendMessageEmbeds(getHelpEmbed()).queue();
    }

    @Override
    public boolean requireAdmin() {
        return false;
    }

    @Override
    public String name() {
        return "help";
    }

    @Override
    public List<String> subCommands() {
        return null;
    }

    private MessageEmbed getHelpEmbed() {
        EmbedBuilder builder = new EmbedBuilder();

        String commandPrefix = server.getPrefix();

        builder.setTitle("__Skynet Commands__");

        Map<String, BaseCommand> commands = discordBot.getCommands();
        SortedSet<String> keys = new TreeSet<>(commands.keySet());
        List<BaseCommand> commandsSorted = new ArrayList<>();
        for(String key : keys) {
            commandsSorted.add(commands.get(key));
        }

        StringBuilder sb = new StringBuilder();
        for(BaseCommand baseCommand : commandsSorted) {
            List<String> subCommands = baseCommand.subCommands();
            sb.append("\n» **").append(commandPrefix).append(baseCommand.name()).append("**");
            if(subCommands == null || subCommands.isEmpty()) {
                sb.append("\n");
            }


            if(subCommands != null && !subCommands.isEmpty()) {
                for(String string : subCommands) {
                    sb.append("\n» **").append(commandPrefix).append(baseCommand.name()).append(" ").append(string).append("**");
                }

                sb.append("\n");
            }
        }

        builder.setDescription(sb);

        builder.setColor(Yuh4jMessageUtility.getTheme());

        return builder.build();
    }

    @Override
    public CommandType getCommanedType() {
        return CommandType.GLOBAL;
    }
}
