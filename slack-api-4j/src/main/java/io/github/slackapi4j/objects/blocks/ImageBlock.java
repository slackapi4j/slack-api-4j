package io.github.slackapi4j.objects.blocks;

import java.net.MalformedURLException;
import java.net.URL;

import io.github.slackapi4j.internal.Utilities;
import io.github.slackapi4j.objects.blocks.composition.TextObject;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Created for the AddstarMC Project. Created by Narimm on 21/02/2019.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
public class ImageBlock extends Block{
    private TextObject title;
    private URL imageUrl;
    private String altText;

    public ImageBlock() {
        super.setType(BlockType.IMAGE);
    }

    @Override
    protected void load(JsonObject root, JsonDeserializationContext context) {
        super.load(root, context);
        this.title = Utilities.getTextObject(root.get("title"), context, TextObject.TextType.PLAIN);
        try {
            this.imageUrl = new URL(root.get("imageURL").getAsString());
        } catch (MalformedURLException e) {
            throw new JsonParseException("URL could not be decoded");
        }
        this.altText = Utilities.getAsString(root.get("alt_text"));
        
    }
    
    @Override
    protected JsonObject save(JsonObject root, JsonSerializationContext context) {
        super.save(root, context);
        Utilities.serializeTextObject(root, "title", this.title, context);
        root.addProperty("alt_text", this.altText);
        root.addProperty("image_url", this.imageUrl.toString());
        return root;
    }
}
