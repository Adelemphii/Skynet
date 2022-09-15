package tech.adelemphii.skynet.discord.yuh4j.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import tech.adelemphii.skynet.discord.BaseCommand;
import tech.adelemphii.skynet.discord.yuh4j.Yuh4j;
import tech.adelemphii.skynet.objects.Server;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class CommandHelp implements BaseCommand {

    private Server server;
    private final Yuh4j discordBot;

    public CommandHelp(Yuh4j discordBot) {
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

        builder.setTitle("__Bot Commands__");

        Map<String, BaseCommand> commands = discordBot.getCommands();

        StringBuilder sb = new StringBuilder();
        for(BaseCommand baseCommand : commands.values()) {
            sb.append("\n» **").append(commandPrefix).append(baseCommand.name()).append("**");

            List<String> subCommands = baseCommand.subCommands();

            if(subCommands != null && !subCommands.isEmpty()) {
                for(String string : subCommands) {
                    sb.append("\n» **").append(commandPrefix).append(baseCommand.name()).append(" ").append(string).append("**");
                }
                continue;
            }
            sb.append("\n");
        }

        builder.setDescription(sb);

        builder.setColor(Color.BLACK);

        return builder.build();
    }
}
