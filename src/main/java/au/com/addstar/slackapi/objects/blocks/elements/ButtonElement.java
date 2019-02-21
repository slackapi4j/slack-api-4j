package au.com.addstar.slackapi.objects.blocks.elements;

import java.net.MalformedURLException;
import java.net.URL;

import au.com.addstar.slackapi.internal.Utilities;
import au.com.addstar.slackapi.objects.blocks.composition.TextObject;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
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
public class ButtonElement extends Element {
    
    private TextObject text;
    private String action_id;
    private URL url;
    private String value;
    private Object confirm;
    
    @Override
    protected void load(JsonObject root, JsonDeserializationContext context) {
        super.load(root, context);
        text  =  Utilities.getTextObject(root.get("text"), context, TextObject.TextType.PLAIN);
        action_id = Utilities.getAsString(root.get("action_id"));
        value = Utilities.getAsString(root.get("value"));
        try {
            this.url = new URL(root.get("url").getAsString());
        } catch (MalformedURLException e) {
            throw new JsonParseException("URL could not be decoded");
        }
    }
    
    @Override
    protected JsonObject save(JsonObject root, JsonSerializationContext context) {
        return super.save(root, context);
    }
}
