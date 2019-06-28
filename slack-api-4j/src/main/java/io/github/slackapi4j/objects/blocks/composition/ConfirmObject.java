package io.github.slackapi4j.objects.blocks.composition;

import io.github.slackapi4j.internal.Utilities;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import lombok.*;

/**
 * Created for the AddstarMC Project. Created by Narimm on 21/02/2019.
 */
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=true)
public class ConfirmObject extends CompositionObject{
    
    private TextObject title;
    private TextObject text;
    private TextObject confirm;
    private TextObject deny;

    @Override
    protected JsonElement save(JsonObject root, JsonSerializationContext context) {
        Utilities.serializeTextObject(root, "title", this.title, context);
        Utilities.serializeTextObject(root, "text", this.text, context);
        Utilities.serializeTextObject(root, "confirm", this.confirm, context);
        Utilities.serializeTextObject(root, "deny", this.deny, context);
        return root;
    }
    
    @Override
    protected void load(JsonObject root, JsonDeserializationContext context) {
        this.title = Utilities.getTextObject(root.get("title"), context, TextObject.TextType.PLAIN);
        this.text = Utilities.getTextObject(root.get("text"), context, null);
        this.confirm = Utilities.getTextObject(root.get("confirm"), context, TextObject.TextType.PLAIN);
        this.deny = Utilities.getTextObject(root.get("deny"), context, TextObject.TextType.PLAIN);
    }
}
