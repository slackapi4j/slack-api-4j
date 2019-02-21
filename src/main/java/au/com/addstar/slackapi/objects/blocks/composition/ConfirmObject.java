package au.com.addstar.slackapi.objects.blocks.composition;

import au.com.addstar.slackapi.internal.Utilities;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created for the AddstarMC Project. Created by Narimm on 21/02/2019.
 */
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
public class ConfirmObject extends CompositionObject{
    
    private TextObject title;
    private TextObject text;
    private TextObject confirm;
    private TextObject deny;
    @Override
    protected JsonElement save(JsonObject root, JsonSerializationContext context) {
        Utilities.serializeTextObject(root,"title",title,context);
        Utilities.serializeTextObject(root,"text",text,context);
        Utilities.serializeTextObject(root,"confirm",confirm,context);
        Utilities.serializeTextObject(root,"deny",deny,context);
        return root;
    }
    
    @Override
    protected void load(JsonObject root, JsonDeserializationContext context) {
        title = Utilities.getTextObject(root.get("title"),context, TextObject.TextType.PLAIN);
        text = Utilities.getTextObject(root.get("text"),context,null);
        confirm = Utilities.getTextObject(root.get("confirm"),context, TextObject.TextType.PLAIN);
        deny = Utilities.getTextObject(root.get("deny"),context, TextObject.TextType.PLAIN);
    }
}
