package tech.adelemphii.skynet.discord.global.events;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import tech.adelemphii.skynet.Skynet;
import tech.adelemphii.skynet.discord.global.commands.BaseCommand;
import tech.adelemphii.skynet.discord.DiscordBot;
import tech.adelemphii.skynet.discord.forumscraper.objects.ForumScraperServer;
import tech.adelemphii.skynet.discord.global.enums.CommandType;
import tech.adelemphii.skynet.discord.yuh4j.objects.Yuh4jServer;
import tech.adelemphii.skynet.discord.global.objects.Cooldown;
import tech.adelemphii.skynet.discord.global.objects.Server;
import tech.adelemphii.skynet.discord.global.utility.GeneralUtility;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class CommandListener implements EventListener {

    private final ArrayList<Cooldown> cooldowns = new ArrayList<>();

    private final DiscordBot discordBot;
    public CommandListener(DiscordBot discordBot) {
        this.discordBot = discordBot;
    }

    @Override
    public void onEvent(@NotNull GenericEvent genericEvent) {
        if(genericEvent instanceof MessageReceivedEvent) {
            MessageReceivedEvent event = (MessageReceivedEvent) genericEvent;
            if(event.getAuthor().isBot() || event.getAuthor().isSystem()) {
                return;
            }

            Guild guild = event.getGuild();
            Message message = event.getMessage();

            Server server = discordBot.getServerConfiguration().getServer(guild.getIdLong());
            if(server == null) {
                Skynet.getInstance().getLogger().info("Server \"" + guild.getIdLong() + ":" + guild.getName()
                        + "\" does not have a saved config, generating a new one.");
                Yuh4jServer yuh4jServer = new Yuh4jServer(false, 0, 0, 0, 0);
                ForumScraperServer forumScraperServer = new ForumScraperServer(0, 0,
                        0, 0, 0, 0, false);
                server = new Server(guild.getIdLong(), 0, 0, "!", yuh4jServer, forumScraperServer);
                discordBot.getServerConfiguration().addServer(server);
            }

            Member member = event.getMember();
            if(member == null) {
                return;
            }

            String prefix = server.getPrefix();
            if(message.getContentRaw().startsWith(prefix)) {
                if(!event.isFromGuild() || event.isWebhookMessage()) {
                    event.getMessage().reply("I can only accept commands in a server.").queue();
                    return;
                }


                if(server.getCommandChannelID() != 0 && server.getCommandChannelID() != event.getChannel().getIdLong()
                        && !GeneralUtility.isAdmin(guild, member, server.getAdminRoleID())) {
                    event.getMessage().reply("I can only accept commands from <#" + server.getCommandChannelID() + ">").queue();
                    return;
                }

                String[] split = message.getContentRaw().split(" ");
                if(split.length == 0) {
                    return;
                }
                String command = split[0];
                command = command.replace(prefix, "");

                BaseCommand baseCommand = discordBot.getCommands().get(command);

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
                    if(baseCommand.getCommanedType() == CommandType.FORUMSCRAPER) {
                        if(!server.getForumScraperServer().isEnabled()) {
                            message.reply("This module is disabled for this server!")
                                    .queue(msg -> msg.delete().queueAfter(30, TimeUnit.SECONDS));

                            if(GeneralUtility.isAdmin(guild, member, server.getAdminRoleID())) {
                                message.reply("You can enable it using " + server.getPrefix() + "module toggle forumscraper")
                                        .queue(msg -> msg.delete().queueAfter(30, TimeUnit.SECONDS));
                            }
                            return;
                        }
                    } else if(baseCommand.getCommanedType() == CommandType.YUH4J) {
                        if(!server.getYuh4jServer().isEnabled()) {
                            message.reply("This module is disabled for this server!")
                                    .queue(msg -> msg.delete().queueAfter(30, TimeUnit.SECONDS));

                            if(GeneralUtility.isAdmin(guild, member, server.getAdminRoleID())) {
                                message.reply("You can enable it using " + server.getPrefix() + "module toggle yuh4j")
                                        .queue(msg -> msg.delete().queueAfter(30, TimeUnit.SECONDS));
                            }
                            return;
                        }
                    }
                    if(baseCommand.requireAdmin()) {
                        if(!GeneralUtility.isAdmin(guild, member, server.getAdminRoleID())) {
                            event.getMessage().reply("Only admins can use this command.")
                                    .queue(msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
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
