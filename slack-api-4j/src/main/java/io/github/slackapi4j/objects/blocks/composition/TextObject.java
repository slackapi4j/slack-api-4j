package io.github.slackapi4j.objects.blocks.composition;

import io.github.slackapi4j.internal.Utilities;
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
        this.type = TextType.getTextType(root.get("type").getAsString());
        this.emoji = Utilities.getAsBoolean(root.get("emoji"), false);
        this.verbatim = Utilities.getAsBoolean(root.get("verbatim"), false);
        this.text = root.get("text").getAsString();
    }
    
    @Override
    protected JsonElement save(JsonObject root, JsonSerializationContext context) {
        root.addProperty("type", this.type.getValue());
        if (this.emoji != null && this.type == TextType.PLAIN) {
            root.addProperty("emoji", this.emoji);
        }
        if (this.verbatim != null) {
            root.addProperty("verbatim", this.verbatim);
        }
        root.addProperty("text", this.text);
        return root;
    }
    
    public enum TextType {
        PLAIN("plain_text"),
        MARKDOWN("mrkdwn");
        @Getter
        private final String value;

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
