package tech.adelemphii.skynet.discord;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public interface BaseCommand {

    void execute(MessageReceivedEvent event);

    boolean requireAdmin();

    String name();

    List<String> subCommands();
}
