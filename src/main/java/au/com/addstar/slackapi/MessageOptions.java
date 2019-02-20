package au.com.addstar.slackapi;

import java.net.URL;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MessageOptions
{
    public static final MessageOptions DEFAULT = builder()
        .unfurlLinks(true)
        .unfurlMedia(false)
        .asUser(true)
        .linkNames(false)
        .mode(ParseMode.Partial)
        .format(false)
        .build();
    
    private boolean unfurlLinks = true;
    private boolean unfurlMedia = false;
    private URL iconUrl;
    private String iconEmoji;
    private boolean asUser = true;
    private boolean linkNames = false;
    private String username;
    private ParseMode mode = ParseMode.Partial;
    private List<Attachment> attachments;
    private boolean format = false;
    
    public enum ParseMode
    {
        Full,
        Partial,
        None
    }
}
