package io.github.slackapi4j.objects.blocks;

import io.github.slackapi4j.internal.Utilities;
import io.github.slackapi4j.objects.blocks.composition.TextObject;
import io.github.slackapi4j.objects.blocks.elements.Element;
import com.google.gson.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by benjamincharlton on 20/02/2019.
 */

@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
public class Section extends Block {
    private TextObject text;
    private List<TextObject> fields;
    private Element accessory;

    public Section() {
        super.setType(BlockType.SECTION);
    }

    @Override
    protected JsonObject save(final JsonObject root, final JsonSerializationContext context) {
        super.save(root, context);
        root.add("text",context.serialize(this.text));
        if(this.fields != null && !this.fields.isEmpty()){
            final JsonArray out = new JsonArray();
            for(final TextObject t:this.fields){
                out.add(context.serialize(t));
            }
            root.add("fields",out);
        }
        if(this.accessory!=null){
            root.add("accessory",context.serialize(this.accessory));
        }
        return root;
}
    
    @Override
    protected void load(JsonObject root, JsonDeserializationContext context) {
        super.load(root, context);
        this.setText(context.deserialize(root.get("text"),TextObject.class));
        if(root.has("fields")) {
            JsonArray ar = root.getAsJsonArray("fields");
            this.setFields(new ArrayList<>());
            for (JsonElement el : ar) {
                this.fields.add(Utilities.getTextObject(el,context,null));
            }
        }else{
            this.setFields(null);
        }
        if(root.has("accessory")){
            this.setAccessory(context.deserialize(root.get("accessory"),Element.class));
        }
    }
}
