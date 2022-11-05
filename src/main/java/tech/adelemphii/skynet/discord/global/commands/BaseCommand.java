package tech.adelemphii.skynet.discord.global.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import tech.adelemphii.skynet.discord.global.enums.CommandType;

import java.util.List;

public interface BaseCommand {

    void execute(MessageReceivedEvent event);

    boolean requireAdmin();

    String name();

    List<String> subCommands();

    CommandType getCommanedType();
}
