package tech.adelemphii.skynet.discord.yuh4j.objects;

public class Yuh4jServer {

    private boolean enabled;

    private int minutesBeforeOpTimer = 30;
    private long scheduleChannel;
    private long timeline;
    private long timelineMessage;

    public Yuh4jServer(boolean enabled, int minutesBeforeOpTimer, long scheduleChannel, long timeline, long timelineMessage) {
        this.enabled = enabled;
        if(minutesBeforeOpTimer != 0) {
            this.minutesBeforeOpTimer = minutesBeforeOpTimer;
        }
        this.scheduleChannel = scheduleChannel;
        this.timeline = timeline;
        this.timelineMessage = timelineMessage;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getMinutesBeforeOpTimer() {
        return minutesBeforeOpTimer;
    }

    public void setMinutesBeforeOpTimer(int minutesBeforeOpTimer) {
        this.minutesBeforeOpTimer = minutesBeforeOpTimer;
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

    @Override
    public String toString() {
        return "Yuh4jServer{" +
                "enabled=" + enabled +
                ", minutesBeforeOpTimer=" + minutesBeforeOpTimer +
                ", scheduleChannel=" + scheduleChannel +
                ", timeline=" + timeline +
                ", timelineMessage=" + timelineMessage +
                '}';
    }
}
