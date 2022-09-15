package tech.adelemphii.skynet.discord.yuh4j.events;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import tech.adelemphii.skynet.Skynet;
import tech.adelemphii.skynet.discord.BaseCommand;
import tech.adelemphii.skynet.discord.yuh4j.Yuh4j;
import tech.adelemphii.skynet.discord.yuh4j.utility.Yuh4jMessageUtility;
import tech.adelemphii.skynet.objects.Cooldown;
import tech.adelemphii.skynet.objects.Server;
import tech.adelemphii.skynet.utility.GeneralUtility;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class CommandListener implements EventListener {

    private final ArrayList<Cooldown> cooldowns = new ArrayList<>();

    private final Yuh4j discordBot;
    public CommandListener(Yuh4j discordBot) {
        this.discordBot = discordBot;
    }

    @Override
    public void onEvent(@NotNull GenericEvent genericEvent) {
        if(genericEvent instanceof MessageReceivedEvent) {
            MessageReceivedEvent event = (MessageReceivedEvent) genericEvent;
            if(event.getAuthor().isBot() || event.getAuthor().isSystem()) {
                return;
            }

            if(!event.isFromGuild() || event.isWebhookMessage()
                    || event.getChannel().getType() != ChannelType.TEXT) {
                event.getMessage().reply("I can only accept commands in a server.").queue();
                return;
            }

            Guild guild = event.getGuild();
            Message message = event.getMessage();

            Server server = discordBot.getServerConfiguration().getServer(guild.getIdLong());
            if(server == null) {
                Skynet.getInstance().getLogger().info("Server \"" + guild.getIdLong() + ":" + guild.getName()
                        + "\" does not have a saved config, generating a new one.");
                server = new Server(guild.getIdLong(), 0, 0, 0, 0, null);
                discordBot.getServerConfiguration().addServer(server);
            }

            String prefix = discordBot.getConfiguration().getPrefix();
            if(message.getContentRaw().startsWith(prefix)) {
                String[] split = message.getContentRaw().split(" ");
                if(split.length == 0) {
                    return;
                }
                String command = split[0];
                command = command.replace(prefix, "");

                BaseCommand baseCommand = discordBot.getCommands().get(command);

                Member member = event.getMember();

                ArrayList<Cooldown> toRemove = new ArrayList<>();
                if(!cooldowns.isEmpty()) {
                    for (Cooldown cooldown : cooldowns) {
                        if(cooldown.getMember().equals(member) && !GeneralUtility.isAdmin(guild, member, server.getAdminRoleID())) {
                            if(cooldown.getCooldown() <= 0) {
                                toRemove.add(cooldown);
                            } else {
                                message.reply("You are on command cooldown for " + cooldown.getCooldown() + " seconds")
                                        .queue(msg -> msg.delete().queueAfter(cooldown.getCooldown(), TimeUnit.SECONDS));
                                return;
                            }
                        }
                    }
                    if(!toRemove.isEmpty()) {
                        cooldowns.removeAll(toRemove);
                    }
                }

                if(baseCommand != null) {
                    if(baseCommand.requireAdmin()) {
                        assert member != null;
                        if(!GeneralUtility.isAdmin(guild, member, server.getAdminRoleID())) {
                            event.getMessage().reply("Only admins can use this command.")
                                    .queue(msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS));
                            return;
                        }
                    }
                    baseCommand.execute(event);
                    cooldowns.add(new Cooldown(member));
                }
            }
        }
    }
}
