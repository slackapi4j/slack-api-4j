package au.com.addstar.slackapi.objects.blocks;

import java.util.ArrayList;
import java.util.List;

import au.com.addstar.slackapi.objects.blocks.elements.Element;
import com.google.gson.*;
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
public class ActionBlock extends Block{
    private List<Element> elements;
    
    @Override
    protected void load(JsonObject root, JsonDeserializationContext context) {
        super.load(root, context);
        JsonArray arr = root.getAsJsonArray("elements");
        this.elements = Element.deserialize(arr,context);
    }
    
    @Override
    protected JsonObject save(JsonObject root, JsonSerializationContext context) {
        return super.save(root, context);
    }
    
    
}
