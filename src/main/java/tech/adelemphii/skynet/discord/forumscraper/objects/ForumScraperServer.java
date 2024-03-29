package tech.adelemphii.skynet.discord.forumscraper.objects;

public class ForumScraperServer {

    private long popularTopicsChannel;
    private long latestTopicsChannel;
    private long statusUpdatesChannel;
    private long pingUpdateChannel;

    private long popularTopicMessage;
    private long latestTopicsMessage;
    private long statusUpdatesMessage;
    private long pingUpdateMessage;
    private boolean enabled;

    public ForumScraperServer(long popularTopicsChannel, long latestTopicsChannel, long statusUpdatesChannel,
                              long popularTopicMessage, long latestTopicsMessage, long statusUpdatesMessage, boolean enabled) {
        this.popularTopicsChannel = popularTopicsChannel;
        this.latestTopicsChannel = latestTopicsChannel;
        this.statusUpdatesChannel = statusUpdatesChannel;
        this.popularTopicMessage = popularTopicMessage;
        this.latestTopicsMessage = latestTopicsMessage;
        this.statusUpdatesMessage = statusUpdatesMessage;
        this.enabled = enabled;
    }

    public long getPopularTopicsChannel() {
        return popularTopicsChannel;
    }

    public void setPopularTopicsChannel(long popularTopicsChannel) {
        this.popularTopicsChannel = popularTopicsChannel;
    }

    public long getLatestTopicsChannel() {
        return latestTopicsChannel;
    }

    public void setLatestTopicsChannel(long latestTopicsChannel) {
        this.latestTopicsChannel = latestTopicsChannel;
    }

    public long getStatusUpdatesChannel() {
        return statusUpdatesChannel;
    }

    public void setStatusUpdatesChannel(long statusUpdatesChannel) {
        this.statusUpdatesChannel = statusUpdatesChannel;
    }

    public long getPopularTopicMessage() {
        return popularTopicMessage;
    }

    public void setPopularTopicMessage(long popularTopicMessage) {
        this.popularTopicMessage = popularTopicMessage;
    }

    public long getLatestTopicsMessage() {
        return latestTopicsMessage;
    }

    public void setLatestTopicsMessage(long latestTopicsMessage) {
        this.latestTopicsMessage = latestTopicsMessage;
    }

    public long getStatusUpdatesMessage() {
        return statusUpdatesMessage;
    }

    public void setStatusUpdatesMessage(long statusUpdatesMessage) {
        this.statusUpdatesMessage = statusUpdatesMessage;
    }

    public long getPingUpdateChannel() {
        return pingUpdateChannel;
    }

    public void setPingUpdateChannel(long pingUpdateChannel) {
        this.pingUpdateChannel = pingUpdateChannel;
    }

    public long getPingUpdateMessage() {
        return pingUpdateMessage;
    }

    public void setPingUpdateMessage(long pingUpdateMessage) {
        this.pingUpdateMessage = pingUpdateMessage;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "ForumScraperServer{" +
                "popularTopicsChannel=" + popularTopicsChannel +
                ", latestTopicsChannel=" + latestTopicsChannel +
                ", statusUpdatesChannel=" + statusUpdatesChannel +
                ", pingUpdateChannel=" + pingUpdateChannel +
                ", popularTopicMessage=" + popularTopicMessage +
                ", latestTopicsMessage=" + latestTopicsMessage +
                ", statusUpdatesMessage=" + statusUpdatesMessage +
                ", pingUpdateMessage=" + pingUpdateMessage +
                ", enabled=" + enabled +
                '}';
    }
}
