package io.github.slackapi4j;

import java.net.URL;
import java.util.List;

import io.github.slackapi4j.objects.Attachment;
import io.github.slackapi4j.objects.blocks.Block;
import lombok.Builder;
import lombok.Getter;

@SuppressWarnings({"FieldMayBeFinal", "RedundantFieldInitialization"})
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
    private boolean asUser = false;
    private boolean linkNames = false;
    private String username;
    private ParseMode mode = ParseMode.Partial;
    @SuppressWarnings("deprecation")
    private List<Attachment> attachments;
    private List<Block> blocks;
    private boolean format = false;

    public enum ParseMode
    {
        Full,
        Partial,
        None
    }
}
