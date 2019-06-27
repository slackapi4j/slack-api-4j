package au.com.addstar.slackapi.objects.blocks.composition;

import java.util.ArrayList;
import java.util.List;

import au.com.addstar.slackapi.internal.Utilities;
import com.google.gson.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.checker.nullness.Opt;

/**
 * Created for the AddstarMC Project. Created by Narimm on 21/02/2019.
 */
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
public class OptionGroup extends CompositionObject{
    private TextObject label;
    private List<Option> options;
    @Override
    protected JsonElement save(JsonObject root, JsonSerializationContext context) {
        Utilities.serializeTextObject(root,"label",label,context);
        JsonArray arr = new JsonArray();
        for(Option opt:options){
            arr.add(context.serialize(opt,Option.class));
        }
        root.add("options",arr);
        return root;
    }
    
    @Override
    protected void load(JsonObject root, JsonDeserializationContext context) {
        label = Utilities.getTextObject(root.get("label"),context, TextObject.TextType.PLAIN);
        JsonArray array = root.getAsJsonArray("options");
        options = new ArrayList<>();
        for(JsonElement el: array){
            options.add(context.deserialize(el,Option.class));
        }
    }
}
