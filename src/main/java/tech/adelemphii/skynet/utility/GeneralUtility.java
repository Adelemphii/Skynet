package tech.adelemphii.skynet.utility;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.Emoji;

public class GeneralUtility {

    public static boolean isAdmin(Guild guild, Member member, long adminID) {
        Role role = guild.getRoleById(adminID);
        return member.getRoles().contains(role) || member.getPermissions().contains(Permission.ADMINISTRATOR);
    }

    public static void addPositiveReaction(Message message) {
        message.addReaction(Emoji.fromUnicode("\uD83D\uDC4D")).queue();
    }

    public static void addNegativeReaction(Message message) {
        message.addReaction(Emoji.fromUnicode("\uD83D\uDC4E")).queue();
    }

}
