package tech.adelemphii.skynet.discord.global.objects;

import org.jetbrains.annotations.Nullable;
import tech.adelemphii.skynet.discord.forumscraper.objects.ForumScraperServer;
import tech.adelemphii.skynet.discord.yuh4j.objects.Yuh4jServer;

public class Server {

    private final long serverID;
    private long adminRoleID;
    private String prefix = "!";
    private long commandChannelID;

    private Yuh4jServer yuh4jServer;
    private ForumScraperServer forumScraperServer;

    public Server(long serverID, long adminRoleID, long commandChannelID, @Nullable String prefix,
                  Yuh4jServer yuh4jServer, ForumScraperServer forumScraperServer) {
        this.serverID = serverID;
        this.adminRoleID = adminRoleID;
        this.commandChannelID = commandChannelID;
        if(prefix != null) {
            this.prefix = prefix;
        }

        this.yuh4jServer = yuh4jServer;
        this.forumScraperServer = forumScraperServer;
    }

    public long getServerID() {
        return serverID;
    }

    public long getAdminRoleID() {
        return adminRoleID;
    }

    public void setAdminRoleID(long adminRoleID) {
        this.adminRoleID = adminRoleID;
    }

    public long getCommandChannelID() {
        return commandChannelID;
    }

    public void setCommandChannelID(long commandChannelID) {
        this.commandChannelID = commandChannelID;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Yuh4jServer getYuh4jServer() {
        return yuh4jServer;
    }

    public void setYuh4jServer(Yuh4jServer yuh4jServer) {
        this.yuh4jServer = yuh4jServer;
    }

    public ForumScraperServer getForumScraperServer() {
        return forumScraperServer;
    }

    public void setForumScraperServer(ForumScraperServer forumScraperServer) {
        this.forumScraperServer = forumScraperServer;
    }

    @Override
    public String toString() {
        return "Server{" +
                "serverID=" + serverID +
                ", adminRoleID=" + adminRoleID +
                ", prefix='" + prefix + '\'' +
                ", commandChannelID=" + commandChannelID +
                ", yuh4jServer=" + yuh4jServer +
                ", forumScraperServer=" + forumScraperServer +
                '}';
    }
}
