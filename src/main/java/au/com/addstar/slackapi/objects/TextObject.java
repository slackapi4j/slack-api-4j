package au.com.addstar.slackapi.objects;

import au.com.addstar.slackapi.internal.Utilities;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by benjamincharlton on 20/02/2019.
 */
@NoArgsConstructor
@Getter
public class TextObject extends BaseObject {

    private TextType type;
    private Boolean emoji;
    private Boolean verbatim;
    private String text;

    @Override
    protected void load(JsonObject root, JsonDeserializationContext context) {
        type = TextType.valueOf(root.get("type").getAsString());
        emoji = Utilities.getAsBoolean(root.get("emoji"), false);
        verbatim = Utilities.getAsBoolean(root.get("verbatim"), false);
        text =  root.get("text").getAsString();
    }

    private enum TextType {
        PLAIN("plain_text"),
        MARKDOWN("mrkdwn");

        private String value;

        TextType(String value) {
            this.value = value;
        }
    }
}
