package au.com.addstar.slackapi.objects.blocks.composition;

import au.com.addstar.slackapi.internal.Utilities;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import lombok.*;

/**
 * Created for the AddstarMC Project. Created by Narimm on 21/02/2019.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=true)
public class Option extends CompositionObject {
    private TextObject text;
    private String value;
    
    @Override
    protected void load(JsonObject root, JsonDeserializationContext context) {
        text = Utilities.getTextObject(root.get("text"),context, TextObject.TextType.PLAIN);
        value = Utilities.getAsString(root.get("value"));
    }
    
    @Override
    protected JsonElement save(JsonObject root, JsonSerializationContext context) {
        JsonObject t = new JsonObject();
        root.add("text",context.serialize(text));
        root.addProperty("value",value);
        return root;
    }
}
