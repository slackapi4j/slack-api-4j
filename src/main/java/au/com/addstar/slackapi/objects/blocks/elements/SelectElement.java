package au.com.addstar.slackapi.objects.blocks.elements;

import java.util.ArrayList;
import java.util.List;

import au.com.addstar.slackapi.internal.Utilities;
import au.com.addstar.slackapi.objects.blocks.composition.ConfirmObject;
import au.com.addstar.slackapi.objects.blocks.composition.Option;
import au.com.addstar.slackapi.objects.blocks.composition.OptionGroup;
import au.com.addstar.slackapi.objects.blocks.composition.TextObject;
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
public class SelectElement extends Element {
    private TextObject placeHolder;
    private String action_id;
    private List<Option> options;
    private List<OptionGroup> optionGroups;
    private Option initialOption;
    private ConfirmObject confirm;
    
    
    @Override
    protected void load(JsonObject root, JsonDeserializationContext context) {
        super.load(root, context);
        placeHolder  =  Utilities.getTextObject(root.get("placeholder"), context, TextObject.TextType.PLAIN);
        action_id = Utilities.getAsString(root.get("action_id"));
        if(root.has("optons")) {
            if(root.has("optionGroups")) {
                throw new JsonParseException("SelectElement cannot have both Options and OptionGroups");
            } else {
                options = new ArrayList<>();
                JsonArray arr = root.getAsJsonArray("options");
                for(JsonElement el:arr){
                    options.add(context.deserialize(el,Option.class));
                }
            }
        } else {
            if (!root.has("optionGroups")) {
                throw new JsonParseException("SelectElement must have either Options or OptionGroups");
            }
            optionGroups = context.deserialize(root.get("optionGroups"),OptionGroup.class);
        }
        initialOption = context.deserialize(root.get("initial_option"),Option.class);
        confirm = context.deserialize(root.get("confirm"),ConfirmObject.class);
    }
    
    @Override
    protected JsonObject save(JsonObject root, JsonSerializationContext context) {
        super.save(root, context);
        Utilities.serializeTextObject(root,"placeholder",placeHolder,context);
        root.addProperty("action_id",action_id);
        if(options !=null && !options.isEmpty()) {
            if(optionGroups !=null && !optionGroups.isEmpty()) {
                throw new JsonParseException("SelectElement cannot have both Options and OptionGroups");
            }
            JsonArray arr = new JsonArray();
            for (Option opt : options) {
                arr.add(context.serialize(opt, Option.class));
            }
            root.add("options", arr);
        } else {
            if(optionGroups !=null && !optionGroups.isEmpty()) {
                root.add("optionGroups",context.serialize(optionGroups,OptionGroup.class));
            } else {
                throw new JsonParseException("SelectElement must have either Options or OptionGroups");
            }
        }
        root.add("initial_options",context.serialize(initialOption,Option.class));
        root.add("confirm",context.serialize(confirm,ConfirmObject.class));
        return root;
    }
}
