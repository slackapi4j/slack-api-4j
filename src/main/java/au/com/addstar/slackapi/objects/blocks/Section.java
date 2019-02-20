package au.com.addstar.slackapi.objects.blocks;

import au.com.addstar.slackapi.objects.TextObject;
import au.com.addstar.slackapi.objects.blocks.elements.Element;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by benjamincharlton on 20/02/2019.
 */

@NoArgsConstructor
@Getter
@EqualsAndHashCode(callSuper=true)
public class Section extends Block {
    private TextObject text;
    private List<TextObject> fields;
    private Element accessory;

    @Override
    protected void load(JsonObject root, JsonDeserializationContext context) {
        super.load(root, context);

    }
}
