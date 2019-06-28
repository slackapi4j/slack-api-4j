package io.github.slackapi4j.objects.blocks;

import java.util.List;

import io.github.slackapi4j.objects.blocks.elements.Element;
import com.google.gson.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Created for the AddstarMC Project. Created by Narimm on 21/02/2019.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
public class ActionBlock extends Block{
    private List<Element> elements;

    public ActionBlock() {
        this.setType(BlockType.ACTIONS);
    }

    @Override
    protected void load(JsonObject root, JsonDeserializationContext context) {
        super.load(root, context);
        JsonArray arr = root.getAsJsonArray("elements");
        this.elements = Element.deserialize(arr,context);
    }
    
    @Override
    protected JsonObject save(JsonObject root, JsonSerializationContext context)
    {
        JsonArray array = new JsonArray();
        for (Element el : this.elements) {
            JsonElement e  = context.serialize(el);
            array.add(e);
        }
        root.add("elements",array);
        return super.save(root, context);
    }
    
    
}
