package tech.adelemphii.skynet.objects;

import org.jetbrains.annotations.Nullable;

public class Server {

    private final long serverID;
    private long scheduleChannel;
    private long timeline;
    private long timelineMessage;

    private long adminRoleID;
    private String prefix = "!";

    private int minutesBeforeOpTimer;

    public Server(long serverID, long scheduleChannel, long timeline, long timelineMessage,
                  long adminRoleID, @Nullable String prefix, int minutesBeforeOpTimer) {
        this.serverID = serverID;
        this.scheduleChannel = scheduleChannel;
        this.timeline = timeline;
        this.timelineMessage = timelineMessage;
        this.adminRoleID = adminRoleID;
        if(prefix != null) {
            this.prefix = prefix;
        }
        this.minutesBeforeOpTimer = minutesBeforeOpTimer;
    }

    public long getServerID() {
        return serverID;
    }

    public long getScheduleChannel() {
        return scheduleChannel;
    }

    public void setScheduleChannel(long scheduleChannel) {
        this.scheduleChannel = scheduleChannel;
    }

    public long getTimeline() {
        return timeline;
    }

    public void setTimeline(long timeline) {
        this.timeline = timeline;
    }

    public long getTimelineMessage() {
        return timelineMessage;
    }

    public void setTimelineMessage(long timelineMessage) {
        this.timelineMessage = timelineMessage;
    }

    public long getAdminRoleID() {
        return adminRoleID;
    }

    public void setAdminRoleID(long adminRoleID) {
        this.adminRoleID = adminRoleID;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public int getMinutesBeforeOpTimer() {
        return minutesBeforeOpTimer;
    }

    public void setMinutesBeforeOpTimer(int minutesBeforeOpTimer) {
        this.minutesBeforeOpTimer = minutesBeforeOpTimer;
    }

    @Override
    public String toString() {
        return "Server{" +
                "serverID=" + serverID +
                ", scheduleChannel=" + scheduleChannel +
                ", timeline=" + timeline +
                ", timelineMessage=" + timelineMessage +
                ", adminRoleID=" + adminRoleID +
                ", prefix='" + prefix + '\'' +
                ", minutesBeforeOpTimer=" + minutesBeforeOpTimer +
                '}';
    }
}
