package au.com.addstar.slackapi.internal;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 20/02/2019.
 */
public enum SlackConversationType {
    PUBLIC("public"),
    PRIVATE("private"),
    IM("im"),
    MPIM("mpim");

    private String value;
    SlackConversationType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }}
