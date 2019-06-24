package au.com.addstar.slackapi.objects.blocks.composition;

import java.lang.management.PlatformLoggingMXBean;

import au.com.addstar.slackapi.internal.Utilities;
import au.com.addstar.slackapi.objects.BaseObject;
import com.google.gson.*;
import lombok.*;

/**
 * Created by benjamincharlton on 20/02/2019.
 */
@Data
@Builder
@EqualsAndHashCode(callSuper=true)
@AllArgsConstructor
public class TextObject extends CompositionObject  {

    private TextType type = TextType.PLAIN;
    private Boolean emoji;
    private Boolean verbatim;
    private String text;

    public TextObject() {
    }

    @Override
    protected void load(JsonObject root, JsonDeserializationContext context) {
        type = TextType.getTextType(root.get("type").getAsString());
        emoji = Utilities.getAsBoolean(root.get("emoji"), false);
        verbatim = Utilities.getAsBoolean(root.get("verbatim"), false);
        text =  root.get("text").getAsString();
    }
    
    @Override
    protected JsonElement save(JsonObject root, JsonSerializationContext context) {
        root.addProperty("type",type.getValue());
        if(emoji !=null && type==TextType.PLAIN) {
            root.addProperty("emoji",emoji);
        }
        if(verbatim != null) {
            root.addProperty("verbatim", verbatim);
        }
        root.addProperty("text",text);
        return root;
    }
    
    public enum TextType {
        PLAIN("plain_text"),
        MARKDOWN("mrkdwn");
        @Getter
        private String value;

        TextType(String value) {
            this.value = value;
        }
        
        public static TextType getTextType(final String val){
            if(PLAIN.value.equals(val)) {
                return PLAIN;
            }
            if(MARKDOWN.value.equals(val)) {
                return MARKDOWN;
            }
            else {
                throw new JsonParseException("Invalid TextObject Type: " + val);
            }
        }
    }
}
