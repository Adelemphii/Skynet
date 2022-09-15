package tech.adelemphii.skynet.discord.yuh4j.utility;

import net.dv8tion.jda.api.entities.Message;

public class Mission {

    private final String missionName;
    private final String timestamp;
    private final long timestampInMilliseconds;

    private final Message message;

    public Mission(String missionName, String timestamp, long timestampInMilliseconds, Message message) {
        this.missionName = missionName;
        this.timestamp = timestamp;
        this.timestampInMilliseconds = timestampInMilliseconds;
        this.message = message;
    }

    public String getMissionName() {
        return missionName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public long getTimestampInMilliseconds() {
        return timestampInMilliseconds;
    }

    public Message getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Mission{" +
                "mission='" + missionName + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", timestampInMilliseconds=" + timestampInMilliseconds +
                ", message=" + message +
                '}';
    }
}
